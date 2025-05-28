package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalAdventure {
    private String uid;
    private String title;
    private String cover;
    private int initialHero;
    private int initialCoward;
    private int initialKiller;
}
