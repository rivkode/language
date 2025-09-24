package com.learner.language.domain.ai

import com.learner.language.domain.chat.AudioRecord
import com.learner.language.infrastructure.chat.AudioRecordRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse
import org.springframework.ai.openai.OpenAiAudioSpeechModel
import org.springframework.ai.openai.OpenAiAudioSpeechOptions
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions
import org.springframework.ai.openai.api.OpenAiAudioApi
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat
import org.springframework.ai.openai.audio.speech.SpeechPrompt
import org.springframework.ai.openai.audio.speech.SpeechResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*


private val logger = KotlinLogging.logger {}

@Service
class AiAudioService(
    private val transcriptionModel: OpenAiAudioTranscriptionModel,
    private val speechModel: OpenAiAudioSpeechModel,
) {
    @Value("\${app.audio.storage.path}")
    lateinit var audioStoragePath: String

    fun speechAudio(speechText: String, userId: Long): String {
        val speechOptions = OpenAiAudioSpeechOptions.builder()
            .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA.getValue())
            .speed(1.0f)
            .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
            .model(OpenAiAudioApi.TtsModel.TTS_1.value)
            .build()

        val speechPrompt = SpeechPrompt(
            speechText,
            speechOptions
        )
        val response: SpeechResponse = speechModel.call(speechPrompt)
        val audioBytes = response.result.output
        val filePath = saveAudioFile(audioBytes)

        return filePath
    }

    fun saveAudioFile(audioBytes: ByteArray): String {
        val audioDir = Paths.get(audioStoragePath)

        if (!Files.exists(audioDir)) {
            Files.createDirectories(audioDir)
        }

        val fileName = generateFileName()
        val filePath = audioDir.resolve(fileName)

        Files.write(filePath, audioBytes)

        val fileNamePath = "audio/${fileName}"

        return fileNamePath
    }

    fun generateFileName(): String {
        val now = Date()
        val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)
        val dateString = dateFormat.format(now)

        // 2. UUID를 생성하고, 앞의 8자리만 추출 (충분히 고유성을 가짐)
        val uuid = UUID.randomUUID().toString().substring(0, 8)

        // 3. '날짜_UUID.mp3' 형식으로 파일명 결합
        return "tts-${dateString}-${uuid}.mp3"
    }

    fun transcribe(audioFile: MultipartFile): String {

        val processedAudioFile = processAudioFile(audioFile)
        val responseFormat = TranscriptResponseFormat.JSON
        val transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
            .model("whisper-1")
            .language("ko")
            .responseFormat(responseFormat)
            .build()

        val transcriptionRequest = AudioTranscriptionPrompt(
            FileSystemResource(processedAudioFile),
            transcriptionOptions
        )
        val response: AudioTranscriptionResponse = transcriptionModel.call(transcriptionRequest)
        val responseText = response.results[0].output
        println("responseText: $responseText")

        return responseText
    }

    // 1

//    fun processAudioFile(audioFile: MultipartFile): File {
//        // 1. MultipartFile을 임시 파일로 저장
//        val tempFile = Files.createTempFile("upload-", ".wav").toFile()
//        audioFile.transferTo(tempFile)
//
//        // 2. FFmpeg를 사용해서 OpenAI가 요구하는 형식으로 변환
//        val processedFile = Files.createTempFile("processed-", ".wav").toFile()
//
//        val ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe" // 설치한 경로
//        val cmd = arrayOf(
//            ffmpegPath, "-y",
//            "-i", tempFile.absolutePath,
//            "-c:a", "pcm_s16le", // 16-bit PCM
//            "-ar", "16000",      // ✅ 44.1kHz 샘플레이트로 변경
//            "-ac", "1",          // mono 채널 유지
//            "-f", "wav",         // WAV 컨테이너
//            "-acodec", "pcm_s16le", // 코덱 명시적 지정
//            processedFile.absolutePath
//        )
//
//        val process = ProcessBuilder(*cmd).inheritIO().start()
//        val exitCode = process.waitFor()
//
//        // 임시 파일 정리
//        tempFile.delete()
//
//        if (exitCode != 0) {
//            throw RuntimeException("FFmpeg processing failed with exit code: $exitCode")
//        }
//
//        // 파일이 실제로 생성되었는지 확인
//        if (!processedFile.exists() || processedFile.length() == 0L) {
//            throw RuntimeException("Processed audio file is empty or not created")
//        }
//
//        return processedFile
//    }

    // 2
//    fun processAudioFile(audioFile: MultipartFile): File {
//        val tempFile = Files.createTempFile("upload-", ".wav").toFile()
//        audioFile.transferTo(tempFile)
//
//        val processedFile = Files.createTempFile("processed-", ".wav").toFile()
//
//        val ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe"
//        val cmd = arrayOf(
//            ffmpegPath, "-y",
//            "-i", tempFile.absolutePath,
//            "-ar", "16000",      // 16kHz
//            "-ac", "1",          // mono
//            "-c:a", "pcm_s16le", // 16-bit PCM
//            "-f", "wav",
//            processedFile.absolutePath
//        )
//
//        val process = ProcessBuilder(*cmd).inheritIO().start()
//        val exitCode = process.waitFor()
//
//        tempFile.delete()
//
//        if (exitCode != 0) {
//            throw RuntimeException("FFmpeg processing failed with exit code: $exitCode")
//        }
//
//        if (!processedFile.exists() || processedFile.length() == 0L) {
//            throw RuntimeException("Processed audio file is empty or not created")
//        }
//
//        // 변환된 파일 정보 검증
//        validateAudioFile(processedFile)
//
//        return processedFile
//    }
//
//    fun validateAudioFile(file: File) {
//        val ffprobePath = "C:\\ffmpeg\\bin\\ffprobe.exe"
//        val cmd = arrayOf(
//            ffprobePath, "-v", "quiet",
//            "-print_format", "json",
//            "-show_format",
//            "-show_streams",
//            file.absolutePath
//        )
//
//        val process = ProcessBuilder(*cmd).start()
//        val output = process.inputStream.bufferedReader().readText()
//        val exitCode = process.waitFor()
//
//        if (exitCode != 0) {
//            throw RuntimeException("FFprobe validation failed")
//        }
//
//        // JSON 파싱하여 샘플레이트와 채널 수 확인
//        if (!output.contains("\"sample_rate\":\"16000\"") || !output.contains("\"channels\":1")) {
//            throw RuntimeException("Audio file validation failed: Expected 16kHz mono, got: $output")
//        }
//
//        println("Audio file validation passed: $output")
//    }

    // 3
//    fun processAudioFile(audioFile: MultipartFile): File {
//        val tempFile = Files.createTempFile("upload-", ".wav").toFile()
//        audioFile.transferTo(tempFile)
//
//        val processedFile = Files.createTempFile("processed-", ".wav").toFile()
//
//        val ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe"
//        val cmd = arrayOf(
//            ffmpegPath, "-y",
//            "-i", tempFile.absolutePath,
//            "-ar", "44100",      // gpt-4o-mini-transcribe는 44.1kHz 지원
//            "-ac", "2",          // stereo 채널 유지 (원본과 동일)
//            "-c:a", "pcm_s16le", // 16-bit PCM
//            "-f", "wav",         // WAV 컨테이너
//            processedFile.absolutePath
//        )
//
//        val process = ProcessBuilder(*cmd).inheritIO().start()
//        val exitCode = process.waitFor()
//
//        tempFile.delete()
//
//        if (exitCode != 0) {
//            throw RuntimeException("FFmpeg processing failed with exit code: $exitCode")
//        }
//
//        if (!processedFile.exists() || processedFile.length() == 0L) {
//            throw RuntimeException("Processed audio file is empty or not created")
//        }
//
//        return processedFile
//    }

    // 4

    fun processAudioFile(audioFile: MultipartFile): File {
        val tempFile = Files.createTempFile("upload-", ".wav").toFile()
        audioFile.transferTo(tempFile)

        // gpt-4o-mini-transcribe는 원본 WAV 형식을 지원할 수 있음
        // 변환 없이 원본 그대로 사용
        return tempFile
    }

}