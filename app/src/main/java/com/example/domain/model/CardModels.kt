package com.example.domain.model

enum class CardSuit(val arabicName: String, val symbol: String, val isRed: Boolean) {
    SPADA("سباطا", "♠", false),
    COPPA("كوبي", "♥", true),
    DINARI("ديناري", "♦", true),
    BASTONI("بستوني (ذبان)", "♣", false)
}

enum class CardRank(val value: Int, val arabicName: String, val shortName: String) {
    ACE(1, "لاص", "1"),
    TWO(2, "دوس", "2"),
    THREE(3, "تريس", "3"),
    FOUR(4, "كواترو (طاولا)", "4"),
    FIVE(5, "شنكا", "5"),
    SIX(6, "شيش", "6"),
    SEVEN(7, "سبعة", "7"),
    EIGHT(8, "موجيرة", "8"),
    NINE(9, "كوال", "9"),
    TEN(10, "بوف-ري", "10")
}

enum class AiDifficulty(val arabicName: String, val description: String) {
    EASY("سهل", "حركات سريعة ومباشرة للمبتدئين"),
    MEDIUM("متوسط", "لعب متوازن مع حساب ذكي لأوراق الديناري والسبعات"),
    HARD("محترف (خبير)", "استراتيجية متقدمة لمنع الشكبة، صيد البرميلة، وتجنب الجيفة")
}

enum class CardSkinTheme(
    val id: String,
    val arabicName: String,
    val description: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val patternType: String
) {
    CLASSIC_EMERALD("classic", "الزمرد الكلاسيكي", "أخضر كازينو عريق بنقوش هندسية مذهبة", 0xFF0F3822, 0xFFFFD700, "EMERALD"),
    ROYAL_GOLD("gold", "الذهبي الملكي", "إطار ذهبي فخم مع تاج إمبريالي أنيق", 0xFF854D0E, 0xFFFDE047, "GOLD"),
    VINTAGE_RUBY("vintage", "الياقوت العتيق", "مخمل أحمر داكن مع زخارف نبيلة", 0xFF881337, 0xFFFBBF24, "RUBY"),
    SAPPHIRE_NIGHT("royal", "الأزرق الليلي الفاخر", "أزرق ياقوتي ملكي مع كريستال النجوم", 0xFF1E3A8A, 0xFF60A5FA, "SAPPHIRE"),
    CYBER_NEON("cyber", "المستقبلي النيون", "كربون أسود عصري مع توهج نيون أزرق", 0xFF09090B, 0xFF06B6D4, "CYBER");

    companion object {
        fun fromId(id: String): CardSkinTheme {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CLASSIC_EMERALD
        }
    }
}

data class PlayingCard(
    val suit: CardSuit,
    val rank: CardRank,
    val id: String = "${suit.name}_${rank.name}"
) {
    val isHaya: Boolean get() = suit == CardSuit.DINARI && rank == CardRank.SEVEN
    val displayName: String get() = if (isHaya) "الحية (7 ديناري)" else "${rank.arabicName} ${suit.arabicName}"
}

enum class PlayerTeam {
    TEAM_A, // User & User's partners
    TEAM_B  // Opponents
}

data class GamePlayer(
    val id: Int,
    val name: String,
    val isHuman: Boolean,
    val team: PlayerTeam,
    val hand: List<PlayingCard> = emptyList(),
    val capturedCards: List<PlayingCard> = emptyList(),
    val chkobbaCount: Int = 0,
    val chkobbaPoints: Int = 0,
    val hasUsedPartnerRevealThisRound: Boolean = false
)

data class RoundScoreBreakdown(
    val roundNumber: Int,
    val teamACardsCount: Int = 0,
    val teamBCardsCount: Int = 0,
    val teamACardsPoint: Int = 0,
    val teamBCardsPoint: Int = 0,

    val teamADinariCount: Int = 0,
    val teamBDinariCount: Int = 0,
    val teamADinariPoint: Int = 0,
    val teamBDinariPoint: Int = 0,

    val teamASevens: Int = 0,
    val teamASixes: Int = 0,
    val teamBSevens: Int = 0,
    val teamBSixes: Int = 0,
    val barmilaResult: String = "",
    val teamABarmilaPoint: Int = 0,
    val teamBBarmilaPoint: Int = 0,

    val teamAChkobbaPoints: Int = 0,
    val teamBChkobbaPoints: Int = 0,

    val teamATotalRoundScore: Int = 0,
    val teamBTotalRoundScore: Int = 0,
    val specialEvent: String? = null, // "جيفة (ديناري كامل)", "جيفة (برميلة 4+4)", etc.
    val remainingCardsSweepSummary: String? = null // شروط انتهاء الجولة: إضافة الأوراق المتبقية لآخر شخص أكل
)

data class HintMessage(
    val fromPlayerName: String,
    val requestedRank: CardRank,
    val suit: CardSuit? = null,
    val timestamp: Long = System.currentTimeMillis()
)
