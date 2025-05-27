package com.alejandroacg.choicebound.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalUser {
    private String username;
    private String uid;
    private String prefLanguage;
}
