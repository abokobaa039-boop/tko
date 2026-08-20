package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AdBannerComponent
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
import java.util.concurrent.TimeUnit

@Composable
fun GameHubScreen(
    viewModel: MainViewModel,
    onAdminClick: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val banners by viewModel.allBanners.collectAsState()
    val banner2 = banners.firstOrNull { it.id == 2 }
    val adminSettings by viewModel.adminSettings.collectAsState()

    var showSetupDialog by remember { mutableStateOf(false) }

    // Calculate Remaining Subscription Time
    val now = System.currentTimeMillis()
    val expiry = currentUser?.subscriptionExpiryTimestamp ?: (now + 7L * 24 * 3600 * 1000)
    val diffMillis = maxOf(0L, expiry - now)
    val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
    val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis) % 24
    val isExpired = diffMillis <= 0

    val aiDifficulty by viewModel.aiDifficulty.collectAsState()

    if (showSetupDialog) {
        GameSetupDialog(
            defaultTimer = adminSettings?.defaultTurnTimerSeconds ?: 20,
            currentDifficulty = aiDifficulty,
            onDismiss = { showSetupDialog = false },
            onStartGame = { players, chawat, timer, difficulty ->
                showSetupDialog = false
                viewModel.startNewGame(players, chawat, timer, difficulty)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CasinoFeltDark, CasinoDockBg, CasinoFeltDeep)
                )
            )
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CasinoGreenBadge)
                                .border(2.dp, CasinoGreenAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (currentUser?.fullName?.take(2) ?: "لاعب").uppercase(),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "مرحباً، ${currentUser?.fullName ?: "اللاعب"}",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (currentUser?.isPaid == true) CasinoGreenAccent else CasinoGold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentUser?.isPaid == true) "حساب مدفوع (نشط)" else "فترة تجريبية مجانية",
                                    color = CasinoGreenMint,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Winners Button
                    Row {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.WINNERS) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CasinoGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CasinoGold),
                            modifier = Modifier.testTag("hub_winners_btn")
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Winners", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الفائزين", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Subscription Status Card with Countdown Timer
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CasinoHeaderBg),
                    border = BorderStroke(1.dp, if (isExpired) CasinoRed else CasinoGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (isExpired) CasinoRed else CasinoGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isExpired) "انتهت صلاحية الاشتراك!" else "الوقت المتبقي في الاشتراك:",
                                    color = if (isExpired) CasinoRed else CasinoGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isExpired) "يرجى شحن كارت جديد لمتابعة اللعب" else "$diffDays يوم و $diffHours ساعة",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                            colors = ButtonDefaults.buttonColors(containerColor = CasinoGreenBadge),
                            border = BorderStroke(1.dp, CasinoGreenAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("hub_renew_card_btn")
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = "Card", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("شحن كارت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Banner 2: Game Hub Placement
            item {
                AdBannerComponent(banner = banner2)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Game Selection Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اختر لعبة الورق (5 ألعاب):",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Game 1: الشكبة (ACTIVE)
            item {
                ActiveGameCard(
                    title = "1. الشكبة (Chkobba)",
                    subtitle = "اللعبة التونسية والعربية التقليدية (40 ورقة) - متاحة الآن!",
                    badge = "متاحة للعب",
                    badgeColor = CasinoGreenAccent,
                    icon = "♠ ♦ ♣ ♥",
                    onPlayClick = { showSetupDialog = true }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Game 2: طرنيب (LOCKED)
            item {
                LockedGameCard(
                    title = "2. طرنيب (Tarneeb)",
                    subtitle = "لعبة الورق الشهيرة بنظام طلب اللطوش وحساب النقاط",
                    icon = "🃏"
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Game 3: ريمينو (LOCKED)
            item {
                LockedGameCard(
                    title = "3. ريمينو (Ramino)",
                    subtitle = "تجميع المجموعات والتسلسلات ونظام التنزيل",
                    icon = "🎴"
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Game 4: الحصلة في الري (LOCKED)
            item {
                LockedGameCard(
                    title = "4. الحصلة في الري (Hassla fel Ray)",
                    subtitle = "تجنب أخذ الري الحاكم في الجولة الفاصلة",
                    icon = "👑"
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Game 5: القلبة (LOCKED)
            item {
                LockedGameCard(
                    title = "5. القلبة (El Galba)",
                    subtitle = "قلب الطاولة والرهان على الورقة الأخيرة",
                    icon = "🔄"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ActiveGameCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: String,
    onPlayClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoHeaderBg),
        border = BorderStroke(1.5.dp, CasinoGold),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("game_card_chkobba")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 20.sp, color = CasinoGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = badge, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(containerColor = CasinoGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("play_chkobba_btn")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "العب الآن (ضد الكمبيوتر)",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LockedGameCard(
    title: String,
    subtitle: String,
    icon: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoDockBg),
        border = BorderStroke(1.dp, CasinoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = CasinoGreenMint,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x332D453B))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = CasinoGreenMint, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "قريباً",
                        color = CasinoGreenMint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
