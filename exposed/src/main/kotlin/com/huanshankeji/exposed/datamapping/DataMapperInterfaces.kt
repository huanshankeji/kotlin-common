package com.huanshankeji.exposed.datamapping

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

fun interface SimpleDataQueryMapper<Data : Any> {
    fun resultRowToData(resultRow: ResultRow): Data
}

fun interface SimpleDataUpdateMapper<Data : Any> {
    fun setUpdateBuilder(data: Data, updateBuilder: UpdateBuilder<*>)
}

fun <Data : Any, ColumnSetT : ColumnSet> SimpleDataUpdateMapper<Data>.updateBuilderSetter(data: Data):
        ColumnSetT.(UpdateBuilder<*>) -> Unit = {
    setUpdateBuilder(data, it)
}

interface SimpleDataMapper<Data : Any> :
    SimpleDataQueryMapper<Data>, SimpleDataUpdateMapper<Data>


interface DataQueryMapper<Data : Any> : SimpleDataQueryMapper<Data> {
    val neededColumns: List<Column<*>>
}

typealias DataUpdateMapper<Data> = SimpleDataUpdateMapper<Data>

interface DataMapper<Data : Any> : DataQueryMapper<Data>, SimpleDataMapper<Data>
