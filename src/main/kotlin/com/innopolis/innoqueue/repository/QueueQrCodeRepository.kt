package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.QueueQrCode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface QueueQrCodeRepository : CrudRepository<QueueQrCode, Long>