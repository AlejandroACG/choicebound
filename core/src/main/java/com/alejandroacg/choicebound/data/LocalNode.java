package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalNode {
    private String uid;
    private String adventureId;
    private String text;
    private String image;
    private String music;
    private Map<String, LocalChoice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalChoice {
        private String choiceId;
        private String text;
        private String nextNodeId;
        private Map<String, Integer> modifierValues;
        private List<String> triggerToSet;
        private List<String> triggerToRemove;
        private String conditionValues;
        private List<String> conditionTriggersPositive;
        private List<String> conditionTriggersNegative;
    }
}
