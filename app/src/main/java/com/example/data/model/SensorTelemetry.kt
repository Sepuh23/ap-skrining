package com.example.data.model

data class SensorTelemetry(
    val microTensionIndex: Int = 13, // %
    val eyeBlinkRhythmRate: Int = 70, // %
    val au4ForeheadWrinkle: Int = 17, // % (AU4 Brow Furrow)
    val au12SmileMuscle: Int = 67, // % (AU12 Zygomatic Smile)
    val facialStatus: String = "Sedikit Lelah • 95% Akurat",
    val isCameraOn: Boolean = true,
    val pitchCadence: Int = 84, // %
    val pitchIntonation: String = "Normal",
    val pitchPauseSeconds: Float = 0.4f, // 0.4s Reflektif
    val acousticStress: Float = 0.14f,
    val isSuppressedSmile: Boolean = true
) {
    val isSmilingDepressionSuspected: Boolean
        get() = (au12SmileMuscle > 50 && au4ForeheadWrinkle > 15) || (microTensionIndex > 45)

    val au4StatusLabel: String
        get() = if (au4ForeheadWrinkle < 25) "Alis Tidak Tegang" else "Ketegangan Dahi Tinggi"

    val au12StatusLabel: String
        get() = if (isSuppressedSmile || au12SmileMuscle > 50) "Suppressed / Lelah" else "Senyum Alami"

    val microTensionLabel: String
        get() = if (microTensionIndex < 25) "Kondisi Relaks" else "Kondisi Tegang"

    val tensionStatusLabel: String
        get() = "${microTensionIndex}% ($microTensionLabel)"

    val blinkRhythmLabel: String
        get() = "Ritme Kedip Stabil"
}
