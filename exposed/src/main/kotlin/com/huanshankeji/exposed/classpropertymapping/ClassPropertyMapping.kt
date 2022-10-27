package com.huanshankeji.exposed.classpropertymapping

import com.huanshankeji.exposed.classpropertymapping.OnDuplicateColumnPropertyNames.*
import com.huanshankeji.exposed.classpropertymapping.PropertyColumnMapping.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.slf4j.LoggerFactory
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.sequences.Sequence

// Our own class mapping implementation using reflection which should be adapted using annotation processors and code generation in the future.


typealias PropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, *>>
//typealias LessStrictlyTypedPropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, Any?>>
/** In the order of the constructor arguments. */
typealias ClassPropertyColumnMappings<Data> = PropertyColumnMappings<Data>

// TODO: decouple/remove `property` and `Data` from this class.
sealed class PropertyColumnMapping<Data : Any, PropertyData>(val property: KProperty1<Data, PropertyData>) {
    // TODO: rename all occurrences of "PrimitiveValue" to "PropertyData".
    class ExposedSqlPrimitive<Data : Any, PrimitiveValue>(
        property: KProperty1<Data, PrimitiveValue>,
        val column: Column<PrimitiveValue>
    ) : PropertyColumnMapping<Data, PrimitiveValue>(property)

    // TODO: rename all occurrences of "NestedData" to "PropertyData".
    class NestedClass<Data : Any, NestedData>(
        property: KProperty1<Data, NestedData>,
        val nullability: Nullability<NestedData>,
        val adt: Adt<NestedData & Any>
    ) : PropertyColumnMapping<Data, NestedData>(property) {
        sealed class Nullability<NestedData> {
            class NonNullable<NotNullNestedData : Any> : Nullability<NotNullNestedData>()
            class Nullable<NotNullNestedData : Any>(val nullDependentColumn: Column<*>) :
                Nullability<NotNullNestedData?>()
        }

        // ADT: algebraic data type
        sealed class Adt<NotNullNestedData : Any> {
            class Product<NotNullNestedData : Any>(val nestedMappings: ClassPropertyColumnMappings<NotNullNestedData>) :
                Adt<NotNullNestedData>()

            class Sum<NotNullNestedData : Any, CaseValue>(
                // I first used the Guava BiMap but then realized it's not necessary. TODO: remove this comment
                val subclassMap: Map<KClass<out NotNullNestedData>, Product<out NotNullNestedData>>,
                val sumTypeCaseConfig: SumTypeCaseConfig<NotNullNestedData, CaseValue>
            ) : Adt<NotNullNestedData>()
        }

    }

    // TODO: remove the temporary workaround `& Any` if and when the `Any` upper bound is removed.
    class Custom<Data : Any, PropertyData>(
        property: KProperty1<Data, PropertyData>, val classPropertyMapper: ClassPropertyMapper<PropertyData & Any>
    ) : PropertyColumnMapping<Data, PropertyData>(property)

    class Skip<Data : Any, PropertyData>(property: KProperty1<Data, PropertyData>) :
        PropertyColumnMapping<Data, PropertyData>(property)
}

class SumTypeCaseConfig<Superclass : Any, Case>(
    val caseValueColumn: Column<Case>,
    val caseValueToClass: (Case) -> KClass<out Superclass>,
    val classToCaseValue: (KClass<out Superclass>?) -> Case
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

fun KClass<*>.isExposedSqlPrimitiveType(): Boolean =
    this in defaultNotNullExposedSqlPrimitiveClasses || isSubclassOf(Enum::class)

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

val logger = LoggerFactory.getLogger("class property mapping")

/**
 * @param skip both writing and reading. Note that the property type need not be nullable if it's only used for writing.
 * @param nullDependentColumn required for nullable properties.
 */
class PropertyColumnMappingConfig<P>(
    type: KType,
    usedForQuery: Boolean = true,
    val skip: Boolean = false,
    val differentColumnPropertyName: String? = null,
    val nullDependentColumn: Column<*>? = null, // for query
    val adt: Adt<P & Any>? = null, // for query and update
) {
    init {
        // perform the checks

        if (type.isMarkedNullable) {
            if (skip && nullDependentColumn !== null || adt !== null)
                logger.warn("${::nullDependentColumn.name} and ${::adt.name} are unnecessary when ${::skip.name} is configured to true.")
        } else {
            // Non-nullable properties can be skipped when updating but not when querying.
            if (usedForQuery)
                require(!skip)
            require(nullDependentColumn === null)
        }


        if (type.isExposedSqlPrimitiveType() && nullDependentColumn === null && adt === null)
            logger.warn("${::nullDependentColumn} or ${::adt} is set for a primitive type $type and will be ignored.")

        @Suppress("UNCHECKED_CAST")
        val clazz = type.classifier as KClass<P & Any>
        when (adt) {
            is Adt.Product -> require(!clazz.isAbstract)
            is Adt.Sum<*, *> -> require(clazz.isOpen)
            null -> {}
        }
    }

    companion object {
        inline fun <reified PropertyData> create(
            skip: Boolean = false,
            usedForQuery: Boolean = true,
            columnPropertyName: String? = null,
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
            val subclassProductConfigMapOrOverride: Map<KClass<out Data>, Product<out Data>>,
            val sumTypeCaseConfig: SumTypeCaseConfig<Data, CaseValue>
        ) : Adt<Data>() {
            init {
                require(subclassProductConfigMapOrOverride.keys.all { !it.isAbstract && it.isSubclassOf(clazz) })
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

                inline fun <reified Data : Any, CaseValue> createForAbstractNotSealed(
                    subclassProductConfigMap: Map<KClass<out Data>, Product<out Data>>,
                    sumTypeCaseConfig: SumTypeCaseConfig<Data, CaseValue>
                ): Sum<Data, CaseValue> {
                    val clazz = Data::class
                    require(clazz.isAbstract && !clazz.isSealed)
                    return Sum(clazz, subclassProductConfigMap, sumTypeCaseConfig)
                }
            }
        }
    }
}

typealias PropertyColumnMappingConfigMap<Data /*: Any*/> = Map<KProperty1<Data, *>, PropertyColumnMappingConfig<*>>

fun <Data : Any> getDefaultClassPropertyColumnMappings(
    clazz: KClass<Data>,
    columnByPropertyNameMap: Map<String, Column<*>>,
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap(),
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ClassPropertyColumnMappings<Data> {
    val customMappingProperties = customMappings.asSequence().map { it.property }.toSet()
    val dataMemberPropertyMap =
        (clazz.memberProperties.toSet() - customMappingProperties).associateBy { it.name }
    val customMappingMap = customMappings.associateBy { it.property.name }

    return clazz.primaryConstructor!!.parameters.map {
        val name = it.name!!

        val customMapping = customMappingMap[name]
        if (customMapping !== null)
            return@map customMapping

        val property = dataMemberPropertyMap.getValue(name)
        require(it.type == property.returnType)

        // This function is added to introduce a new type parameter `PropertyData` to constrain the types better.
        fun <PropertyData> typeParameterHelper(property: KProperty1<Data, PropertyData>): PropertyColumnMapping<Data, PropertyData> {
            val config = propertyColumnMappingConfigMapOverride[property]
            if (config?.skip == true)
                return Skip(property)

            val columnPropertyName = config?.differentColumnPropertyName ?: name
            val type = property.returnType

            @Suppress("UNCHECKED_CAST")
            val propertyClazz = type.classifier as KClass<*> as KClass<PropertyData & Any>
            return if (propertyClazz.isExposedSqlPrimitiveType())
                @Suppress("UNCHECKED_CAST")
                ExposedSqlPrimitive(
                    property, columnByPropertyNameMap.getValue(columnPropertyName) as Column<PropertyData>
                )
            else {
                val isNullable = type.isMarkedNullable

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
                                NestedClass.Nullability.Nullable<PropertyData & Any>(config?.nullDependentColumn!!)
                            else
                                NestedClass.Nullability.NonNullable<PropertyData & Any>()
                            )
                            as NestedClass.Nullability<PropertyData>


                @Suppress("UNCHECKED_CAST")
                val adtConfig = config?.adt as PropertyColumnMappingConfig.Adt<PropertyData & Any>?
                val adt = if (propertyClazz.isAbstract) {
                    //requireNotNull(adtConfig)
                    require(adtConfig is PropertyColumnMappingConfig.Adt.Sum<*, *>)
                    adtConfig as PropertyColumnMappingConfig.Adt.Sum<PropertyData & Any, *>
                    val subclassProductConfigMapOrOverride = adtConfig.subclassProductConfigMapOrOverride


                    val subclassProductNestedConfigMapMapOrOverride =
                        subclassProductConfigMapOrOverride.mapValues { it.value.nestedConfigMap }
                    val subclassProductNestedConfigMapMap =
                        if (propertyClazz.isSealed)
                            propertyClazz.sealedLeafSubclasses().associateWith {
                                emptyMap<KProperty1<out PropertyData & Any, *>, PropertyColumnMappingConfig<*>>()
                            } + subclassProductNestedConfigMapMapOrOverride
                        else {
                            require(subclassProductConfigMapOrOverride.isNotEmpty()) { "A custom config needs to be specified for a non-sealed abstract class" }
                            subclassProductNestedConfigMapMapOrOverride
                        }

                    NestedClass.Adt.Sum(
                        subclassProductNestedConfigMapMap.mapValues {
                            fun <SubclassData : PropertyData & Any> typeParameterHelper(
                                clazz: KClass<SubclassData>, configMap: PropertyColumnMappingConfigMap<SubclassData>
                            ): NestedClass.Adt.Product<SubclassData> =
                                NestedClass.Adt.Product(
                                    getDefaultClassPropertyColumnMappings(clazz, columnByPropertyNameMap, configMap)
                                )
                            @Suppress("UNCHECKED_CAST")
                            typeParameterHelper(
                                it.key as KClass<PropertyData & Any>,
                                it.value as PropertyColumnMappingConfigMap<PropertyData & Any>
                            )
                        },
                        adtConfig.sumTypeCaseConfig
                    )
                } else {
                    require(adtConfig is PropertyColumnMappingConfig.Adt.Product?)
                    NestedClass.Adt.Product(
                        getDefaultClassPropertyColumnMappings(
                            propertyClazz,
                            columnByPropertyNameMap,
                            (adtConfig?.nestedConfigMap ?: emptyMap())
                        )
                    )
                }

                NestedClass(property, nullability, adt)
            }
        }
        typeParameterHelper(property)
    }
}

// This implementation currently has poor performance (O(depth * size) time complexity).
fun <T : Any> KClass<T>.sealedLeafSubclasses(): List<KClass<out T>> =
    if (isSealed) sealedSubclasses.flatMap { it.sealedLeafSubclasses() }
    else listOf(this)

fun <Data : Any> getDefaultClassPropertyColumnMappings(
    clazz: KClass<Data>,
    tables: List<Table>, onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST,
    customMappings: PropertyColumnMappings<Data> = emptyList(),
    propertyColumnMappingConfigMapOverride: PropertyColumnMappingConfigMap<Data> = emptyMap()
): ClassPropertyColumnMappings<Data> =
    getDefaultClassPropertyColumnMappings(
        clazz,
        getColumnByPropertyNameMap(tables, onDuplicateColumnPropertyNames),
        propertyColumnMappingConfigMapOverride,
        customMappings
    )

interface ClassPropertyQueryMapper<Data : Any> : SimpleClassPropertyQueryMapper<Data> {
    val neededColumns: List<Column<*>>
}

typealias ClassPropertyUpdateMapper<Data> = ClassPropertyQueryMapper<Data>

interface ClassPropertyMapper<Data : Any> : ClassPropertyQueryMapper<Data>, SimpleClassPropertyMapper<Data, ColumnSet>

// TODO: decouple query mapper and update mapper.
/** Supports classes with nested composite class properties and multiple tables */
class ReflectionBasedClassPropertyMapper<Data : Any>(
    val clazz: KClass<Data>,
    val classPropertyColumnMappings: ClassPropertyColumnMappings<Data>,
) : ClassPropertyMapper<Data> {
    override val neededColumns = classPropertyColumnMappings.getNeededColumns()
    override fun resultRowToData(resultRow: ResultRow): Data =
        constructDataWithResultRow(clazz, classPropertyColumnMappings, resultRow)

    override fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>) {
        setUpdateBuilder(classPropertyColumnMappings, data, updateBuilder)
    }
}

fun <Data : Any> constructDataWithResultRow(
    clazz: KClass<Data>, classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, resultRow: ResultRow
): Data =
    clazz.primaryConstructor!!.call(*classPropertyColumnMappings.map {
        fun <PropertyData> typeParameterHelper(
            propertyColumnMapping: PropertyColumnMapping<Data, PropertyData>,
            nestedClass: KClass<PropertyData & Any>
        ) =
            when (propertyColumnMapping) {
                is ExposedSqlPrimitive -> resultRow.getValue(propertyColumnMapping.column)
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
                        is NestedClass.Nullability.Nullable<*> -> if (resultRow[nullability.nullDependentColumn] !== null) constructNotNullData() else null
                    }
                }

                is Custom -> propertyColumnMapping.classPropertyMapper.resultRowToData(resultRow)
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
                is ExposedSqlPrimitive ->
                    updateBuilder[propertyColumnMapping.column] = propertyData

                is NestedClass -> {
                    // `propertyColumnMapping.nullability` is not needed here
                    when (val adt = propertyColumnMapping.adt) {
                        is NestedClass.Adt.Product -> {
                            val nestedMappings = adt.nestedMappings
                            if (propertyData !== null)
                                setUpdateBuilder(nestedMappings, propertyData, updateBuilder)
                            else
                                setUpdateBuilderToNulls(nestedMappings, updateBuilder)
                        }

                        is NestedClass.Adt.Sum<PropertyData & Any, *> -> {
                            fun <CaseValue> typeParameterHelper() {
                                @Suppress("UNCHECKED_CAST")
                                adt as NestedClass.Adt.Sum<PropertyData & Any, CaseValue>
                                val sumTypeCaseConfig = adt.sumTypeCaseConfig
                                val subclassMap = adt.subclassMap
                                if (propertyData !== null) {
                                    // TODO: it seems to be a compiler bug that the non-null assertion is needed here.
                                    val propertyDataClass = propertyData!!::class
                                    with(sumTypeCaseConfig) {
                                        updateBuilder[caseValueColumn] = classToCaseValue(propertyDataClass)
                                    }
                                    fun <SubclassData : PropertyData & Any> typeParameterHelper(
                                        subclassMapping: NestedClass.Adt.Product<SubclassData>,
                                        propertyData: SubclassData
                                    ) =
                                        setUpdateBuilder(subclassMapping.nestedMappings, propertyData, updateBuilder)
                                    @Suppress("UNCHECKED_CAST")
                                    typeParameterHelper(
                                        subclassMap.getValue(propertyDataClass) as NestedClass.Adt.Product<PropertyData & Any>,
                                        propertyData
                                    )
                                } else {
                                    with(sumTypeCaseConfig) {
                                        updateBuilder[caseValueColumn] = classToCaseValue(null)
                                    }
                                    // TODO: pre-process the combined columns and eliminate duplicates.
                                    for (product in subclassMap.values)
                                        setUpdateBuilderToNulls(product.nestedMappings, updateBuilder)
                                }
                            }
                            typeParameterHelper<Any?>()
                        }
                    }
                }

                is Custom ->
                    // TODO: remove this cast if and when not only non-nullable data are supported.
                    propertyColumnMapping.classPropertyMapper.setUpdateBuilder(
                        propertyData as (PropertyData & Any), updateBuilder
                    )

                is Skip -> {}
            }
        }

        typeParameterHelper(propertyColumnMapping)
    }
}

fun ClassPropertyColumnMappings<*>.forEachColumn(block: (Column<*>) -> Unit) {
    for (propertyColumnMapping in this)
        when (propertyColumnMapping) {
            is ExposedSqlPrimitive -> block(propertyColumnMapping.column)
            is NestedClass -> when (val adt = propertyColumnMapping.adt) {
                is NestedClass.Adt.Product -> adt.nestedMappings.forEachColumn(block)
                is NestedClass.Adt.Sum<*, *> -> {
                    block(adt.sumTypeCaseConfig.caseValueColumn)
                    adt.subclassMap.values.forEach { it.nestedMappings.forEachColumn(block) }
                }
            }

            is Custom -> propertyColumnMapping.classPropertyMapper.neededColumns.forEach(block)
            is Skip -> {}
        }
}

fun setUpdateBuilderToNulls(
    classPropertyColumnMappings: ClassPropertyColumnMappings<*>, updateBuilder: UpdateBuilder<*>
) =
    classPropertyColumnMappings.forEachColumn {
        @Suppress("UNCHECKED_CAST")
        updateBuilder[it as Column<Any?>] = null
    }

fun ClassPropertyColumnMappings<*>.getNeededColumns(): List<Column<*>> =
    buildList { forEachColumn { add(it) } }

inline fun <reified Data : Any> reflectionBasedClassPropertyMapper(
    tables: List<Table>,
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST,
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ReflectionBasedClassPropertyMapper<Data> {
    val clazz = Data::class
    return ReflectionBasedClassPropertyMapper(
        clazz, getDefaultClassPropertyColumnMappings(clazz, tables, onDuplicateColumnPropertyNames, customMappings)
    )
}

inline fun <reified Data : Any/*, TableT : Table*/> reflectionBasedClassPropertyMapper(
    table: Table,
    customMappings: PropertyColumnMappings<Data> = emptyList()
) =
    reflectionBasedClassPropertyMapper(listOf(table), THROW, customMappings)
