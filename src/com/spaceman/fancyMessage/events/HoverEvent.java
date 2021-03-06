package com.spaceman.fancyMessage.events;

import com.spaceman.colorFormatter.ColorTheme;
import com.spaceman.fancyMessage.Message;
import com.spaceman.fancyMessage.TextComponent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.spaceman.fancyMessage.TextComponent.textComponent;

@TextEvent.InteractiveTextEvent
public class HoverEvent implements TextEvent {
    
    private ArrayList<TextComponent> text = new ArrayList<>();
    
    public HoverEvent() {
    }
    
    @Override
    public String toString() {
        return text.stream().map(TextComponent::toString).collect(Collectors.joining());
    }
    
    public HoverEvent(TextComponent... textComponents) {
        for (TextComponent textComponent : textComponents) {
            textComponent.clearInteractiveEvents();
            this.text.add(textComponent);
        }
    }
    
    public static HoverEvent hoverEvent(String simpleText) {
        return hoverEvent(textComponent(simpleText));
    }
    
    public static HoverEvent hoverEvent(String simpleText, ChatColor color) {
        return hoverEvent(textComponent(simpleText, color));
    }
    
    public static HoverEvent hoverEvent(TextComponent... textComponent) {
        HoverEvent hEvent = new HoverEvent();
        for (TextComponent text : textComponent) {
            hEvent.addText(text);
        }
        return hEvent;
    }
    
    @Override
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme) {
        String q = mode.getQuote();
        return "" + q + "hoverEvent" + q + ":{" + q + "action" + q + ":" + q + "show_text" + q + "," + q + "value" + q + ":[" +
                this.text.stream().map(t -> t.translateJSON(mode, theme)).collect(Collectors.joining(",")) +
                "]}";
    }
    
    public void addMessage(Message message) {
        if (message != null) {
            for (TextComponent textComponent : message.getText()) {
                addText(textComponent);
            }
        }
    }
    
    public void addText(TextComponent... text) {
        for (TextComponent textComponent : text) {
            textComponent.clearInteractiveEvents();
            this.text.add(textComponent);
        }
    }
    
    public void addText(String simpleText) {
        this.text.add(textComponent(simpleText));
    }
    
    public void addText(String simpleText, ChatColor color) {
        this.text.add(textComponent(simpleText, color));
    }
    
    public ArrayList<TextComponent> getText() {
        return text;
    }
    
    public void removeLast() {
        this.text.remove(text.size() - 1);
    }
    
}

