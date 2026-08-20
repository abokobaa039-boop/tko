package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CardRank
import com.example.domain.model.CardSuit
import com.example.domain.model.PlayingCard

@Composable
fun PlayingCardView(
    card: PlayingCard,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    scaleFactor: Float = 1.0f,
    onClick: (() -> Unit)? = null
) {
    val animatedElevation by animateFloatAsState(
        targetValue = if (isSelected) 14f else 4f,
        label = "cardElevation"
    )

    val animatedOffsetY by animateDpAsState(
        targetValue = if (isSelected) (-8).dp else 0.dp,
        label = "cardOffsetY"
    )

    val cardWidth = 66.dp * scaleFactor
    val cardHeight = 96.dp * scaleFactor

    val isRed = card.suit.isRed
    val suitColor = if (isRed) Color(0xFFDC2626) else Color(0xFF111827)

    val borderStroke = when {
        isSelected -> BorderStroke(2.dp, Color(0xFFFACC15))
        isHighlighted -> BorderStroke(2.dp, Color(0xFF4CAF50))
        else -> BorderStroke(1.dp, Color(0xFFD1D5DB))
    }

    Box(
        modifier = modifier
            .offset(y = animatedOffsetY)
            .width(cardWidth)
            .height(cardHeight)
            .shadow(animatedElevation.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFFFFFF))
            .border(borderStroke, RoundedCornerShape(4.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(4.dp * scaleFactor)
    ) {
        // Special golden radial aura for 7 of Dinari (الحية)
        if (card.isHaya) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x33FACC15), Color.Transparent)
                        )
                    )
            )
        }

        // Top-left rank & suit
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = card.rank.shortName,
                color = suitColor,
                fontSize = (13 * scaleFactor).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (13 * scaleFactor).sp
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = (11 * scaleFactor).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (11 * scaleFactor).sp
            )
        }

        // Center visual
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (card.rank) {
                CardRank.TEN -> {
                    Text(
                        text = "👑",
                        fontSize = (18 * scaleFactor).sp
                    )
                    Text(
                        text = "بوف",
                        color = suitColor,
                        fontSize = (9 * scaleFactor).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                CardRank.NINE -> {
                    Text(
                        text = "🏇",
                        fontSize = (16 * scaleFactor).sp
                    )
                    Text(
                        text = "كوال",
                        color = suitColor,
                        fontSize = (9 * scaleFactor).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                CardRank.EIGHT -> {
                    Text(
                        text = "👸",
                        fontSize = (16 * scaleFactor).sp
                    )
                    Text(
                        text = "موجيرة",
                        color = suitColor,
                        fontSize = (8 * scaleFactor).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                CardRank.SEVEN -> {
                    if (card.isHaya) {
                        Text(
                            text = "🐍",
                            fontSize = (18 * scaleFactor).sp
                        )
                        Text(
                            text = "الحية",
                            color = Color(0xFFDC2626),
                            fontSize = (10 * scaleFactor).sp,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Text(
                            text = card.suit.symbol,
                            color = suitColor,
                            fontSize = (24 * scaleFactor).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                CardRank.ACE -> {
                    Text(
                        text = card.suit.symbol,
                        color = suitColor,
                        fontSize = (28 * scaleFactor).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                else -> {
                    Text(
                        text = card.suit.symbol,
                        color = suitColor,
                        fontSize = (22 * scaleFactor).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom-right inverted rank & suit
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(180f)
        ) {
            Text(
                text = card.rank.shortName,
                color = suitColor,
                fontSize = (13 * scaleFactor).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (13 * scaleFactor).sp
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = (11 * scaleFactor).sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (11 * scaleFactor).sp
            )
        }
    }
}

@Composable
fun CardBackView(
    modifier: Modifier = Modifier,
    scaleFactor: Float = 1.0f,
    skin: String = "classic"
) {
    val cardWidth = 66.dp * scaleFactor
    val cardHeight = 96.dp * scaleFactor

    val (bgGradient, borderColor, centerIcon) = when (skin) {
        "gold" -> Triple(
            listOf(Color(0xFF8A6511), Color(0xFFD4AF37), Color(0xFF533E0B)),
            Color(0xFFFACC15),
            "⚜️"
        )
        "vintage" -> Triple(
            listOf(Color(0xFF5D101D), Color(0xFF8B1E2D), Color(0xFF3B070F)),
            Color(0xFFE2B068),
            "♠"
        )
        "cyber" -> Triple(
            listOf(Color(0xFF09090B), Color(0xFF18181B), Color(0xFF020617)),
            Color(0xFF06B6D4),
            "⚡"
        )
        "royal" -> Triple(
            listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF0A2E68)),
            Color(0xFF90CAF9),
            "💎"
        )
        else -> Triple( // classic Professional Polish Felt
            listOf(Color(0xFF1A2E26), Color(0xFF2D453B), Color(0xFF0E1B16)),
            Color(0xFF4CAF50),
            "🂠"
        )
    }

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .shadow(4.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.linearGradient(bgGradient))
            .border(1.5.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(3.dp * scaleFactor),
        contentAlignment = Alignment.Center
    ) {
        // Inner geometric pattern border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = centerIcon,
                fontSize = (20 * scaleFactor).sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

