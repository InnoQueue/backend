package com.innopolis.innoqueue.testcontainers

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.Table
import javax.persistence.metamodel.Metamodel
import kotlin.reflect.full.findAnnotation

/**
 * Test utility service that allows to truncate all tables in the test database.
 * Inspired by: http://www.greggbolinger.com/truncate-all-tables-in-spring-boot-jpa-app/
 * @author Sebastien Dubois
 */
@Service
@Profile("test")
class DatabaseCleanupService @Autowired constructor(private val entityManager: EntityManager) : InitializingBean {
    private lateinit var tableNames: List<String>

    /**
     * Uses the JPA metamodel to find all managed types then try to get the [Table] annotation's from each
     * (if present) to discover the table name.
     * If the [Table] annotation is not defined then we skip that entity (oops :p)
     */
    override fun afterPropertiesSet() {
        val metaModel: Metamodel = entityManager.metamodel
        tableNames = metaModel.managedTypes
            .filter {
                it.javaType.kotlin.findAnnotation<Table>() != null
            }
            .map {
                val tableAnnotation: Table? = it.javaType.kotlin.findAnnotation()
                checkNotNull(tableAnnotation?.name)
                tableAnnotation?.name!!
            }
    }

    /**
     * Utility method that truncates all identified tables
     */
    @Transactional
    fun truncate() {
        entityManager.flush()
        tableNames.forEach { tableName ->
            entityManager.createNativeQuery("TRUNCATE TABLE $tableName CASCADE").executeUpdate()
        }
    }
}
