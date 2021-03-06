package com.spaceman.fancyMessage.events;

import com.spaceman.colorFormatter.ColorTheme;
import com.spaceman.fancyMessage.Message;

public class ScoreEvent implements TextEvent {
    
    private String name;
    private String objective;
    
    public ScoreEvent() {
    }
    
    @Override
    public String toString() {
        return "ScoreEvent{" +
                "name='" + name + '\'' +
                ", objective='" + objective + '\'' +
                '}';
    }
    
    public ScoreEvent(String name, String objective) {
        this.name = name;
        this.objective = objective;
    }
    
    public static ScoreEvent scoreEvent(String name, String objective) {
        return new ScoreEvent(name, objective);
    }
    
    @Override
    public String translateJSON(Message.TranslateMode mode, ColorTheme theme) {
        String q = mode.getQuote();
        return String.format(q +"score" + q + ":{" + q + "name" + q + ":" + q + "%s" + q + "," + q + "objective" + q + ":" + q + "%s" + q + "}", name, objective);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setObjective(String objective) {
        this.objective = objective;
    }
    
    public String getName() {
        return name;
    }
    
    public String getObjective() {
        return objective;
    }
}
