package com.huanshankeji.exposed.datamapping

import com.huanshankeji.ExperimentalApi
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnSet
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

@ExperimentalApi
fun interface SimpleNullableDataQueryMapper<Data> {
    fun resultRowToData(resultRow: ResultRow): Data
}

@ExperimentalApi
fun interface SimpleDataQueryMapper<Data : Any> : SimpleNullableDataQueryMapper<Data>

@ExperimentalApi
fun interface NullableDataUpdateMapper<Data> {
    fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>)
}

@ExperimentalApi
fun interface DataUpdateMapper<Data : Any> : NullableDataUpdateMapper<Data>

@ExperimentalApi
fun <Data : Any, ColumnSetT : ColumnSet> DataUpdateMapper<Data>.updateBuilderSetter(data: Data):
        ColumnSetT.(UpdateBuilder<*>) -> Unit = {
    setUpdateBuilder(data, it)
}

@ExperimentalApi
interface SimpleDataMapper<Data : Any> : SimpleDataQueryMapper<Data>, DataUpdateMapper<Data>


@ExperimentalApi
interface NullableDataQueryMapper<Data> : SimpleNullableDataQueryMapper<Data> {
    val neededColumns: List<Column<*>> // TODO consider refactoring to `ExpressionWithColumnType`
}

@ExperimentalApi
interface DataQueryMapper<Data : Any> : NullableDataQueryMapper<Data>

@ExperimentalApi
interface NullableDataMapper<Data> : NullableDataQueryMapper<Data>, NullableDataUpdateMapper<Data>

// TODO rename to `NotNullDataMapper`
@ExperimentalApi
interface DataMapper<Data : Any> : NullableDataMapper<Data>, DataQueryMapper<Data>, SimpleDataMapper<Data>
