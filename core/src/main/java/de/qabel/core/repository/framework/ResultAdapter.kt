package de.qabel.core.repository.framework

import de.qabel.core.repository.EntityManager
import de.qabel.core.repository.exception.PersistenceException
import java.sql.ResultSet

interface ResultAdapter<out T> {

    fun hydrateOne(resultSet: ResultSet, entityManager: EntityManager): T

    fun <T : PersistenceEnum> enumValue(value: Int, values: Array<T>): T =
        values.find { it.type == value } ?: throw PersistenceException("Invalid enum value!")

}


