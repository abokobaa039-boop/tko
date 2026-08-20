package com.example.ui.screens.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Expandable Card Pile UI Component displayed on the table.
 * Allows clicking to toggle a quick interactive strategy tray or open the full Modal Dialog.
 */
@Composable
fun EatenCardsPileWidget(
    capturedCards: List<PlayingCard>,
    chkobbaCount: Int,
    playerName: String = "أنت",
    cardSkin: String = "classic",
    onOpenFullModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val totalCards = capturedCards.size
    val dinariCards = capturedCards.filter { it.suit == CardSuit.DINARI }
    val dinariCount = dinariCards.size
    val hasHaya = capturedCards.any { it.isHaya }
    val sevensCount = capturedCards.count { it.rank == CardRank.SEVEN }
    val sixesCount = capturedCards.count { it.rank == CardRank.SIX }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CasinoDockBg.copy(alpha = 0.95f))
            .border(1.dp, CasinoBorder, RoundedCornerShape(12.dp))
            .testTag("eaten_cards_pile_widget")
    ) {
        // Main Clickable Pile Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Pile Icon and Count
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stacked Pile Visual Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CasinoHeaderBg)
                        .border(1.dp, if (totalCards > 20) CasinoGreenAccent else CasinoGold, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$totalCards",
                        color = if (totalCards > 20) CasinoGreenAccent else CasinoGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "مأكولات $playerName",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (hasHaya) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "👑 الحية",
                                color = CasinoRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "ديناري: $dinariCount/10 | 7: $sevensCount/4 | شكبة: $chkobbaCount",
                        color = CasinoGreenMint,
                        fontSize = 9.sp
                    )
                }
            }

            // Right: Toggle & Fullscreen Modal Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Expand / Collapse indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CasinoBorder)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isExpanded) "تصغير ▲" else "توسيع ▼",
                        color = CasinoGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onOpenFullModal,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        Icons.Default.Fullscreen,
                        contentDescription = "Full Inspector",
                        tint = CasinoGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Expandable Quick Strategy Drawer Tray
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CasinoHeaderBg)
                    .padding(8.dp)
            ) {
                // Mini Strategy Progress Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Karta Progress
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الكارطة", color = CasinoGold, fontSize = 9.sp)
                            Text("$totalCards/40 ${if (totalCards > 20) "✓+1" else ""}", color = CasinoGreenMint, fontSize = 9.sp)
                        }
                        LinearProgressIndicator(
                            progress = { (totalCards / 40f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (totalCards > 20) CasinoGreenAccent else CasinoGold,
                            trackColor = CasinoBorder
                        )
                    }

                    // Dinari Progress
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الديناري", color = CasinoGold, fontSize = 9.sp)
                            Text("$dinariCount/10 ${if (dinariCount > 5) "✓+1" else ""}", color = CasinoGreenMint, fontSize = 9.sp)
                        }
                        LinearProgressIndicator(
                            progress = { (dinariCount / 10f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (dinariCount > 5) CasinoGreenAccent else CasinoGold,
                            trackColor = CasinoBorder
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Carousel of Captured Cards
                if (capturedCards.isEmpty()) {
                    Text(
                        text = "لم تأكل أي ورقة بعد في هذه الجولة.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        capturedCards.forEach { card ->
                            Box(
                                modifier = Modifier
                                    .size(width = 38.dp, height = 54.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(
                                        1.dp,
                                        if (card.isHaya) CasinoRed else if (card.suit == CardSuit.DINARI) CasinoGold else CasinoBorder,
                                        RoundedCornerShape(4.dp)
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

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Action to Open Full Detailed Modal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CasinoBorder)
                        .clickable { onOpenFullModal() }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊 فتح شاشة استراتيجية الأوراق المفصلة",
                        color = CasinoGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
