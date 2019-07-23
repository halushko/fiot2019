package com.halushko.fiot2019.pk.bot.actions.tasks;

public final class UserInfo {
    public final Long userId;
    private Integer number = null;
    private String name = null;

    public UserInfo(Long userId) {
        this.userId = userId;
    }

    public UserInfo setNumber(int number){
        this.number = number;
        return this;
    }

    public Integer getNumber(){
        return number;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        return this;
    }
}
