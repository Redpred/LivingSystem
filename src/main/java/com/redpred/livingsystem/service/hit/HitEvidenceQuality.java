package com.redpred.livingsystem.service.hit;

/**
 * 命中信息质量等级（见开发文档 §6.2）。表示本次命中部位判定所依据证据的可靠程度。
 */
public enum HitEvidenceQuality {
    /** 精确命中坐标。 */
    EXACT,
    /** 投射物射线近似。 */
    RAY_APPROXIMATION,
    /** 仅有方向信息。 */
    DIRECTION_ONLY,
    /** 使用来源专用规则。 */
    SOURCE_RULE,
    /** 确定性加权随机后备。 */
    RANDOM_FALLBACK,
    /** 无法局部化（全身作用）。 */
    NOT_LOCALIZABLE
}
