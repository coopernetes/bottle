package com.github.tomcooperca.bottle.message

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

class MessageControllerTest extends Specification {
    @Subject
    MessageController messageController

    MockMvc mockMvc
    MessageService messageService = Mock()

    def setup() {
        messageController = new MessageController(messageService)
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .build()
    }

    def "RandomMessage should return a single message content"() {
        when:
        def response = mockMvc.perform(get("/message")).andReturn().response

        then:
        1 * messageService.randomMessage() >> "Random message"
        response.status == HttpStatus.OK.value()
        response.contentAsString == "Random message"

    }

    def "GetMessageByUuid should return a message"() {
        given:
        def uuid = UUID.randomUUID().toString()

        when:
        def response = mockMvc.perform(get("/message/${uuid}")).andReturn().response

        then:
        1 * messageService.getMessage(uuid) >> Optional.of(new Message(uuid, "Specific message",
                "localhost", "en_CA"))
        response.status == HttpStatus.OK.value()
        response.contentAsString == "Specific message"
    }

    def "GetMessageByUuid should return 400 when message is not found"() {
        given:
        def uuid = UUID.randomUUID().toString()

        when:
        def response = mockMvc.perform(get("/message/${uuid}")).andReturn().response

        then:
        1 * messageService.getMessage(uuid) >> Optional.empty()
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    def "AddMessage for unique content should return 201 with correct location"() {
        given:
        def uuid = UUID.randomUUID().toString()
        def message = "Creating a new message"

        when:
        def response = mockMvc.perform(post("/message")
                .content(message)
                .contentType(MediaType.TEXT_PLAIN)).andReturn().response

        then:
        1 * messageService.saveMessage(message, _) >> Optional.of(new Message(uuid, message, "localhost",
                "en_CA"))
        response.status == HttpStatus.CREATED.value()
        response.getHeader("Location") ==~ /http:\/\/localhost\/message\/${uuid}/
    }

    def "AddMessage for non-unique content should return 400"() {
        given:
        def uuid = UUID.randomUUID().toString()
        def message = "Creating a new duplicate message"

        when:
        def response = mockMvc.perform(post("/message")
                .content(message)
                .contentType(MediaType.TEXT_PLAIN)).andReturn().response

        then:
        1 * messageService.saveMessage(message, _) >> Optional.empty()
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    def "AllMessages should return a JSON list of messages"() {
        when:
        def response = mockMvc.perform(get("/messages")).andReturn().response

        then:
        1 * messageService.allMessages() >> [new Message("1", "First message", "localhost", "en_CA"),
                                             new Message("2", "Second message", "localhost", "en_CA")]
        response.status == HttpStatus.OK.value()
        final List<Message> messages = new Gson().fromJson(response.contentAsString, new TypeToken<List<Message>>() {}.getType())
        messages.any {
            it.content == "First message" || it.content == "Second message"
        }
    }

    def "DeleteAllMessages should delete all messages"() {
        when:
        def response = mockMvc.perform(delete("/messages")).andReturn().response

        then:
        1 * messageService.deleteAllMessages()
        response.status == HttpStatus.NO_CONTENT.value()
    }
}
