package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
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
        private String lastNode;
        private Map<String, Integer> currentValues;
        private Set<String> triggers;

        public LocalProgress(String adventureId, boolean unlocked, String currentNode, Map<String, Integer> values) {
            this.adventureId = adventureId;
            this.unlocked = unlocked;
            this.currentNode = currentNode;
            this.lastNode = null;
            this.currentValues = values != null ? new HashMap<>(values) : new HashMap<>();
            this.triggers = new HashSet<>();
        }
    }
}
