package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalUser {
    private String username;
    private String uid;
    private String prefLanguage;
    private Map<String, LocalProgress> progress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalProgress {
        private String adventureId;
        private boolean unlocked;
        private String currentNode;
        private int currentHero;
        private int currentCoward;
        private int currentKiller;
    }
}
