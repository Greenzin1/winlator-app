package com.winlator.box86;

import androidx.annotation.NonNull;

public class Box86Preset {
    public static final String STABILITY = "STABILITY";
    public static final String CONSERVATIVE = "CONSERVATIVE";
    public static final String INTERMEDIATE = "INTERMEDIATE";
    public static final String PERFORMANCE = "PERFORMANCE";
    public static final String CUSTOM = "CUSTOM";
    public static final String DEFAULT = PERFORMANCE;
    public final String id;
    public final String name;

    public Box86Preset(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isCustom() {
        return id.startsWith(CUSTOM);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}