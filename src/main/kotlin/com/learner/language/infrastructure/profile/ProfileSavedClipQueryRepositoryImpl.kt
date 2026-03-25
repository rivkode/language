package com.learner.language.infrastructure.profile

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.ClipLearningSentence
import com.learner.language.domain.cliplearning.ClipSourceVideo
import com.learner.language.domain.cliplearning.UserSavedClip
import com.learner.language.domain.user.User
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery as createJdslQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ProfileSavedClipQueryRepositoryImpl(
    private val entityManager: EntityManager
) : ProfileSavedClipQueryRepository {
    private val renderContext = JpqlRenderContext()

    override fun findSavedClipRows(userId: Long, cursor: String?, size: Int, category: String?): List<ProfileSavedClipRow> {
        val query = jpql {
            val savedClip = entity(UserSavedClip::class, "savedClip")
            val clip = entity(ClipLearningClip::class, "clip")
            val sourceVideo = entity(ClipSourceVideo::class, "sourceVideo")
            val sentence = entity(ClipLearningSentence::class, "sentence")

            selectNew<ProfileSavedClipRow>(
                savedClip(UserSavedClip::id),
                savedClip(UserSavedClip::createdAt),
                clip(ClipLearningClip::id),
                sourceVideo(ClipSourceVideo::id),
                sourceVideo(ClipSourceVideo::youtubeVideoId),
                sourceVideo(ClipSourceVideo::sourceUrl),
                sourceVideo(ClipSourceVideo::thumbnailUrl),
                clip(ClipLearningClip::title),
                clip(ClipLearningClip::category),
                sourceVideo(ClipSourceVideo::channelName),
                clip(ClipLearningClip::clipStartMs),
                clip(ClipLearningClip::clipEndMs),
                clip(ClipLearningClip::clipDurationMs),
                sentence(ClipLearningSentence::primarySentence)
            ).from(
                savedClip,
                innerJoin(savedClip(UserSavedClip::clip)).`as`(clip),
                innerJoin(clip(ClipLearningClip::sourceVideo)).`as`(sourceVideo),
                sentence
            ).whereAnd(
                sentence(ClipLearningSentence::clip).equal(clip),
                savedClip(UserSavedClip::user)(User::id).equal(userId),
                *buildPredicates(savedClip, clip, category, cursor)
            ).orderBy(
                savedClip(UserSavedClip::id).desc()
            )
        }

        return entityManager.createJdslQuery(query, renderContext)
            .setMaxResults(size)
            .resultList
    }

    private fun com.linecorp.kotlinjdsl.dsl.jpql.Jpql.buildPredicates(
        savedClip: com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity<UserSavedClip>,
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
            predicates += savedClip(UserSavedClip::id).lessThan(cursorId)
        }

        return predicates.toTypedArray()
    }
}
