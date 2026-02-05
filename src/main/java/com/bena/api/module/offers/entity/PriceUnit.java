package com.bena.api.module.offers.entity;

import lombok.Getter;

/**
 * وحدات السعر المتاحة
 */
@Getter
public enum PriceUnit {
    METER("للمتر"),
    PROJECT("للمشروع"),
    HOUR("للساعة"),
    DAY("لليوم"),
    CONSULTATION("للاستشارة");

    private final String arabicName;

    PriceUnit(String arabicName) {
        this.arabicName = arabicName;
    }
}
