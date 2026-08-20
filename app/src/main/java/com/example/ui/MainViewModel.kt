package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AdBannerEntity
import com.example.data.local.entity.AdminSettingsEntity
import com.example.data.local.entity.SubscriptionCardEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.AiDifficulty
import com.example.domain.model.CardRank
import com.example.domain.model.CardSkinTheme
import com.example.domain.model.CardSuit
import com.example.domain.model.GamePlayer
import com.example.domain.model.HintMessage
import com.example.domain.model.PlayerTeam
import com.example.domain.model.PlayingCard
import com.example.domain.model.RoundScoreBreakdown
import com.example.game.ChkobbaEngine
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

enum class AppScreen {
    AUTH,
    GAME_HUB,
    GAME_TABLE,
    WINNERS,
    ADMIN_PANEL,
    ADMIN_WEB_PORTAL
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val cardDao = db.subscriptionCardDao()
    private val adDao = db.adBannerDao()
    private val adminDao = db.adminSettingsDao()
    private val chkobbaEngine = ChkobbaEngine()
    private val soundManager = SoundManager

    // Navigation & Screen State
    private val _currentScreen = MutableStateFlow(AppScreen.AUTH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Sound & Audio State
    private val _isSoundMuted = MutableStateFlow(false)
    val isSoundMuted: StateFlow<Boolean> = _isSoundMuted.asStateFlow()

    // AI Difficulty State
    private val _aiDifficulty = MutableStateFlow(AiDifficulty.MEDIUM)
    val aiDifficulty: StateFlow<AiDifficulty> = _aiDifficulty.asStateFlow()

    // User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eligibleWinners: StateFlow<List<UserEntity>> = userDao.getEligibleWinners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Subscription Cards State
    val allCards: StateFlow<List<SubscriptionCardEntity>> = cardDao.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val usedCardsCount: StateFlow<Int> = cardDao.getUsedCardsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val availableCardsCount: StateFlow<Int> = cardDao.getAvailableCardsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Ad Banners State
    val allBanners: StateFlow<List<AdBannerEntity>> = adDao.getAllBanners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Settings State
    val adminSettings: StateFlow<AdminSettingsEntity?> = adminDao.getAdminSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Game Table & Active Match State
    private val _tableCards = MutableStateFlow<List<PlayingCard>>(emptyList())
    val tableCards: StateFlow<List<PlayingCard>> = _tableCards.asStateFlow()

    private val _players = MutableStateFlow<List<GamePlayer>>(emptyList())
    val players: StateFlow<List<GamePlayer>> = _players.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    val currentTurnIndex: StateFlow<Int> = _currentTurnIndex.asStateFlow()

    private val _remainingDeck = MutableStateFlow<List<PlayingCard>>(emptyList())
    val remainingDeck: StateFlow<List<PlayingCard>> = _remainingDeck.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _targetChawat = MutableStateFlow(1) // 1 or 3
    val targetChawat: StateFlow<Int> = _targetChawat.asStateFlow()

    private val _chawatTeamAWins = MutableStateFlow(0)
    val chawatTeamAWins: StateFlow<Int> = _chawatTeamAWins.asStateFlow()

    private val _chawatTeamBWins = MutableStateFlow(0)
    val chawatTeamBWins: StateFlow<Int> = _chawatTeamBWins.asStateFlow()

    private val _roundScoreHistory = MutableStateFlow<List<RoundScoreBreakdown>>(emptyList())
    val roundScoreHistory: StateFlow<List<RoundScoreBreakdown>> = _roundScoreHistory.asStateFlow()

    private val _selectedHandCard = MutableStateFlow<PlayingCard?>(null)
    val selectedHandCard: StateFlow<PlayingCard?> = _selectedHandCard.asStateFlow()

    private val _possibleCaptures = MutableStateFlow<List<List<PlayingCard>>>(emptyList())
    val possibleCaptures: StateFlow<List<List<PlayingCard>>> = _possibleCaptures.asStateFlow()

    // UI Features: Zoom 200%, Teammate Reveal, Hints, Turn Timer, Chkobba popup
    private val _isZoomed200 = MutableStateFlow(false)
    val isZoomed200: StateFlow<Boolean> = _isZoomed200.asStateFlow()

    private val _teammateHandRevealed = MutableStateFlow<List<PlayingCard>?>(null)
    val teammateHandRevealed: StateFlow<List<PlayingCard>?> = _teammateHandRevealed.asStateFlow()

    private val _activeHintMessage = MutableStateFlow<HintMessage?>(null)
    val activeHintMessage: StateFlow<HintMessage?> = _activeHintMessage.asStateFlow()

    private val _chkobbaClaimActive = MutableStateFlow<Pair<String, Int>?>(null) // Player Name, Points
    val chkobbaClaimActive: StateFlow<Pair<String, Int>?> = _chkobbaClaimActive.asStateFlow()

    private val _turnSecondsRemaining = MutableStateFlow(20)
    val turnSecondsRemaining: StateFlow<Int> = _turnSecondsRemaining.asStateFlow()

    private val _isGameOverDialogVisible = MutableStateFlow(false)
    val isGameOverDialogVisible: StateFlow<Boolean> = _isGameOverDialogVisible.asStateFlow()

    private val _gameStatusBanner = MutableStateFlow<String>("جاري اللعب")
    val gameStatusBanner: StateFlow<String> = _gameStatusBanner.asStateFlow()

    private var turnTimerJob: Job? = null
    private var lastCapturingPlayerIndex: Int? = null

    init {
        viewModelScope.launch {
            AppDatabase.populateInitialData(db)
            // Auto login first user or guest for fast play
            val users = userDao.getUserById(1)
            if (users != null) {
                _currentUser.value = users
            }
        }
    }

    // -------------------------------------------------------------
    // NAVIGATION
    // -------------------------------------------------------------
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // -------------------------------------------------------------
    // AUTHENTICATION & SUBSCRIPTIONS
    // -------------------------------------------------------------
    fun loginOrRegister(fullName: String, phone: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (phone.isBlank() || fullName.isBlank()) {
                onError("يرجى ملء جميع الحقول المطلوبة")
                return@launch
            }
            val existing = userDao.getUserByPhone(phone.trim())
            if (existing != null) {
                _currentUser.value = existing
                _currentScreen.value = AppScreen.GAME_HUB
                onSuccess()
            } else {
                val settings = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
                val trialDays = settings.guestTrialDays.coerceAtLeast(1).toLong()
                val newUser = UserEntity(
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    password = pass,
                    isPaid = false,
                    isGuest = false,
                    subscriptionExpiryTimestamp = System.currentTimeMillis() + (trialDays * 24 * 60 * 60 * 1000)
                )
                val newId = userDao.insertUser(newUser)
                _currentUser.value = newUser.copy(id = newId)
                _currentScreen.value = AppScreen.GAME_HUB
                onSuccess()
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            val settings = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
            val trialDays = settings.guestTrialDays.coerceAtLeast(1).toLong()
            val guest = UserEntity(
                fullName = "زائر ${Random().nextInt(900) + 100}",
                phone = "0900000000",
                password = "",
                isPaid = false,
                isGuest = true,
                subscriptionExpiryTimestamp = System.currentTimeMillis() + (trialDays * 24 * 60 * 60 * 1000)
            )
            val id = userDao.insertUser(guest)
            _currentUser.value = guest.copy(id = id)
            _currentScreen.value = AppScreen.GAME_HUB
        }
    }

    fun activateSubscriptionCard(rawCardCode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanCode = rawCardCode.trim().replace(" ", "").replace("-", "")
            // Format to XXX-XXX-XXX-XXXX
            val formatted = if (cleanCode.length == 13) {
                "${cleanCode.substring(0, 3)}-${cleanCode.substring(3, 6)}-${cleanCode.substring(6, 9)}-${cleanCode.substring(9, 13)}"
            } else {
                rawCardCode.trim()
            }

            val card = cardDao.getCardByCode(formatted)
            if (card == null) {
                onResult(false, "كود الكارت غير صحيح أو غير موجود")
                return@launch
            }
            if (card.isUsed) {
                onResult(false, "هذا الكارت تم استخدامه مسبقاً")
                return@launch
            }

            val user = _currentUser.value
            if (user == null) {
                onResult(false, "يرجى تسجيل الدخول أولاً")
                return@launch
            }

            val addedDuration = card.durationDays.toLong() * 24 * 60 * 60 * 1000
            val currentExpiry = maxOf(System.currentTimeMillis(), user.subscriptionExpiryTimestamp)
            val newExpiry = currentExpiry + addedDuration

            val updatedUser = user.copy(
                isPaid = true,
                cardCodeUsed = card.cardCode,
                subscriptionExpiryTimestamp = newExpiry
            )
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser

            val updatedCard = card.copy(
                isUsed = true,
                usedByUserId = user.id,
                usedByUserName = user.fullName,
                usedByUserPhone = user.phone,
                usedTimestamp = System.currentTimeMillis()
            )
            cardDao.updateCard(updatedCard)

            onResult(true, "تم تفعيل الاشتراك بنجاح! الفئة: ${card.category}")
        }
    }

    // -------------------------------------------------------------
    // CHKOBBA GAMEPLAY ENGINE & SOUND INTEGRATION
    // -------------------------------------------------------------
    fun toggleSoundMute() {
        val newMuted = !_isSoundMuted.value
        _isSoundMuted.value = newMuted
        soundManager.setSoundEnabled(!newMuted)
    }

    fun setCardSkinTheme(theme: CardSkinTheme) {
        viewModelScope.launch {
            val current = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
            val updated = current.copy(cardSkin = theme.id)
            adminDao.insertAdminSettings(updated)
            soundManager.playClick()
        }
    }

    fun startNewGame(
        numPlayers: Int,
        chawat: Int,
        timerSeconds: Int,
        difficulty: AiDifficulty = _aiDifficulty.value
    ) {
        _aiDifficulty.value = difficulty
        _targetChawat.value = chawat
        _chawatTeamAWins.value = 0
        _chawatTeamBWins.value = 0
        _currentRound.value = 1
        _roundScoreHistory.value = emptyList()
        _isGameOverDialogVisible.value = false
        _isZoomed200.value = false
        _teammateHandRevealed.value = null
        _activeHintMessage.value = null
        _selectedHandCard.value = null
        _possibleCaptures.value = emptyList()
        lastCapturingPlayerIndex = null

        setupPlayersAndDeal(numPlayers, timerSeconds)
        soundManager.playCardDeal()
        _currentScreen.value = AppScreen.GAME_TABLE
    }

    private fun setupPlayersAndDeal(numPlayers: Int, timerSeconds: Int) {
        lastCapturingPlayerIndex = null
        val user = _currentUser.value
        val userName = user?.fullName?.ifBlank { "أنت" } ?: "أنت"

        val playerList = mutableListOf<GamePlayer>()
        when (numPlayers) {
            2 -> {
                // 1v1: كل واحد في فريق
                playerList.add(GamePlayer(0, userName, true, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(1, "الخصم (الذكاء الاصطناعي)", false, PlayerTeam.TEAM_B))
            }
            4 -> {
                // 4 لاعبين: كل اثنين في فريق (2v2)
                playerList.add(GamePlayer(0, userName, true, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(1, "الخصم 1", false, PlayerTeam.TEAM_B))
                playerList.add(GamePlayer(2, "زميلك (فريق أ)", false, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(3, "الخصم 2", false, PlayerTeam.TEAM_B))
            }
            6 -> {
                // 6 لاعبين: كل ثلاثة في فريق (3v3)
                playerList.add(GamePlayer(0, userName, true, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(1, "الخصم 1", false, PlayerTeam.TEAM_B))
                playerList.add(GamePlayer(2, "زميلك 1 (فريق أ)", false, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(3, "الخصم 2", false, PlayerTeam.TEAM_B))
                playerList.add(GamePlayer(4, "زميلك 2 (فريق أ)", false, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(5, "الخصم 3", false, PlayerTeam.TEAM_B))
            }
            else -> {
                playerList.add(GamePlayer(0, userName, true, PlayerTeam.TEAM_A))
                playerList.add(GamePlayer(1, "الخصم", false, PlayerTeam.TEAM_B))
            }
        }

        val deck = chkobbaEngine.generateDeck().toMutableList()
        // Deal 4 cards to table
        val table = mutableListOf<PlayingCard>()
        for (i in 0 until 4) {
            if (deck.isNotEmpty()) table.add(deck.removeAt(0))
        }
        _tableCards.value = table

        // Deal 3 cards to each player
        val updatedPlayers = playerList.map { p ->
            val hand = mutableListOf<PlayingCard>()
            for (i in 0 until 3) {
                if (deck.isNotEmpty()) hand.add(deck.removeAt(0))
            }
            p.copy(hand = hand)
        }

        _players.value = updatedPlayers
        _remainingDeck.value = deck
        _currentTurnIndex.value = 0
        _gameStatusBanner.value = "دور: ${updatedPlayers[0].name}"

        startTurnTimer(timerSeconds)
    }

    private fun startTurnTimer(timerSeconds: Int) {
        turnTimerJob?.cancel()
        if (timerSeconds <= 0) {
            _turnSecondsRemaining.value = 0 // unlimited
            return
        }

        _turnSecondsRemaining.value = timerSeconds
        turnTimerJob = viewModelScope.launch {
            while (_turnSecondsRemaining.value > 0) {
                delay(1000)
                _turnSecondsRemaining.value -= 1
            }
            // Auto play when timer runs out
            onTurnTimeout()
        }
    }

    private fun onTurnTimeout() {
        val playersList = _players.value
        val currentIndex = _currentTurnIndex.value
        if (currentIndex !in playersList.indices) return

        val currentPlayer = playersList[currentIndex]
        if (currentPlayer.hand.isNotEmpty()) {
            val (card, capture) = chkobbaEngine.chooseBestAiMove(
                currentPlayer,
                _tableCards.value,
                _remainingDeck.value.isEmpty() && playersList.all { it.hand.size <= 1 },
                _aiDifficulty.value,
                _activeHintMessage.value
            )
            executeMove(currentPlayer.id, card, capture)
        }
    }

    fun selectHandCard(card: PlayingCard) {
        if (_currentTurnIndex.value != 0) return // Only human user can click hand cards
        val userPlayer = _players.value.firstOrNull() ?: return
        if (!userPlayer.hand.contains(card)) return

        soundManager.playClick()
        _selectedHandCard.value = card
        val captures = chkobbaEngine.findValidCaptures(card, _tableCards.value)
        _possibleCaptures.value = captures
    }

    fun playSelectedCardDirectly() {
        val card = _selectedHandCard.value ?: return
        val userPlayer = _players.value.firstOrNull() ?: return
        val captures = _possibleCaptures.value

        if (captures.isEmpty()) {
            // No capture possible, just play card to table
            executeMove(userPlayer.id, card, null)
        } else {
            // Take the best or first capture combination
            executeMove(userPlayer.id, card, captures.first())
        }
    }

    fun playCardWithCapture(captureCombination: List<PlayingCard>) {
        val card = _selectedHandCard.value ?: return
        val userPlayer = _players.value.firstOrNull() ?: return
        executeMove(userPlayer.id, card, captureCombination)
    }

    private fun executeMove(playerId: Int, card: PlayingCard, capturedCards: List<PlayingCard>?) {
        turnTimerJob?.cancel()
        _selectedHandCard.value = null
        _possibleCaptures.value = emptyList()

        val playersList = _players.value.toMutableList()
        val playerIndex = playersList.indexOfFirst { it.id == playerId }
        if (playerIndex == -1) return

        val player = playersList[playerIndex]
        val newHand = player.hand.filter { it.id != card.id }
        val currentTable = _tableCards.value.toMutableList()

        var isChkobba = false
        var chkobbaPts = 0
        val newCaptured = player.capturedCards.toMutableList()

        if (capturedCards != null && capturedCards.isNotEmpty()) {
            // Capture cards from table + played card
            currentTable.removeAll { c -> capturedCards.any { it.id == c.id } }
            newCaptured.addAll(capturedCards)
            newCaptured.add(card)
            lastCapturingPlayerIndex = playerIndex

            // Check if Chkobba (cleared table to 0 and not end of full deck)
            val isEndOfGame = _remainingDeck.value.isEmpty() && playersList.all { it.hand.size <= 1 }
            if (currentTable.isEmpty() && !isEndOfGame) {
                isChkobba = true
                chkobbaPts = card.rank.value
                _chkobbaClaimActive.value = Pair(player.name, chkobbaPts)
                _gameStatusBanner.value = "🔥 شكبة لـ ${player.name} بقيمة $chkobbaPts نقطة!"
                soundManager.playChkobbaFanfare()
            } else {
                _gameStatusBanner.value = "${player.name} أكل ${capturedCards.size} ورقة"
                soundManager.playCapture()
            }
        } else {
            // Throw card to table
            currentTable.add(card)
            _gameStatusBanner.value = "${player.name} رمى ${card.displayName}"
            soundManager.playCardPlay()
        }

        val updatedPlayer = player.copy(
            hand = newHand,
            capturedCards = newCaptured,
            chkobbaCount = player.chkobbaCount + (if (isChkobba) 1 else 0),
            chkobbaPoints = player.chkobbaPoints + chkobbaPts
        )
        playersList[playerIndex] = updatedPlayer
        _players.value = playersList
        _tableCards.value = currentTable

        // Advance Turn
        advanceToNextTurn()
    }

    private fun advanceToNextTurn() {
        val playersList = _players.value
        // Check if all players hands are empty
        val allHandsEmpty = playersList.all { it.hand.isEmpty() }
        if (allHandsEmpty) {
            val deck = _remainingDeck.value.toMutableList()
            if (deck.isNotEmpty()) {
                // Deal 3 more cards to each player
                soundManager.playCardDeal()
                val updatedPlayers = playersList.map { p ->
                    val hand = mutableListOf<PlayingCard>()
                    for (i in 0 until 3) {
                        if (deck.isNotEmpty()) hand.add(deck.removeAt(0))
                    }
                    p.copy(hand = hand)
                }
                _players.value = updatedPlayers
                _remainingDeck.value = deck
                _currentTurnIndex.value = 0
                _gameStatusBanner.value = "تم توزيع 3 ورقات جديدة - دور: ${updatedPlayers[0].name}"
                checkNextTurnExecution()
            } else {
                // Deck is empty, round is complete!
                endCurrentRound()
            }
        } else {
            val nextIndex = (_currentTurnIndex.value + 1) % playersList.size
            _currentTurnIndex.value = nextIndex
            _gameStatusBanner.value = "دور: ${playersList[nextIndex].name}"
            checkNextTurnExecution()
        }
    }

    private fun checkNextTurnExecution() {
        val currentIdx = _currentTurnIndex.value
        val playersList = _players.value
        if (currentIdx in playersList.indices) {
            val nextPlayer = playersList[currentIdx]
            val timerSetting = adminSettings.value?.defaultTurnTimerSeconds ?: 20
            startTurnTimer(timerSetting)

            if (!nextPlayer.isHuman) {
                // AI Turn
                viewModelScope.launch {
                    delay(1100) // Realistic bot think delay
                    if (_currentTurnIndex.value == currentIdx && nextPlayer.hand.isNotEmpty()) {
                        val (aiCard, aiCapture) = chkobbaEngine.chooseBestAiMove(
                            nextPlayer,
                            _tableCards.value,
                            _remainingDeck.value.isEmpty() && playersList.all { it.hand.size <= 1 },
                            _aiDifficulty.value,
                            _activeHintMessage.value
                        )
                        executeMove(nextPlayer.id, aiCard, aiCapture)
                    }
                }
            }
        }
    }

    private fun endCurrentRound() {
        // Last player with capture takes remaining table cards
        val playersList = _players.value.toMutableList()
        val remainingTable = _tableCards.value
        var sweepSummary: String? = null

        if (remainingTable.isNotEmpty()) {
            val lastCaptorIdx = lastCapturingPlayerIndex ?: 0
            if (lastCaptorIdx in playersList.indices) {
                val captor = playersList[lastCaptorIdx]
                val newCaptured = captor.capturedCards + remainingTable
                playersList[lastCaptorIdx] = captor.copy(capturedCards = newCaptured)
                _players.value = playersList
                _tableCards.value = emptyList()

                sweepSummary = "أضيفت ${remainingTable.size} ورقة متبقية على الطاولة إلى أوراق آخر من أكل (${captor.name})"
                _gameStatusBanner.value = "نهاية الجولة: $sweepSummary"
            }
        }

        // Calculate Round Score
        val teamAPlayers = playersList.filter { it.team == PlayerTeam.TEAM_A }
        val teamBPlayers = playersList.filter { it.team == PlayerTeam.TEAM_B }

        val breakdown = chkobbaEngine.calculateRoundScore(
            _currentRound.value,
            teamAPlayers,
            teamBPlayers,
            sweepSummary
        )
        val history = _roundScoreHistory.value.toMutableList()
        history.add(breakdown)
        _roundScoreHistory.value = history

        // Determine round winner
        val teamAWon = breakdown.teamATotalRoundScore >= breakdown.teamBTotalRoundScore
        if (breakdown.teamATotalRoundScore > breakdown.teamBTotalRoundScore) {
            _chawatTeamAWins.value += 1
            soundManager.playVictory()
        } else if (breakdown.teamBTotalRoundScore > breakdown.teamATotalRoundScore) {
            _chawatTeamBWins.value += 1
            soundManager.playDefeat()
        } else {
            soundManager.playVictory()
        }

        // Update User stats in Room DB
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val updated = user.copy(
                    gamesWonRounds = user.gamesWonRounds + (if (teamAWon) 1 else 0),
                    gamesLostRounds = user.gamesLostRounds + (if (!teamAWon) 1 else 0)
                )
                userDao.updateUser(updated)
                _currentUser.value = updated
            }
        }

        _isGameOverDialogVisible.value = true
    }

    fun continueToNextRoundOrFinish() {
        _isGameOverDialogVisible.value = false
        val neededWins = if (_targetChawat.value == 3) 2 else 1
        if (_chawatTeamAWins.value >= neededWins || _chawatTeamBWins.value >= neededWins) {
            // Match Finished! Update Chawat stats
            viewModelScope.launch {
                val user = _currentUser.value
                if (user != null) {
                    val userWonMatch = _chawatTeamAWins.value > _chawatTeamBWins.value
                    val updated = user.copy(
                        gamesWonChawat = user.gamesWonChawat + (if (userWonMatch) 1 else 0),
                        gamesLostChawat = user.gamesLostChawat + (if (!userWonMatch) 1 else 0)
                    )
                    userDao.updateUser(updated)
                    _currentUser.value = updated
                }
            }
            _currentScreen.value = AppScreen.GAME_HUB
        } else {
            // Next round
            _currentRound.value += 1
            val timerSetting = adminSettings.value?.defaultTurnTimerSeconds ?: 20
            setupPlayersAndDeal(_players.value.size, timerSetting)
        }
    }

    // Interactive Chkobba Claim Button
    fun claimChkobba() {
        _chkobbaClaimActive.value = null
    }

    fun dismissChkobbaClaim() {
        _chkobbaClaimActive.value = null
    }

    // Zoom 200% Toggle
    fun toggleZoom200() {
        _isZoomed200.value = !_isZoomed200.value
    }

    // Teammate Reveal (1 use per round)
    fun revealTeammateCards() {
        val userPlayer = _players.value.firstOrNull() ?: return
        if (userPlayer.hasUsedPartnerRevealThisRound) return

        val teammates = _players.value.filter { it.team == PlayerTeam.TEAM_A && it.id != userPlayer.id }
        if (teammates.isNotEmpty()) {
            _teammateHandRevealed.value = teammates.flatMap { it.hand }
            // Mark as used
            val updatedPlayers = _players.value.map {
                if (it.id == userPlayer.id) it.copy(hasUsedPartnerRevealThisRound = true) else it
            }
            _players.value = updatedPlayers
        }
    }

    fun closeTeammateCardsDialog() {
        _teammateHandRevealed.value = null
    }

    // Teammate Hint System ("طلب ورقة من الزميل")
    fun requestCardHintFromPartner(rank: CardRank, suit: CardSuit? = null) {
        val userPlayer = _players.value.firstOrNull() ?: return
        _activeHintMessage.value = HintMessage(userPlayer.name, rank, suit)
    }

    fun dismissHint() {
        _activeHintMessage.value = null
    }

    // -------------------------------------------------------------
    // ADMIN PANEL OPERATIONS
    // -------------------------------------------------------------
    fun loginAdmin(user: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val settings = adminDao.getAdminSettingsOnce()
            val expectedUser = settings?.adminUsername ?: "uas"
            val expectedPass = settings?.adminPassword ?: "6090081"

            if (user.trim() == expectedUser && pass.trim() == expectedPass) {
                _isAdminLoggedIn.value = true
                _currentScreen.value = AppScreen.ADMIN_PANEL
                onSuccess()
            } else {
                onError("اسم المستخدم أو كلمة المرور غير صحيحة")
            }
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _currentScreen.value = AppScreen.GAME_HUB
    }

    fun updateAdminCredentials(newUsername: String, newPass: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val current = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
            val updated = current.copy(adminUsername = newUsername.trim(), adminPassword = newPass.trim())
            adminDao.insertAdminSettings(updated)
            onComplete()
        }
    }

    fun updateGameAndTimerSettings(timerSeconds: Int, cardSkin: String) {
        viewModelScope.launch {
            val current = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
            val updated = current.copy(defaultTurnTimerSeconds = timerSeconds, cardSkin = cardSkin)
            adminDao.insertAdminSettings(updated)
        }
    }

    fun updateGuestTrialDays(days: Int) {
        viewModelScope.launch {
            val current = adminDao.getAdminSettingsOnce() ?: AdminSettingsEntity()
            val safeDays = days.coerceAtLeast(1)
            val updated = current.copy(guestTrialDays = safeDays)
            adminDao.insertAdminSettings(updated)
        }
    }

    fun updateAdBanner(banner: AdBannerEntity) {
        viewModelScope.launch {
            adDao.updateBanner(banner)
        }
    }

    fun toggleWinnerInclusion(userId: Long, isIncluded: Boolean) {
        viewModelScope.launch {
            userDao.updateDrawInclusion(userId, isIncluded)
        }
    }

    fun toggleAllWinnersInclusion(isIncluded: Boolean) {
        viewModelScope.launch {
            userDao.updateAllDrawInclusion(isIncluded)
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            userDao.deleteUser(userId)
        }
    }

    fun extendUserSubscription(userId: Long, days: Int) {
        viewModelScope.launch {
            val user = userDao.getUserById(userId) ?: return@launch
            val newExpiry = maxOf(System.currentTimeMillis(), user.subscriptionExpiryTimestamp) + (days.toLong() * 24 * 60 * 60 * 1000)
            userDao.updateUser(user.copy(isPaid = true, subscriptionExpiryTimestamp = newExpiry))
        }
    }

    fun generateSubscriptionCards(category: String, quantity: Int, customCode: String? = null) {
        viewModelScope.launch {
            val (price, days) = when {
                category.contains("5") -> Pair(5, 180)
                category.contains("10") -> Pair(10, 365)
                else -> Pair(1, 30)
            }

            val random = Random()
            val newCards = mutableListOf<SubscriptionCardEntity>()

            if (!customCode.isNullOrBlank()) {
                val clean = customCode.replace("-", "").trim()
                val formatted = if (clean.length == 13) {
                    "${clean.substring(0, 3)}-${clean.substring(3, 6)}-${clean.substring(6, 9)}-${clean.substring(9, 13)}"
                } else customCode
                newCards.add(
                    SubscriptionCardEntity(
                        cardCode = formatted,
                        category = category,
                        priceDinars = price,
                        durationDays = days
                    )
                )
            } else {
                for (i in 0 until quantity) {
                    val p1 = String.format(Locale.US, "%03d", random.nextInt(1000))
                    val p2 = String.format(Locale.US, "%03d", random.nextInt(1000))
                    val p3 = String.format(Locale.US, "%03d", random.nextInt(1000))
                    val p4 = String.format(Locale.US, "%04d", random.nextInt(10000))
                    val code = "$p1-$p2-$p3-$p4"
                    newCards.add(
                        SubscriptionCardEntity(
                            cardCode = code,
                            category = category,
                            priceDinars = price,
                            durationDays = days
                        )
                    )
                }
            }
            cardDao.insertCards(newCards)
        }
    }

    fun generateExcelCsvData(winners: List<UserEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,الاسم الثلاثي,رقم الهاتف,الجولات الفائزة,الجولات الخاسرة,الأشواط الفائزة,الأشواط الخاسرة,رقم الكارت,تاريخ التسجيل\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        for (w in winners) {
            sb.append("${w.id},\"${w.fullName}\",\"${w.phone}\",${w.gamesWonRounds},${w.gamesLostRounds},${w.gamesWonChawat},${w.gamesLostChawat},\"${w.cardCodeUsed ?: "غير متوفر"}\",\"${sdf.format(Date(w.registeredTimestamp))}\"\n")
        }
        return sb.toString()
    }
}
