package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.AdBannerEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.CardSkinTheme
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AdBannerComponent
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Standalone Web Portal Interface for Administrative Control.
 * Simulates a secure Web URL interface (https://admin.chkobba.vip/portal/dashboard)
 * with dedicated browser address bar, independent authentication,
 * and real-time live synchronization with the game's Room Database.
 */
@Composable
fun AdminWebPortalScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var portalUrl by remember { mutableStateOf("https://admin.chkobba.vip/portal/dashboard") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("admin_web_portal_screen")
    ) {
        // 1. Web Browser Address Bar & HTTPS Security Header
        WebBrowserHeader(
            url = portalUrl,
            onRefresh = {
                Toast.makeText(context, "تم تحديث ومزامنة البيانات الفورية بنجاح 🟢", Toast.LENGTH_SHORT).show()
            },
            onCopyUrl = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Admin Web Portal URL", portalUrl)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "تم نسخ رابط لوحة التحكم الإلكترونية!", Toast.LENGTH_SHORT).show()
            },
            onExitPortal = {
                viewModel.navigateTo(AppScreen.GAME_HUB)
            }
        )

        // 2. Real-time Database Live Sync Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "بوابة الإدارة السحابية - مزامنة فورية مع قاعدة بيانات اللعبة Room DB",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "SSL 256-bit Encrypted",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
            )
        }

        // 3. Main Portal Body: Login Screen OR Dashboard
        if (!isAdminLoggedIn) {
            WebPortalLoginView(viewModel = viewModel)
        } else {
            WebPortalDashboardView(viewModel = viewModel, context = context)
        }
    }
}

@Composable
private fun WebBrowserHeader(
    url: String,
    onRefresh: () -> Unit,
    onCopyUrl: () -> Unit,
    onExitPortal: () -> Unit
) {
    Surface(
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Return to Game Button
                IconButton(onClick = onExitPortal, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Return to Game",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // URL Address Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Secure HTTPS",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = url,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = onCopyUrl, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Refresh Button
                IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WebPortalLoginView(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Admin Portal",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "بوابة الإدارة الإلكترونية",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "admin.chkobba.vip - Secure Web Portal",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("اسم المستخدم (الافتراضي: uas)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = Color(0xFFFFD700)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور (الافتراضي: 6090081)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Pass", tint = Color(0xFFFFD700)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(visible = errorMsg != null) {
                    errorMsg?.let {
                        Text(
                            text = it,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        errorMsg = null
                        viewModel.loginAdmin(
                            user = username,
                            pass = password,
                            onSuccess = {},
                            onError = { errorMsg = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("تسجيل الدخول إلى البوابة", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun WebPortalDashboardView(viewModel: MainViewModel, context: Context) {
    var selectedSidebarTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Web Navigation Tabs Bar with Logout option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WebPortalNavTab("👥 المستخدمين", selectedSidebarTab == 0) { selectedSidebarTab = 0 }
            WebPortalNavTab("🏆 الفائزين وتصدير Excel", selectedSidebarTab == 1) { selectedSidebarTab = 1 }
            WebPortalNavTab("📢 إدارة الإعلانات", selectedSidebarTab == 2) { selectedSidebarTab = 2 }
            WebPortalNavTab("💳 كروت الشحن", selectedSidebarTab == 3) { selectedSidebarTab = 3 }
            WebPortalNavTab("⚙️ الإعدادات العامة", selectedSidebarTab == 4) { selectedSidebarTab = 4 }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.logoutAdmin() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("خروج", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Sub-view Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedSidebarTab) {
                0 -> WebUsersManagementSection(viewModel)
                1 -> WebWinnersSection(viewModel, context)
                2 -> WebAdsManagementSection(viewModel)
                3 -> WebSubscriptionCardsSection(viewModel)
                4 -> WebGeneralSettingsSection(viewModel)
            }
        }
    }
}

@Composable
private fun WebPortalNavTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF0F172A))
            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// -------------------------------------------------------------
// 1. USERS MANAGEMENT SECTION
// -------------------------------------------------------------
@Composable
private fun WebUsersManagementSection(viewModel: MainViewModel) {
    val users by viewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true) ||
                (it.cardCodeUsed ?: "").contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "إدارة حسابات المستخدمين والاشتراكات (${filteredUsers.size} مستخدم)",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("بحث بالاسم، رقم الهاتف، أو كود الكارت") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFFFD700)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = Color(0xFF475569),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredUsers) { user ->
                val now = System.currentTimeMillis()
                val diff = maxOf(0L, user.subscriptionExpiryTimestamp - now)
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val isExpired = diff <= 0

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "هاتف: ${user.phone} | كارت: ${user.cardCodeUsed ?: "بدون كارت"}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(
                                    text = "فوز: ${user.gamesWonRounds}ج / ${user.gamesWonChawat}ش  |  خسارة: ${user.gamesLostRounds}ج / ${user.gamesLostChawat}ش",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (user.isPaid) Color(0x3310B981) else Color(0x33F59E0B))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (user.isPaid) "مدفوع (VIP)" else "تجريبي",
                                    color = if (user.isPaid) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpired) "⏳ انتهى الاشتراك" else "⏳ متبقي: $days يوم و $hours س",
                                color = if (isExpired) Color(0xFFEF4444) else Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row {
                                Button(
                                    onClick = { viewModel.extendUserSubscription(user.id, 30) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("+30 يوم", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { viewModel.deleteUser(user.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. WINNERS & EXCEL EXPORT SECTION
// -------------------------------------------------------------
@Composable
private fun WebWinnersSection(viewModel: MainViewModel, context: Context) {
    val users by viewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var allSelected by remember { mutableStateOf(true) }

    val filtered = users.filter {
        it.fullName.contains(searchQuery.trim(), ignoreCase = true) || it.phone.contains(searchQuery.trim())
    }.sortedByDescending { it.gamesWonRounds }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "🏆 سجل الفائزين وتصدير البيانات إلى Excel",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "حدد اللاعبين المؤهلين للجوائز ثم اضغط تصدير Excel لتوليد تقرير شامل.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Select All & Export Excel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { checked ->
                            allSelected = checked
                            viewModel.toggleAllWinnersInclusion(checked)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFD700))
                    )
                    Text("تحديد الكل", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val selectedWinners = users.filter { it.isIncludedInDraw }
                        val csv = viewModel.generateExcelCsvData(selectedWinners)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Winners CSV", csv)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ بيانات Excel/CSV (${selectedWinners.size} فائز) بنجاح!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("web_export_excel_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير Excel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filtered) { user ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (user.isIncludedInDraw) Color(0xFFFFD700) else Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = user.isIncludedInDraw,
                            onCheckedChange = { isChecked ->
                                viewModel.toggleWinnerInclusion(user.id, isChecked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "هاتف: ${user.phone} | كارت: ${user.cardCodeUsed ?: "بدون كارت"}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = "فوز: ${user.gamesWonRounds}ج / ${user.gamesWonChawat}ش  |  خسارة: ${user.gamesLostRounds}ج / ${user.gamesLostChawat}ش",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ADS MANAGEMENT SECTION
// -------------------------------------------------------------
@Composable
private fun WebAdsManagementSection(viewModel: MainViewModel) {
    val banners by viewModel.allBanners.collectAsState()
    var editingBanner by remember { mutableStateOf<AdBannerEntity?>(null) }

    if (editingBanner != null) {
        WebEditAdDialog(
            banner = editingBanner!!,
            onDismiss = { editingBanner = null },
            onSave = { updated ->
                viewModel.updateAdBanner(updated)
                editingBanner = null
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "📢 إدارة الإعلانات (4 مواقع بنرات متكيفة)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "يمكنك رفع الصور واختيار النماذج وضبط طريقة العرض (أفقي، عمودي، انبثاق) مع ملاءمة الحجم تلقائياً.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(banners) { banner ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (banner.isActive) Color(0xFFFFD700) else Color(0xFF334155)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = "Ad", tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بنر #${banner.id} - ${banner.placementName}",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (banner.isActive) "مفعل" else "معطل",
                                color = if (banner.isActive) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = banner.isActive,
                                onCheckedChange = { active ->
                                    viewModel.updateAdBanner(banner.copy(isActive = active))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "العنوان: ${banner.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "الوصف: ${banner.description}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(text = "طريقة العرض: ${banner.displayMode} ${if (banner.imageUrl.isNotBlank()) " | 🖼️ صورة مخصصة" else ""}", color = Color(0xFF38BDF8), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { editingBanner = banner },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("تعديل الإعلان ورفع الصور", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Preview Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "معاينة الإعلانات الحية كما تظهر داخل اللعبة:",
                color = Color(0xFFFFD700),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(banners) { banner ->
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(text = "موقع: ${banner.placementName}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                AdBannerComponent(banner = banner)
            }
        }
    }
}

@Composable
private fun WebEditAdDialog(
    banner: AdBannerEntity,
    onDismiss: () -> Unit,
    onSave: (AdBannerEntity) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(banner.title) }
    var desc by remember { mutableStateOf(banner.description) }
    var imageUrl by remember { mutableStateOf(banner.imageUrl) }
    var displayMode by remember { mutableStateOf(banner.displayMode) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val destinationFile = File(context.filesDir, "ad_banner_${banner.id}_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    destinationFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                imageUrl = destinationFile.absolutePath
                Toast.makeText(context, "تم رفع وتعيين الصورة من ذاكرة الجهاز بنجاح 📁", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                imageUrl = uri.toString()
            }
        }
    }

    val presetImages = listOf(
        Pair("اشتراك VIP", "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80"),
        Pair("بطولة الشكبة", "https://images.unsplash.com/photo-1511193311914-0346f16efe90?w=600&auto=format&fit=crop&q=80"),
        Pair("رصيد مضاعف", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80"),
        Pair("سحب الجوائز", "https://images.unsplash.com/photo-1541278107931-e006523892df?w=600&auto=format&fit=crop&q=80"),
        Pair("بدون صورة", "")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "تعديل بنر: ${banner.placementName}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("وصف الإعلان") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Image Upload from Device Memory Section
                Text("صورة الإعلان (مرفوعة من ذاكرة الجهاز):", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📁 اختيار ورفع صورة من ذاكرة الجهاز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "Uploaded", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (imageUrl.contains("ad_banner_") || imageUrl.startsWith("/")) "✅ تم رفع الصورة من الهاتف (${File(imageUrl).name})" else "الرابط: $imageUrl",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        TextButton(onClick = { imageUrl = "" }) {
                            Text("إزالة الصورة", color = Color(0xFFEF4444), fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Image Presets Selector
                Text("أو اختر نموذجاً جاهزاً سريعاً:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetImages.forEach { (name, url) ->
                        val isSelected = imageUrl == url
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155), RoundedCornerShape(6.dp))
                                .clickable { imageUrl = url }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = name,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("طريقة العرض وملاءمة الحجم:", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WebFilterChip("أفقي (Horizontal)", displayMode == "HORIZONTAL") { displayMode = "HORIZONTAL" }
                    WebFilterChip("عمودي (Vertical)", displayMode == "VERTICAL") { displayMode = "VERTICAL" }
                    WebFilterChip("انبثاق (Popup)", displayMode == "POPUP") { displayMode = "POPUP" }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Preview Box Inside Dialog
                Text("معاينة ملاءمة البنر للصورة:", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0A0F1D))
                        .padding(6.dp)
                ) {
                    AdBannerComponent(
                        banner = banner.copy(
                            title = title,
                            description = desc,
                            imageUrl = imageUrl,
                            displayMode = displayMode,
                            isActive = true
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSave(
                            banner.copy(
                                title = title,
                                description = desc,
                                imageUrl = imageUrl,
                                displayMode = displayMode
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("حفظ التغييرات", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SUBSCRIPTION CARDS MANAGEMENT SECTION
// -------------------------------------------------------------
@Composable
private fun WebSubscriptionCardsSection(viewModel: MainViewModel) {
    val cards by viewModel.allCards.collectAsState()
    val usedCount by viewModel.usedCardsCount.collectAsState()
    val availableCount by viewModel.availableCardsCount.collectAsState()

    var selectedCategory by remember { mutableStateOf("1 دينار (شهر)") }
    var quantityInput by remember { mutableStateOf("5") }
    var customCodeInput by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "💳 توليد وإدارة كروت الاشتراك (13 رقم)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WebStatCard("الكروت المتوفرة", "$availableCount", Color(0xFF10B981), Modifier.weight(1f))
                WebStatCard("الكروت المستهلكة", "$usedCount", Color(0xFFEF4444), Modifier.weight(1f))
                WebStatCard("إجمالي الكروت", "${cards.size}", Color(0xFFFFD700), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generator Box
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "توليد كروت جديدة تلقائياً", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { quantityInput = it },
                            label = { Text("العدد") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                val count = quantityInput.toIntOrNull() ?: 1
                                viewModel.generateSubscriptionCards(
                                    category = selectedCategory,
                                    quantity = count,
                                    customCode = customCodeInput.takeIf { it.isNotBlank() }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(52.dp)
                        ) {
                            Text("توليد الكروت الآن", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "سجل الكروت المنشأة:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(cards) { card ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (card.isUsed) Color(0xFF475569) else Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = card.cardCode, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${card.category} | ${if (card.isUsed) "مستخدم" else "متاح وغير مستخدم"}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. GENERAL SETTINGS SECTION
// -------------------------------------------------------------
@Composable
private fun WebGeneralSettingsSection(viewModel: MainViewModel) {
    val adminSettings by viewModel.adminSettings.collectAsState()
    val availableThemes = CardSkinTheme.entries
    val currentTrialDays = adminSettings?.guestTrialDays ?: 7
    var trialDaysInput by remember(adminSettings) { mutableIntStateOf(currentTrialDays) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "⚙️ الإعدادات العامة وقواعد اللعبة الأساسية",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Guest Trial Duration Control Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏱️ مدة مكوث وصلاحية الزائر / الفترة التجريبية:", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("$trialDaysInput يوم", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "المدة الممنوحة تلقائياً لكل زائر ومستخدم جديد عند التسجيل قبل طلب تفعيل كارت الاشتراك.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Increase / Decrease Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = {
                            if (trialDaysInput > 1) {
                                val newVal = trialDaysInput - 1
                                trialDaysInput = newVal
                                viewModel.updateGuestTrialDays(newVal)
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Text("-", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$trialDaysInput أيام",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    FilledIconButton(
                        onClick = {
                            val newVal = trialDaysInput + 1
                            trialDaysInput = newVal
                            viewModel.updateGuestTrialDays(newVal)
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Text("+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Select Chips
                Text("خيارات سريعة للمدة:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 3, 7, 14, 30, 60).forEach { days ->
                        WebFilterChip("$days يوم", trialDaysInput == days) {
                            trialDaysInput = days
                            viewModel.updateGuestTrialDays(days)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card Skin Global Theme
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFFFFD700)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "سمة وتصميم أوراق اللعب الافتراضية:", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                availableThemes.forEach { theme ->
                    val isSelected = (adminSettings?.cardSkin ?: "classic").equals(theme.id, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF0F172A) else Color.Transparent)
                            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155), RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.updateGameAndTimerSettings(
                                    timerSeconds = adminSettings?.defaultTurnTimerSeconds ?: 20,
                                    cardSkin = theme.id
                                )
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = theme.arabicName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = theme.description, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Game Turn Timer Setting
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "مؤقت حركة اللاعب الافتراضي:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "المدة الحالية: ${adminSettings?.defaultTurnTimerSeconds ?: 20} ثانية",
                    color = Color(0xFF10B981),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WebStatCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun WebFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF0F172A))
            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
