package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MoodEntity
import com.example.data.model.*
import com.example.data.repository.CounselorDirectoryRepository
import com.example.data.repository.VibeBotEngine
import com.example.data.sensor.RealDeviceHealthSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    LANDING,
    LOGIN,
    DASHBOARD,
    CURHAT_SCAN,
    GAME_STRESS,
    DIRECTORY,
    PROFILE
}

enum class StressGameMode {
    XOX_TICTACTOE,
    TETRIS_BLOCKS,
    VOICE_PITCH_JUMP
}

data class QuickTestResult(
    val tensionScore: Int = 18,
    val isSmilingDepressionDetected: Boolean = true,
    val smileAuthenticity: Int = 67,
    val statusSummary: String = "Deteksi Smiling Depression: Suppressed",
    val recommendation: String = "Terdeteksi ketegangan dahi AU4 (17%) dan senyum tertahan AU12 (67%). Yuk curhat di Ruang Aman!"
)

data class XoxBoardState(
    val board: List<String> = List(9) { "" },
    val isUserTurn: Boolean = true,
    val winner: String? = null,
    val scoreUser: Int = 0,
    val scoreAi: Int = 0
)

data class TetrisState(
    val score: Int = 0,
    val linesCleared: Int = 0,
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false
)

data class GameState(
    val activeMode: StressGameMode = StressGameMode.XOX_TICTACTOE,
    val xoxState: XoxBoardState = XoxBoardState(),
    val tetrisState: TetrisState = TetrisState(),
    val score: Int = 0,
    val starsCollected: Int = 0,
    val stressBiomarker: Int = 68,
    val playerY: Float = 0.5f,
    val smileIntensity: Int = 30,
    val voicePitchLevel: Int = 40,
    val isPlaying: Boolean = false,
    val isCompleted: Boolean = false
)

data class PsyVibeUiState(
    val currentScreen: AppScreen = AppScreen.LANDING,
    val previousScreen: AppScreen = AppScreen.LANDING,
    val isRegisterTabActive: Boolean = false,
    val userName: String = "Ahmad Farel",
    val userSchool: String = "SMAN 1 Jakarta",
    val userEmail: String = "ahmadfarel272@gmail.com",
    val userNisn: String = "2026/0491",
    val isAnonymousGuest: Boolean = false,
    val isLoggedIn: Boolean = false,
    
    // Telemetry & Dual Sensing
    val liveTelemetry: SensorTelemetry = SensorTelemetry(),
    val isCameraActive: Boolean = true,
    val isScanningFaceNow: Boolean = false,
    val faceScanProgress: Float = 0f,
    val isMicActive: Boolean = false,
    val isRecordingSpeech: Boolean = false,
    
    // Real Device Health & Steps
    val realDeviceSteps: Int = 0,
    val realActiveMinutes: Int = 0,
    
    // Mood History (Clean from Room DB)
    val selectedMood: String = "Tenang",
    val moodHistory: List<MoodEntry> = emptyList(),

    // Curhat Chat
    val chatMessages: List<ChatMessage> = listOf(VibeBotEngine.INITIAL_GREETING),
    val curhatInputText: String = "",
    val isAiTyping: Boolean = false,

    // Quick 5-sec Tester
    val isQuickTesting: Boolean = false,
    val quickTestProgress: Float = 0f,
    val quickTestResult: QuickTestResult? = null,
    val showQuickTestModal: Boolean = false,

    // Stress Relief Game
    val gameState: GameState = GameState(),

    // Directory & Real-Time GPS
    val selectedCategory: DirectoryCategory = DirectoryCategory.ALL,
    val searchQuery: String = "",
    val selectedDirectoryItem: DirectoryItem? = null,
    val showBookingDialog: Boolean = false,
    val isGpsActive: Boolean = true,
    val isGpsUpdating: Boolean = false,
    val userLatitude: Double = -6.1754,
    val userLongitude: Double = 106.8272,
    val currentGpsAddress: String = "Gambir, Jakarta Pusat (Dekat SMAN 1)",
    val currentGpsCoordinates: String = "-6.1754° S, 106.8272° E (Akurasi Tinggi)"
)

class PsyVibeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val moodDao = db.moodDao()
    private val sensorManager = RealDeviceHealthSensorManager(application)
    private val gpsManager = com.example.data.sensor.RealDeviceGpsManager(
        context = application,
        coroutineScope = viewModelScope
    ) { lat, lng, address, accuracy ->
        updateLiveGpsLocation(lat, lng, address, accuracy)
    }

    private val _uiState = MutableStateFlow(PsyVibeUiState())
    val uiState: StateFlow<PsyVibeUiState> = _uiState.asStateFlow()

    private var telemetryJob: Job? = null
    private var gameLoopJob: Job? = null
    private var healthTimerJob: Job? = null

    init {
        startLiveTelemetrySimulation()
        listenToRealSensors()
        loadPersistedMoods()
        gpsManager.requestRealLocation()
    }

    private fun listenToRealSensors() {
        viewModelScope.launch {
            sensorManager.realSteps.collect { steps ->
                _uiState.update { it.copy(realDeviceSteps = steps) }
            }
        }

        healthTimerJob?.cancel()
        healthTimerJob = viewModelScope.launch {
            while (true) {
                delay(60000) // update every minute
                val minutes = sensorManager.getSessionScreenMinutes()
                _uiState.update { it.copy(realActiveMinutes = minutes) }
            }
        }
    }

    private fun loadPersistedMoods() {
        viewModelScope.launch {
            moodDao.getAllMoods().collect { list ->
                val converted = list.map { entity ->
                    MoodEntry(
                        id = entity.id,
                        emoji = entity.emoji,
                        moodName = entity.moodName,
                        timestamp = entity.timestamp,
                        note = entity.note,
                        microTensionScore = entity.microTensionScore
                    )
                }
                _uiState.update { it.copy(moodHistory = converted) }
            }
        }
    }

    fun resetAllDataToZero() {
        viewModelScope.launch {
            moodDao.clearAllMoods()
            sensorManager.resetStepsToZero()
            _uiState.update {
                it.copy(
                    realDeviceSteps = 0,
                    realActiveMinutes = 0,
                    moodHistory = emptyList(),
                    chatMessages = listOf(VibeBotEngine.INITIAL_GREETING),
                    gameState = GameState()
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { 
            it.copy(
                previousScreen = it.currentScreen,
                currentScreen = screen
            ) 
        }
    }

    fun toggleCamera() {
        _uiState.update { 
            val newCamState = !it.isCameraActive
            it.copy(
                isCameraActive = newCamState,
                liveTelemetry = it.liveTelemetry.copy(isCameraOn = newCamState)
            ) 
        }
    }

    fun scanFaceNow() {
        _uiState.update { it.copy(isScanningFaceNow = true, faceScanProgress = 0f) }
        viewModelScope.launch {
            for (i in 1..10) {
                delay(120)
                _uiState.update { it.copy(faceScanProgress = i / 10f) }
            }
            val randomTension = (12..18).random()
            val randomAu4 = (14..19).random()
            val randomAu12 = (65..72).random()
            val randomBlink = (68..74).random()
            _uiState.update { 
                it.copy(
                    isScanningFaceNow = false,
                    faceScanProgress = 1f,
                    liveTelemetry = it.liveTelemetry.copy(
                        microTensionIndex = randomTension,
                        eyeBlinkRhythmRate = randomBlink,
                        au4ForeheadWrinkle = randomAu4,
                        au12SmileMuscle = randomAu12,
                        facialStatus = "Sedikit Lelah • 98% Akurat"
                    )
                ) 
            }
        }
    }

    fun openRegistrationScreen() {
        _uiState.update { 
            it.copy(
                isRegisterTabActive = true,
                currentScreen = AppScreen.LOGIN
            ) 
        }
    }

    fun openLoginScreen() {
        _uiState.update { 
            it.copy(
                isRegisterTabActive = false,
                currentScreen = AppScreen.LOGIN
            ) 
        }
    }

    fun toggleRegisterTab(isRegister: Boolean) {
        _uiState.update { it.copy(isRegisterTabActive = isRegister) }
    }

    fun updateLiveGpsLocation(lat: Double, lng: Double, address: String = "Jakarta Pusat", accuracy: String = "Akurasi Tinggi") {
        _uiState.update {
            it.copy(
                userLatitude = lat,
                userLongitude = lng,
                currentGpsAddress = address,
                currentGpsCoordinates = String.format("%.4f° S, %.4f° E (%s)", lat, lng, accuracy)
            )
        }
    }

    fun refreshGpsLocation() {
        _uiState.update { it.copy(isGpsUpdating = true) }
        gpsManager.requestRealLocation()
        viewModelScope.launch {
            delay(600)
            _uiState.update { it.copy(isGpsUpdating = false) }
        }
    }

    fun loginAsStudent(name: String = "Ahmad Farel", school: String = "SMAN 1 Jakarta", nisn: String = "2026/0491") {
        val finalName = name.ifBlank { "Ahmad Farel" }
        val finalSchool = school.ifBlank { "SMAN 1 Jakarta" }
        val finalNisn = nisn.ifBlank { "2026/0491" }
        _uiState.update { 
            it.copy(
                userName = finalName,
                userSchool = finalSchool,
                userNisn = finalNisn,
                isAnonymousGuest = false,
                isLoggedIn = true,
                isRegisterTabActive = false,
                currentScreen = AppScreen.DASHBOARD
            ) 
        }
    }

    fun registerStudent(name: String, school: String, nisn: String, email: String) {
        val finalName = name.ifBlank { "Ahmad Farel" }
        val finalSchool = school.ifBlank { "SMAN 1 Jakarta" }
        val finalNisn = nisn.ifBlank { "2026/0491" }
        val finalEmail = email.ifBlank { "ahmadfarel272@gmail.com" }
        _uiState.update { 
            it.copy(
                userName = finalName,
                userSchool = finalSchool,
                userNisn = finalNisn,
                userEmail = finalEmail,
                isAnonymousGuest = false,
                isLoggedIn = true,
                isRegisterTabActive = false,
                currentScreen = AppScreen.DASHBOARD
            ) 
        }
    }

    fun loginAsGuest() {
        _uiState.update { 
            it.copy(
                userName = "Siswa Anonim",
                userSchool = "Ruang Terenkripsi",
                isAnonymousGuest = true,
                isLoggedIn = true,
                isRegisterTabActive = false,
                currentScreen = AppScreen.DASHBOARD
            ) 
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                isRegisterTabActive = false,
                currentScreen = AppScreen.LANDING
            )
        }
    }

    fun selectMood(mood: String, customNote: String = "") {
        val emoji = when (mood) {
            "Senang" -> "😊"
            "Tenang" -> "😌"
            "Cemas" -> "😟"
            "Lelah" -> "🥱"
            "Sedih" -> "🌧️"
            else -> "✨"
        }
        val timeFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        val timestamp = timeFormat.format(Date())
        val note = if (customNote.isNotBlank()) customNote else "Mood tercatat di dashboard"

        val entity = MoodEntity(
            id = java.util.UUID.randomUUID().toString(),
            emoji = emoji,
            moodName = mood,
            timestamp = timestamp,
            note = note,
            microTensionScore = _uiState.value.liveTelemetry.microTensionIndex
        )

        viewModelScope.launch {
            moodDao.insertMood(entity)
        }

        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun onCurhatInputChanged(text: String) {
        _uiState.update { it.copy(curhatInputText = text) }
    }

    fun sendCurhatMessage(text: String? = null) {
        val messageText = (text ?: _uiState.value.curhatInputText).trim()
        if (messageText.isEmpty()) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = messageText,
            sensorSnapshot = _uiState.value.liveTelemetry
        )

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMessage,
                curhatInputText = "",
                isAiTyping = true
            )
        }

        viewModelScope.launch {
            delay(900)
            val aiResponse = VibeBotEngine.generateEmpatheticResponse(
                userInput = messageText,
                telemetry = _uiState.value.liveTelemetry,
                userName = _uiState.value.userName
            )
            _uiState.update { 
                it.copy(
                    chatMessages = it.chatMessages + aiResponse,
                    isAiTyping = false
                ) 
            }
        }
    }

    fun startHoldToSpeak() {
        _uiState.update { it.copy(isRecordingSpeech = true, isMicActive = true) }
    }

    fun stopHoldToSpeak(sampleSpeech: String = "Hari ini tugas sekolah numpuk banget, tapi yaudah lah aku baik-baik aja kok, tetep senyum.") {
        _uiState.update { it.copy(isRecordingSpeech = false) }
        sendCurhatMessage(sampleSpeech)
    }

    // Quick 5-Second Expression Tester
    fun startQuickTest() {
        _uiState.update { 
            it.copy(
                isQuickTesting = true, 
                quickTestProgress = 0f, 
                showQuickTestModal = true,
                quickTestResult = null
            ) 
        }
        viewModelScope.launch {
            for (i in 1..20) {
                delay(250)
                _uiState.update { it.copy(quickTestProgress = i / 20f) }
            }
            val result = QuickTestResult(
                tensionScore = 17,
                isSmilingDepressionDetected = true,
                smileAuthenticity = 67,
                statusSummary = "Deteksi Smiling Depression: Suppressed",
                recommendation = "Hasil scan menunjukkan AU4 Kerutan Dahi 17% & AU12 Senyum Tertahan 67%. Jangan sungkan curhat ya!"
            )
            _uiState.update { 
                it.copy(
                    isQuickTesting = false,
                    quickTestResult = result
                ) 
            }
        }
    }

    fun dismissQuickTestModal() {
        _uiState.update { it.copy(showQuickTestModal = false) }
    }

    // Stress Games Switch
    fun selectGameMode(mode: StressGameMode) {
        _uiState.update { 
            it.copy(
                gameState = it.gameState.copy(
                    activeMode = mode,
                    isPlaying = true
                )
            ) 
        }
    }

    // --- XOX (Tic-Tac-Toe) Logic ---
    fun playXoxMove(index: Int) {
        val currXox = _uiState.value.gameState.xoxState
        if (currXox.board[index].isNotEmpty() || currXox.winner != null || !currXox.isUserTurn) return

        val newBoard = currXox.board.toMutableList()
        newBoard[index] = "X"

        val userWon = checkWinner(newBoard, "X")
        val isFull = newBoard.none { it.isEmpty() }

        if (userWon) {
            _uiState.update { state ->
                val newScore = state.gameState.score + 100
                val newStress = (state.gameState.stressBiomarker - 15).coerceAtLeast(10)
                state.copy(
                    gameState = state.gameState.copy(
                        score = newScore,
                        stressBiomarker = newStress,
                        xoxState = currXox.copy(
                            board = newBoard,
                            winner = "X",
                            scoreUser = currXox.scoreUser + 1
                        )
                    )
                )
            }
            return
        }

        if (isFull) {
            _uiState.update { state ->
                state.copy(
                    gameState = state.gameState.copy(
                        xoxState = currXox.copy(board = newBoard, winner = "DRAW")
                    )
                )
            }
            return
        }

        // VibeBot AI Turn
        _uiState.update { state ->
            state.copy(
                gameState = state.gameState.copy(
                    xoxState = currXox.copy(board = newBoard, isUserTurn = false)
                )
            )
        }

        viewModelScope.launch {
            delay(500)
            val emptyIndices = newBoard.indices.filter { newBoard[it].isEmpty() }
            if (emptyIndices.isNotEmpty()) {
                val aiIndex = emptyIndices.random()
                newBoard[aiIndex] = "O"
                val aiWon = checkWinner(newBoard, "O")
                val isDraw = newBoard.none { it.isEmpty() }

                _uiState.update { state ->
                    state.copy(
                        gameState = state.gameState.copy(
                            xoxState = currXox.copy(
                                board = newBoard,
                                isUserTurn = true,
                                winner = if (aiWon) "O" else if (isDraw) "DRAW" else null,
                                scoreAi = if (aiWon) currXox.scoreAi + 1 else currXox.scoreAi
                            )
                        )
                    )
                }
            }
        }
    }

    fun resetXoxGame() {
        _uiState.update { state ->
            state.copy(
                gameState = state.gameState.copy(
                    xoxState = state.gameState.xoxState.copy(
                        board = List(9) { "" },
                        isUserTurn = true,
                        winner = null
                    )
                )
            )
        }
    }

    private fun checkWinner(board: List<String>, player: String): Boolean {
        val wins = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return wins.any { line -> line.all { board[it] == player } }
    }

    // --- Tetris / Block Clear logic ---
    fun clearTetrisBlock() {
        _uiState.update { state ->
            val curr = state.gameState.tetrisState
            val newCleared = curr.linesCleared + 1
            val newScore = curr.score + 120
            val newStress = (state.gameState.stressBiomarker - 10).coerceAtLeast(10)
            state.copy(
                gameState = state.gameState.copy(
                    score = state.gameState.score + 120,
                    stressBiomarker = newStress,
                    tetrisState = curr.copy(
                        score = newScore,
                        linesCleared = newCleared
                    )
                )
            )
        }
    }

    // Voice Pitch Jump Game
    fun triggerSmileBoost() {
        _uiState.update { state ->
            val curr = state.gameState
            val newSmile = (curr.smileIntensity + 25).coerceAtMost(100)
            val newStress = (curr.stressBiomarker - 12).coerceAtLeast(10)
            val newScore = curr.score + 150
            val newStars = curr.starsCollected + 1
            val isCompleted = newStress <= 15
            state.copy(
                gameState = curr.copy(
                    smileIntensity = newSmile,
                    stressBiomarker = newStress,
                    score = newScore,
                    starsCollected = newStars,
                    isCompleted = isCompleted
                )
            )
        }
    }

    fun triggerVoicePitchJump() {
        _uiState.update { state ->
            val curr = state.gameState
            val newPitch = (curr.voicePitchLevel + 30).coerceAtMost(100)
            val newStress = (curr.stressBiomarker - 8).coerceAtLeast(10)
            val newScore = curr.score + 100
            val newStars = curr.starsCollected + 1
            val isCompleted = newStress <= 15
            state.copy(
                gameState = curr.copy(
                    voicePitchLevel = newPitch,
                    stressBiomarker = newStress,
                    score = newScore,
                    starsCollected = newStars,
                    playerY = 0.2f,
                    isCompleted = isCompleted
                )
            )
        }
    }

    // Directory category & booking
    fun selectCategory(cat: DirectoryCategory) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun onSearchQueryChanged(q: String) {
        _uiState.update { it.copy(searchQuery = q) }
    }

    fun openBookingDialog(item: DirectoryItem) {
        _uiState.update { 
            it.copy(
                selectedDirectoryItem = item,
                showBookingDialog = true
            ) 
        }
    }

    fun dismissBookingDialog() {
        _uiState.update { 
            it.copy(
                selectedDirectoryItem = null,
                showBookingDialog = false
            ) 
        }
    }

    private fun startLiveTelemetrySimulation() {
        telemetryJob?.cancel()
        telemetryJob = viewModelScope.launch {
            var tick = 0
            while (true) {
                delay(1500)
                tick++
                val tension = if (tick % 5 == 0) (35..45).random() else (12..16).random()
                val blink = (68..74).random()
                val au4 = if (tick % 5 == 0) 28 else 17
                val au12 = 67
                val facialLabel = "Sedikit Lelah • 95% Akurat"

                _uiState.update { state ->
                    state.copy(
                        liveTelemetry = state.liveTelemetry.copy(
                            microTensionIndex = tension,
                            eyeBlinkRhythmRate = blink,
                            au4ForeheadWrinkle = au4,
                            au12SmileMuscle = au12,
                            facialStatus = facialLabel,
                            pitchCadence = 84,
                            pitchIntonation = "Normal",
                            pitchPauseSeconds = 0.4f,
                            acousticStress = 0.14f
                        )
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetryJob?.cancel()
        gameLoopJob?.cancel()
        healthTimerJob?.cancel()
        sensorManager.unregister()
        gpsManager.stopLocationUpdates()
    }
}
