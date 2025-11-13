package com.nano.clinicbooking.controller.chatbot;

import com.nano.clinicbooking.service.chat.RagChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class RagChatController {

    private final RagChatService ragChatService;

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return ragChatService.ask(question);
    }
}
