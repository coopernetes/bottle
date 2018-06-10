package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.message.MessageService;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.animator.client.CssAnimation;
import org.vaadin.jouni.dom.client.Css;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringUI
@RequiredArgsConstructor
@Slf4j
public class IndexUI extends UI {

    private static final String DEFAULT_TITLE = "What others are saying...";

    private final MessageService messageService;
    private TextArea messageTextArea1 = new TextArea();
    private TextArea messageTextArea2 = new TextArea();
    private TextArea messageTextArea3 = new TextArea();
    private List<TextArea> messageAreas = Arrays.asList(messageTextArea1, messageTextArea2,
            messageTextArea3);
    private Panel mainPanel = new Panel("Latest messages");
    private Button send = new Button("Send a message", VaadinIcons.PENCIL);
    private TextField messageField = new TextField();


    @Override
    protected void init(VaadinRequest request) {
        setPollInterval(5000);
        addPollListener(e -> {
                updateOneMessage(request.getRemoteAddr());
                send.setIcon(VaadinIcons.PENCIL);
                send.setCaption("Send a message");
        });
        // Parent layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        setContent(mainLayout);

        // Message panel
        // Inner layout
        VerticalLayout messageLayout = new VerticalLayout();
        messageLayout.setSizeFull();
        messageAreas.forEach(this::configureTextArea);

        // Form panel
        FormLayout formLayout = new FormLayout();
        messageField.setPlaceholder("Enter a message...");
        messageField.setMaxLength(180);
        messageField.setWidth("90%");
        messageField.addShortcutListener(new ShortcutListener("Enter key shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                saveMessage(request);
            }
        });
        send.addClickListener(e -> saveMessage(request));
        formLayout.addComponents(messageField, send);
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setComponentAlignment(send, Alignment.MIDDLE_RIGHT);

        messageLayout.addComponents(messageTextArea1, messageTextArea2, messageTextArea3, formLayout);
        mainPanel.setContent(messageLayout);
        mainLayout.addComponent(mainPanel);

    }

    private void saveMessage(VaadinRequest request) {
        if (!messageField.isEmpty()) {
            messageService.saveMessage(messageField.getValue(), request.getRemoteAddr());
            send.setIcon(VaadinIcons.CHECK);
            send.setCaption("Sent!");
            messageField.clear();
        }
    }

    private void updateOneMessage(String originator) {
        Collections.shuffle(messageAreas);
        messageService.obtainFreshMessage(originator)
                .ifPresent(s -> messageAreas.get(0).setValue(s));

    }

    private void configureTextArea(TextArea textArea) {
        textArea.setReadOnly(true);
        textArea.setWordWrap(true);
        textArea.setWidth("100%");
        textArea.addValueChangeListener((e) -> {
            Animator.animate(new CssAnimation(e.getComponent(), new Css().opacity(0))
                    .duration(500));
            Animator.animate(new CssAnimation(e.getComponent(), new Css().opacity(1))
                    .duration(250));
        });
    }
}
