package com.example.ui.screens.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.CardRank
import com.example.domain.model.CardSuit
import com.example.domain.model.PlayingCard
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CasinoBorder
import com.example.ui.theme.CasinoDockBg
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGreenAccent
import com.example.ui.theme.CasinoGreenBadge
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenMint
import com.example.ui.theme.CasinoHeaderBg
import com.example.ui.theme.CasinoRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CapturedCardsDialog(
    capturedCards: List<PlayingCard>,
    chkobbaCount: Int,
    playerName: String = "أنت",
    cardSkin: String = "classic",
    onDismiss: () -> Unit
) {
    val totalCount = capturedCards.size
    val dinariCards = capturedCards.filter { it.suit == CardSuit.DINARI }
    val dinariCount = dinariCards.size
    val hasHaya = capturedCards.any { it.isHaya }
    val sevensCount = capturedCards.count { it.rank == CardRank.SEVEN }
    val sixesCount = capturedCards.count { it.rank == CardRank.SIX }
    val courtCards = capturedCards.filter { it.rank == CardRank.TEN || it.rank == CardRank.NINE || it.rank == CardRank.EIGHT }

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, DINARI, BARMELEH, COURT

    val filteredCards = when (selectedFilter) {
        "DINARI" -> dinariCards
        "BARMELEH" -> capturedCards.filter { it.rank == CardRank.SEVEN || it.rank == CardRank.SIX }
        "COURT" -> courtCards
        else -> capturedCards
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = CasinoHeaderBg,
            border = BorderStroke(1.5.dp, CasinoGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("captured_cards_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CasinoDockBg)
                                .border(1.dp, CasinoGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = "Captured",
                                tint = CasinoGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "أوراقك المأكولة ($playerName)",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "لوحة تحليل الاستراتيجية والنتائج الفورية",
                                color = CasinoGreenMint,
                                fontSize = 10.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Strategy Scoreboard Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CasinoDockBg),
                    border = BorderStroke(1.dp, CasinoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📊 مؤشرات الفوز بالنقاط (Live Match Strategy):",
                            color = CasinoGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress 1: Karta (Majority of 40)
                        StrategyProgressRow(
                            label = "1. الكارطة (أكثر من 20 ورقة)",
                            current = totalCount,
                            target = 21,
                            max = 40,
                            isAchieved = totalCount >= 21,
                            accentColor = if (totalCount >= 21) CasinoGreenAccent else CasinoGold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress 2: Dinari (Majority of 10)
                        StrategyProgressRow(
                            label = "2. الديناري (أكثر من 5 قطع)",
                            current = dinariCount,
                            target = 6,
                            max = 10,
                            isAchieved = dinariCount >= 6,
                            accentColor = if (dinariCount >= 6) CasinoGreenAccent else CasinoGold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress 3: El Haya
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("3. السبعة الحية (7♦):", color = Color.White, fontSize = 10.sp)
                            if (hasHaya) {
                                Text("👑 معك (+1 نقطة مضمونة)", color = CasinoRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("⏳ قيد التنافس / مع الخصم", color = CasinoGreenLight, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress 4: Barmeleh (7s and 6s)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("4. البرميلة (السبعات والستات):", color = Color.White, fontSize = 10.sp)
                            Text(
                                text = "${sevensCount} سبعات | ${sixesCount} ستات (${if (sevensCount >= 3) "+1 متقدم" else "تنافس"})",
                                color = CasinoGreenMint,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Key Summary Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ModalStatBox(
                        title = "الكارطة",
                        value = "$totalCount / 40",
                        sub = if (totalCount > 20) "مضمونة (+1)" else "باقي ${maxOf(0, 21 - totalCount)}",
                        icon = Icons.Default.Layers,
                        accentColor = if (totalCount > 20) CasinoGreenAccent else CasinoGold,
                        modifier = Modifier.weight(1f)
                    )
                    ModalStatBox(
                        title = "الديناري",
                        value = "$dinariCount / 10",
                        sub = if (hasHaya) "👑 الحية معك" else "بدون حية",
                        icon = Icons.Default.Diamond,
                        accentColor = if (hasHaya) CasinoRed else CasinoGold,
                        modifier = Modifier.weight(1f)
                    )
                    ModalStatBox(
                        title = "البرميلة",
                        value = "${sevensCount}س / ${sixesCount}ش",
                        sub = if (sevensCount >= 3) "متقدم (+1)" else "قيد الجمع",
                        icon = Icons.Default.Star,
                        accentColor = CasinoGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                    ModalStatBox(
                        title = "الشكبة",
                        value = "$chkobbaCount",
                        sub = "${chkobbaCount} نقاط",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = CasinoGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Tabs Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChipButton("الكل ($totalCount)", selectedFilter == "ALL", Modifier.weight(1f)) { selectedFilter = "ALL" }
                    FilterChipButton("الديناري ($dinariCount)", selectedFilter == "DINARI", Modifier.weight(1f)) { selectedFilter = "DINARI" }
                    FilterChipButton("البرميلة (${sevensCount + sixesCount})", selectedFilter == "BARMELEH", Modifier.weight(1f)) { selectedFilter = "BARMELEH" }
                    FilterChipButton("الكبار (${courtCards.size})", selectedFilter == "COURT", Modifier.weight(1f)) { selectedFilter = "COURT" }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cards Gallery View
                if (filteredCards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CasinoDockBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أوراق في هذا القسم حالياً.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredCards.forEach { card ->
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 76.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (card.isHaya) 2.dp else if (card.suit == CardSuit.DINARI) 1.5.dp else 1.dp,
                                        color = if (card.isHaya) CasinoRed else if (card.suit == CardSuit.DINARI) CasinoGold else CasinoBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                PlayingCardView(
                                    card = card,
                                    isSelected = false,
                                    onClick = {}
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dismiss Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CasinoBorder)
                        .clickable { onDismiss() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "إغلاق والعودة إلى الطاولة",
                        color = CasinoGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StrategyProgressRow(
    label: String,
    current: Int,
    target: Int,
    max: Int,
    isAchieved: Boolean,
    accentColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.White, fontSize = 10.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$current / $max",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isAchieved) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("✓ نقطة مضمونة", color = CasinoGreenAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { (current.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp)),
            color = accentColor,
            trackColor = CasinoBorder
        )
    }
}

@Composable
private fun ModalStatBox(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoDockBg),
        border = BorderStroke(1.dp, CasinoBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, color = CasinoGreenLight, fontSize = 9.sp)
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = sub,
                color = accentColor,
                fontSize = 7.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FilterChipButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) CasinoGold else CasinoDockBg)
            .border(1.dp, if (isSelected) CasinoGold else CasinoBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
