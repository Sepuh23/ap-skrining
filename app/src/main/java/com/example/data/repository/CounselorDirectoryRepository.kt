package com.example.data.repository

import com.example.data.model.DirectoryCategory
import com.example.data.model.DirectoryItem

object CounselorDirectoryRepository {

    val items = listOf(
        DirectoryItem(
            id = "bk_1",
            name = "Ibu Ratna Kumalasari, S.Psi.",
            roleTitle = "Guru BK / Konselor Siswa",
            institution = "SMAN 1 Jakarta (Mitra Sekolah)",
            type = DirectoryCategory.GURU_BK,
            baseDistanceKm = 0.1,
            latitude = -6.1668,
            longitude = 106.8340,
            rating = 4.9,
            reviewCount = 124,
            availabilityStatus = "Tersedia Hari Ini (08:00 - 15:00)",
            isOnlineAvailable = true,
            isFreeOrBPJS = true,
            address = "Gedung A, Ruang BK Lt. 2, SMAN 1 Jakarta",
            phoneContact = "081298765432",
            tags = listOf("Akademik", "Curhat Pribadi", "Tatap Muka / Chat")
        ),
        DirectoryItem(
            id = "bk_2",
            name = "Pak Dimas Prasetyo, M.Pd.",
            roleTitle = "Konselor Minat & Bakat Remaja",
            institution = "Pusat Konseling Sahabat Pelajar",
            type = DirectoryCategory.GURU_BK,
            baseDistanceKm = 0.8,
            latitude = -6.1712,
            longitude = 106.8385,
            rating = 4.8,
            reviewCount = 89,
            availabilityStatus = "Online Sesi Sore (15:30 - 18:00)",
            isOnlineAvailable = true,
            isFreeOrBPJS = true,
            address = "Jl. Budi Utomo No. 7, Jakarta Pusat",
            phoneContact = "081311223344",
            tags = listOf("Karir Pelajar", "Manajemen Stres", "Gratis")
        ),
        DirectoryItem(
            id = "psi_1",
            name = "Dr. Andini Putri, M.Psi., Psikolog",
            roleTitle = "Psikolog Klinis Anak & Remaja",
            institution = "Klinik Pratama Sehati Sejiwa",
            type = DirectoryCategory.PSIKOLOG_KLINIS,
            baseDistanceKm = 1.2,
            latitude = -6.1856,
            longitude = 106.8492,
            rating = 5.0,
            reviewCount = 210,
            availabilityStatus = "Praktik Aktif (Janji Temu Online)",
            isOnlineAvailable = true,
            isFreeOrBPJS = false,
            address = "Jl. Salemba Raya No. 42, Jakarta Pusat",
            phoneContact = "0213145678",
            tags = listOf("Anxiety", "Smiling Depression", "Terapi CBT")
        ),
        DirectoryItem(
            id = "psi_2",
            name = "Rangga Wicaksono, M.Psi.",
            roleTitle = "Spesialis Kesehatan Mental Remaja",
            institution = "Biro Psikologi Asa Muda",
            type = DirectoryCategory.PSIKOLOG_KLINIS,
            baseDistanceKm = 2.1,
            latitude = -6.1915,
            longitude = 106.8402,
            rating = 4.9,
            reviewCount = 156,
            availabilityStatus = "Bisa Konsultasi Video Call",
            isOnlineAvailable = true,
            isFreeOrBPJS = false,
            address = "Jl. Cikini Raya No. 18, Jakarta Pusat",
            phoneContact = "082199887766",
            tags = listOf("Overthinking", "Burnout Belajar", "Konseling Daring")
        ),
        DirectoryItem(
            id = "rs_1",
            name = "Puskesmas Kecamatan Menteng",
            roleTitle = "Poli Jiwa & Konseling Remaja (PKPR)",
            institution = "Fasilitas Kesehatan Tingkat Pertama (FKTP)",
            type = DirectoryCategory.PUSKESMAS_RS,
            baseDistanceKm = 1.8,
            latitude = -6.1982,
            longitude = 106.8398,
            rating = 4.7,
            reviewCount = 340,
            availabilityStatus = "Senin - Jumat (07:30 - 14:00)",
            isOnlineAvailable = false,
            isFreeOrBPJS = true,
            address = "Jl. Pegangsaan Barat No. 14, Menteng",
            phoneContact = "0213904567",
            tags = listOf("BPJS Gratis", "Skrining Jiwa", "Faskes 1")
        ),
        DirectoryItem(
            id = "rs_2",
            name = "RSUD Tarakan Jakarta",
            roleTitle = "Instalasi Kesehatan Jiwa & IGD 24 Jam",
            institution = "Rumah Sakit Umum Daerah",
            type = DirectoryCategory.PUSKESMAS_RS,
            baseDistanceKm = 2.4,
            latitude = -6.1685,
            longitude = 106.8094,
            rating = 4.8,
            reviewCount = 490,
            availabilityStatus = "Layanan Darurat 24 Jam",
            isOnlineAvailable = true,
            isFreeOrBPJS = true,
            address = "Jl. Kyai Caringin No. 7, Jakarta Pusat",
            phoneContact = "0213842930",
            tags = listOf("IGD 24 Jam", "Rujukan BPJS", "Spesialis Jiwa")
        )
    )
}
