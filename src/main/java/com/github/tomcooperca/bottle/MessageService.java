package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.github.tomcooperca.bottle.repository.MessageRepository;
import lombok.Getter;
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
    @Getter
    private volatile Message newMessage = null;

    public synchronized void exipireNewMessage() {
        log.debug("Checking if new messages have been received");
        if (newMessage != null) {
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                log.warn("3s cooldown on message received interrupted");
            }
            newMessage = null;
        }
    }

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
            newMessage = messageRepository.save(new Message(UUID.randomUUID(), message, originator, Locale.getDefault()));
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
}
