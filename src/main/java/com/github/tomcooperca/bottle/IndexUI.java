package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.google.common.collect.EvictingQueue;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
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
    private Button send = new Button("Send a message", VaadinIcons.PENCIL);
    private TextField messageField = new TextField();
    private EvictingQueue<String> messageQueue = EvictingQueue.create(3);

    @Override
    protected void init(VaadinRequest request) {
        setPollInterval(5000);
        addPollListener(e -> {
                displayMessage(request);
                send.setIcon(VaadinIcons.PENCIL);
                send.setCaption("Send a message");
        });
        // Parent layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        // Message panel
        // Inner layout
        VerticalLayout messageLayout = new VerticalLayout();

        messagesTextArea.setReadOnly(true);
        messagesTextArea.setWordWrap(true);
        messagesTextArea.setSizeFull();
        // Form panel
        FormLayout formLayout = new FormLayout();
        messageField.setWidth("75%");
        messageField.setPlaceholder("Enter a message...");
        messageField.setMaxLength(180);
        messageField.addShortcutListener(new ShortcutListener("Enter key shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                saveMessage(request);
            }
        });

        send.addClickListener(e -> saveMessage(request));
        formLayout.addComponents(messageField, send);

        messageLayout.addComponents(messagesTextArea, formLayout);
        mainPanel.setContent(messageLayout);
        mainLayout.addComponent(mainPanel);
        setContent(mainLayout);

    }

    private void saveMessage(VaadinRequest request) {
        if (!messageField.isEmpty()) {
            messageService.saveMessage(messageField.getValue(), request.getRemoteAddr());
            send.setIcon(VaadinIcons.CHECK);
            send.setCaption("Sent!");
            messageField.clear();
        }
    }

    private void displayMessage(VaadinRequest request) {
        int retry = 0;
        Message message = messageService.randomMessageEntity();
        while (message.getOriginator().equals(request.getRemoteAddr()) &&
                !messagesTextArea.getValue().equals(message.getContent())) {
            log.debug("Attempt to display a message that was posted by this user");
            message = messageService.randomMessageEntity();
            retry += 1;
            if (retry > 3) return;
        }
        log.debug("Adding message {} to queue", message.getUuid());
        if (!messageQueue.contains(message.getContent())) messageQueue.add(message.getContent());
        messagesTextArea.setValue(String.join("\n\n", messageQueue));
    }
}
