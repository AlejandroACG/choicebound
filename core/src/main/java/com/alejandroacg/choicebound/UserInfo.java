package com.alejandroacg.choicebound;

public class UserInfo {
    private final String displayName;
    private final String uid;

    public UserInfo(String displayName, String uid) {
        this.displayName = displayName != null ? displayName : "";
        this.uid = uid != null ? uid : "";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    public boolean isEmpty() {
        return displayName.isEmpty() && uid.isEmpty();
    }
}
