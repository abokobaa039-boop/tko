package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Lightweight, high-performance sound engine using synthesized PCM waveforms.
 * Zero external audio assets required, guaranteed instant playback and offline reliability.
 */
object SoundManager {

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    fun toggleSound(): Boolean {
        _isSoundEnabled.value = !_isSoundEnabled.value
        return _isSoundEnabled.value
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
    }

    /**
     * Card Dealing Sound: Soft tactile whoosh/flutter
     */
    fun playCardDeal() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            generateAndPlayTone(
                frequencies = doubleArrayOf(320.0, 480.0, 640.0),
                durationMs = 60,
                volume = 0.45f,
                decay = true
            )
        }
    }

    /**
     * Card Play / Table Drop Sound: Crisp casino table felt tap
     */
    fun playCardPlay() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            generateAndPlayTone(
                frequencies = doubleArrayOf(240.0, 180.0),
                durationMs = 50,
                volume = 0.5f,
                decay = true
            )
        }
    }

    /**
     * Card Capture Sound: Joyful ascending double chime
     */
    fun playCapture() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            // First chime
            generateAndPlayTone(doubleArrayOf(587.33), 80, 0.6f, decay = true) // D5
            // Second higher chime
            generateAndPlayTone(doubleArrayOf(880.0), 120, 0.7f, decay = true) // A5
        }
    }

    /**
     * الشكبة! Chkobba Fanfare: Grand 4-note victory chord
     */
    fun playChkobbaFanfare() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            // Arpeggiated C Major chord: C5 -> E5 -> G5 -> C6 triumph
            generateAndPlayTone(doubleArrayOf(523.25), 90, 0.7f, decay = false)
            generateAndPlayTone(doubleArrayOf(659.25), 90, 0.75f, decay = false)
            generateAndPlayTone(doubleArrayOf(783.99), 110, 0.8f, decay = false)
            // Grand sustained chord
            generateAndPlayTone(doubleArrayOf(1046.50, 783.99, 523.25), 450, 0.9f, decay = true)
        }
    }

    /**
     * Round Victory Fanfare
     */
    fun playVictory() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            generateAndPlayTone(doubleArrayOf(440.0, 554.37), 120, 0.65f, decay = false)
            generateAndPlayTone(doubleArrayOf(659.25), 140, 0.7f, decay = false)
            generateAndPlayTone(doubleArrayOf(880.0, 1108.73), 380, 0.85f, decay = true)
        }
    }

    /**
     * Defeat / Minor low tone
     */
    fun playDefeat() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            generateAndPlayTone(doubleArrayOf(330.0), 150, 0.5f, decay = true)
            generateAndPlayTone(doubleArrayOf(261.63), 300, 0.55f, decay = true)
        }
    }

    /**
     * Tactile Button Click
     */
    fun playClick() {
        if (!_isSoundEnabled.value) return
        audioScope.launch {
            generateAndPlayTone(doubleArrayOf(1200.0), 20, 0.3f, decay = true)
        }
    }

    private fun generateAndPlayTone(
        frequencies: DoubleArray,
        durationMs: Int,
        volume: Float,
        decay: Boolean
    ) {
        try {
            val numSamples = (durationMs * SAMPLE_RATE) / 1000
            val generatedSnd = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                var sampleSum = 0.0
                val time = i.toDouble() / SAMPLE_RATE

                for (freq in frequencies) {
                    sampleSum += sin(2.0 * Math.PI * freq * time)
                }
                sampleSum /= frequencies.size

                val envelope = if (decay) {
                    1.0 - (i.toDouble() / numSamples)
                } else {
                    1.0
                }

                val finalVal = (sampleSum * 32767.0 * volume * envelope).toInt()
                generatedSnd[i] = finalVal.coerceIn(-32768, 32767).toShort()
            }

            val bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(maxOf(bufferSize, generatedSnd.size * 2))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()

            // Release after playing
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore audio track interruption or failure gracefully
        }
    }
}
