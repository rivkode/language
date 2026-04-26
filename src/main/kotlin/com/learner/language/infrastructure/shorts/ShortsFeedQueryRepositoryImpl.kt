package com.learner.language.infrastructure.shorts

import com.learner.language.domain.shorts.Shorts
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery as createJdslQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ShortsFeedQueryRepositoryImpl(
    private val entityManager: EntityManager
) : ShortsFeedQueryRepository {
    private val renderContext = JpqlRenderContext()

    override fun findFeedRows(cursor: String?, size: Int): List<ShortsFeedRow> {
        val query = jpql {
            val short = entity(Shorts::class, "short")

            selectNew<ShortsFeedRow>(
                short(Shorts::id),
                short(Shorts::youtubeVideoId),
                short(Shorts::sourceUrl),
            ).from(
                short,
            ).whereAnd(
                *buildPredicates(short, cursor)
            ).orderBy(
                short(Shorts::id).desc()
            )
        }

        return entityManager.createJdslQuery(query, renderContext)
            .setMaxResults(size)
            .resultList
    }

    private fun com.linecorp.kotlinjdsl.dsl.jpql.Jpql.buildPredicates(
        short: com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity<Shorts>,
        cursor: String?,
    ): Array<com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate> {
        val predicates = mutableListOf<com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate>()

        predicates += short(Shorts::isActive).equal(true)

        val cursorId = cursor?.toLongOrNull()
        if (cursorId != null) {
            predicates += short(Shorts::id).lessThan(cursorId)
        }

        return predicates.toTypedArray()
    }
}
