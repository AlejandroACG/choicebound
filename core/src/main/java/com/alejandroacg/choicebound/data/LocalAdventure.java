package com.alejandroacg.choicebound.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalAdventure {
    private String uid;
    private String title;
    private String cover;
    private Map<String, Integer> initialValues;
}
