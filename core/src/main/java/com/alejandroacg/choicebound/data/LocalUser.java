package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        private Set<String> triggers;

        public LocalProgress(String adventureId, boolean unlocked, String currentNode, int currentHero, int currentCoward, int currentKiller) {
            this.adventureId = adventureId;
            this.unlocked = unlocked;
            this.currentNode = currentNode;
            this.currentHero = currentHero;
            this.currentCoward = currentCoward;
            this.currentKiller = currentKiller;
            this.triggers = new HashSet<>();
        }
    }
}
