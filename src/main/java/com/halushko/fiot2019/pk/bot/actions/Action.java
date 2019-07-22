package com.halushko.fiot2019.pk.bot.actions;

import java.util.HashMap;
import java.util.Map;

public abstract class Action<T> {
    private T input;
    protected Action(T input) {
        this.input = input;
    }

    public final void doAction(){
        doAction(new HashMap<>());
    }
    public final void doAction(Map<String, Object> params){
        if(isValidCase(input)) {
            doActionRealisation(params);
        }
    }

    protected abstract boolean isValidCase(T input);
    protected abstract void doActionRealisation(Map<String, Object> params);
}
