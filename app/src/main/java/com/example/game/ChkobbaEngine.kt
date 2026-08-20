package com.example.game

import com.example.domain.model.CardRank
import com.example.domain.model.CardSuit
import com.example.domain.model.GamePlayer
import com.example.domain.model.PlayerTeam
import com.example.domain.model.PlayingCard
import com.example.domain.model.RoundScoreBreakdown

data class MoveResult(
    val player: GamePlayer,
    val playedCard: PlayingCard,
    val capturedCards: List<PlayingCard>,
    val isChkobba: Boolean,
    val chkobbaPointsEarned: Int,
    val message: String
)

class ChkobbaEngine {

    fun generateDeck(): List<PlayingCard> {
        val deck = mutableListOf<PlayingCard>()
        for (suit in CardSuit.values()) {
            for (rank in CardRank.values()) {
                deck.add(PlayingCard(suit, rank))
            }
        }
        return deck.shuffled()
    }

    /**
     * Finds all valid capture combinations on the table for a given played card.
     */
    fun findValidCaptures(playedCard: PlayingCard, tableCards: List<PlayingCard>): List<List<PlayingCard>> {
        val targetValue = playedCard.rank.value
        val validCombinations = mutableListOf<List<PlayingCard>>()

        // 1. Direct single match
        val directMatches = tableCards.filter { it.rank.value == targetValue }
        for (match in directMatches) {
            validCombinations.add(listOf(match))
        }

        // If direct match exists, in classical Chkobba single match is mandatory or preferred.
        // We will also find sum combinations of 2 or more cards
        val remainingForSums = tableCards.filter { it.rank.value < targetValue }
        findSubsetsSummingTo(targetValue, remainingForSums, 0, mutableListOf(), validCombinations)

        return validCombinations
    }

    private fun findSubsetsSummingTo(
        target: Int,
        cards: List<PlayingCard>,
        startIndex: Int,
        current: MutableList<PlayingCard>,
        results: MutableList<List<PlayingCard>>
    ) {
        val currentSum = current.sumOf { it.rank.value }
        if (currentSum == target && current.size >= 2) {
            results.add(current.toList())
            return
        }
        if (currentSum > target) return

        for (i in startIndex until cards.size) {
            current.add(cards[i])
            findSubsetsSummingTo(target, cards, i + 1, current, results)
            current.removeAt(current.size - 1)
        }
    }

    /**
     * AI evaluates the best move to play from its hand according to selected difficulty.
     */
    fun chooseBestAiMove(
        aiPlayer: GamePlayer,
        tableCards: List<PlayingCard>,
        isLastTrickOfRound: Boolean,
        difficulty: com.example.domain.model.AiDifficulty = com.example.domain.model.AiDifficulty.MEDIUM,
        partnerHint: com.example.domain.model.HintMessage? = null
    ): Pair<PlayingCard, List<PlayingCard>?> {
        val hand = aiPlayer.hand
        if (hand.isEmpty()) throw IllegalStateException("Hand is empty")

        // Easy Mode: occasionally plays casual / random moves
        if (difficulty == com.example.domain.model.AiDifficulty.EASY && java.util.Random().nextFloat() < 0.4f) {
            val randomCard = hand.random()
            val captures = findValidCaptures(randomCard, tableCards)
            return Pair(randomCard, captures.firstOrNull())
        }

        var bestCard: PlayingCard = hand.first()
        var bestCapture: List<PlayingCard>? = null
        var highestScore = -9999

        for (card in hand) {
            val captures = findValidCaptures(card, tableCards)
            if (captures.isEmpty()) {
                // Strategic Discard Evaluation
                var discardScore = 0

                // 1. NEVER discard 7 of Dinari (Haya) unless absolutely no other choice
                if (card.isHaya) {
                    discardScore -= 500
                }

                // 2. Protect Dinari suit
                if (card.suit == CardSuit.DINARI) {
                    discardScore -= if (difficulty == com.example.domain.model.AiDifficulty.HARD) 45 else 20
                }

                // 3. Protect 7s and 6s (Barmeleh)
                if (card.rank == CardRank.SEVEN) {
                    discardScore -= if (difficulty == com.example.domain.model.AiDifficulty.HARD) 40 else 15
                }
                if (card.rank == CardRank.SIX) {
                    discardScore -= if (difficulty == com.example.domain.model.AiDifficulty.HARD) 30 else 10
                }

                // 4. Partner Hint synergy: if partner requested a card, and AI teammate has it, throw it
                if (partnerHint != null && card.rank == partnerHint.requestedRank) {
                    discardScore += 80
                }

                // 5. Hard Difficulty: Defensive Chkobba & Danger Calculation
                if (difficulty == com.example.domain.model.AiDifficulty.HARD) {
                    // Check if throwing this card makes the table very easy to clean (e.g., leaving a single card sum 7, 8, 9, 10)
                    val newTableSum = tableCards.sumOf { it.rank.value } + card.rank.value
                    if (newTableSum <= 10) {
                        // High danger! Opponent might hold a single card equal to newTableSum and score Chkobba!
                        discardScore -= 90
                    }

                    // Prefer discarding duplicate rank if opponent previously showed no interest
                    val hasSameRankInTable = tableCards.any { it.rank.value == card.rank.value }
                    if (hasSameRankInTable) {
                        discardScore += 15
                    }
                }

                // Prefer throwing middle low cards (3, 4, 5) over key point cards
                discardScore -= (card.rank.value * 2)

                if (discardScore > highestScore) {
                    highestScore = discardScore
                    bestCard = card
                    bestCapture = null
                }
            } else {
                for (capture in captures) {
                    var captureScore = 30

                    // 1. Chkobba (clears all table cards)
                    val isChkobba = capture.size == tableCards.size && !isLastTrickOfRound
                    if (isChkobba) {
                        captureScore += if (difficulty == com.example.domain.model.AiDifficulty.HARD) 300 else 150
                    }

                    // 2. Capture 7 of Dinari (Haya)
                    val capturesHaya = capture.any { it.isHaya } || card.isHaya
                    if (capturesHaya) {
                        captureScore += if (difficulty == com.example.domain.model.AiDifficulty.HARD) 200 else 80
                    }

                    // 3. Dinari suit cards
                    val dinariCount = capture.count { it.suit == CardSuit.DINARI } + (if (card.suit == CardSuit.DINARI) 1 else 0)
                    captureScore += dinariCount * (if (difficulty == com.example.domain.model.AiDifficulty.HARD) 35 else 20)

                    // 4. Barmeleh (7s and 6s)
                    val sevensCount = capture.count { it.rank == CardRank.SEVEN } + (if (card.rank == CardRank.SEVEN) 1 else 0)
                    val sixesCount = capture.count { it.rank == CardRank.SIX } + (if (card.rank == CardRank.SIX) 1 else 0)
                    captureScore += sevensCount * (if (difficulty == com.example.domain.model.AiDifficulty.HARD) 40 else 25)
                    captureScore += sixesCount * (if (difficulty == com.example.domain.model.AiDifficulty.HARD) 30 else 15)

                    // 5. Card volume count (الكارطة)
                    captureScore += (capture.size + 1) * 8

                    if (captureScore > highestScore) {
                        highestScore = captureScore
                        bestCard = card
                        bestCapture = capture
                    }
                }
            }
        }

        return Pair(bestCard, bestCapture)
    }

    /**
     * Calculates the official Chkobba round scoring
     */
    fun calculateRoundScore(
        roundNumber: Int,
        teamAPlayers: List<GamePlayer>,
        teamBPlayers: List<GamePlayer>,
        sweepSummary: String? = null
    ): RoundScoreBreakdown {
        val teamACaptured = teamAPlayers.flatMap { it.capturedCards }
        val teamBCaptured = teamBPlayers.flatMap { it.capturedCards }

        val teamACount = teamACaptured.size
        val teamBCount = teamBCaptured.size

        // 1. Most cards (الكارطة)
        var teamACardsPt = 0
        var teamBCardsPt = 0
        if (teamACount > 20 || teamACount > teamBCount) {
            teamACardsPt = 1
        } else if (teamBCount > 20 || teamBCount > teamACount) {
            teamBCardsPt = 1
        }

        // 2. Dinari (الديناري)
        val teamADinari = teamACaptured.count { it.suit == CardSuit.DINARI }
        val teamBDinari = teamBCaptured.count { it.suit == CardSuit.DINARI }
        var teamADinariPt = 0
        var teamBDinariPt = 0
        var specialEvent: String? = null

        var resetTeamB = false
        var resetTeamA = false

        if (teamADinari == 10) {
            specialEvent = "جيفة لفريق أ (10 ديناري كاملة)!"
            teamADinariPt = 5
        } else if (teamBDinari == 10) {
            specialEvent = "جيفة لفريق ب (10 ديناري كاملة)!"
            teamBDinariPt = 5
        } else if (teamADinari >= 8) {
            teamADinariPt = 1
            resetTeamB = true
        } else if (teamBDinari >= 8) {
            teamBDinariPt = 1
            resetTeamA = true
        } else if (teamADinari > teamBDinari) {
            teamADinariPt = 1
        } else if (teamBDinari > teamADinari) {
            teamBDinariPt = 1
        }

        // 3. El Barmila (البرميلة - 7s and 6s)
        val teamASevens = teamACaptured.count { it.rank == CardRank.SEVEN }
        val teamASixes = teamACaptured.count { it.rank == CardRank.SIX }
        val teamBSevens = teamBCaptured.count { it.rank == CardRank.SEVEN }
        val teamBSixes = teamBCaptured.count { it.rank == CardRank.SIX }

        var teamABarmilaPt = 0
        var teamBBarmilaPt = 0
        var barmilaDesc = "لا يوجد"

        // Barmila combinations:
        // 4 sevens + 4 sixes -> Jifa
        // 4 sevens only -> 1 pt
        // 2 sevens + 3 sixes -> 1 pt
        // 2 sevens + 2 sixes each -> Bagie (Tie)
        // 3 sevens + 1 six -> 1 pt
        // 3 sevens + 0 sixes -> 1 pt
        if (teamASevens == 4 && teamASixes == 4) {
            teamABarmilaPt = 5
            specialEvent = "جيفة لفريق أ (برميلة 4 سبعات و 4 ستات)!"
            barmilaDesc = "جيفة 4+4 لفريق أ"
        } else if (teamBSevens == 4 && teamBSixes == 4) {
            teamBBarmilaPt = 5
            specialEvent = "جيفة لفريق ب (برميلة 4 سبعات و 4 ستات)!"
            barmilaDesc = "جيفة 4+4 لفريق ب"
        } else if (teamASevens == 4) {
            teamABarmilaPt = 1
            barmilaDesc = "برميلة (4 سبعات) لفريق أ"
        } else if (teamBSevens == 4) {
            teamBBarmilaPt = 1
            barmilaDesc = "برميلة (4 سبعات) لفريق ب"
        } else if (teamASevens == 2 && teamASixes == 3) {
            teamABarmilaPt = 1
            barmilaDesc = "برميلة (2 سبعات + 3 ستات) لفريق أ"
        } else if (teamBSevens == 2 && teamBSixes == 3) {
            teamBBarmilaPt = 1
            barmilaDesc = "برميلة (2 سبعات + 3 ستات) لفريق ب"
        } else if (teamASevens == 2 && teamASixes == 2 && teamBSevens == 2 && teamBSixes == 2) {
            barmilaDesc = "تعادل (باجي 2+2)"
        } else if (teamASevens == 3 && (teamASixes == 1 || teamASixes == 0)) {
            teamABarmilaPt = 1
            barmilaDesc = "برميلة (3 سبعات) لفريق أ"
        } else if (teamBSevens == 3 && (teamBSixes == 1 || teamBSixes == 0)) {
            teamBBarmilaPt = 1
            barmilaDesc = "برميلة (3 سبعات) لفريق ب"
        }

        // 4. Chkobba Points
        val teamAChkobba = teamAPlayers.sumOf { it.chkobbaPoints }
        val teamBChkobba = teamBPlayers.sumOf { it.chkobbaPoints }

        var totalA = teamACardsPt + teamADinariPt + teamABarmilaPt + teamAChkobba
        var totalB = teamBCardsPt + teamBDinariPt + teamBBarmilaPt + teamBChkobba

        if (resetTeamB) {
            totalB = 0
            specialEvent = "تم تصفير نقاط فريق ب لتفوق فريق أ بالديناري (8+)"
        }
        if (resetTeamA) {
            totalA = 0
            specialEvent = "تم تصفير نقاط فريق أ لتفوق فريق ب بالديناري (8+)"
        }

        return RoundScoreBreakdown(
            roundNumber = roundNumber,
            teamACardsCount = teamACount,
            teamBCardsCount = teamBCount,
            teamACardsPoint = teamACardsPt,
            teamBCardsPoint = teamBCardsPt,
            teamADinariCount = teamADinari,
            teamBDinariCount = teamBDinari,
            teamADinariPoint = teamADinariPt,
            teamBDinariPoint = teamBDinariPt,
            teamASevens = teamASevens,
            teamASixes = teamASixes,
            teamBSevens = teamBSevens,
            teamBSixes = teamBSixes,
            barmilaResult = barmilaDesc,
            teamABarmilaPoint = teamABarmilaPt,
            teamBBarmilaPoint = teamBBarmilaPt,
            teamAChkobbaPoints = teamAChkobba,
            teamBChkobbaPoints = teamBChkobba,
            teamATotalRoundScore = totalA,
            teamBTotalRoundScore = totalB,
            specialEvent = specialEvent,
            remainingCardsSweepSummary = sweepSummary
        )
    }
}
