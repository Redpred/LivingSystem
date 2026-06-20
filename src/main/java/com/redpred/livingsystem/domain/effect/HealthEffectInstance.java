package com.redpred.livingsystem.domain.effect;

import java.util.UUID;

/**
 * 健康影响实例的密封接口。表示玩家实际获得的持续性健康影响（创伤、热/电/化学损伤、呼吸异常、
 * 中毒、感染、辐射、代谢与魔法异常）。
 *
 * <p>持续变化的健康影响使用可变运行时类实现本接口；用于网络与持久化的 DTO 另用不可变 {@code record}
 * （见开发文档 §5.4）。同一来源产生的多个影响共享 {@link #sourceEventId()}。</p>
 */
public sealed interface HealthEffectInstance
        permits TraumaInjuryState,
                ThermalInjuryState,
                ElectricalInjuryState,
                ChemicalInjuryState,
                RespiratoryInsultState,
                ToxicExposureState,
                PathogenState,
                RadiationExposureState,
                MetabolicConditionState,
                ArcaneConditionState {

    /** 实例唯一标识。 */
    UUID id();

    /** 产生该影响的伤害/暴露事件标识；同一事件的多个影响共享此值。 */
    UUID sourceEventId();

    /** 健康影响分类。 */
    HealthEffectCategory category();

    /** 来源快照。 */
    CauseSnapshot cause();

    /** 总体严重度，0.0～1.0。 */
    float severity();

    /** 创建时的游戏时间。 */
    long createdGameTime();

    /** 上次更新时的游戏时间。 */
    long lastUpdatedGameTime();

    /** 是否仍处于活动状态。 */
    boolean active();
}
