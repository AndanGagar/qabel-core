package de.qabel.core.repository.framework

class QueryBuilder {

    companion object {
        private const val EQUALS = "="
    }

    enum class Direction(val sql: String) {
        ASCENDING("ASC"),
        DESCENDING("DESC")
    }

    private val select = StringBuilder()
    private val from = StringBuilder()
    private val joins = StringBuilder()
    private val where = StringBuilder()
    private val orderBy = StringBuilder()
    private val groupBy = StringBuilder()
    val params = mutableListOf<Any>()

    fun select(field: Field) {
        select(field.select())
    }

    fun select(fields: List<Field>) {
        for (field in fields) {
            select(field)
        }
    }

    fun select(text: String) {
        if (select.isEmpty()) {
            select.append("SELECT ")
        } else {
            select.append(", ")
        }
        select.append(text)
    }

    fun from(table: String, alias: String) {
        if (from.isEmpty()) {
            from.append("FROM ")
        } else {
            from.append(", ")
        }
        from.append(table)
        from.append(" ")
        from.append(alias)
    }

    fun innerJoin(table: String, tableAlias: String,
                  joinField: String, targetField: String) {
        joins.append("INNER ")
        appendJoin(table, tableAlias, joinField, targetField)
    }

    fun leftJoin(table: String, tableAlias: String,
                 joinField: String, targetField: String) {
        joins.append("LEFT ")
        appendJoin(table, tableAlias, joinField, targetField)
    }


    private fun appendJoin(table: String, tableAlias: String,
                           joinField: String, targetField: String) {
        joins.append("JOIN ")
        joins.append(table)
        joins.append(" ")
        joins.append(tableAlias)
        joins.append(" ON ")
        joins.append(joinField)
        joins.append("=")
        joins.append(targetField)
    }

    fun whereAndEquals(field: Field, value: Any) {
        appendWhere(field.exp(), EQUALS, "?", " AND ")
        params.add(value)
    }

    fun whereAndNull(field: Field) {
        appendWhere(field.exp(), " IS NULL ", "", " AND ");
    }

    private fun appendWhere(field: String, condition: String, valuePlaceholder: String, concatenation: String) {
        if (where.isEmpty()) {
            where.append("WHERE ")
        } else if (!where.last().toString().equals("(")) {
            where.append(concatenation)
        }
        where.append(field)
        where.append(condition)
        where.append(valuePlaceholder)
    }

    fun appendWhere(sql: String) {
        where.append(sql)
    }

    fun orderBy(field: String, direction: Direction = Direction.ASCENDING) {
        if (orderBy.isEmpty()) {
            orderBy.append("ORDER BY ")
        } else {
            orderBy.append(", ")
        }
        orderBy.append(field)
        orderBy.append(" ")
        orderBy.append(direction.sql)
    }

    fun groupBy(field: DBField) {
        if (groupBy.isEmpty()) {
            groupBy.append("GROUP BY ")
        } else {
            groupBy.append(", ")
        }
        groupBy.append(field.exp())
    }

    fun queryString(): String = select.toString() + " " +
        from.toString() + " " +
        (if (!joins.isEmpty()) joins.toString() else "") + " " +
        (if (!where.isEmpty()) where.toString() else "") + " " +
        (if (!groupBy.isEmpty()) groupBy.toString() else "") + " " +
        (if (!orderBy.isEmpty()) orderBy.toString() else "")

}

