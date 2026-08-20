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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.AdBannerEntity
import com.example.data.local.entity.SubscriptionCardEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AdBannerComponent
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun AdminPanelScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedSidebarTab by remember { mutableIntStateOf(0) } // 0: Users, 1: Winners Draw, 2: Ads, 3: Cards, 4: Settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D))
    ) {
        // Top Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.logoutAdmin() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "لوحة التحكم (Admin Panel)",
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Button(
                onClick = { viewModel.logoutAdmin() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("خروج", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Navigation Tabs (Horizontal for Android screen layout)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminNavTab("1. المستخدمين", selectedSidebarTab == 0) { selectedSidebarTab = 0 }
            AdminNavTab("2. سحب الفائزين", selectedSidebarTab == 1) { selectedSidebarTab = 1 }
            AdminNavTab("3. إدارة الإعلانات", selectedSidebarTab == 2) { selectedSidebarTab = 2 }
            AdminNavTab("4. كروت الاشتراك", selectedSidebarTab == 3) { selectedSidebarTab = 3 }
            AdminNavTab("5. إعدادات المؤقت واللعبة", selectedSidebarTab == 4) { selectedSidebarTab = 4 }
        }

        // Content Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedSidebarTab) {
                0 -> UsersManagementSection(viewModel)
                1 -> WinnersLotterySection(viewModel, context)
                2 -> AdsManagementSection(viewModel)
                3 -> SubscriptionCardsSection(viewModel)
                4 -> GameSettingsSection(viewModel, context)
            }
        }
    }
}

@Composable
private fun AdminNavTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF1E293B))
            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF334155), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

// -------------------------------------------------------------
// 1. USERS MANAGEMENT SECTION
// -------------------------------------------------------------
@Composable
private fun UsersManagementSection(viewModel: MainViewModel) {
    val users by viewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("ALL") } // ALL, PAID, GUEST, OLDEST, NEWEST

    val filtered = users.filter {
        it.fullName.contains(searchQuery.trim(), ignoreCase = true) || it.phone.contains(searchQuery.trim())
    }.filter {
        when (filterMode) {
            "PAID" -> it.isPaid
            "GUEST" -> it.isGuest
            else -> true
        }
    }.sortedWith(
        when (filterMode) {
            "OLDEST" -> compareBy { it.registeredTimestamp }
            "NEWEST" -> compareByDescending { it.registeredTimestamp }
            else -> compareByDescending { it.subscriptionExpiryTimestamp }
        }
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "قاعدة بيانات المستخدمين (${filtered.size} مستخدم)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم الثلاثي أو رقم الهاتف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFFFD700)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF475569),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_users_search_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AdminFilterChip("الكل", filterMode == "ALL") { filterMode = "ALL" }
                AdminFilterChip("مدفوع", filterMode == "PAID") { filterMode = "PAID" }
                AdminFilterChip("زائر", filterMode == "GUEST") { filterMode = "GUEST" }
                AdminFilterChip("الأحدث", filterMode == "NEWEST") { filterMode = "NEWEST" }
                AdminFilterChip("الأقدم", filterMode == "OLDEST") { filterMode = "OLDEST" }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(filtered) { user ->
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
                        Column {
                            Text(text = user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "هاتف: ${user.phone}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (user.isPaid) Color(0x3310B981) else Color(0x33F59E0B))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (user.isPaid) "مدفوع (كارت)" else "زائر / تجريبي",
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

// -------------------------------------------------------------
// 2. WINNERS LOTTERY & EXCEL EXPORT SECTION
// -------------------------------------------------------------
@Composable
private fun WinnersLotterySection(viewModel: MainViewModel, context: Context) {
    val users by viewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("WINS") } // WINS, LOSSES, NEWEST, OLDEST
    var allSelected by remember { mutableStateOf(true) }

    val filtered = users.filter {
        it.fullName.contains(searchQuery.trim(), ignoreCase = true) || it.phone.contains(searchQuery.trim())
    }.sortedWith(
        when (filterMode) {
            "LOSSES" -> compareByDescending { it.gamesLostRounds + it.gamesLostChawat }
            "NEWEST" -> compareByDescending { it.registeredTimestamp }
            "OLDEST" -> compareBy { it.registeredTimestamp }
            else -> compareByDescending { it.gamesWonRounds + it.gamesWonChawat }
        }
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "إدارة السحب على الفائزين وتصدير النتائج",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم أو الهاتف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFFFD700)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF475569),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_winners_search_input")
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AdminFilterChip("الأكثر فوزاً", filterMode == "WINS") { filterMode = "WINS" }
                AdminFilterChip("الأكثر خسارة", filterMode == "LOSSES") { filterMode = "LOSSES" }
                AdminFilterChip("الأحدث", filterMode == "NEWEST") { filterMode = "NEWEST" }
                AdminFilterChip("الأقدم", filterMode == "OLDEST") { filterMode = "OLDEST" }
            }
            Spacer(modifier = Modifier.height(10.dp))

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
                        Toast.makeText(context, "تم نسخ بيانات Excel/CSV (${selectedWinners.size} فائز) إلى الحافظة بنجاح!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_export_excel_btn")
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
// 3. ADS MANAGEMENT SECTION (4 BOXES + PREVIEW)
// -------------------------------------------------------------
@Composable
private fun AdsManagementSection(viewModel: MainViewModel) {
    val banners by viewModel.allBanners.collectAsState()
    var editingBanner by remember { mutableStateOf<AdBannerEntity?>(null) }

    if (editingBanner != null) {
        EditAdDialog(
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
                text = "إدارة الإعلانات (4 مواقع بنرات)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "يمكنك تعديل محتوى الإعلان، طريقة العرض (أفقي، عمودي، انبثاق)، وتفعيل/تعطيل كل بنر.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4 Horizontal Boxes
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
                    Text(text = "طريقة العرض: ${banner.displayMode}", color = Color(0xFF38BDF8), fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { editingBanner = banner },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تعديل الإعلان", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Preview Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "معاينة الإعلانات الحية كما تظهر للمستخدمين:",
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
private fun EditAdDialog(
    banner: AdBannerEntity,
    onDismiss: () -> Unit,
    onSave: (AdBannerEntity) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(banner.title) }
    var desc by remember { mutableStateOf(banner.description) }
    var imageUrl by remember { mutableStateOf(banner.imageUrl) }
    var displayMode by remember { mutableStateOf(banner.displayMode) } // HORIZONTAL, VERTICAL, POPUP

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

    // Image Presets for Quick Selection
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

                Spacer(modifier = Modifier.height(10.dp))

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
                                text = if (imageUrl.contains("ad_banner_") || imageUrl.startsWith("/")) "✅ تم رفع الصورة من الجهاز (${File(imageUrl).name})" else "الرابط: $imageUrl",
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
                    AdminFilterChip("أفقي (Horizontal)", displayMode == "HORIZONTAL") { displayMode = "HORIZONTAL" }
                    AdminFilterChip("عمودي (Vertical)", displayMode == "VERTICAL") { displayMode = "VERTICAL" }
                    AdminFilterChip("انبثاق (Popup)", displayMode == "POPUP") { displayMode = "POPUP" }
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
private fun SubscriptionCardsSection(viewModel: MainViewModel) {
    val cards by viewModel.allCards.collectAsState()
    val usedCount by viewModel.usedCardsCount.collectAsState()
    val availableCount by viewModel.availableCardsCount.collectAsState()

    var selectedCategory by remember { mutableStateOf("1 دينار (شهر)") }
    var quantityInput by remember { mutableStateOf("5") }
    var customCodeInput by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("ALL") } // ALL, AVAILABLE, USED, DATE_DESC, DATE_ASC

    val filteredCards = cards.filter {
        when (filterMode) {
            "AVAILABLE" -> !it.isUsed
            "USED" -> it.isUsed
            else -> true
        }
    }.sortedWith(
        when (filterMode) {
            "DATE_ASC" -> compareBy { it.createdTimestamp }
            else -> compareByDescending { it.createdTimestamp }
        }
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "إدارة وتوليد كروت الاشتراك (13 رقم)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Stats Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الكروت المتاحة", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("$availableCount", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الكروت المستخدمة", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("$usedCount", color = Color(0xFFEF4444), fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي الكروت", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("${cards.size}", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Generator Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("شريط إضافة وتوليد كروت جديدة:", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Plan categories
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AdminFilterChip("1 دينار (شهر)", selectedCategory == "1 دينار (شهر)") { selectedCategory = "1 دينار (شهر)" }
                        AdminFilterChip("5 دينار (6 أشهر)", selectedCategory == "5 دينار (6 أشهر)") { selectedCategory = "5 دينار (6 أشهر)" }
                        AdminFilterChip("10 دينار (سنة)", selectedCategory == "10 دينار (سنة)") { selectedCategory = "10 دينار (سنة)" }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { quantityInput = it },
                            label = { Text("الكمية") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.width(90.dp)
                        )

                        OutlinedTextField(
                            value = customCodeInput,
                            onValueChange = { customCodeInput = it },
                            label = { Text("كود مخصص (اختياري 13 رقم)") },
                            placeholder = { Text("XXX-XXX-XXX-XXXX") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val qty = quantityInput.toIntOrNull() ?: 1
                            viewModel.generateSubscriptionCards(
                                category = selectedCategory,
                                quantity = qty,
                                customCode = customCodeInput.ifBlank { null }
                            )
                            customCodeInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("admin_generate_cards_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("توليد وإضافة الكروت فوراً", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AdminFilterChip("الكل", filterMode == "ALL") { filterMode = "ALL" }
                AdminFilterChip("متاح فقط", filterMode == "AVAILABLE") { filterMode = "AVAILABLE" }
                AdminFilterChip("مستخدم فقط", filterMode == "USED") { filterMode = "USED" }
                AdminFilterChip("الأحدث أولاً", filterMode == "DATE_DESC") { filterMode = "DATE_DESC" }
                AdminFilterChip("الأقدم أولاً", filterMode == "DATE_ASC") { filterMode = "DATE_ASC" }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(filteredCards) { card ->
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (card.isUsed) Color(0xFFEF4444) else Color(0xFF10B981)),
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
                        Text(
                            text = card.cardCode,
                            color = Color(0xFFFFD700),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (card.isUsed) Color(0x33EF4444) else Color(0x3310B981))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (card.isUsed) "مستخدم" else "متاح للشحن",
                                color = if (card.isUsed) Color(0xFFEF4444) else Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "الفئة: ${card.category} (${card.priceDinars} دينار - ${card.durationDays} يوم)", color = Color(0xFFCBD5E1), fontSize = 12.sp)

                    if (card.isUsed) {
                        Text(
                            text = "مستخدم بواسطة: ${card.usedByUserName ?: "مستخدم"} (${card.usedByUserPhone ?: ""})",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp
                        )
                    } else {
                        Text(text = "تاريخ الإنشاء: ${sdf.format(Date(card.createdTimestamp))}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. GAME & TIMER SETTINGS & CREDENTIALS SECTION
// -------------------------------------------------------------
@Composable
private fun GameSettingsSection(viewModel: MainViewModel, context: Context) {
    val settings by viewModel.adminSettings.collectAsState()
    var timerSeconds by remember(settings) { mutableIntStateOf(settings?.defaultTurnTimerSeconds ?: 20) }
    var cardSkin by remember(settings) { mutableStateOf(settings?.cardSkin ?: "classic") }
    val trialDays = settings?.guestTrialDays ?: 7
    var trialDaysInput by remember(settings) { mutableIntStateOf(trialDays) }

    var newAdminUser by remember { mutableStateOf("") }
    var newAdminPass by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "إعدادات اللعبة ومدة مكوث الزائر وتصميم الأوراق",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Guest Trial Duration Control Card
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
                            AdminFilterChip("$days يوم", trialDaysInput == days) {
                                trialDaysInput = days
                                viewModel.updateGuestTrialDays(days)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timer Settings Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("مؤقت حركة اللاعب الافتراضي في اللعبة:", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminFilterChip("15 ثانية", timerSeconds == 15) {
                            timerSeconds = 15
                            viewModel.updateGameAndTimerSettings(15, cardSkin)
                        }
                        AdminFilterChip("20 ثانية", timerSeconds == 20) {
                            timerSeconds = 20
                            viewModel.updateGameAndTimerSettings(20, cardSkin)
                        }
                        AdminFilterChip("30 ثانية", timerSeconds == 30) {
                            timerSeconds = 30
                            viewModel.updateGameAndTimerSettings(30, cardSkin)
                        }
                        AdminFilterChip("وقت مفتوح (0)", timerSeconds == 0) {
                            timerSeconds = 0
                            viewModel.updateGameAndTimerSettings(0, cardSkin)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Skin Themes
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("تصميم ومظهر ظهر الأوراق (Card Deck Skin):", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AdminFilterChip("كلاسيك أخضر", cardSkin == "classic") {
                            cardSkin = "classic"
                            viewModel.updateGameAndTimerSettings(timerSeconds, "classic")
                        }
                        AdminFilterChip("ذهبي ملكي", cardSkin == "gold") {
                            cardSkin = "gold"
                            viewModel.updateGameAndTimerSettings(timerSeconds, "gold")
                        }
                        AdminFilterChip("أحمر عتيق", cardSkin == "vintage") {
                            cardSkin = "vintage"
                            viewModel.updateGameAndTimerSettings(timerSeconds, "vintage")
                        }
                        AdminFilterChip("أزرق رويال", cardSkin == "royal") {
                            cardSkin = "royal"
                            viewModel.updateGameAndTimerSettings(timerSeconds, "royal")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Change Admin Credentials Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("تغيير اسم المستخدم وكلمة المرور للوحة التحكم:", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("الاسم الحالي: ${settings?.adminUsername ?: "uas"} | المرور: ${settings?.adminPassword ?: "6090081"}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newAdminUser,
                        onValueChange = { newAdminUser = it },
                        label = { Text("اسم المستخدم الجديد") },
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
                        value = newAdminPass,
                        onValueChange = { newAdminPass = it },
                        label = { Text("كلمة المرور الجديدة") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (newAdminUser.isNotBlank() && newAdminPass.isNotBlank()) {
                                viewModel.updateAdminCredentials(newAdminUser, newAdminPass) {
                                    Toast.makeText(context, "تم تحديث بيانات تسجيل دخول الإدارة بنجاح!", Toast.LENGTH_SHORT).show()
                                    newAdminUser = ""
                                    newAdminPass = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تحديث بيانات الدخول", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF0F172A))
            .border(1.dp, if (isSelected) Color(0xFFFFD700) else Color(0xFF475569), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color(0xFFCBD5E1),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}
