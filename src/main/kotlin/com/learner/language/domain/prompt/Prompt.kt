package com.learner.language.domain.prompt

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "prompt")
class Prompt(
    @Column(name = "name", nullable = false)
    var name: String,

    @Lob
    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    var prompt: String,

    @Convert(converter = PersonaTypeConverter::class)
    @Column
    var personaType: PersonaType,

    @Column(name = "active", nullable = false)
    var active: Boolean = true
): BaseEntity()
