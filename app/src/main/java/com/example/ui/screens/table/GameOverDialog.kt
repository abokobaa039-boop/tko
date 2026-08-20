package com.example.ui.screens.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.RoundScoreBreakdown
import com.example.ui.theme.CasinoBorder
import com.example.ui.theme.CasinoDockBg
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGreenAccent
import com.example.ui.theme.CasinoGreenMint
import com.example.ui.theme.CasinoHeaderBg
import com.example.ui.theme.CasinoRed

@Composable
fun GameOverDialog(
    scoreBreakdown: RoundScoreBreakdown?,
    teamAWins: Int,
    teamBWins: Int,
    targetChawat: Int,
    onContinue: () -> Unit
) {
    if (scoreBreakdown == null) return

    val isMatchFinished = (targetChawat == 1) || (teamAWins >= 2 || teamBWins >= 2)
    val isTeamAWinner = scoreBreakdown.teamATotalRoundScore >= scoreBreakdown.teamBTotalRoundScore

    Dialog(onDismissRequest = onContinue) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CasinoHeaderBg,
            border = BorderStroke(2.dp, CasinoGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = CasinoGold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isMatchFinished) "نهاية الشوط والمباراة!" else "نهاية الجولة ${scoreBreakdown.roundNumber}!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (isTeamAWinner) "🎉 مبروك! فوز فريقك بهذه الجولة" else "حظاً أوفر في الجولة القادمة",
                    color = if (isTeamAWinner) CasinoGreenAccent else CasinoRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (scoreBreakdown.specialEvent != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x332D453B))
                            .border(1.dp, CasinoGreenAccent, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scoreBreakdown.specialEvent,
                            color = CasinoGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (scoreBreakdown.remainingCardsSweepSummary != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CasinoDockBg)
                            .border(1.dp, CasinoGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🃏 ${scoreBreakdown.remainingCardsSweepSummary}",
                            color = CasinoGreenMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CasinoDockBg, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("فئة النقاط", color = CasinoGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("فريقك (أ)", color = CasinoGreenAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    Text("الخصم (ب)", color = CasinoRed, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }

                ScoreRow("الكارطة (>20)", "${scoreBreakdown.teamACardsCount} ورقة (${scoreBreakdown.teamACardsPoint}ن)", "${scoreBreakdown.teamBCardsCount} ورقة (${scoreBreakdown.teamBCardsPoint}ن)")
                ScoreRow("الديناري (♦)", "${scoreBreakdown.teamADinariCount} ديناري (${scoreBreakdown.teamADinariPoint}ن)", "${scoreBreakdown.teamBDinariCount} ديناري (${scoreBreakdown.teamBDinariPoint}ن)")
                ScoreRow("البرميلة (7+6)", "${scoreBreakdown.teamASevens}س+${scoreBreakdown.teamASixes}ش (${scoreBreakdown.teamABarmilaPoint}ن)", "${scoreBreakdown.teamBSevens}س+${scoreBreakdown.teamBSixes}ش (${scoreBreakdown.teamBBarmilaPoint}ن)")
                ScoreRow("الشكبة (سحب)", "${scoreBreakdown.teamAChkobbaPoints} نقطة", "${scoreBreakdown.teamBChkobbaPoints} نقطة")

                // Total Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CasinoBorder, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("المجموع الكلي", color = CasinoGold, fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                    Text("${scoreBreakdown.teamATotalRoundScore} نقطة", color = CasinoGreenAccent, fontWeight = FontWeight.Black, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    Text("${scoreBreakdown.teamBTotalRoundScore} نقطة", color = CasinoRed, fontWeight = FontWeight.Black, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chawat Score Tracker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CasinoDockBg)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("أشواط فريقك: $teamAWins", color = CasinoGreenAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("|", color = CasinoBorder)
                    Text("أشواط الخصم: $teamBWins", color = CasinoRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = CasinoGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("game_over_continue_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Continue", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMatchFinished) "العودة للرئيسية" else "متابعة الجولة التالية",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(category: String, teamA: String, teamB: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, CasinoBorder)
            .background(CasinoHeaderBg)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(category, color = Color(0xFFE2E8F0), fontSize = 11.sp, modifier = Modifier.weight(1.5f))
        Text(teamA, color = Color(0xFFE2E8F0), fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(teamB, color = Color(0xFFE2E8F0), fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
    }
}
