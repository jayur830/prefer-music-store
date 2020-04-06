package com.prefer_music_store.app.util;

import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

public class MessageUtils {
    private static MessageSourceAccessor messageSourceAccessor;

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        MessageUtils.messageSourceAccessor = messageSourceAccessor;
    }

    public static String getMessage(String code) {
        return messageSourceAccessor.getMessage(code, Locale.getDefault());
    }

    public static String getMessage(String code, Object[] objects) {
        return messageSourceAccessor.getMessage(code, objects, Locale.getDefault());
    }
}
