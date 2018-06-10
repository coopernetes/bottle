package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.github.tomcooperca.bottle.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j

public class MessageService {

    private final MessageRepository messageRepository;
    private static final int AVERAGE_WPS = 3;

    public String randomMessage() {
        List<String> allMessages = allMessages().stream()
                .map(Message::getContent)
                .collect(Collectors.toList());
        Collections.shuffle(allMessages);
        return allMessages.get(new Random().nextInt(allMessages.size()));
    }

    public void saveMessage(String message, String originator) {
        if (StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .noneMatch(m -> m.getContent().equals(message))) {
            messageRepository.save(new Message(UUID.randomUUID().toString(), message, originator, Locale.getDefault().toString()));
        }
    }

    public Message randomMessageEntity() {
        List<Message> allMessages = allMessages();
        Collections.shuffle(allMessages);
        return allMessages.get(new Random().nextInt(allMessages.size()));
    }

    public List<Message> allMessages() {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Message> randomizedAllMessages() {
        List<Message> messages = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Collections.shuffle(messages);
        return messages;
    }

    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }

    public int calculatePoll(String message) {
        return message == null ? 5000 : (message.split(" ").length / AVERAGE_WPS) * 1125;
    }
}
