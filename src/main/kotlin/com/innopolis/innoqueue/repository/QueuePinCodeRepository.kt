package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.QueuePinCode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface QueuePinCodeRepository : CrudRepository<QueuePinCode, Long>