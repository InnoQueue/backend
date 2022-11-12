package com.innopolis.innoqueue.dao.specifications

import com.innopolis.innoqueue.models.QueueQrCode
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

class QueueQrCodeExpiredSpecification(
    private val expiredDateTime: LocalDateTime
) : Specification<QueueQrCode> {
    override fun toPredicate(
        root: Root<QueueQrCode>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val qrCodeDate = root.get<LocalDateTime>(QueueQrCode::dateCreated.name)
        return criteriaBuilder.lessThanOrEqualTo(qrCodeDate, expiredDateTime)
    }
}
