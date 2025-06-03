package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalAdventure {
    private String uid;
    private String title;
    private String cover;
    private Map<String, Integer> initialValues;

    public LocalAdventure(String uid, String title, String cover, int initialHero, int initialCoward, int initialKiller, int initialLives) {
        this.uid = uid;
        this.title = title;
        this.cover = cover;
        this.initialValues = new HashMap<>();
        this.initialValues.put("hero", initialHero);
        this.initialValues.put("coward", initialCoward);
        this.initialValues.put("killer", initialKiller);
        this.initialValues.put("lives", initialLives);
    }
}
