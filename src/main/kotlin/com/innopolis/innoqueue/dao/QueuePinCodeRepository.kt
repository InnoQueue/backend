package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.QueuePinCode
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface QueuePinCodeRepository : CrudRepository<QueuePinCode, Long>, JpaSpecificationExecutor<QueuePinCode>
