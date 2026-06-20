package com.redpred.livingsystem.domain.effect;

/**
 * 病原体传播途径（见开发文档 §5.8）。
 */
public enum TransmissionRoute {
    AIRBORNE,
    DROPLET,
    DIRECT_CONTACT,
    FOOD_OR_WATER,
    WOUND,
    VECTOR
}
