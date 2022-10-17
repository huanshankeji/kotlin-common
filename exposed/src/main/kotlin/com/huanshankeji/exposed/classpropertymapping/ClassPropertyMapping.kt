package com.huanshankeji.exposed.classpropertymapping

import com.huanshankeji.exposed.classpropertymapping.OnDuplicateColumnPropertyNames.*
import com.huanshankeji.exposed.classpropertymapping.PropertyColumnMapping.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

// Our own class mapping implementation using reflection which should be adapted using annotation processors and code generation in the future.


typealias PropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, *>>
//typealias LessStrictlyTypedPropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, Any?>>
/** In the order of the constructor arguments. */
typealias ClassPropertyColumnMappings<Data> = PropertyColumnMappings<Data>

sealed class PropertyColumnMapping<Data : Any, PropertyValue>(val property: KProperty1<Data, PropertyValue>) {
    class ExposedSqlPrimitive<Data : Any, PrimitiveValue>(
        property: KProperty1<Data, PrimitiveValue>,
        val column: Column<PrimitiveValue>
    ) : PropertyColumnMapping<Data, PrimitiveValue>(property)

    class NestedClass<Data : Any, NestedData>(
        property: KProperty1<Data, NestedData>,
        //val nullabilityDependentColumn: Column<*>,
        val nestedMappings: ClassPropertyColumnMappings<NestedData & Any>
    ) : PropertyColumnMapping<Data, NestedData>(property)

    class Skip<Data : Any, PropertyValue>(property: KProperty1<Data, PropertyValue>) :
        PropertyColumnMapping<Data, PropertyValue>(property)
}


// see: https://kotlinlang.org/docs/basic-types.html, https://www.postgresql.org/docs/current/datatype.html
// Types that are commented out are not ensured to work yet.
val defaultNotNullExposedSqlPrimitiveTypes = listOf(
    typeOf<Byte>(), typeOf<Short>(), typeOf<Int>(), typeOf<Long>(), /*typeOf<BigInteger>(),*/
    typeOf<UByte>(), typeOf<UShort>(), typeOf<UInt>(), typeOf<ULong>(),
    typeOf<Float>(), typeOf<Double>(), /*typeOf<BigDecimal>(),*/
    typeOf<Boolean>(),
    typeOf<ByteArray>(),
    //typeOf<Char>(),
    typeOf<String>(),
    // types related to time and date
)

val enumType = typeOf<Enum<*>>()
fun KType.notNullTypeIsExposedSqlPrimitiveType(): Boolean =
    this in defaultNotNullExposedSqlPrimitiveTypes || isSubtypeOf(enumType)

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


fun <Data : Any> getDefaultClassColumnMappings(
    clazz: KClass<Data>,
    columnByPropertyNameMap: Map<String, Column<*>>,
    customMappings: PropertyColumnMappings<Data> = emptyList()
): ClassPropertyColumnMappings<Data> {
    val customMappingProperties = customMappings.asSequence().map { it.property }.toSet()
    val dataMemberPropertyMap =
        (clazz.memberProperties.toSet() - customMappingProperties).associateBy { it.name }

    return clazz.primaryConstructor!!.parameters.map {
        val name = it.name!!
        val property = dataMemberPropertyMap[name]
        val notNullType = it.type.withNullability(false)
        @Suppress("UNCHECKED_CAST")
        if (notNullType.notNullTypeIsExposedSqlPrimitiveType())
            ExposedSqlPrimitive(
                property as KProperty1<Data, Any?>,
                columnByPropertyNameMap.getValue(name) as Column<Any?>
            )
        else
            NestedClass(
                property as KProperty1<Data, Any?>,
                getDefaultClassColumnMappings(
                    notNullType.classifier as KClass<*>, columnByPropertyNameMap
                ) as ClassPropertyColumnMappings<Any>
            )
    }
}

fun <Data : Any> getDefaultClassColumnMappings(
    clazz: KClass<Data>,
    tables: List<Table>,
    customMappings: PropertyColumnMappings<Data> = emptyList(),
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST
): ClassPropertyColumnMappings<Data> =
    getDefaultClassColumnMappings(
        clazz, getColumnByPropertyNameMap(tables, onDuplicateColumnPropertyNames), customMappings
    )

interface ClassPropertyMapper<Data : Any> : SimpleClassPropertyMapper<Data, Table> {
    val neededColumns: List<Column<*>>
}

/** Supports classes with nested composite class properties and multiple tables */
class ReflectionBasedClassPropertyMapper<Data : Any>(
    val clazz: KClass<Data>,
    val classPropertyColumnMappings: ClassPropertyColumnMappings<Data>,
) : ClassPropertyMapper<Data> {
    override val neededColumns = classPropertyColumnMappings.getNeededColumns()
    override fun resultRowToData(resultRow: ResultRow): Data =
        constructDataWithResultRow(clazz, classPropertyColumnMappings, resultRow)

    override fun updateBuilderSetter(data: Data): Table.(UpdateBuilder<*>) -> Unit = {
        setUpdateBuilder(classPropertyColumnMappings, data, it)
    }
}

fun <Data : Any> constructDataWithResultRow(
    clazz: KClass<Data>, classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, resultRow: ResultRow
): Data =
    clazz.primaryConstructor!!.call(*classPropertyColumnMappings.map {
        when (it) {
            is ExposedSqlPrimitive -> resultRow.getValue(it.column)
            is NestedClass ->
                // TODO: the nullable case is not implemented yet
                @Suppress("UNCHECKED_CAST")
                constructDataWithResultRow(
                    it.property.returnType.classifier as KClass<Any>,
                    it.nestedMappings as ClassPropertyColumnMappings<Any>,
                    resultRow
                )

            is Skip -> null
        }
    }.toTypedArray())

fun <Data : Any> setUpdateBuilder(
    classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, data: Data, updateBuilder: UpdateBuilder<*>
) {
    for (propertyColumnMapping in classPropertyColumnMappings)
        when (propertyColumnMapping) {
            is ExposedSqlPrimitive ->
                @Suppress("UNCHECKED_CAST")
                updateBuilder[propertyColumnMapping.column as Column<Any?>] = propertyColumnMapping.property(data)

            is NestedClass -> {
                @Suppress("NAME_SHADOWING", "UNCHECKED_CAST")
                val propertyColumnMapping = propertyColumnMapping as NestedClass<Data, Any?>
                val nestedMappings = propertyColumnMapping.nestedMappings
                propertyColumnMapping.property(data)?.let {
                    setUpdateBuilder(nestedMappings, it, updateBuilder)
                }
                    ?: setUpdateBuilderToNulls(nestedMappings, updateBuilder)
            }

            is Skip -> {}
        }
}

fun ClassPropertyColumnMappings<*>.forEachColumn(block: (Column<*>) -> Unit) {
    for (propertyColumnMapping in this)
        when (propertyColumnMapping) {
            is ExposedSqlPrimitive -> block(propertyColumnMapping.column)
            is NestedClass -> propertyColumnMapping.nestedMappings.forEachColumn(block)
            is Skip -> {}
        }
}

fun <Data : Any> setUpdateBuilderToNulls(
    classPropertyColumnMappings: ClassPropertyColumnMappings<Data>, updateBuilder: UpdateBuilder<*>
) =
    classPropertyColumnMappings.forEachColumn {
        @Suppress("UNCHECKED_CAST")
        updateBuilder[it as Column<Any?>] = null
    }

fun ClassPropertyColumnMappings<*>.getNeededColumns(): List<Column<*>> =
    buildList { forEachColumn { add(it) } }

inline fun <reified Data : Any> reflectionBasedClassPropertyMapper(
    tables: List<Table>,
    customMappings: PropertyColumnMappings<Data> = emptyList(),
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST
): ReflectionBasedClassPropertyMapper<Data> {
    val clazz = Data::class
    return ReflectionBasedClassPropertyMapper(
        clazz, getDefaultClassColumnMappings(clazz, tables, customMappings, onDuplicateColumnPropertyNames)
    )
}
