package com.draw.onme.domain.model

/**
 * Mood and behavioral states of the AI mascot Pup.
 */
enum class PupMood {
    /**
     * Pup is sitting quietly in the corner, happily watching the user draw.
     */
    IDLE,

    /**
     * Pup is analyzing the strokes and processing predictions.
     */
    THINKING,

    /**
     * Pup identified the drawing with high confidence (>= 80%) and wants to guess.
     */
    EXCITED,

    /**
     * The user confirmed Pup's guess or drew a high-scoring stencil drawing.
     */
    PROUD,

    /**
     * Pup is offering friendly tips on how to improve a stencil drawing.
     */
    ENCOURAGING
}
