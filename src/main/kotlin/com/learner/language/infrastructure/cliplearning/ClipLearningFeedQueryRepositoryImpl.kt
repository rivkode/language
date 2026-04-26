package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.ClipSourceVideo
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery as createJdslQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ClipLearningFeedQueryRepositoryImpl(
    private val entityManager: EntityManager
) : ClipLearningFeedQueryRepository {
    private val renderContext = JpqlRenderContext()

    override fun findFeedRows(category: String?, cursor: String?, size: Int): List<ClipLearningFeedRow> {
        val query = jpql {
            val clip = entity(ClipLearningClip::class, "clip")
            val sourceVideo = entity(ClipSourceVideo::class, "sourceVideo")

            selectNew<ClipLearningFeedRow>(
                clip(ClipLearningClip::id),
                sourceVideo(ClipSourceVideo::youtubeVideoId),
                clip(ClipLearningClip::title),
                clip(ClipLearningClip::clipStartMs),
                clip(ClipLearningClip::clipEndMs),
            ).from(
                clip,
                innerJoin(clip(ClipLearningClip::sourceVideo)).`as`(sourceVideo),
            ).whereAnd(
                *buildPredicates(clip, category, cursor)
            ).orderBy(
                clip(ClipLearningClip::id).desc()
            )
        }

        return entityManager.createJdslQuery(query, renderContext)
            .setMaxResults(size)
            .resultList
    }

    override fun findClipRowByClipId(clipId: Long): ClipLearningFeedRow? {
        val query = jpql {
            val clip = entity(ClipLearningClip::class, "clip")
            val sourceVideo = entity(ClipSourceVideo::class, "sourceVideo")

            selectNew<ClipLearningFeedRow>(
                clip(ClipLearningClip::id),
                sourceVideo(ClipSourceVideo::youtubeVideoId),
                clip(ClipLearningClip::title),
                clip(ClipLearningClip::clipStartMs),
                clip(ClipLearningClip::clipEndMs),
            ).from(
                clip,
                innerJoin(clip(ClipLearningClip::sourceVideo)).`as`(sourceVideo),
            ).whereAnd(
                clip(ClipLearningClip::id).equal(clipId)
            )
        }

        return entityManager.createJdslQuery(query, renderContext)
            .resultList
            .firstOrNull()
    }

    private fun com.linecorp.kotlinjdsl.dsl.jpql.Jpql.buildPredicates(
        clip: com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity<ClipLearningClip>,
        category: String?,
        cursor: String?
    ): Array<com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate> {
        val predicates = mutableListOf<com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate>()

        if (!category.isNullOrBlank()) {
            predicates += clip(ClipLearningClip::category).equal(category)
        }

        val cursorId = cursor?.toLongOrNull()
        if (cursorId != null) {
            predicates += clip(ClipLearningClip::id).lessThan(cursorId)
        }

        return predicates.toTypedArray()
    }
}
