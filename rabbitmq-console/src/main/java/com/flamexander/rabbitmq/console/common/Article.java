package com.flamexander.rabbitmq.console.common;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = -216459326668452359L;

    private String theme;
    private String content;

    public Article(String theme, String content) {
        this.theme = theme;
        this.content = content;
    }

    @Override
    public String toString() {
        return "=========================\nTheme: " +
                theme + "\n\n" + content;
    }
}
