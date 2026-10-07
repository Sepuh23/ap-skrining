package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.model.DirectoryCategory
import com.example.data.model.DirectoryItem
import com.example.data.repository.CounselorDirectoryRepository
import com.example.ui.components.CrisisHotlineBanner
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeUiState
import com.example.viewmodel.PsyVibeViewModel

@Composable
fun DirectoryScreen(
    viewModel: PsyVibeViewModel,
    uiState: PsyVibeUiState,
    modifier: Modifier = Modifier
) {
    val categoryScrollState = rememberScrollState()
    val context = LocalContext.current
    var bookingConfirmed by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (hasLocationPermission) {
            viewModel.refreshGpsLocation()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Filter and sort items by real-time calculated distance
    val filteredItems = CounselorDirectoryRepository.items.filter { item ->
        val matchesCategory = when (uiState.selectedCategory) {
            DirectoryCategory.ALL -> true
            else -> item.type == uiState.selectedCategory
        }
        val matchesSearch = uiState.searchQuery.isBlank() ||
                item.name.contains(uiState.searchQuery, ignoreCase = true) ||
                item.institution.contains(uiState.searchQuery, ignoreCase = true) ||
                item.tags.any { it.contains(uiState.searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }.sortedBy { it.calculateLiveDistance(uiState.userLatitude, uiState.userLongitude) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .padding(bottom = 64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
                Column {
                    Text(
                        text = "Direktori Konseling & Faskes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnSurface
                    )
                    Text(
                        text = "Guru BK, Psikolog Klinis & Puskesmas Remaja",
                        fontSize = 11.sp,
                        color = PsyOnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(PsySurfaceContainerLow, CircleShape)
                    .clickable { viewModel.refreshGpsLocation() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isGpsUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PsyPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS Refresh",
                        tint = PsyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Live Real-Time GPS Status Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = PsySurfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PsyPrimaryContainer.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Lokasi GPS Real-Time",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = PsyGreenSafe.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "● Aktif",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyGreenSafe,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${uiState.currentGpsAddress} • ${uiState.currentGpsCoordinates}",
                            fontSize = 10.sp,
                            color = PsyOnSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }

                TextButton(
                    onClick = { viewModel.refreshGpsLocation() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Perbarui", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PsyPrimary)
                }
            }
        }

        // Search Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            color = PsySurfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = PsyOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Cari Guru BK, Psikolog, atau RS...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(categoryScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf(
                DirectoryCategory.ALL to "Semua",
                DirectoryCategory.GURU_BK to "🏫 Guru BK Sekolah",
                DirectoryCategory.PSIKOLOG_KLINIS to "🧠 Psikolog Klinis",
                DirectoryCategory.PUSKESMAS_RS to "🏥 Puskesmas / RS"
            )
            categories.forEach { (cat, label) ->
                val isSelected = uiState.selectedCategory == cat
                Surface(
                    modifier = Modifier.clickable { viewModel.selectCategory(cat) },
                    shape = RoundedCornerShape(100.dp),
                    color = if (isSelected) PsyPrimaryContainer else PsySurfaceContainerLowest,
                    shadowElevation = if (isSelected) 2.dp else 1.dp
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PsyOnPrimary else PsyOnSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Directory List with real live distances
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems) { item ->
                val liveDist = item.calculateLiveDistance(uiState.userLatitude, uiState.userLongitude)
                DirectoryCard(
                    item = item,
                    liveDistance = liveDist,
                    onBookClick = { viewModel.openBookingDialog(item) },
                    onCallClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.phoneContact}"))
                        context.startActivity(dialIntent)
                    },
                    onMapClick = {
                        val geoUri = Uri.parse("geo:${item.latitude},${item.longitude}?q=${Uri.encode(item.name + " " + item.address)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                        context.startActivity(mapIntent)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                CrisisHotlineBanner()
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Booking Dialog
    if (uiState.showBookingDialog && uiState.selectedDirectoryItem != null) {
        val item = uiState.selectedDirectoryItem
        Dialog(onDismissRequest = { 
            bookingConfirmed = false
            viewModel.dismissBookingDialog() 
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PsySurfaceContainerLowest,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jadwalkan Konsultasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        IconButton(onClick = { 
                            bookingConfirmed = false
                            viewModel.dismissBookingDialog() 
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    if (!bookingConfirmed) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                            Text(
                                text = "${item.roleTitle} • ${item.institution}",
                                fontSize = 12.sp,
                                color = PsyPrimary
                            )
                            Text(
                                text = "Status: ${item.availabilityStatus}",
                                fontSize = 11.sp,
                                color = PsyGreenSafe,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PsySurfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "🔒 Privasi Terjamin",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PsyPrimary
                                )
                                Text(
                                    text = "Identitasmu disamarkan. Sesi dapat dilakukan via chat privat online atau tatap muka langsung di Ruang Konseling.",
                                    fontSize = 11.sp,
                                    color = PsyOnSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Button(
                            onClick = { bookingConfirmed = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                        ) {
                            Text("Konfirmasi Janji Temu", fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(PsyGreenSafe.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = PsyGreenSafe,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = "Permintaan Konseling Terkirim!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PsyOnSurface
                            )
                            Text(
                                text = "Konselor telah menerima permintaanmu dan akan menghubungi via chat privat terenkripsi di aplikasi.",
                                fontSize = 12.sp,
                                color = PsyOnSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Button(
                                onClick = {
                                    bookingConfirmed = false
                                    viewModel.dismissBookingDialog()
                                    viewModel.navigateTo(AppScreen.CURHAT_SCAN)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                            ) {
                                Text("Kembali ke Ruang Curhat", fontWeight = FontWeight.Bold, color = PsyOnPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectoryCard(
    item: DirectoryItem,
    liveDistance: Double,
    onBookClick: () -> Unit,
    onCallClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PsySurfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                when (item.type) {
                                    DirectoryCategory.GURU_BK -> PsyPrimaryContainer.copy(alpha = 0.2f)
                                    DirectoryCategory.PSIKOLOG_KLINIS -> PsyTertiaryContainer.copy(alpha = 0.3f)
                                    else -> PsySurfaceContainerHighest
                                },
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                DirectoryCategory.GURU_BK -> Icons.Default.School
                                DirectoryCategory.PSIKOLOG_KLINIS -> Icons.Default.Psychology
                                else -> Icons.Default.LocalHospital
                            },
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyOnSurface
                        )
                        Text(
                            text = "${item.roleTitle} • ${item.institution}",
                            fontSize = 11.sp,
                            color = PsyOnSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = PsySurfaceContainerLow
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PsyPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "$liveDistance km",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PsyPrimary
                        )
                    }
                }
            }

            // Tags & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.isFreeOrBPJS) PsyGreenSafe.copy(alpha = 0.15f) else PsySurfaceContainerLow
                ) {
                    Text(
                        text = if (item.isFreeOrBPJS) "BPJS / Gratis" else "Klinik Mandiri",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (item.isFreeOrBPJS) PsyGreenSafe else PsyOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = item.availabilityStatus,
                    fontSize = 10.sp,
                    color = PsySecondary
                )
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBookClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Jadwalkan Konseling",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PsyOnPrimary
                    )
                }

                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(PsySurfaceContainerLow, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Telepon",
                        tint = PsyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(PsySurfaceContainerLow, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Peta & Rute GPS",
                        tint = PsyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
