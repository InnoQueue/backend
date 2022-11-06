package com.innopolis.innoqueue.dao

import com.innopolis.innoqueue.models.QueueQrCode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface QueueQrCodeRepository : CrudRepository<QueueQrCode, Long>
