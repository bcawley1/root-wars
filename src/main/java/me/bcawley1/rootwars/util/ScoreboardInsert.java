package me.bcawley1.rootwars.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScoreboardInsert{
    @JsonProperty
    private String name;
    @JsonProperty
    private int insertNum;
    @JsonProperty
    private String tag;

    private ScoreboardInsert(){}

    public static ScoreboardInsert filter(List<ScoreboardInsert> list, String tagToGet){
        return list.stream()
                .filter(i -> i.tag.equalsIgnoreCase(tagToGet))
                .findFirst()
                .get();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getInsertNum() {
        return insertNum;
    }

    public String getTag() {
        return tag;
    }
}