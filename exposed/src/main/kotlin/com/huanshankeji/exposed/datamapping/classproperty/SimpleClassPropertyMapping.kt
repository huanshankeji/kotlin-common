package com.huanshankeji.exposed.datamapping.classproperty

import com.huanshankeji.exposed.datamapping.SimpleDataMapper
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.sequences.Sequence

fun ResultRow.getValue(column: Column<*>): Any? =
    this[column].let {
        if (it is EntityID<*>) it.value else it
    }

/** Nested classes are not supported. */
interface ReflectionBasedSimpleClassPropertyDataMapper<Data : Any> : SimpleDataMapper<Data> {
    val propertyAndColumnPairs: List<Pair<KProperty1<Data, *>, Column<*>>>
    val dataPrimaryConstructor: KFunction<Data>

    override fun resultRowToData(resultRow: ResultRow): Data {
        val params = propertyAndColumnPairs.map { (_, column) -> resultRow.getValue(column) }
        return dataPrimaryConstructor.call(*params.toTypedArray())
    }

    override fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>) {
        for ((property, column) in propertyAndColumnPairs)
            @Suppress("UNCHECKED_CAST")
            updateBuilder[column as Column<Any?>] = property(data)
    }
}

inline fun <reified Data : Any> reflectionBasedSimpleClassPropertyDataMapper(table: Table): ReflectionBasedSimpleClassPropertyDataMapper<Data> =
    object : ReflectionBasedSimpleClassPropertyDataMapper<Data> {
        private val clazz = Data::class

        // This property needs to initialize first.
        override val dataPrimaryConstructor = clazz.primaryConstructor!!
        override val propertyAndColumnPairs = run {
            //require(dClass.isData)
            val dataMemberPropertyMap = clazz.memberProperties.associateBy { it.name }
            val columnMap = getColumnByPropertyNameMapWithTypeParameter(table)
            dataPrimaryConstructor.parameters.map {
                val name = it.name!!
                dataMemberPropertyMap.getValue(name) to columnMap.getValue(name)
            }
        }
    }

@Suppress("UNCHECKED_CAST")
fun <TableT : Table> getColumnProperties(clazz: KClass<TableT>): Sequence<KProperty1<TableT, Column<*>>> =
    clazz.memberProperties.asSequence()
        .filter { it.returnType.run { classifier == Column::class && !isMarkedNullable } }
            as Sequence<KProperty1<TableT, Column<*>>>

fun <TableT : Table> getColumnPropertyByNameMap(clazz: KClass<TableT>): Map<String, KProperty1<TableT, Column<*>>> =
    getColumnProperties(clazz).associateBy { it.name }

inline fun <reified TableT : Table> getColumnByPropertyNameMapWithTypeParameter(table: TableT): Map<String, Column<*>> =
    getColumnPropertyByNameMap(TableT::class)
        .mapValues { it.value(table) }

inline fun <reified Data : Any, reified TableT : Table> reflectionBasedSimpleClassPropertyDataMapperForAlias(
    tableDataMapper: ReflectionBasedSimpleClassPropertyDataMapper<Data>, alias: Alias<TableT>
): ReflectionBasedSimpleClassPropertyDataMapper<Data> =
    object : ReflectionBasedSimpleClassPropertyDataMapper<Data> {
        override val dataPrimaryConstructor = tableDataMapper.dataPrimaryConstructor
        override val propertyAndColumnPairs =
            tableDataMapper.propertyAndColumnPairs.map { it.first to alias[it.second] }
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
