package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.service.context.DamageContext;

import java.util.Optional;

/**
 * 解析伤害来源对应的 {@link DamageProfile}（DamageType、武器、实体、投射物配置，见开发文档 §23）。
 */
public interface DamageProfileResolver {

    /** 解析本次伤害的画像；无配置时返回空。 */
    Optional<DamageProfile> resolve(DamageContext context);
}
