package com.example.ui.screens.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.CardRank
import com.example.domain.model.CardSkinTheme
import com.example.domain.model.CardSuit
import com.example.domain.model.GamePlayer
import com.example.domain.model.PlayerTeam
import com.example.domain.model.PlayingCard
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AdBannerComponent
import com.example.ui.components.CardBackView
import com.example.ui.components.CardCustomizationDialog
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CasinoBorder
import com.example.ui.theme.CasinoDockBg
import com.example.ui.theme.CasinoFeltCenter
import com.example.ui.theme.CasinoFeltDark
import com.example.ui.theme.CasinoFeltDeep
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGreenAccent
import com.example.ui.theme.CasinoGreenBadge
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenMint
import com.example.ui.theme.CasinoHeaderBg
import com.example.ui.theme.CasinoRed
import com.example.ui.theme.CasinoRedDark
import java.util.concurrent.TimeUnit

@Composable
fun GameTableScreen(viewModel: MainViewModel) {
    val tableCards by viewModel.tableCards.collectAsState()
    val players by viewModel.players.collectAsState()
    val currentTurnIdx by viewModel.currentTurnIndex.collectAsState()
    val remainingDeck by viewModel.remainingDeck.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val targetChawat by viewModel.targetChawat.collectAsState()
    val chawatTeamAWins by viewModel.chawatTeamAWins.collectAsState()
    val chawatTeamBWins by viewModel.chawatTeamBWins.collectAsState()
    val scoreHistory by viewModel.roundScoreHistory.collectAsState()
    val selectedHandCard by viewModel.selectedHandCard.collectAsState()
    val possibleCaptures by viewModel.possibleCaptures.collectAsState()
    val isZoomed200 by viewModel.isZoomed200.collectAsState()
    val teammateHandRevealed by viewModel.teammateHandRevealed.collectAsState()
    val activeHintMessage by viewModel.activeHintMessage.collectAsState()
    val chkobbaClaimActive by viewModel.chkobbaClaimActive.collectAsState()
    val turnSeconds by viewModel.turnSecondsRemaining.collectAsState()
    val isGameOverDialogVisible by viewModel.isGameOverDialogVisible.collectAsState()
    val gameStatusBanner by viewModel.gameStatusBanner.collectAsState()
    val adminSettings by viewModel.adminSettings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSoundMuted by viewModel.isSoundMuted.collectAsState()
    val banners by viewModel.allBanners.collectAsState()
    val banner3 = banners.firstOrNull { it.id == 3 }

    var showHintDialog by remember { mutableStateOf(false) }
    var showScoreboardDialog by remember { mutableStateOf(false) }
    var showCapturedCardsDialog by remember { mutableStateOf(false) }
    var showCardCustomizationDialog by remember { mutableStateOf(false) }

    val userPlayer = players.firstOrNull { it.isHuman } ?: players.firstOrNull()
    val scaleFactor = if (isZoomed200) 1.35f else 1.0f
    val cardSkin = adminSettings?.cardSkin ?: "classic"

    // Subscription Remaining Days Calculation
    val now = System.currentTimeMillis()
    val expiry = currentUser?.subscriptionExpiryTimestamp ?: (now + 7L * 24 * 3600 * 1000)
    val diffMillis = maxOf(0L, expiry - now)
    val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

    // Captured Cards Dialog
    if (showCapturedCardsDialog && userPlayer != null) {
        CapturedCardsDialog(
            capturedCards = userPlayer.capturedCards,
            chkobbaCount = userPlayer.chkobbaCount,
            playerName = userPlayer.name,
            cardSkin = cardSkin,
            onDismiss = { showCapturedCardsDialog = false }
        )
    }

    // Card Customization Dialog
    if (showCardCustomizationDialog) {
        CardCustomizationDialog(
            currentSkinId = cardSkin,
            onDismiss = { showCardCustomizationDialog = false },
            onSelectSkin = { theme ->
                viewModel.setCardSkinTheme(theme)
            }
        )
    }

    // Game Over Dialog
    if (isGameOverDialogVisible) {
        GameOverDialog(
            scoreBreakdown = scoreHistory.lastOrNull(),
            teamAWins = chawatTeamAWins,
            teamBWins = chawatTeamBWins,
            targetChawat = targetChawat,
            onContinue = { viewModel.continueToNextRoundOrFinish() }
        )
    }

    // Teammate Hand Reveal Dialog
    if (teammateHandRevealed != null) {
        Dialog(onDismissRequest = { viewModel.closeTeammateCardsDialog() }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CasinoHeaderBg,
                border = BorderStroke(1.5.dp, CasinoGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "أوراق زميلك في الفريق (كشف لمرة واحدة)",
                            color = CasinoGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { viewModel.closeTeammateCardsDialog() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        teammateHandRevealed?.forEach { card ->
                            PlayingCardView(card = card, scaleFactor = 1.1f)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.closeTeammateCardsDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = CasinoGold)
                    ) {
                        Text("تم المشاهدة", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Hint Request Dialog ("طلب ورقة من الزميل")
    if (showHintDialog) {
        Dialog(onDismissRequest = { showHintDialog = false }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CasinoHeaderBg,
                border = BorderStroke(1.5.dp, CasinoGreenLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "طلب ورقة معينة من الزميل (تلميح سري)",
                            color = CasinoGreenMint,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showHintDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "اختر الورقة التي ترغب في أن يلعبها زميلك (لا يراها الخصوم):",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val ranks = listOf(
                        CardRank.SEVEN to "7 (الحية / سبعة)",
                        CardRank.TEN to "10 (بوف-ري)",
                        CardRank.NINE to "9 (كوال)",
                        CardRank.EIGHT to "8 (موجيرة)",
                        CardRank.SIX to "6 (شيش)",
                        CardRank.ACE to "1 (لاص)"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ranks.forEach { (rank, name) ->
                            Button(
                                onClick = {
                                    viewModel.requestCardHintFromPartner(rank)
                                    showHintDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CasinoDockBg),
                                border = BorderStroke(1.dp, CasinoBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("طلب: $name", color = CasinoGreenMint, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Upper Full Scoreboard Dialog
    if (showScoreboardDialog) {
        Dialog(onDismissRequest = { showScoreboardDialog = false }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CasinoHeaderBg,
                border = BorderStroke(1.5.dp, CasinoGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "لوحة النقاط وسجل جميع الجولات",
                            color = CasinoGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showScoreboardDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (scoreHistory.isEmpty()) {
                        Text(
                            text = "هذه الجولة الأولى قيد اللعب - ستظهر النتائج فور اكتمال الجولة",
                            color = CasinoGreenMint,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        scoreHistory.forEach { b ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = CasinoDockBg),
                                border = BorderStroke(1.dp, CasinoBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("الجولة ${b.roundNumber}: فريقك ${b.teamATotalRoundScore}ن | الخصم ${b.teamBTotalRoundScore}ن", color = CasinoGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("أوراق: أ(${b.teamACardsCount}) ب(${b.teamBCardsCount}) | ديناري: أ(${b.teamADinariCount}) ب(${b.teamBDinariCount})", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                    Text("برميلة: ${b.barmilaResult} | شكبة: أ(${b.teamAChkobbaPoints}) ب(${b.teamBChkobbaPoints})", color = CasinoGreenLight, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoFeltDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Professional Polish Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CasinoHeaderBg)
                    .border(BorderStroke(1.dp, CasinoBorder))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Player Avatar Badge & Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.GAME_HUB) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CasinoGreenBadge)
                            .border(2.dp, CasinoGreenAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.fullName?.take(2) ?: "لاعب").uppercase(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "لاعب: ${currentUser?.fullName ?: "علي محمود"}",
                            color = CasinoGreenMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "شكبة - جولة $currentRound (شوط $targetChawat)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Right: Actions (Sound, Theme, Captured Cards, Scoreboard)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sound Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleSoundMute() },
                        modifier = Modifier.size(32.dp).testTag("sound_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isSoundMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isSoundMuted) "Unmute" else "Mute",
                            tint = if (isSoundMuted) CasinoRed else CasinoGreenMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Card Skins Customization Button
                    IconButton(
                        onClick = { showCardCustomizationDialog = true },
                        modifier = Modifier.size(32.dp).testTag("card_skin_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Card Theme",
                            tint = CasinoGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Round Points Counter & Subscription Days
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CasinoDockBg)
                                .border(1.dp, CasinoBorder, RoundedCornerShape(6.dp))
                                .clickable { showScoreboardDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نقاط: ",
                                color = CasinoGreenMint,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$chawatTeamAWins - $chawatTeamBWins",
                                color = CasinoGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "إشتراك: $diffDays يوماً",
                            color = CasinoGreenLight,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 2. Main Game Felt Table Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(CasinoFeltCenter, CasinoFeltDark, CasinoFeltDeep)
                        )
                    )
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top: Other Players Cards Area (Opponents & Teammates)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.85f),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            // Other players in game
                            players.filter { !it.isHuman }.forEach { otherPlayer ->
                                val isTheirTurn = currentTurnIdx == otherPlayer.id
                                val isTeamA = otherPlayer.team == PlayerTeam.TEAM_A
                                val scale = if (players.size >= 6) 0.52f else if (players.size >= 4) 0.60f else 0.68f

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isTheirTurn) Color(0x33F59E0B) else Color.Transparent)
                                        .border(
                                            if (isTheirTurn) 1.5.dp else 0.5.dp,
                                            if (isTheirTurn) CasinoGold else if (isTeamA) CasinoGreenAccent.copy(alpha = 0.5f) else CasinoBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (isTeamA) "[فريقك]" else "[خصم]",
                                            color = if (isTeamA) CasinoGreenAccent else CasinoRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${otherPlayer.name} (${otherPlayer.hand.size})",
                                            color = if (isTheirTurn) CasinoGold else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isTheirTurn) FontWeight.Black else FontWeight.Bold
                                        )
                                    }
                                    if (isTheirTurn) {
                                        Text(
                                            text = "⏳ يفكر الآن...",
                                            color = CasinoGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy((-18).dp)) {
                                        repeat(otherPlayer.hand.size) {
                                            CardBackView(scaleFactor = scale, skin = cardSkin)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Center Felt Table (Played Cards)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x22000000))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (tableCards.isEmpty()) {
                            Text(
                                text = "الطاولة فارغة (شكبة)",
                                color = CasinoGreenMint.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                tableCards.forEach { card ->
                                    val isHighlighted = possibleCaptures.any { combo -> combo.any { it.id == card.id } }
                                    PlayingCardView(
                                        card = card,
                                        scaleFactor = scaleFactor * 0.95f,
                                        isHighlighted = isHighlighted
                                    )
                                }
                            }
                        }

                        // Deck Pile in corner
                        if (remainingDeck.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CardBackView(scaleFactor = 0.5f, skin = cardSkin)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${remainingDeck.size}",
                                        color = CasinoGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Capture combinations prompt (if selected card can capture)
                    if (selectedHandCard != null && possibleCaptures.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            possibleCaptures.forEachIndexed { idx, combo ->
                                Button(
                                    onClick = { viewModel.playCardWithCapture(combo) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CasinoHeaderBg),
                                    border = BorderStroke(1.dp, CasinoGold),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("capture_combo_$idx")
                                ) {
                                    val comboText = combo.joinToString(" + ") { "${it.rank.shortName}${it.suit.symbol}" }
                                    Text("أكل: $comboText", color = CasinoGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Floating Action Controls on the Right (Timer & Zoom)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (turnSeconds > 0) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CasinoHeaderBg)
                                .border(1.5.dp, if (turnSeconds <= 5) CasinoRed else CasinoBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${turnSeconds}s",
                                color = if (turnSeconds <= 5) CasinoRed else CasinoGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Floating Glass Zoom Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x44FFFFFF), CircleShape)
                            .clickable { viewModel.toggleZoom200() }
                            .testTag("zoom_200_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isZoomed200) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                            contentDescription = "Zoom",
                            tint = if (isZoomed200) CasinoGreenAccent else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Captured Cards Quick View Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CasinoHeaderBg)
                            .border(1.dp, CasinoGold, CircleShape)
                            .clickable { showCapturedCardsDialog = true }
                            .testTag("captured_cards_floating_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🃏",
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${userPlayer?.capturedCards?.size ?: 0}",
                                color = CasinoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (players.size > 2) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CasinoHeaderBg)
                                .border(1.dp, CasinoBorder, CircleShape)
                                .clickable { viewModel.revealTeammateCards() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Reveal Partner",
                                tint = if (userPlayer?.hasUsedPartnerRevealThisRound == true) Color(0xFF64748B) else CasinoGreenMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 3. Professional Polish Bottom Dock (Controls & Hand Cards)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CasinoDockBg)
                    .border(BorderStroke(1.dp, CasinoBorder))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Partner Hint from teammate notification
                AnimatedVisibility(visible = activeHintMessage != null) {
                    activeHintMessage?.let { hint ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CasinoHeaderBg)
                                .border(1.dp, CasinoGreenAccent, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💬 تلميح من ${hint.fromPlayerName}: يطلب ورقة ${hint.requestedRank.arabicName}",
                                    color = CasinoGreenMint,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.dismissHint() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }

                // Expandable Eaten Cards Pile Strategy Bar
                if (userPlayer != null) {
                    EatenCardsPileWidget(
                        capturedCards = userPlayer.capturedCards,
                        chkobbaCount = userPlayer.chkobbaCount,
                        playerName = userPlayer.name,
                        cardSkin = cardSkin,
                        onOpenFullModal = { showCapturedCardsDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                }

                // Row: Hint / Captured Cards Action Buttons, User Hand Cards, and Action Pulse
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left Actions: Captured Cards & Hint
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showCapturedCardsDialog = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CasinoBorder)
                                    .border(1.dp, CasinoGold, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${userPlayer?.capturedCards?.size ?: 0}",
                                    color = CasinoGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "مأكولاتي",
                                color = CasinoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (players.size > 2) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { showHintDialog = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CasinoBorder)
                                        .border(1.dp, CasinoGreenBadge, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = "Hint",
                                        tint = CasinoGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "تلميح",
                                    color = CasinoGreenLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Center: User Hand Cards
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        userPlayer?.hand?.forEach { card ->
                            val isSelected = selectedHandCard?.id == card.id
                            PlayingCardView(
                                card = card,
                                isSelected = isSelected,
                                scaleFactor = scaleFactor,
                                onClick = { viewModel.selectHandCard(card) }
                            )
                        }
                    }

                    // Right Action: Chkobba Action Pulse or Direct Play Action
                    if (selectedHandCard != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.playSelectedCardDirectly() }
                                .testTag("play_card_action_btn")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CasinoGold)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (possibleCaptures.isNotEmpty()) "أكل" else "رمي",
                                color = CasinoGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (chkobbaClaimActive != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.claimChkobba() }
                                .testTag("chkobba_claim_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(CasinoRed)
                                    .border(3.dp, CasinoRedDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "شكبة!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+نقطة",
                                color = CasinoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Empty spacer maintaining dock layout balance
                        Spacer(modifier = Modifier.size(44.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Ad Banner (#3)
                AdBannerComponent(banner = banner3)
            }
        }
    }
}
