package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalNode {
    private String uid;
    private String adventureId;
    private String text;
    private String image;
    private String music;
    private List<LocalChoice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalChoice {
        private String text;
        private String nextNodeId;
        private Integer modifierLife;
        private Integer modifierHero;
        private Integer modifierCoward;
        private Integer modifierKiller;
    }
}
