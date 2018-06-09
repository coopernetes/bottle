package com.github.tomcooperca.bottle.service

import com.github.tomcooperca.bottle.MessageService
import com.github.tomcooperca.bottle.repository.Message
import com.github.tomcooperca.bottle.repository.MessageRepository
import spock.lang.Specification
import spock.lang.Subject

class MessageServiceTest extends Specification {
    @Subject
    MessageService messageService
    MessageRepository messageRepository = Mock()

    def setup() {
        messageService = new MessageService(messageRepository)
    }


    def "saveMessage should write to the repository"() {
        when:
        messageService.saveMessage("this is a random message", "localhost")

        then:
        1 * messageRepository.save(_)
    }

    def "randomMessage should return a randomized message"() {
        when:
        def result = messageService.randomMessage()

        then:
        1 * messageRepository.findAll() >> [new Message(1, "message", "localhost"),
                                            new Message(2, "another message", "localhost"),
                                            new Message(3, "last message", "127.0.0.1")]
        result == "message" || result == "another message" || result == "last message"
    }
}
