package com.jmunoz.sec12.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class SlackMember {

    private static final Logger log = LoggerFactory.getLogger(SlackMember.class);

    private final String name;
    private Consumer<String> messageConsumer;

    public SlackMember(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void says(String msg) {
        this.messageConsumer.accept(msg);
    }

    void setMessageConsumer(Consumer<String> publisher) {
        this.messageConsumer = publisher;
    }

    void receives(String msg) {
        log.info(msg);
    }
}
