package com.learner.language.interfaces.user

import com.learner.language.application.user.UserFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserApiController(
    private val userFacade: UserFacade
) {

    @PostMapping
    fun registerUser(
        @Valid @RequestBody registerRequest: UserDto.RegisterRequest
    ): ResponseEntity<UserDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val userInfo = userFacade.registerUser(command)
        val response = UserDto.RegisterResponse(userInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/validation-number")
    fun sendValidationNumber(
        @Valid @RequestBody request: UserDto.ValidateNumberRequest
    ): ResponseEntity<Void> {
        userFacade.sendValidationNumberToEmail(request.email)

        return ResponseEntity.noContent().build()
    }

    @PostMapping("/validation-email")
    fun validateEmail(
        @Valid @RequestBody request: UserDto.ValidateNumberRequest
    ) : ResponseEntity<Void> {
        userFacade.validateNumber(request)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me")
    fun getMyInfo(@LoginUser userId: Long
    ): ResponseEntity<UserDto.InfoResponse> {
        val userInfo = userFacade.retrieveUserInfo(userId)
        val response = UserDto.InfoResponse(userInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
