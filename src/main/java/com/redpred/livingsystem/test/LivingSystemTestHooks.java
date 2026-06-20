package com.redpred.livingsystem.test;

/**
 * 测试框架占位（见开发文档 §34）。
 *
 * <p>阶段一仅建立组织位置，列出后续阶段须覆盖的测试类型：单元测试（概率夹取、防护不重复减伤、症状
 * 叠加、治疗目标限制、暴露累积、恢复阶段转换、死亡因果链、配置覆盖优先级）、序列化与迁移测试、
 * GameTest 可复现场景、网络权限测试、多人性能测试。所有随机判定须支持固定随机种子。</p>
 *
 * <p>GameTest 启用需在 {@code build.gradle} 配置 {@code neoforge.enabledGameTestNamespaces} 并使用
 * {@code @GameTestHolder}/{@code @GameTest}，在后续阶段接入。</p>
 */
public final class LivingSystemTestHooks {

    private LivingSystemTestHooks() {
    }
}
