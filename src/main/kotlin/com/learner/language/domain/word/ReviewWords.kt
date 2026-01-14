package com.learner.language.domain.word

data class ReviewWords(
    var noCountWordInfo: List<WordInfo>,
    var countOneWordInfo: List<WordInfo>,
    var countTwoWordInfo: List<WordInfo>
)