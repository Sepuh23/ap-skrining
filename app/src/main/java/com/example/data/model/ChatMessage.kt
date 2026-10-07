package com.example.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: String = "Sekarang",
    val sensorSnapshot: SensorTelemetry? = null,
    val isCrisisAlert: Boolean = false,
    val suggestedAction: SuggestedActionType? = null
)

enum class MessageSender {
    USER,
    VIBEBOT_AI
}

enum class SuggestedActionType {
    MINI_GAME_STRESS_RELIEF,
    GURU_BK_CONSULTATION,
    JOURNAL_PROMPT,
    SEJIWA_HOTLINE_119,
    BREATHING_EXERCISE
}
