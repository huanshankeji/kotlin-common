package com.huanshankeji.exposed.datamapping.classproperty

import com.huanshankeji.exposed.datamapping.DataMapper
import com.huanshankeji.exposed.datamapping.NullableDataMapper
import com.huanshankeji.exposed.datamapping.classproperty.OnDuplicateColumnPropertyNames.CHOOSE_FIRST
import com.huanshankeji.exposed.datamapping.classproperty.OnDuplicateColumnPropertyNames.THROW
import com.huanshankeji.exposed.datamapping.classproperty.PropertyColumnMapping.*
import com.huanshankeji.kotlin.reflect.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

// Our own class mapping implementation using reflection which should be adapted using annotation processors and code generation in the future.


// TODO: unify type parameter names

typealias PropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, *>>
//typealias LessStrictlyTypedPropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, Any?>>
/** In the order of the constructor arguments. */
typealias ClassPropertyColumnMappings<Data> = PropertyColumnMappings<Data>

/*
TODO: consider decoupling/removing `property` and `Data` from this class and rename it to `ColumnMapping`
 and add a `PropertyColumnMapping` containing the property and the `ColumnMapping`.
 However, after the refactor, `ColumnMapping` will still be coupled with `ClassPropertyColumnMappings` which is coupled with `PropertyColumnMapping`,
 so I am not sure whether this is necessary.
*/
sealed class PropertyColumnMapping<Data : Any, PropertyData>(val property: KProperty1<Data, PropertyData>) {
    class SqlPrimitive<Data : Any, PropertyData>(
        property: KProperty1<Data, PropertyData>,
        val column: Column<PropertyData>
    ) : PropertyColumnMapping<Data, PropertyData>(property)

    class NestedClass<Data : Any, PropertyData>(
        property: KProperty1<Data, PropertyData>,
        val nullability: Nullability<PropertyData>,
        val adt: Adt<PropertyData & Any>
    ) : PropertyColumnMapping<Data, PropertyData>(property) {
        sealed class Nullability<PropertyData> {
            class NonNullable<NotNullPropertyData : Any> : Nullability<NotNullPropertyData>()
            class Nullable<NotNullPropertyData : Any>(val whetherNullDependentColumn: Column<*>) :
                Nullability<NotNullPropertyData?>()
        }

        // ADT: algebraic data type
        sealed class Adt<NotNullPropertyData : Any> {
            class Product<NotNullPropertyData : Any>(val nestedMappings: ClassPropertyColumnMappings<NotNullPropertyData>) :
                Adt<NotNullPropertyData>()

            class Sum<NotNullPropertyData : Any, CaseValue>(
                val subclassMap: Map<KClass<out NotNullPropertyData>, Product<out NotNullPropertyData>>,
                val sumTypeCaseConfig: SumTypeCaseConfig<NotNullPropertyData, CaseValue>
            ) : Adt<NotNullPropertyData>() {
                val columnsForAllSubclasses = buildSet {
                    for (subclassProductMapping in subclassMap.values)
                        addAll(subclassProductMapping.nestedMappings.getColumnSet())
                }.toList()
            }
        }
    }

    class Custom<Data : Any, PropertyData>(
        property: KProperty1<Data, PropertyData>, val nullableDataMapper: NullableDataMapper<PropertyData>
    ) : PropertyColumnMapping<Data, PropertyData>(property)

    class Skip<Data : Any, PropertyData>(property: KProperty1<Data, PropertyData>) :
        PropertyColumnMapping<Data, PropertyData>(property)
}

class SumTypeCaseConfig<SuperclassData : Any, CaseValue>(
    val caseValueColumn: Column<CaseValue>,
    // TODO: use `BidirectionalConversion`
    val caseValueToClass: (CaseValue) -> KClass<out SuperclassData>,
    val classToCaseValue: (KClass<out SuperclassData>?) -> CaseValue
)


// see: https://kotlinlang.org/docs/basic-types.html, https://www.postgresql.org/docs/current/datatype.html
// Types that are commented out are not ensured to work yet.
val defaultNotNullExposedSqlPrimitiveClasses = listOf(
    Byte::class, Short::class, Int::class, Long::class, /*BigInteger::class,*/
    UByte::class, UShort::class, UInt::class, ULong::class,
    Float::class, Double::class, /*BigDecimal::class,*/
    Boolean::class,
    ByteArray::class,
    //Char::class,
    String::class,
    // types related to time and date
)

private fun KClass<*>.isEnumClass() =
    isSubclassOf(Enum::class)

fun KClass<*>.isExposedSqlPrimitiveType(): Boolean =
    this in defaultNotNullExposedSqlPrimitiveClasses || isEnumClass()

fun KType.isExposedSqlPrimitiveType() =
    (classifier as KClass<*>).isExposedSqlPrimitiveType()

class ColumnWithPropertyName(val propertyName: String, val column: Column<*>)

fun getColumnsWithPropertyNamesWithoutTypeParameter(
    table: Table, clazz: KClass<out Table> = table::class
): Sequence<ColumnWithPropertyName> =
    getColumnProperties(clazz).map {
        @Suppress("UNCHECKED_CAST")
        ColumnWithPropertyName(it.name, (it as KProperty1<Any, Column<*>>)(table))
    }

enum class OnDuplicateColumnPropertyNames {
    CHOOSE_FIRST, THROW
}

fun getColumnByPropertyNameMap(
    tables: List<Table>,
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST // defaults to `CHOOSE_FIRST` because there are very likely to be duplicate columns when joining table
): Map<String, Column<*>> {
    val columnsMap = tables.asSequence()
        .flatMap { table -> getColumnsWithPropertyNamesWithoutTypeParameter(table) }
        .groupBy { it.propertyName }
    return columnsMap.mapValues {
        it.value.run {
            when (onDuplicateColumnPropertyNames) {
                CHOOSE_FIRST -> first()
                THROW -> single()
            }
                .column
        }
    }
}

private val logger = LoggerFactory.getLogger("class property mapping")

private fun KClass<*>.isInheritable() =
    isOpen || isAbstract || isSealed

private fun KClass<*>.isAbstractOrSealed() =
    isAbstract || isSealed

/**
 * @param skip both writing and reading. Note that the property type need not be nullable if it's only used for writing.
 * @param whetherNullDependentColumn required for nullable properties.
 */
class PropertyColumnMappingConfig<P>(
    type: KType,
    val skip: Boolean = false,
    usedForQuery: Boolean = true,
    val columnPropertyName: String? = null, // TODO: use the property directly instead of the name string
    val whetherNullDependentColumn: Column<*>? = null, // for query
    /* TODO: whether it's null can depend on all columns:
        the property is null if when all columns are null (warn if some columns are not null),
        or a necessary column is null,
        in both cases of which warn if all nested properties are nullable */
    val adt: Adt<P & Any>? = null, // for query and update
) {
    init {
        // perform the checks

        if (type.isMarkedNullable) {
            if (skip && whetherNullDependentColumn !== null || adt !== null)
                logger.warn("${::whetherNullDependentColumn.name} and ${::adt.name} are unnecessary when ${::skip.name} is configured to true.")
        } else {
            // Non-nullable properties can be skipped when updating but not when querying.
            if (usedForQuery)
                require(!skip)
            require(whetherNullDependentColumn === null)
        }


        if (type.isExposedSqlPrimitiveType()) {
            if (whetherNullDependentColumn !== null)
                logger.warn("${::whetherNullDependentColumn} is set for a primitive type $type and will be ignored.")
            if (adt !== null)
                logger.warn("${::adt} is set for a primitive type $type and will be ignored.")
        }
        @Suppress("UNCHECKED_CAST")
        val clazz = type.classifier as KClass<P & Any>
        when (adt) {
            is Adt.Product -> require(clazz.isFinal || clazz.isOpen) { "the class $clazz must be instantiable (final or open) to be treated as a product type" }
            is Adt.Sum<*, *> -> require(clazz.isInheritable()) { "the class $clazz must be inheritable (open, abstract, or sealed) to be treated as a sum type" }
            null -> {}
        }
    }

    companion object {
        inline fun <reified PropertyData> create(
            skip: Boolean = false,
            usedForQuery: Boolean = true,
            columnPropertyName: String? = null, // TODO: use the column property
            nullDependentColumn: Column<*>? = null,
            adt: Adt<PropertyData & Any>? = null
        ) =
            PropertyColumnMappingConfig(
                typeOf<PropertyData>(), skip, usedForQuery, columnPropertyName, nullDependentColumn, adt
            )
    }

    // ADT: algebraic data type
    sealed class Adt<Data : Any> {
        class Product<Data : Any>(val nestedConfigMap: PropertyColumnMappingConfigMap<Data>) :
            Adt<Data>()

        class Sum<Data : Any, CaseValue>(
            clazz: KClass<Data>,
            val subclassProductConfigMapOverride: Map<KClass<out Data>, Product<out Data>>, // TODO: why can't a sum type nest another sum type?
            val sumTypeCaseConfig: SumTypeCaseConfig<Data, CaseValue>
        ) : Adt<Data>() {
            init {
                require(subclassProductConfigMapOverride.keys.all { !it.isInheritable() && it.isSubclassOf(clazz) })
            }

            companion object {
                inline fun <reified Data : Any, CaseValue> createForSealed(
                    subclassProductConfigMapOverride: Map<KClass<out Data>, Product<out Data>> = emptyMap(),
                    sumTypeCaseConfig: SumTypeCaseConfig<Data, CaseValue>
                ): Sum<Data, CaseValue> {
                    val clazz = Data::class
                    require(clazz.isSealed)
                    return Sum(clazz, subclassProductConfigMapOverride, sumTypeCaseConfig)
                }

                inline fun <reified Data : Any, CaseValue> createForAbstract(
                    subclassProductConfigMap: Map<KClass<out Data>, Product<out Data>>,
                    sumTypeCaseConfig: SumTypeCaseConfig<Data, CaseValue>
                ): Sum<Data, CaseValue> {
                    val clazz = Data::class
                    require(clazz.isAbstract)
                    return Sum(clazz, subclassProductConfigMap, sumTypeCaseConfig)
                }
            }
        }

        // not needed
        //class Enum<Data : kotlin.Enum<*>, CaseValue> : Adt<Data>()
    }
}

// TODO: constrain the property return type and the config type parameter to be the same
typealias PropertyColumnMappingConfigMap2<Data /*: Any*/, PropertyReturnType> = Map<KProperty1<Data, PropertyReturnType>, PropertyColumnMappingConfig<PropertyReturnType>>
typealias PropertyColumnMappingConfigMap<Data /*: Any*/> = PropertyColumnMappingConfigMap2<Data, *>

private fun KClass<*>.isObject() =
    objectInstance !== null

private fun <Data : Any> doGetDefaultClassPropertyColumnMappings(
    typeAndClass: TypeAndClass<Data>,
    tables: List<Table>, // for error messages only
    columnByPropertyNameMap: Map<String, Column<*>>, // TODO: refactor as `Data` may be a sum type
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap(),
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ClassPropertyColumnMappings<Data> {
    val customMappingPropertySet = customMappings.asSequence().map { it.property }.toSet()
    val dataCrtMemberPropertyMap = typeAndClass.concreteReturnTypeMemberProperties().asSequence()
        .filterNot { it.property in customMappingPropertySet }
        .associateBy { it.property.name }
    val customMappingMap = customMappings.associateBy { it.property.name }

    val clazz = typeAndClass.clazz
    return if (clazz.isObject()) // mainly for case objects of sealed classes
        emptyList() // TODO: use `null`
    else (clazz.primaryConstructor
        ?: throw IllegalArgumentException("$clazz must have a primary constructor with all the properties to be mapped to columns to be mapped as a product type"))
        .parameters.map {
            val name = it.name!!

            val customMapping = customMappingMap[name]
            if (customMapping !== null)
                return@map customMapping

            val crtProperty = dataCrtMemberPropertyMap.getOrElse(name) {
                throw IllegalArgumentException("primary constructor parameter `$it` is not a property in the class `$clazz`")
            }
            val property = crtProperty.property
            require(it.type == property.returnType) {
                "primary constructor parameter `$it` and property `$property` and different types"
            }

            // This function is added to introduce a new type parameter `PropertyData` to constrain the types better.
            fun <PropertyData> typeParameterHelper(crtProperty: ConcreteReturnTypeProperty1<Data, PropertyData>): PropertyColumnMapping<Data, PropertyData> {
                @Suppress("NAME_SHADOWING")
                val property = crtProperty.property
                val config = propertyColumnMappingConfigMapOverride[property]
                if (config?.skip == true)
                    return Skip(property)

                val columnPropertyName = config?.columnPropertyName ?: name
                val propertyReturnType = crtProperty.concreteReturnType

                val propertyReturnTypeTypeAndClass = TypeAndClass<PropertyData & Any>(propertyReturnType)
                val propertyReturnTypeClass = propertyReturnTypeTypeAndClass.clazz
                return if (propertyReturnTypeClass.isExposedSqlPrimitiveType())
                    @Suppress("UNCHECKED_CAST")
                    SqlPrimitive(
                        property, columnByPropertyNameMap.getOrElse(columnPropertyName) {
                            throw IllegalArgumentException("column with property name `$columnPropertyName` for class property `$property` does not exist in the tables `$tables`")
                        } as Column<PropertyData>
                    )
                else {
                    val isNullable = propertyReturnType.isMarkedNullable

                    @Suppress("UNCHECKED_CAST")
                    val nullability =
                        (
                                if (isNullable)
                                /*
                                I first had the idea of finding a default `nullDependentColumn` but it seems difficult to cover all kinds of cases.

                                There are 3 ways I can think of to find the default `nullDependentColumn` in the corresponding columns mapped by the properties:
                                1. find the first non-nullable column;
                                1. find the first column that's a primary key;
                                1. find the first non-nullable column with the suffix "id".

                                They all have their drawbacks.
                                The first approach is too unpredictable, adding or removing properties can affect which column to choose.
                                Both the second and the third approach can't deal with the case where the column is not within the mapped columns,
                                which happens when selecting a small portion of the fields as data.
                                 */
                                    NestedClass.Nullability.Nullable<PropertyData & Any>(
                                        config?.whetherNullDependentColumn
                                            ?: throw IllegalArgumentException("`PropertyColumnMappingConfig::nullDependentColumn` has to be specified for `$property` because its return type `$propertyReturnType` is a nullable nested data type")
                                    )
                                else
                                    NestedClass.Nullability.NonNullable<PropertyData & Any>()
                                )
                                as NestedClass.Nullability<PropertyData>


                    @Suppress("UNCHECKED_CAST")
                    val adtConfig = config?.adt as PropertyColumnMappingConfig.Adt<PropertyData & Any>?
                    val adt = if (propertyReturnTypeClass.isAbstractOrSealed()) {
                        //requireNotNull(adtConfig)
                        require(adtConfig is PropertyColumnMappingConfig.Adt.Sum<*, *>)
                        adtConfig as PropertyColumnMappingConfig.Adt.Sum<PropertyData & Any, *>
                        val subclassProductConfigMapOverride = adtConfig.subclassProductConfigMapOverride

                        val sealedLeafSubTypes =
                            propertyReturnTypeTypeAndClass.concreteTypeSealedLeafSubtypes() // TODO: also support direct sealed subtypes
                        val subclassProductNestedConfigMapMapOverride =
                            subclassProductConfigMapOverride.mapValues { it.value.nestedConfigMap }
                        val subclassProductNestedConfigMapMap =
                            if (propertyReturnTypeClass.isSealed)
                                sealedLeafSubTypes.asSequence().map { it.clazz }.associateWith {
                                    emptyMap<KProperty1<out PropertyData & Any, *>, PropertyColumnMappingConfig<*>>()
                                } + subclassProductNestedConfigMapMapOverride
                            else {
                                require(subclassProductConfigMapOverride.isNotEmpty()) { "A custom config needs to be specified for a non-sealed abstract class ${propertyReturnTypeTypeAndClass.type}" }
                                subclassProductNestedConfigMapMapOverride
                            }

                        NestedClass.Adt.Sum(
                            sealedLeafSubTypes.associate {
                                fun <SubclassData : PropertyData & Any> typeParameterHelper(
                                    typeAndClass: TypeAndClass<SubclassData>,
                                    configMap: PropertyColumnMappingConfigMap<SubclassData>
                                ): NestedClass.Adt.Product<SubclassData> =
                                    NestedClass.Adt.Product(
                                        doGetDefaultClassPropertyColumnMappings(
                                            typeAndClass,
                                            tables, columnByPropertyNameMap,
                                            configMap
                                        )
                                    )

                                @Suppress("NAME_SHADOWING")
                                val clazz = it.clazz
                                @Suppress("UNCHECKED_CAST")
                                clazz to typeParameterHelper(
                                    it as TypeAndClass<PropertyData & Any>,
                                    subclassProductNestedConfigMapMap[clazz] as PropertyColumnMappingConfigMap<PropertyData & Any>
                                )
                            },
                            adtConfig.sumTypeCaseConfig
                        )
                    } else {
                        require(adtConfig is PropertyColumnMappingConfig.Adt.Product?)
                        NestedClass.Adt.Product(
                            doGetDefaultClassPropertyColumnMappings(
                                propertyReturnTypeTypeAndClass,
                                tables, columnByPropertyNameMap,
                                (adtConfig?.nestedConfigMap ?: emptyMap())
                            )
                        )
                    }

                    NestedClass(property, nullability, adt)
                }
            }
            typeParameterHelper(crtProperty)
        }
}

fun <Data : Any> getDefaultClassPropertyColumnMappings(
    typeAndClass: TypeAndClass<Data>,
    tables: List<Table>, onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST,
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap(),
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ClassPropertyColumnMappings<Data> =
    doGetDefaultClassPropertyColumnMappings(
        typeAndClass,
        tables, getColumnByPropertyNameMap(tables, onDuplicateColumnPropertyNames),
        propertyColumnMappingConfigMapOverride,
        customMappings
    )

// TODO: decouple query mapper and update mapper.
/** Supports classes with nested composite class properties and multiple tables */
class ReflectionBasedClassPropertyDataMapper<Data : Any>(
    val clazz: KClass<Data>,
    val classPropertyColumnMappings: ClassPropertyColumnMappings<Data>,
) : DataMapper<Data> {
    override val neededColumns = classPropertyColumnMappings.getColumnSet().toList()
    override fun resultRowToData(resultRow: ResultRow): Data =
        constructDataWithResultRow(clazz, classPropertyColumnMappings, resultRow)

    override fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>) {
        setUpdateBuilder(classPropertyColumnMappings, data, updateBuilder)
    }
}


private fun <Data : Any> constructDataWithResultRow(
    clazz: KClass<Data>, classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, resultRow: ResultRow
): Data =
    clazz.primaryConstructor!!.call(*classPropertyColumnMappings.map {
        fun <PropertyData> typeParameterHelper(
            propertyColumnMapping: PropertyColumnMapping<Data, PropertyData>,
            nestedClass: KClass<PropertyData & Any>
        ) =
            when (propertyColumnMapping) {
                is SqlPrimitive -> resultRow.getValue(propertyColumnMapping.column)
                is NestedClass -> {
                    fun constructNotNullData() =
                        when (val adt = propertyColumnMapping.adt) {
                            is NestedClass.Adt.Product ->
                                constructDataWithResultRow(nestedClass, adt.nestedMappings, resultRow)

                            is NestedClass.Adt.Sum<PropertyData & Any, *> -> {
                                fun <CaseValue, SubclassData : PropertyData & Any> typeParameterHelper(sum: NestedClass.Adt.Sum<PropertyData & Any, CaseValue>): SubclassData {
                                    val subclass = with(sum.sumTypeCaseConfig) {
                                        caseValueToClass(resultRow[caseValueColumn])
                                    }
                                    @Suppress("UNCHECKED_CAST")
                                    return constructDataWithResultRow(
                                        subclass as KClass<SubclassData>,
                                        sum.subclassMap.getValue(subclass).nestedMappings as ClassPropertyColumnMappings<SubclassData>,
                                        resultRow
                                    )
                                }
                                typeParameterHelper(adt)
                            }
                        }

                    when (val nullability = propertyColumnMapping.nullability) {
                        is NestedClass.Nullability.NonNullable -> constructNotNullData()
                        is NestedClass.Nullability.Nullable<*> -> if (resultRow[nullability.whetherNullDependentColumn] !== null) constructNotNullData() else null
                    }
                }

                is Custom -> propertyColumnMapping.nullableDataMapper.resultRowToData(resultRow)
                is Skip -> null
            }
        @Suppress("UNCHECKED_CAST")
        typeParameterHelper(it as PropertyColumnMapping<Data, Any?>, it.property.returnType.classifier as KClass<Any>)
    }.toTypedArray())

fun <Data : Any> setUpdateBuilder(
    classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, data: Data, updateBuilder: UpdateBuilder<*>
) {
    for (propertyColumnMapping in classPropertyColumnMappings) {
        fun <PropertyData> typeParameterHelper(propertyColumnMapping: PropertyColumnMapping<Data, PropertyData>) {
            val propertyData = propertyColumnMapping.property(data)
            when (propertyColumnMapping) {
                is SqlPrimitive ->
                    updateBuilder[propertyColumnMapping.column] = propertyData

                is NestedClass -> {
                    // `propertyColumnMapping.nullability` is not needed here
                    when (val adt = propertyColumnMapping.adt) {
                        is NestedClass.Adt.Product -> {
                            val nestedMappings = adt.nestedMappings
                            if (propertyData !== null)
                                setUpdateBuilder(nestedMappings, propertyData, updateBuilder)
                            else
                                setUpdateBuilderColumnsToNullsWithMappings(nestedMappings, updateBuilder)
                        }

                        is NestedClass.Adt.Sum<PropertyData & Any, *> -> {
                            fun <CaseValue> typeParameterHelper() {
                                @Suppress("UNCHECKED_CAST")
                                adt as NestedClass.Adt.Sum<PropertyData & Any, CaseValue>
                                with(adt) {
                                    if (propertyData !== null) {
                                        // TODO: it seems to be a compiler bug that the non-null assertion is needed here. see: https://youtrack.jetbrains.com/issue/KT-37878/No-Smart-cast-for-class-literal-reference-of-nullable-generic-type.
                                        val propertyDataClass = propertyData!!::class
                                        with(sumTypeCaseConfig) {
                                            updateBuilder[caseValueColumn] = classToCaseValue(propertyDataClass)
                                        }
                                        fun <SubclassData : PropertyData & Any> typeParameterHelper(
                                            subclassMapping: NestedClass.Adt.Product<SubclassData>,
                                            propertyData: SubclassData
                                        ) =
                                            setUpdateBuilder(
                                                subclassMapping.nestedMappings, propertyData, updateBuilder
                                            )
                                        @Suppress("UNCHECKED_CAST")
                                        typeParameterHelper(
                                            subclassMap.getValue(propertyDataClass) as NestedClass.Adt.Product<PropertyData & Any>,
                                            propertyData
                                        )
                                    } else {
                                        with(sumTypeCaseConfig) {
                                            updateBuilder[caseValueColumn] = classToCaseValue(null)
                                        }
                                        setUpdateBuilderColumnsToNulls(columnsForAllSubclasses, updateBuilder)
                                    }
                                }
                            }
                            typeParameterHelper<Any?>()
                        }
                    }
                }

                is Custom ->
                    propertyColumnMapping.nullableDataMapper.setUpdateBuilder(propertyData, updateBuilder)

                is Skip -> {}
            }
        }

        typeParameterHelper(propertyColumnMapping)
    }
}

fun PropertyColumnMapping<*, *>.forEachColumn(block: (Column<*>) -> Unit) =
    when (this) {
        is SqlPrimitive -> block(column)
        is NestedClass -> {
            when (nullability) {
                is NestedClass.Nullability.NonNullable -> {}
                is NestedClass.Nullability.Nullable -> block(nullability.whetherNullDependentColumn)
            }
            when (adt) {
                is NestedClass.Adt.Product -> adt.nestedMappings.forEachColumn(block)
                is NestedClass.Adt.Sum<*, *> -> {
                    block(adt.sumTypeCaseConfig.caseValueColumn)
                    adt.subclassMap.values.forEach { it.nestedMappings.forEachColumn(block) }
                }
            }
        }

        is Custom -> nullableDataMapper.neededColumns.forEach(block)
        is Skip -> {}
    }

fun ClassPropertyColumnMappings<*>.forEachColumn(block: (Column<*>) -> Unit) {
    for (propertyColumnMapping in this)
        propertyColumnMapping.forEachColumn(block)
}

fun setUpdateBuilderColumnsToNullsWithMappings(
    classPropertyColumnMappings: ClassPropertyColumnMappings<*>, updateBuilder: UpdateBuilder<*>
) =
    classPropertyColumnMappings.forEachColumn {
        @Suppress("UNCHECKED_CAST")
        updateBuilder[it as Column<Any?>] = null
    }

fun setUpdateBuilderColumnsToNulls(columns: List<Column<*>>, updateBuilder: UpdateBuilder<*>) {
    for (column in columns)
        @Suppress("UNCHECKED_CAST")
        updateBuilder[column as Column<Any?>] = null
}

fun ClassPropertyColumnMappings<*>.getColumnSet(): Set<Column<*>> =
    buildSet { forEachColumn { add(it) } }

inline fun <reified Data : Any> reflectionBasedClassPropertyDataMapper(
    tables: List<Table>,
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST,
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap(),
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ReflectionBasedClassPropertyDataMapper<Data> {
    val typeAndClass = typeAndClassOf<Data>()
    return ReflectionBasedClassPropertyDataMapper(
        typeAndClass.clazz, getDefaultClassPropertyColumnMappings(
            typeAndClass,
            tables, onDuplicateColumnPropertyNames, propertyColumnMappingConfigMapOverride, customMappings
        )
    )
}

inline fun <reified Data : Any/*, TableT : Table*/> reflectionBasedClassPropertyDataMapper(
    table: Table,
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap(),
    customMappings: PropertyColumnMappings<Data> = emptyList()
) =
    reflectionBasedClassPropertyDataMapper(listOf(table), THROW, propertyColumnMappingConfigMapOverride, customMappings)
