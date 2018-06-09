package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.github.tomcooperca.bottle.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public String randomMessage() {
        List<String> allMessages = StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .map(Message::getContent)
                .collect(Collectors.toList());
        Collections.shuffle(allMessages);
        return allMessages.get(new Random().nextInt(allMessages.size()));
    }

    public void saveMessage(String message, String originator) {
        messageRepository.save(new Message(new Random().nextLong(), message, originator));
    }
}
