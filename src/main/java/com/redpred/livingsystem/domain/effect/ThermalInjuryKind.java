package com.redpred.livingsystem.domain.effect;

/**
 * 热损伤类型。{@link #SCALD} 仅用于热水、蒸汽或高温液体；岩浆使用 {@link #HEAT_BURN}
 * （见开发文档 §5.8）。
 */
public enum ThermalInjuryKind {
    /** 高温烧伤。 */
    HEAT_BURN,
    /** 烫伤（热水、蒸汽、高温液体）。 */
    SCALD,
    /** 低温冻伤。 */
    COLD_INJURY
}
