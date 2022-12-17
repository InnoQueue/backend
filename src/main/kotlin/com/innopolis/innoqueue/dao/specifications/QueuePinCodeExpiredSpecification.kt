package com.innopolis.innoqueue.dao.specifications

import com.innopolis.innoqueue.models.QueuePinCode
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
) : Specification<QueuePinCode> {
    /**
     * Predicates for searching outdated pin codes in database
     */
    override fun toPredicate(
        root: Root<QueuePinCode>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val pinCodeDate = root.get<LocalDateTime>(QueuePinCode::dateCreated.name)
        return criteriaBuilder.lessThanOrEqualTo(pinCodeDate, expiredDateTime)
    }
}
