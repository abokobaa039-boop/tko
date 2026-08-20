package com.example.ui.screens.games

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.CasinoBorder
import com.example.ui.theme.CasinoDockBg
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGreenMint
import com.example.ui.theme.CasinoHeaderBg

@Composable
fun GameSetupDialog(
    defaultTimer: Int = 20,
    currentDifficulty: com.example.domain.model.AiDifficulty = com.example.domain.model.AiDifficulty.MEDIUM,
    onDismiss: () -> Unit,
    onStartGame: (numPlayers: Int, chawat: Int, timerSeconds: Int, difficulty: com.example.domain.model.AiDifficulty) -> Unit
) {
    var selectedPlayers by remember { mutableIntStateOf(2) } // 2 (1 AI), 3 (2 AIs), 4 (3 AIs: 2v2)
    var selectedChawat by remember { mutableIntStateOf(1) } // 1, 3
    var selectedTimer by remember { mutableIntStateOf(if (defaultTimer > 0) defaultTimer else 20) } // 15, 30, 0 (open)
    var selectedDifficulty by remember { mutableStateOf(currentDifficulty) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CasinoHeaderBg,
            border = BorderStroke(1.5.dp, CasinoGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "♠", color = CasinoGold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إعدادات جولة الشكبة والذكاء الاصطناعي",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CasinoGreenMint)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Number of Players & Teams Setup
                Text(
                    text = "نظام وعدد اللاعبين في الفرق:",
                    color = CasinoGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OptionButton(
                        text = "1v1\n(كل واحد في فريق)",
                        isSelected = selectedPlayers == 2,
                        onClick = { selectedPlayers = 2 },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "4 لاعبين\n(كل اثنين في فريق)",
                        isSelected = selectedPlayers == 4,
                        onClick = { selectedPlayers = 4 },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "6 لاعبين\n(كل ثلاثة في فريق)",
                        isSelected = selectedPlayers == 6,
                        onClick = { selectedPlayers = 6 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. AI Difficulty Level
                Text(
                    text = "مستوى صعوبة وذكاء الخصوم:",
                    color = CasinoGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OptionButton(
                        text = "سهل (مبتدئ)",
                        isSelected = selectedDifficulty == com.example.domain.model.AiDifficulty.EASY,
                        onClick = { selectedDifficulty = com.example.domain.model.AiDifficulty.EASY },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "متوسط (متوازن)",
                        isSelected = selectedDifficulty == com.example.domain.model.AiDifficulty.MEDIUM,
                        onClick = { selectedDifficulty = com.example.domain.model.AiDifficulty.MEDIUM },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "محترف (استراتيجي)",
                        isSelected = selectedDifficulty == com.example.domain.model.AiDifficulty.HARD,
                        onClick = { selectedDifficulty = com.example.domain.model.AiDifficulty.HARD },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Chawat / Rounds
                Text(
                    text = "عدد الأشواط (الجولات):",
                    color = CasinoGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OptionButton(
                        text = "شوط واحد (1)",
                        isSelected = selectedChawat == 1,
                        onClick = { selectedChawat = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "3 أشواط (الأفضل من 3)",
                        isSelected = selectedChawat == 3,
                        onClick = { selectedChawat = 3 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Turn Timer
                Text(
                    text = "مؤقت حركة اللاعب:",
                    color = CasinoGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OptionButton(
                        text = "15 ثانية",
                        isSelected = selectedTimer == 15,
                        onClick = { selectedTimer = 15 },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "30 ثانية",
                        isSelected = selectedTimer == 30,
                        onClick = { selectedTimer = 30 },
                        modifier = Modifier.weight(1f)
                    )
                    OptionButton(
                        text = "وقت مفتوح",
                        isSelected = selectedTimer == 0,
                        onClick = { selectedTimer = 0 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Start Game Button
                Button(
                    onClick = {
                        onStartGame(selectedPlayers, selectedChawat, selectedTimer, selectedDifficulty)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CasinoGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_game_confirm_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "بدء اللعب والدخول للطاولة",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CasinoGold else CasinoDockBg)
            .border(
                1.dp,
                if (isSelected) CasinoGold else CasinoBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
