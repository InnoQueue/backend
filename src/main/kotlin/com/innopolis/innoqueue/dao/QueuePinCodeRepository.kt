package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.QueuePinCode
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * DAO repository for working with "queue_pin_code" db table
 */
@Repository
interface QueuePinCodeRepository : CrudRepository<QueuePinCode, Long>, JpaSpecificationExecutor<QueuePinCode>
