package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AdBannerDao
import com.example.data.local.dao.AdminSettingsDao
import com.example.data.local.dao.SubscriptionCardDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AdBannerEntity
import com.example.data.local.entity.AdminSettingsEntity
import com.example.data.local.entity.SubscriptionCardEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SubscriptionCardEntity::class,
        AdBannerEntity::class,
        AdminSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun subscriptionCardDao(): SubscriptionCardDao
    abstract fun adBannerDao(): AdBannerDao
    abstract fun adminSettingsDao(): AdminSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karta_chkobba_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert default entities
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Admin Settings
            val currentAdmin = database.adminSettingsDao().getAdminSettingsOnce()
            if (currentAdmin == null) {
                database.adminSettingsDao().insertAdminSettings(
                    AdminSettingsEntity(
                        id = 1,
                        adminUsername = "uas",
                        adminPassword = "6090081",
                        defaultTurnTimerSeconds = 20,
                        cardSkin = "classic"
                    )
                )
            }

            // Default Ad Banners (1 to 4)
            val defaultBanners = listOf(
                AdBannerEntity(
                    id = 1,
                    placementName = "صفحة تسجيل الدخول",
                    title = "اشترك الآن واحصل على أسبوع تجريبي مجاناً!",
                    description = "استمتع بأقوى ألعاب الورق التونسية والعربية الكارطة والشكبة اونلاين مع أصدقائك أو ضد الذكاء الاصطناعي",
                    imageUrl = "",
                    displayMode = "HORIZONTAL",
                    isActive = true
                ),
                AdBannerEntity(
                    id = 2,
                    placementName = "صفحة اختيار اللعبة",
                    title = "بطولة الشكبة الكبرى قريباً!",
                    description = "شارك في السحب الشهري على جوائز قيمة لمشتركي باقة 6 أشهر وسنة كاملة",
                    imageUrl = "",
                    displayMode = "HORIZONTAL",
                    isActive = true
                ),
                AdBannerEntity(
                    id = 3,
                    placementName = "صفحة طاولة اللعبة",
                    title = "موقع الكارطة الرسمي",
                    description = "تفعيل كروت الاشتراك السريع متوفر الآن عبر الموزعين المعتمدين",
                    imageUrl = "",
                    displayMode = "HORIZONTAL",
                    isActive = true
                ),
                AdBannerEntity(
                    id = 4,
                    placementName = "صفحة الفائزين",
                    title = "تهانينا لجميع الفائزين في سحب هذا الأسبوع!",
                    description = "العب المزيد من الجولات لزيادة فرصك في السحب القادم على الجوائز الكبرى",
                    imageUrl = "",
                    displayMode = "HORIZONTAL",
                    isActive = true
                )
            )
            database.adBannerDao().insertOrUpdateBanners(defaultBanners)

            // Seed some sample subscription cards
            val sampleCards = listOf(
                SubscriptionCardEntity(
                    cardCode = "847-192-384-5921",
                    category = "1 دينار (شهر)",
                    priceDinars = 1,
                    durationDays = 30,
                    isUsed = false
                ),
                SubscriptionCardEntity(
                    cardCode = "519-482-938-1029",
                    category = "5 دينار (6 أشهر)",
                    priceDinars = 5,
                    durationDays = 180,
                    isUsed = false
                ),
                SubscriptionCardEntity(
                    cardCode = "392-748-103-9482",
                    category = "10 دينار (سنة)",
                    priceDinars = 10,
                    durationDays = 365,
                    isUsed = false
                ),
                SubscriptionCardEntity(
                    cardCode = "918-273-645-8192",
                    category = "1 دينار (شهر)",
                    priceDinars = 1,
                    durationDays = 30,
                    isUsed = false
                )
            )
            database.subscriptionCardDao().insertCards(sampleCards)

            // Seed initial sample users for leaderboards / draw showcase
            val sampleUsers = listOf(
                UserEntity(
                    fullName = "أحمد المنصوري",
                    phone = "0912345678",
                    password = "123",
                    isPaid = true,
                    isGuest = false,
                    cardCodeUsed = "392-748-103-9482",
                    subscriptionExpiryTimestamp = System.currentTimeMillis() + (280L * 24 * 60 * 60 * 1000),
                    gamesWonRounds = 48,
                    gamesLostRounds = 12,
                    gamesWonChawat = 14,
                    gamesLostChawat = 3,
                    isIncludedInDraw = true
                ),
                UserEntity(
                    fullName = "طارق بن سالم",
                    phone = "0923456789",
                    password = "123",
                    isPaid = true,
                    isGuest = false,
                    cardCodeUsed = "519-482-938-1029",
                    subscriptionExpiryTimestamp = System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000),
                    gamesWonRounds = 35,
                    gamesLostRounds = 19,
                    gamesWonChawat = 9,
                    gamesLostChawat = 6,
                    isIncludedInDraw = true
                ),
                UserEntity(
                    fullName = "ياسين القروي",
                    phone = "0934567890",
                    password = "123",
                    isPaid = false,
                    isGuest = false,
                    subscriptionExpiryTimestamp = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000),
                    gamesWonRounds = 18,
                    gamesLostRounds = 14,
                    gamesWonChawat = 4,
                    gamesLostChawat = 5,
                    isIncludedInDraw = false
                )
            )
            for (user in sampleUsers) {
                database.userDao().insertUser(user)
            }
        }
    }
}
