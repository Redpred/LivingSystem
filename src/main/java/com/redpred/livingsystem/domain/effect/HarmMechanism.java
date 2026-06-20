package com.redpred.livingsystem.domain.effect;

/**
 * 内部致伤机制。表示外部来源以何种物理或生物机制影响身体。
 *
 * <p>一个外部来源可根据规则产生多个机制（例如烟花同时产生爆炸、破片与高温）。具体物品名称
 * 不得作为致伤机制，武器/投射物/法术只提供配置并最终转换为统一机制（见开发文档 §5.1）。</p>
 */
public enum HarmMechanism {
    ABRASIVE,
    BLUNT,
    CRUSH,
    CUTTING,
    PENETRATING,
    BALLISTIC,
    BLAST,
    FRAGMENTATION,

    THERMAL_HEAT,
    THERMAL_COLD,
    ELECTRICAL,
    CHEMICAL_CORROSIVE,

    ASPHYXIA,
    TOXIC,
    BIOLOGICAL,
    RADIATION,
    METABOLIC,

    ARCANE,
    NECROTIC,
    VOID,
    FORCED_KILL,
    UNKNOWN
}
