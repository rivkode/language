package com.learner.language.domain.diary

object AccentToneResolver {
    private val TONES = listOf("cream", "apricot", "mint", "lavender", "outline")

    fun of(diaryId: Long): String {
        val idx = ((diaryId % TONES.size) + TONES.size) % TONES.size
        return TONES[idx.toInt()]
    }
}
