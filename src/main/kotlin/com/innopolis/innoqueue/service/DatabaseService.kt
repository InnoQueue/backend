package com.innopolis.innoqueue.service

import com.innopolis.innoqueue.repository.DatabaseRepository
import org.springframework.stereotype.Service

@Service
class DatabaseService(
    private val databaseRepository: DatabaseRepository,
) {
    fun resetDB() = databaseRepository.resetDB()
}