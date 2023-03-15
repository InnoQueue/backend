package com.innopolis.innoqueue.domain.queue.dao.specification

import com.innopolis.innoqueue.domain.queue.model.Queue
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Spring Data specification for searching for outdated QR codes
 * @param expiredDateTime - the date after which QR codes should be deleted
 */
class QueueQrCodeExpiredSpecification(
    private val expiredDateTime: LocalDateTime
) : Specification<Queue> {
    /**
     * Predicates for searching outdated QR codes in database
     */
    override fun toPredicate(
        root: Root<Queue>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val qrCodeDate = root.get<LocalDateTime>(Queue::qrDateCreated.name)
        return criteriaBuilder.lessThanOrEqualTo(qrCodeDate, expiredDateTime)
    }
}
