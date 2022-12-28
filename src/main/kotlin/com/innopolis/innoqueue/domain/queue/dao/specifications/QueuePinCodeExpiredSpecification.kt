package com.innopolis.innoqueue.domain.queue.dao.specifications

import com.innopolis.innoqueue.domain.queue.model.Queue
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Spring Data specification for searching for outdated pin codes
 * @param expiredDateTime - the date after which pin codes should be deleted
 */
class QueuePinCodeExpiredSpecification(
    private val expiredDateTime: LocalDateTime
) : Specification<Queue> {
    /**
     * Predicates for searching outdated pin codes in database
     */
    override fun toPredicate(
        root: Root<Queue>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val pinCodeDate = root.get<LocalDateTime>(Queue::pinDateCreated.name)
        return criteriaBuilder.lessThanOrEqualTo(pinCodeDate, expiredDateTime)
    }
}
