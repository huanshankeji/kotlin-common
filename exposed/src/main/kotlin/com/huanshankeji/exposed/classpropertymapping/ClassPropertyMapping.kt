package com.huanshankeji.exposed.classpropertymapping

import com.huanshankeji.exposed.classpropertymapping.OnDuplicateColumnPropertyNames.*
import com.huanshankeji.exposed.classpropertymapping.PropertyColumnMapping.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
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


interface SimpleClassPropertyMapper<Data : Any, TableT : Table> {
    fun resultRowToData(resultRow: ResultRow): Data
    fun updateBuilderSetter(data: Data): TableT.(UpdateBuilder<*>) -> Unit
}

fun ResultRow.getValue(column: Column<*>): Any? =
    this[column].let {
        if (it is EntityID<*>) it.value else it
    }

/** Nested classes are not supported. */
interface ReflectionBasedSimpleClassPropertyMapper<Data : Any, TableT : Table> :
    SimpleClassPropertyMapper<Data, TableT> {
    val propertyAndColumnPairs: List<Pair<KProperty1<Data, *>, Column<*>>>
    val dataPrimaryConstructor: KFunction<Data>

    override fun resultRowToData(resultRow: ResultRow): Data {
        val params = propertyAndColumnPairs.map { (_, column) -> resultRow.getValue(column) }
        return dataPrimaryConstructor.call(*params.toTypedArray())
    }

    override fun updateBuilderSetter(data: Data): TableT.(UpdateBuilder<*>) -> Unit = {
        for ((property, column) in propertyAndColumnPairs)
            @Suppress("UNCHECKED_CAST")
            it[column as Column<Any?>] = property(data)
    }
}

inline fun <reified Data : Any, reified TableT : Table> reflectionBasedSimpleClassPropertyMapper(table: TableT): ReflectionBasedSimpleClassPropertyMapper<Data, TableT> =
    object : ReflectionBasedSimpleClassPropertyMapper<Data, TableT> {
        private val clazz = Data::class
        override val propertyAndColumnPairs = run {
            //require(dClass.isData)
            val dataMemberPropertyMap = clazz.memberProperties.associateBy { it.name }
            val columnMap = getColumnByPropertyNameMapWithTypeParameter(table)
            dataPrimaryConstructor.parameters.map {
                val name = it.name!!
                dataMemberPropertyMap.getValue(name) to columnMap.getValue(name)
            }
        }
        override val dataPrimaryConstructor = clazz.primaryConstructor!!
    }

@Suppress("UNCHECKED_CAST")
fun <TableT : Table> getColumnProperties(clazz: KClass<TableT>): Sequence<KProperty1<TableT, Column<*>>> =
    clazz.memberProperties.asSequence()
        .filter { it.returnType.classifier == Column::class }
            as Sequence<KProperty1<TableT, Column<*>>>

fun <TableT : Table> getColumnPropertyByNameMap(clazz: KClass<TableT>): Map<String, KProperty1<TableT, Column<*>>> =
    getColumnProperties(clazz).associateBy { it.name }

inline fun <reified TableT : Table> getColumnByPropertyNameMapWithTypeParameter(table: TableT): Map<String, Column<*>> =
    getColumnPropertyByNameMap(TableT::class)
        .mapValues { it.value(table) }

inline fun <reified D : Any, reified T : Table> reflectionBasedSimpleClassPropertyMapperForAlias(
    tableClassPropertyMapper: ReflectionBasedSimpleClassPropertyMapper<D, T>, alias: Alias<T>
): ReflectionBasedSimpleClassPropertyMapper<D, T> =
    object : ReflectionBasedSimpleClassPropertyMapper<D, T> {
        override val dataPrimaryConstructor = tableClassPropertyMapper.dataPrimaryConstructor
        override val propertyAndColumnPairs =
            tableClassPropertyMapper.propertyAndColumnPairs.map { it.first to alias[it.second] }
    }


inline fun <D1 : Any, D2 : Any> innerJoinResultRowToData(
    crossinline resultRowToData1: (ResultRow) -> D1, crossinline resultRowToData2: (ResultRow) -> D2
): (ResultRow) -> Pair<D1, D2> = {
    resultRowToData1(it) to resultRowToData2(it)
}

inline fun <D1 : Any, D2 : Any> leftJoinResultRowToData(
    crossinline resultRowToData1: (ResultRow) -> D1, crossinline resultRowToData2: (ResultRow) -> D2,
    onColumn: Column<*>
): (ResultRow) -> Pair<D1, D2?> = {
    // `it.hasValue` returns true here but the value is `null`
    resultRowToData1(it) to if (it[onColumn] !== null) resultRowToData2(it) else null
}

inline fun <D1 : Any, D2 : Any, D3 : Any> leftJoinResultRowToData(
    crossinline resultRowToData1: (ResultRow) -> D1,
    crossinline resultRowToData2: (ResultRow) -> D2,
    crossinline resultRowToData3: (ResultRow) -> D3,
    onColumn2: Column<*>,
    onColumn3: Column<*>
): (ResultRow) -> Triple<D1, D2?, D3?> = {
    // `it.hasValue` returns `true` here but the value is `null`
    Triple(
        resultRowToData1(it),
        if (it[onColumn2] !== null) resultRowToData2(it) else null,
        if (it[onColumn3] !== null) resultRowToData3(it) else null
    )
}


typealias PropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, *>>
//typealias LessStrictlyTypedPropertyColumnMappings<Data> = List<PropertyColumnMapping<Data, Any?>>
/** In the order of the constructor arguments. */
typealias ClassColumnMappings<Data> = PropertyColumnMappings<Data>

sealed class PropertyColumnMapping<Data : Any, PropertyValue>(val property: KProperty1<Data, PropertyValue>) {
    class ExposedSqlPrimitive<Data : Any, PrimitiveValue>(
        property: KProperty1<Data, PrimitiveValue>,
        val column: Column<PrimitiveValue>
    ) : PropertyColumnMapping<Data, PrimitiveValue>(property)

    class NestedClass<Data : Any, NestedData>(
        property: KProperty1<Data, NestedData>,
        //val nullabilityDependentColumn: Column<*>,
        val nestedMappings: ClassColumnMappings<NestedData & Any>
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
): ClassColumnMappings<Data> {
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
                ) as ClassColumnMappings<Any>
            )
    }
}

fun <Data : Any> getDefaultClassColumnMappings(
    clazz: KClass<Data>,
    tables: List<Table>,
    customMappings: PropertyColumnMappings<Data> = emptyList(),
    onDuplicateColumnPropertyNames: OnDuplicateColumnPropertyNames = CHOOSE_FIRST
): ClassColumnMappings<Data> =
    getDefaultClassColumnMappings(
        clazz, getColumnByPropertyNameMap(tables, onDuplicateColumnPropertyNames), customMappings
    )

interface ClassPropertyMapper<Data : Any> : SimpleClassPropertyMapper<Data, Table> {
    val neededColumns: List<Column<*>>
}

/** Supports classes with nested composite class properties and multiple tables */
class ReflectionBasedClassPropertyMapper<Data : Any>(
    val clazz: KClass<Data>,
    val classColumnMappings: ClassColumnMappings<Data>,
) : ClassPropertyMapper<Data> {
    override val neededColumns = classColumnMappings.getNeededColumns()
    override fun resultRowToData(resultRow: ResultRow): Data =
        constructDataWithResultRow(clazz, classColumnMappings, resultRow)

    override fun updateBuilderSetter(data: Data): Table.(UpdateBuilder<*>) -> Unit = {
        setUpdateBuilder(classColumnMappings, data, it)
    }
}

fun <Data : Any> constructDataWithResultRow(
    clazz: KClass<Data>, classColumnMappings: ClassColumnMappings<Data>, resultRow: ResultRow
): Data =
    clazz.primaryConstructor!!.call(*classColumnMappings.map {
        when (it) {
            is ExposedSqlPrimitive -> resultRow.getValue(it.column)
            is NestedClass ->
                // TODO: the nullable case is not implemented yet
                @Suppress("UNCHECKED_CAST")
                constructDataWithResultRow(
                    it.property.returnType.classifier as KClass<Any>,
                    it.nestedMappings as ClassColumnMappings<Any>,
                    resultRow
                )

            is Skip -> null
        }
    }.toTypedArray())

fun <Data : Any> setUpdateBuilder(
    classColumnMappings: ClassColumnMappings<Data>, data: Data, updateBuilder: UpdateBuilder<*>
) {
    for (propertyColumnMapping in classColumnMappings)
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

fun ClassColumnMappings<*>.forEachColumn(block: (Column<*>) -> Unit) {
    for (propertyColumnMapping in this)
        when (propertyColumnMapping) {
            is ExposedSqlPrimitive -> block(propertyColumnMapping.column)
            is NestedClass -> propertyColumnMapping.nestedMappings.forEachColumn(block)
            is Skip -> {}
        }
}

fun <Data : Any> setUpdateBuilderToNulls(
    classColumnMappings: ClassColumnMappings<Data>, updateBuilder: UpdateBuilder<*>
) =
    classColumnMappings.forEachColumn {
        @Suppress("UNCHECKED_CAST")
        updateBuilder[it as Column<Any?>] = null
    }

fun ClassColumnMappings<*>.getNeededColumns(): List<Column<*>> =
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
