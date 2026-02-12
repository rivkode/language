package com.learner.language.infrastructure.prompt

import org.springframework.data.repository.query.Param
import com.learner.language.domain.prompt.Prompt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PromptRepository : JpaRepository<Prompt, Long> {

    @Query(value = "SELECT * FROM prompt WHERE persona_type = :personaType order by id DESC limit 1", nativeQuery = true)
    fun findByPersonaType(@Param("personaType") personaType: Int): Prompt
}
