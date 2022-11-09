package com.huanshankeji.exposed.datamapping

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

fun interface SimpleNullableDataQueryMapper<Data> {
    fun resultRowToData(resultRow: ResultRow): Data
}

fun interface SimpleDataQueryMapper<Data : Any> : SimpleNullableDataQueryMapper<Data>

fun interface NullableDataUpdateMapper<Data> {
    fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>)
}

fun interface DataUpdateMapper<Data : Any> : NullableDataUpdateMapper<Data>

fun <Data : Any, ColumnSetT : ColumnSet> DataUpdateMapper<Data>.updateBuilderSetter(data: Data):
        ColumnSetT.(UpdateBuilder<*>) -> Unit = {
    setUpdateBuilder(data, it)
}

interface SimpleDataMapper<Data : Any> : SimpleDataQueryMapper<Data>, DataUpdateMapper<Data>


interface NullableDataQueryMapper<Data> : SimpleNullableDataQueryMapper<Data> {
    val neededColumns: List<Column<*>>
}

interface DataQueryMapper<Data : Any> : NullableDataQueryMapper<Data>

interface NullableDataMapper<Data> : NullableDataQueryMapper<Data>, NullableDataUpdateMapper<Data>
interface DataMapper<Data : Any> : NullableDataMapper<Data>, DataQueryMapper<Data>, SimpleDataMapper<Data>
