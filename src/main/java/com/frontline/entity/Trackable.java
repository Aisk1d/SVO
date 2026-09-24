package com.frontline.entity;

import javax.annotation.Nullable;
import java.util.UUID;

/** Воздушная цель, которую видят радар и ПВО. Владелец нужен для системы «свой-чужой». */
public interface Trackable {
    String RADAR_TAG = "FrontlineTracked";

    @Nullable
    UUID getOwnerUuid();

    default boolean isEngageable() {
        return true;
    }

    static boolean friendly(@Nullable UUID a, @Nullable UUID b) {
        return a != null && a.equals(b);
    }
}
