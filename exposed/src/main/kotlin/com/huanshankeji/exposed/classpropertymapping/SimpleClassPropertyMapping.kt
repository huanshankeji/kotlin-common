package com.huanshankeji.exposed.classpropertymapping

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

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
