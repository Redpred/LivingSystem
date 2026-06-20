package com.redpred.livingsystem.rule.definition;

import net.minecraft.resources.ResourceLocation;

/**
 * 所有数据包规则定义的通用接口。每个定义至少具有唯一 ID 与启用标志，并允许覆盖功能策略。
 */
public interface RuleDefinition {

    /** 定义唯一 ID。 */
    ResourceLocation id();

    /** 是否启用该定义。 */
    boolean enabled();
}
