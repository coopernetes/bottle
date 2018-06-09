package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringUI
@RequiredArgsConstructor
@Slf4j
public class IndexUI extends UI {

    private final MessageService messageService;
    private TextArea messagesTextArea = new TextArea("What others are saying...");
    private Panel mainPanel = new Panel("Messages in a bottle");

    @Override
    protected void init(VaadinRequest request) {
        messagesTextArea.setReadOnly(true);
        messagesTextArea.setSizeFull();

        setPollInterval(3000);
        addPollListener(e -> {
                if (needToRefresh()) {
                    repaintMessages(request);
                }
        });
        // Parent layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        // Message panel
        // Inner layout
        VerticalLayout messageLayout = new VerticalLayout();

        // Form panel
        FormLayout formLayout = new FormLayout();
        HorizontalLayout formHorizontal = new HorizontalLayout();
        formHorizontal.setSizeFull();
        TextField messageField = new TextField();
        messageField.setWidth("75%");
        messageField.setPlaceholder("Enter a message...");
        Button send = new Button("Send", VaadinIcons.BOAT);
        send.addClickListener(e -> {
            if (!messageField.isEmpty()) {
                messageService.saveMessage(messageField.getValue(), request.getRemoteAddr());
                e.getButton().setIcon(VaadinIcons.CHECK, "Sent");
                messageField.clear();
            }
        });
        formHorizontal.addComponents(messageField, send);
        formLayout.addComponent(formHorizontal);

        messageLayout.addComponents(messagesTextArea, formLayout);
        mainPanel.setContent(messageLayout);
        mainLayout.addComponent(mainPanel);
        setContent(mainLayout);

    }

    public void repaintMessages(VaadinRequest request) {
        int retry = 0;
        Message message = messageService.randomMessageEntity();
        while (message.getOriginator().equals(request.getRemoteAddr()) &&
                !messagesTextArea.getValue().equals(message.getContent())) {
            log.debug("Attempt to display a message that was posted by this user");
            message = messageService.randomMessageEntity();
            retry += 1;
            if (retry > 3) return;
        }

        log.debug("Setting message text area for message {}", message.toString());
        messagesTextArea.setValue(message.getContent());
    }

    boolean needToRefresh() {
        return messagesTextArea.getValue().isEmpty() ||
                (messageService.getNewMessage() != null &&
                (!messageService.getNewMessage().getContent().equals(messagesTextArea.getValue())));
    }
}
