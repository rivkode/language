package com.learner.language.interfaces.diarychat

import com.learner.language.application.diarychat.DiaryChatPollView
import com.learner.language.application.diarychat.DiaryChatPollingFacade
import com.learner.language.system.login.LoginUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

@RestController
@RequestMapping("/api/v1/diary-chatrooms/{roomId}/messages")
class DiaryChatPollingController(
    private val facade: DiaryChatPollingFacade,
) {

    @GetMapping("/poll")
    fun poll(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
        @RequestParam(required = false, defaultValue = "0") after: Long,
        @RequestParam(required = false, defaultValue = "25") wait: Int,
    ): DeferredResult<DiaryChatPollView> = facade.poll(roomId, userId, after, wait)
}
