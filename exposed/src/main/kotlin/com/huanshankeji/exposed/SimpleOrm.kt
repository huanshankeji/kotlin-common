package com.huanshankeji.exposed

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

// Our own simple ORM implementation using reflection which should be adapted using annotation processors and code generation in the future.


interface SimpleOrm<D : Any, T : Table> {
    fun resultRowToData(resultRow: ResultRow): D
    fun updateBuilderSetter(data: D): T.(UpdateBuilder<Number>) -> Unit
}

interface ReflectionBasedSimpleOrm<D : Any, T : Table> : SimpleOrm<D, T> {
    val propertyAndColumnPairs: List<Pair<KProperty1<D, *>, Column<*>>>
    val dPrimaryConstructor: KFunction<D>

    override fun resultRowToData(resultRow: ResultRow): D {
        val params = propertyAndColumnPairs.map { (_, tColumn) ->
            resultRow[tColumn].let {
                if (it is EntityID<*>) it.value else it
            }
        }
        return dPrimaryConstructor.call(*params.toTypedArray())
    }

    override fun updateBuilderSetter(data: D): T.(UpdateBuilder<Number>) -> Unit = {
        for ((dProperty, tColumn) in propertyAndColumnPairs)
            @Suppress("UNCHECKED_CAST")
            it[tColumn as Column<Any?>] = dProperty(data)
    }
}

inline fun <reified D : Any, reified T : Table> reflectionBasedSimpleOrm(table: T): ReflectionBasedSimpleOrm<D, T> =
    object : ReflectionBasedSimpleOrm<D, T> {
        val dClass = D::class
        override val dPrimaryConstructor = dClass.primaryConstructor!!
        override val propertyAndColumnPairs = run {
            //require(dClass.isData)
            val dMemberPropertyMap = dClass.memberProperties.associateBy { it.name }
            val tMemberPropertyMap = T::class.memberProperties.associateBy { it.name }
            dPrimaryConstructor.parameters.map {
                val name = it.name!!
                dMemberPropertyMap.getValue(name) to tMemberPropertyMap.getValue(name)(table) as Column<*>
            }
        }
    }

inline fun <reified D : Any, reified T : Table> reflectionBasedSimpleOrmForAlias(
    tableOrm: ReflectionBasedSimpleOrm<D, T>, alias: Alias<T>
): ReflectionBasedSimpleOrm<D, T> =
    object : ReflectionBasedSimpleOrm<D, T> {
        override val dPrimaryConstructor = tableOrm.dPrimaryConstructor
        override val propertyAndColumnPairs = tableOrm.propertyAndColumnPairs.map { it.first to alias[it.second] }
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
