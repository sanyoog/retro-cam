package com.retrocam.app.domain.camera

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VideoRecorder"

sealed class RecordingState {
    object Idle : RecordingState()
    data class Recording(val durationMs: Long) : RecordingState()
    data class Paused(val durationMs: Long) : RecordingState()
    data class Completed(val uri: android.net.Uri) : RecordingState()
    data class Error(val message: String) : RecordingState()
}

@Singleton
class VideoRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private var currentRecording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    fun createVideoCapture(quality: Quality = Quality.HD): VideoCapture<Recorder> {
        val qualitySelector = QualitySelector.from(quality)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        
        return VideoCapture.withOutput(recorder).also {
            videoCapture = it
        }
    }

    fun startRecording(
        lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        onError: (String) -> Unit = {}
    ) {
        val capture = videoCapture
        if (capture == null) {
            onError("VideoCapture not initialized")
            _recordingState.value = RecordingState.Error("VideoCapture not initialized")
            return
        }

        if (currentRecording != null) {
            Log.w(TAG, "Recording already in progress")
            return
        }

        try {
            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(Date())
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, "VID_$name")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/RetroCam")
                }
            }

            val mediaStoreOutput = FileOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

            currentRecording = capture.output
                .prepareRecording(context, mediaStoreOutput)
                .start(context.mainExecutor) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            _recordingState.value = RecordingState.Recording(0)
                            Log.d(TAG, "Recording started")
                        }
                        is VideoRecordEvent.Status -> {
                            _recordingState.value = RecordingState.Recording(
                                event.recordingStats.recordedDurationNanos / 1_000_000
                            )
                        }
                        is VideoRecordEvent.Pause -> {
                            val duration = event.recordingStats.recordedDurationNanos / 1_000_000
                            _recordingState.value = RecordingState.Paused(duration)
                        }
                        is VideoRecordEvent.Resume -> {
                            val duration = event.recordingStats.recordedDurationNanos / 1_000_000
                            _recordingState.value = RecordingState.Recording(duration)
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (event.hasError()) {
                                val error = "Recording error: ${event.error}"
                                Log.e(TAG, error)
                                _recordingState.value = RecordingState.Error(error)
                                onError(error)
                            } else {
                                event.outputResults.outputUri.let { uri ->
                                    Log.d(TAG, "Video saved: $uri")
                                    _recordingState.value = RecordingState.Completed(uri)
                                }
                            }
                            currentRecording = null
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            _recordingState.value = RecordingState.Error(e.message ?: "Unknown error")
            onError(e.message ?: "Unknown error")
        }
    }

    fun pauseRecording() {
        currentRecording?.pause()
    }

    fun resumeRecording() {
        currentRecording?.resume()
    }

    fun stopRecording() {
        currentRecording?.stop()
        currentRecording = null
    }

    fun isRecording(): Boolean {
        return _recordingState.value is RecordingState.Recording
    }

    fun release() {
        stopRecording()
        videoCapture = null
    }
}
