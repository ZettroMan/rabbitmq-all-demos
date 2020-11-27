package com.flamexander.rabbitmq.console.common;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = -216459326668452359L;

    private String content;

    public String getContent() {
        return content;
    }

    public Article(String content) {
        this.content = content;
    }
}
