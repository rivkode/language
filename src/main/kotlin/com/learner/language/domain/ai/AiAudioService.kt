package com.learner.language.domain.ai

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

    fun processAudioFile(audioFile: MultipartFile): File {
        val tempFile = Files.createTempFile("upload-", ".wav").toFile()
        audioFile.transferTo(tempFile)

        // gpt-4o-mini-transcribe는 원본 WAV 형식을 지원할 수 있음
        // 변환 없이 원본 그대로 사용
        return tempFile
    }

}