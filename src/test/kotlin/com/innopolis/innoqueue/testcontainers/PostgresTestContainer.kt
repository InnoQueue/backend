package com.innopolis.innoqueue.testcontainers

import org.junit.jupiter.api.AfterEach
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.PostgreSQLContainer

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = [PostgresTestContainer.Initializer::class])
@Suppress("UnnecessaryAbstractClass")
abstract class PostgresTestContainer {

    @Autowired
    private lateinit var truncateDatabaseService: DatabaseCleanupService

    /**
     * Cleans up the test database after each test method.
     */
    @AfterEach
    fun cleanupAfterEach() {
        truncateDatabaseService.truncate()
    }

    companion object {
        val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
    }

    init {
        postgreSQLContainer.start()
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=${postgreSQLContainer.jdbcUrl}",
                "spring.datasource.username=${postgreSQLContainer.username}",
                "spring.datasource.password=${postgreSQLContainer.password}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
