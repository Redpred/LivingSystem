package com.redpred.livingsystem.api.protection;

import com.redpred.livingsystem.rule.definition.ProtectionProfile;

/**
 * 防护相关公开 API（见开发文档 §30）。供其他模组为装备注册防护能力。阶段一为骨架接口。
 */
public interface ProtectionApi {

    /** 注册一个防护装备画像。 */
    void registerProtectionProfile(ProtectionProfile profile);
}
