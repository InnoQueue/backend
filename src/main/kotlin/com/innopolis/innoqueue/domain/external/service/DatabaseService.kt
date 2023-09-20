package com.innopolis.innoqueue.domain.external.service

import com.innopolis.innoqueue.domain.external.dto.HostDto
import com.innopolis.innoqueue.rest.v1.dto.EmptyDto

/**
 * Service for managing the database
 */
interface DatabaseService {
    fun clearExpiredInviteCodes(): EmptyDto

    fun getHost(): HostDto
}
