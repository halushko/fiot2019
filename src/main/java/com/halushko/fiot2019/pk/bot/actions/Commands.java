package com.halushko.fiot2019.pk.bot.actions;

import java.util.HashMap;
import java.util.Map;

public enum  Commands {
    START("start");

    private final String text;
    private final static Map<Commands, String> commands = new HashMap<>();
    private final static Map<String, Commands> texts = new HashMap<>();

    Commands(String text) {
        this.text = text;
    }

    public static Commands get(String text){
        if(commands.isEmpty()) {
            for(Commands a: Commands.values()) {
                commands.put(a, a.text.toLowerCase());
                texts.put(a.text.toLowerCase(), a);
            }
        }
        return text != null ? texts.get((!text.startsWith("/") ? text : text.substring(1)).toLowerCase()) : null;
    }

    public String text(){
        return text;
    }

    public boolean is(String text){
        return text != null && this.equals(get(text));
    }
}
