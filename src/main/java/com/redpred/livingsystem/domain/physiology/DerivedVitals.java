package com.redpred.livingsystem.domain.physiology;

/**
 * 派生生命体征（见开发文档 §7.2）。
 *
 * <p>不是权威持久化资源，由底层状态计算得出；允许缓存，但底层状态变化后必须使缓存失效并重算。
 * 不可变 {@code record}。</p>
 */
public record DerivedVitals(
        float externalBleedRate,
        float internalBleedRate,
        float perfusionIndex,
        float heartRate,
        float systolicPressure,
        float respiratoryRate,
        float oxygenSaturation,
        float totalPain,
        float shockLevel,
        float systemicInfectionBurden,
        float systemicToxinBurden,
        float systemicRadiationBurden,
        float recoveryCapacity
) {
    /** 全部为 0 的占位体征。 */
    public static final DerivedVitals EMPTY =
            new DerivedVitals(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
}
