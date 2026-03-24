package com.learner.language.interfaces.chat

import com.learner.language.application.chat.ChatFacade
import com.learner.language.system.login.LoginUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/chat")
class ChatApiController(
    private val chatFacade: ChatFacade
) {
    @GetMapping("/hello")
    fun hello(): ResponseEntity<ChatDto.HelloResponse> {
        val response = ChatDto.HelloResponse(chatFacade.hello())

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping
    fun registerChat(
        @LoginUser userId: Long,
        @Valid @RequestBody registerRequest: ChatDto.RegisterRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val chatInfo = chatFacade.registerChat(command, userId)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/generate")
    fun generateChat(
        @LoginUser userId: Long,
        @Valid @RequestBody generateRequest: ChatDto.GenerateRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = generateRequest.toCommand()
        val chatInfo = chatFacade.generateChat(command, userId)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Method: POST
     * Content-Type: multipart/form-data
     *
     * @param userId 로그인한 사용자의 ID.
     * @param audioFile 전사할 오디오 파일. 요청의 'audio' 필드에 해당합니다.
     * @return 전사된 텍스트와 관련 정보.
     */
    @PostMapping(
        "/transcribe",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun transcribeChat(
        @LoginUser userId: Long,
        @RequestParam chatRoomId: Long,
        @RequestPart("audio") audioFile: MultipartFile,
        request: HttpServletRequest
    ): ResponseEntity<ChatDto.TranscribeResponse> {

        val origin = request.getHeader("Origin")
        val referer = request.getHeader("Referer")
        val userAgent = request.getHeader("User-Agent")
        val host = request.getHeader("Host")

        println("=== CORS DEBUG INFO ===")
        println("Origin: $origin")
        println("Referer: $referer")
        println("User-Agent: $userAgent")
        println("Host: $host")
        println("Remote Address: ${request.remoteAddr}")
        println("======================")

        val transcribeInfo = chatFacade.transcribeAudio(userId, chatRoomId, audioFile)
        val response = ChatDto.TranscribeResponse(transcribeInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping(
        "/speech",
    )
    fun speechAudio(
        @LoginUser userId: Long,
        @Valid @RequestBody speechRequest: ChatDto.SpeechRequest
    ): ResponseEntity<ChatDto.SpeechResponse> {
        val command = speechRequest.toCommand()
        val speechInfo = chatFacade.speechAudio(command, userId)
        val response = ChatDto.SpeechResponse(speechInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/greeting")
    fun greeting(
        @LoginUser userId: Long,
        @Valid @RequestBody generateRequest: ChatDto.GenerateRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = generateRequest.toCommand()
        val chatInfo = chatFacade.greetingChat(userId, command)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/paraphrase")
    fun phrase(
        @Valid @RequestBody phraseRequest: ChatDto.PhraseRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = phraseRequest.toCommand()
        val chatInfo = chatFacade.phraseChat(command)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

}
