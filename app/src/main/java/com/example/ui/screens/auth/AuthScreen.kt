package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.ui.AppScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AdBannerComponent
import com.example.ui.theme.CasinoBorder
import com.example.ui.theme.CasinoDockBg
import com.example.ui.theme.CasinoFeltDark
import com.example.ui.theme.CasinoFeltDeep
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGreenAccent
import com.example.ui.theme.CasinoGreenBadge
import com.example.ui.theme.CasinoGreenMint
import com.example.ui.theme.CasinoHeaderBg
import com.example.ui.theme.CasinoRed

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAdminClick: () -> Unit
) {
    val banners by viewModel.allBanners.collectAsState()
    val banner1 = banners.firstOrNull { it.id == 1 }
    val adminSettings by viewModel.adminSettings.collectAsState()
    val trialDays = adminSettings?.guestTrialDays ?: 7

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login/Register, 1: Redeem Card
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cardCodeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "♠", fontSize = 32.sp, color = CasinoGreenMint)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "♦", fontSize = 32.sp, color = CasinoRed)
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "الكارطة",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = CasinoGold
                    )
                    Text(
                        text = "منصة ألعاب الورق والشكبة اونلاين",
                        fontSize = 12.sp,
                        color = CasinoGreenMint
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "♣", fontSize = 32.sp, color = CasinoGreenMint)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "♥", fontSize = 32.sp, color = CasinoRed)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner 1: Login Screen Placement
            AdBannerComponent(banner = banner1)

            Spacer(modifier = Modifier.height(16.dp))

            // Trial Info Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoHeaderBg),
                border = BorderStroke(1.dp, CasinoGreenAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CasinoGreenBadge),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Trial",
                            tint = CasinoGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "فترة تجريبية مجانية لمدة $trialDays أيام!",
                            color = CasinoGreenAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "سجل الآن للعب مجاناً لمدة $trialDays أيام ثم قم بتفعيل كارت الاشتراك (13 رقم)",
                            color = CasinoGreenMint,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CasinoDockBg,
                contentColor = CasinoGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CasinoGold
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, CasinoBorder, RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; errorMessage = null; successMessage = null },
                    text = { Text("تسجيل الدخول / حساب جديد", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; errorMessage = null; successMessage = null },
                    text = { Text("تعبئة كارت الاشتراك", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoHeaderBg),
                border = BorderStroke(1.dp, CasinoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (selectedTab == 0) {
                        Text(
                            text = "بيانات الحساب",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("الاسم الثلاثي") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = CasinoGold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CasinoGold,
                                unfocusedBorderColor = CasinoBorder,
                                focusedLabelColor = CasinoGold,
                                unfocusedLabelColor = CasinoGreenMint,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_fullname_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("رقم الهاتف") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = CasinoGold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CasinoGold,
                                unfocusedBorderColor = CasinoBorder,
                                focusedLabelColor = CasinoGold,
                                unfocusedLabelColor = CasinoGreenMint,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("كلمة المرور") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = CasinoGold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CasinoGold,
                                unfocusedBorderColor = CasinoBorder,
                                focusedLabelColor = CasinoGold,
                                unfocusedLabelColor = CasinoGreenMint,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                errorMessage = null
                                successMessage = null
                                viewModel.loginOrRegister(
                                    fullName,
                                    phone,
                                    password,
                                    onSuccess = {},
                                    onError = { errorMessage = it }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CasinoGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_submit_btn")
                        ) {
                            Text(
                                text = "دخول / إنشاء حساب والبدء",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { viewModel.loginAsGuest() },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CasinoBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("auth_guest_btn")
                        ) {
                            Text(
                                text = "الدخول كزائر وتجربة اللعب",
                                color = CasinoGreenMint,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Redeem Card Tab
                        Text(
                            text = "تفعيل كود الاشتراك (13 رقم)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "صيغة الكود: XXX-XXX-XXX-XXXX",
                            color = CasinoGreenMint,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = cardCodeInput,
                            onValueChange = { cardCodeInput = it },
                            label = { Text("أدخل كود الكارت (13 رقم)") },
                            placeholder = { Text("مثال: 847-192-384-5921") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = "Card", tint = CasinoGold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CasinoGold,
                                unfocusedBorderColor = CasinoBorder,
                                focusedLabelColor = CasinoGold,
                                unfocusedLabelColor = CasinoGreenMint,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_cardcode_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pricing Plans Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PlanChip("1 دينار", "شهر")
                            PlanChip("5 دينار", "6 أشهر")
                            PlanChip("10 دينار", "سنة كاملة")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                errorMessage = null
                                successMessage = null
                                viewModel.activateSubscriptionCard(cardCodeInput) { success, msg ->
                                    if (success) {
                                        successMessage = msg
                                        cardCodeInput = ""
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CasinoGreenBadge),
                            border = BorderStroke(1.dp, CasinoGreenAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_activate_card_btn")
                        ) {
                            Text(
                                text = "تأكيد وتفعيل الاشتراك",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Status Messages
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = CasinoRed,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    AnimatedVisibility(visible = successMessage != null) {
                        successMessage?.let {
                            Text(
                                text = it,
                                color = CasinoGreenAccent,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer with Dedicated Web Admin Portal Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN_WEB_PORTAL) },
                    modifier = Modifier.testTag("auth_admin_web_portal_link")
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Admin Web Portal",
                        tint = CasinoGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🌐 بوابة الإدارة الإلكترونية (Web Admin Portal: admin.chkobba.vip)",
                        color = CasinoGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanChip(price: String, duration: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CasinoDockBg)
            .border(1.dp, CasinoBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = price, color = CasinoGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = duration, color = CasinoGreenMint, fontSize = 10.sp)
        }
    }
}
