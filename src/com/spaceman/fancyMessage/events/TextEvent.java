package com.spaceman.fancyMessage.events;

import com.spaceman.colorFormatter.ColorTheme;
import com.spaceman.fancyMessage.Message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface TextEvent {
    
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme);
    
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface InteractiveTextEvent {
    
    }
}
