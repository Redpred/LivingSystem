package com.redpred.livingsystem.api.damage;

import com.redpred.livingsystem.rule.definition.DamageProfile;

/**
 * 伤害相关公开 API（见开发文档 §30）。供其他模组注册伤害画像、武器/实体/投射物配置并提供精确命中证据。
 * 阶段一为骨架接口，方法签名将随功能阶段扩展。
 */
public interface DamageApi {

    /** 注册一个伤害画像。 */
    void registerDamageProfile(DamageProfile profile);
}
