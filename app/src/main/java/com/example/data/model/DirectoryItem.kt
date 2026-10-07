package com.example.data.model

data class DirectoryItem(
    val id: String,
    val name: String,
    val roleTitle: String,
    val institution: String,
    val type: DirectoryCategory,
    val baseDistanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val reviewCount: Int,
    val availabilityStatus: String,
    val isOnlineAvailable: Boolean,
    val isFreeOrBPJS: Boolean,
    val address: String,
    val phoneContact: String,
    val tags: List<String>
) {
    fun calculateLiveDistance(userLat: Double, userLng: Double): Double {
        val earthRadius = 6371.0 // kilometers
        val dLat = Math.toRadians(latitude - userLat)
        val dLng = Math.toRadians(longitude - userLng)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = earthRadius * c
        return if (distance.isNaN() || distance <= 0.05) baseDistanceKm else Math.round(distance * 10.0) / 10.0
    }
}

enum class DirectoryCategory {
    ALL,
    GURU_BK,
    PSIKOLOG_KLINIS,
    PUSKESMAS_RS
}
