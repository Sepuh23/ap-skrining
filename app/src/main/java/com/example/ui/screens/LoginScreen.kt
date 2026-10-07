package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrisisHotlineBanner
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PsyVibeViewModel

@Composable
fun LoginScreen(
    viewModel: PsyVibeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var isRegisterTab by remember(uiState.isRegisterTabActive) { mutableStateOf(uiState.isRegisterTabActive) }
    var studentName by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var studentNisn by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PsyBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.LANDING) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = if (isRegisterTab) "Daftar Akun Baru Siswa" else "Masuk ke Ruang Aman",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PsyOnSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mascot Icon
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(PsyPrimaryContainer.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRegisterTab) Icons.Default.PersonAdd else Icons.Default.LockPerson,
                contentDescription = null,
                tint = PsyPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isRegisterTab) "Buat Akun Sanctuary Siswa" else "Akses Sanctuary Psikologi Siswa",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = PsyOnSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ruang aman bercerita & skrining emosi tanpa stigma dengan enkripsi end-to-end.",
            fontSize = 12.sp,
            color = PsyOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Selector (Masuk vs Daftar)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            color = PsySurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { isRegisterTab = false },
                    shape = RoundedCornerShape(100.dp),
                    color = if (!isRegisterTab) PsyPrimaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                    shadowElevation = if (!isRegisterTab) 2.dp else 0.dp
                ) {
                    Text(
                        text = "Masuk (Login)",
                        fontSize = 13.sp,
                        fontWeight = if (!isRegisterTab) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isRegisterTab) PsyOnPrimary else PsyOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { isRegisterTab = true },
                    shape = RoundedCornerShape(100.dp),
                    color = if (isRegisterTab) PsyPrimaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                    shadowElevation = if (isRegisterTab) 2.dp else 0.dp
                ) {
                    Text(
                        text = "Daftar Akun Baru",
                        fontSize = 13.sp,
                        fontWeight = if (isRegisterTab) FontWeight.Bold else FontWeight.Medium,
                        color = if (isRegisterTab) PsyOnPrimary else PsyOnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PsySurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Nama Lengkap / Panggilan") },
                    placeholder = { Text("Contoh: Ahmad Farel") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("Sekolah Asal (SMP / SMA / SMK)") },
                    placeholder = { Text("Contoh: SMAN 1 Jakarta") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studentNisn,
                    onValueChange = { studentNisn = it },
                    label = { Text("Nomor Induk Siswa (NISN)") },
                    placeholder = { Text("Contoh: 2026/0491") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                if (isRegisterTab) {
                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("Email / No. WhatsApp") },
                        placeholder = { Text("Contoh: ahmadfarel272@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi / PIN") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (isRegisterTab) {
                            viewModel.registerStudent(
                                name = studentName,
                                school = schoolName,
                                nisn = studentNisn,
                                email = emailOrPhone
                            )
                        } else {
                            viewModel.loginAsStudent(
                                name = studentName,
                                school = schoolName,
                                nisn = studentNisn
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PsyPrimaryContainer)
                ) {
                    Icon(
                        imageVector = if (isRegisterTab) Icons.Default.Check else Icons.Default.Login,
                        contentDescription = null,
                        tint = PsyOnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRegisterTab) "Daftar & Masuk ke Dashboard" else "Masuk Sebagai Siswa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PsyOnPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = PsySurfaceContainerHighest)
            Text(
                text = " ATAU ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PsyOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = PsySurfaceContainerHighest)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Anonymous Guest Mode
        OutlinedButton(
            onClick = { viewModel.loginAsGuest() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PsyPrimary),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(PsyPrimaryContainer))
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = PsyPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Masuk Mode Anonim (100% Rahasia)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy Guarantee
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = PsySurfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PsyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Data obrolan dan hasil scan dienkripsi E2E. Tidak ada catatan identitas yang dibagikan ke pihak sekolah tanpa izinmu.",
                    fontSize = 11.sp,
                    color = PsyOnSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        CrisisHotlineBanner()
    }
}
