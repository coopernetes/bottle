package com.github.tomcooperca.bottle.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/message")
    public ResponseEntity<String> randomMessage() {
        // average reading speed = 200 wpm = 3(ish) words per second
        return ResponseEntity.ok(messageService.randomMessage());
    }

    @GetMapping("/message/{uuid:(?!all)[\\w\\-]+}")
    public ResponseEntity<String> getMessageByUuid(@PathVariable String uuid) {
        return messageService.getMessage(uuid)
                .map(Message::getContent)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("Could not find message ".concat(uuid)));
    }

    @PostMapping("/message")
    public ResponseEntity addMessage(HttpServletRequest request, @RequestBody String message) {
        return messageService.saveMessage(message, request.getRemoteAddr())
                .map(m -> ResponseEntity.created(linkBuilder(m.getUuid())).build())
                .orElse(ResponseEntity.badRequest().body("Could not save message."));
    }

    @GetMapping("/messages")
    public ResponseEntity allMessages() {
        return ResponseEntity.ok(messageService.allMessages());
    }

    @DeleteMapping("/messages")
    public ResponseEntity deleteAllMessages() {
        messageService.deleteAllMessages();
        return ResponseEntity.noContent().build();
    }

    private URI linkBuilder(String uuid) {
        return linkTo(methodOn(MessageController.class).getMessageByUuid(uuid)).toUri();
    }

}
