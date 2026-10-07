package com.example.data.repository

import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.SensorTelemetry
import com.example.data.model.SuggestedActionType

object VibeBotEngine {

    val INITIAL_GREETING = ChatMessage(
        sender = MessageSender.VIBEBOT_AI,
        text = "“Aku perhatikan dari getaran suaramu dan sedikit kerutan di dahi, kamu sepertinya lagi menahan lelah ya? Santai aja, ceritakan apa yang bikin berat hari ini. Aku di sini mendengarkan tanpa menghakimi. 🔒 100% Aman & Anonim.”"
    )

    fun generateEmpatheticResponse(
        userInput: String,
        telemetry: SensorTelemetry,
        userName: String = "Ahmad Farel"
    ): ChatMessage {
        val lowerText = userInput.lowercase().trim()

        // 1. CRISIS SAFEGUARD (24/7 Hotline Sejiwa Kemenkes 119)
        val crisisKeywords = listOf("bunuh diri", "nyerah hidup", "mau mati", "mengakhiri hidup", "self harm", "ga sanggup lagi hidup", "putus asa banget", "mati aja")
        if (crisisKeywords.any { lowerText.contains(it) }) {
            return ChatMessage(
                sender = MessageSender.VIBEBOT_AI,
                text = "Aku denger rasa sakit dan beratnya yang lagi kamu rasain sekarang, $userName. Kamu sangat berharga dan kamu berhak dapet bantuan yang aman.\n\nButuh Bantuan Krisis Cepat? Hubungi Hotline Sejiwa Kemenkes 119 (Bebas Pulsa 24 Jam).",
                isCrisisAlert = true,
                suggestedAction = SuggestedActionType.SEJIWA_HOTLINE_119
            )
        }

        // 2. DETECT SMILING DEPRESSION VIA SCAN MUKA (MISMATCH DETECTION)
        val hidingEmotionsKeywords = listOf("baik-baik aja", "baik baik aja", "biasa aja", "gak apa-apa", "gapapa", "fine", "aman kok", "tetep senyum", "santai aja")
        val isClaimingFine = hidingEmotionsKeywords.any { lowerText.contains(it) }

        if (isClaimingFine && (telemetry.au12SmileMuscle > 45 && telemetry.au4ForeheadWrinkle > 12)) {
            return ChatMessage(
                sender = MessageSender.VIBEBOT_AI,
                text = "Hasil scan muka kamu nunjukin ada ketegangan tersembunyi meskipun kamu senyum... Kamu nggak harus selalu keliatan kuat di depanku kok. Cerita aja pelan-pelan ya.",
                sensorSnapshot = telemetry,
                suggestedAction = SuggestedActionType.JOURNAL_PROMPT
            )
        }

        // 3. STRESS-RELIEF GAMES (XOX & TETRIS)
        if (lowerText.contains("game") || lowerText.contains("main") || lowerText.contains("stress") || lowerText.contains("stres") || lowerText.contains("pusing") || lowerText.contains("tugas")) {
            return ChatMessage(
                sender = MessageSender.VIBEBOT_AI,
                text = "Pikiran lagi penuh ya? Mau rehat sejenak sambil main XOX (Tic-Tac-Toe) atau Tetris santai bareng aku di Vibe Arena?",
                sensorSnapshot = telemetry,
                suggestedAction = SuggestedActionType.MINI_GAME_STRESS_RELIEF
            )
        }

        // 4. GPS & HEALTHCARE ROUTING
        if (lowerText.contains("psikolog") || lowerText.contains("bk") || lowerText.contains("dokter") || lowerText.contains("puskesmas") || lowerText.contains("konseling") || lowerText.contains("lokasi")) {
            return ChatMessage(
                sender = MessageSender.VIBEBOT_AI,
                text = "Aku bisa bantu hubungkan kamu ke Guru BK sekolah atau Psikolog Klinis & Puskesmas terdekat berdasarkan lokasi GPS kamu. Mau aku bukakan direktori faskes?",
                sensorSnapshot = telemetry,
                suggestedAction = SuggestedActionType.GURU_BK_CONSULTATION
            )
        }

        // 5. MENTAL EXHAUSTION / BURNOUT
        if (lowerText.contains("capek") || lowerText.contains("lelah") || lowerText.contains("burnout") || lowerText.contains("males") || lowerText.contains("overthinking")) {
            return ChatMessage(
                sender = MessageSender.VIBEBOT_AI,
                text = "Nggak apa-apa banget kalau energimu lagi drop hari ini, $userName. Wajar kok merasa lelah. Tarik nafas panjang dulu ya, ceritain pelan-pelan apa yang paling bikin ganjel di dada.",
                sensorSnapshot = telemetry,
                suggestedAction = SuggestedActionType.BREATHING_EXERCISE
            )
        }

        // 6. GENERAL EMPATHETIC FALLBACK
        return ChatMessage(
            sender = MessageSender.VIBEBOT_AI,
            text = "Aku dengerin setiap ceritamu, $userName. Makasih udah mau berbagi di ruang aman ini tanpa takut dihakimi. Mau kita bahas bagian mana lagi?",
            sensorSnapshot = telemetry
        )
    }
}
