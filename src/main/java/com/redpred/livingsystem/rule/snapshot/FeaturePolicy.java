package com.redpred.livingsystem.rule.snapshot;

/**
 * 功能策略（见开发文档 §3.6）。主要功能不应只有一个 {@code enabled} 字段，而是四个维度分别控制。
 *
 * @param generationEnabled      是否允许创建新的伤势、异常或症状来源
 * @param simulationEnabled      已有状态是否继续发展、恢复或参与健康循环
 * @param gameplayEffectsEnabled 是否影响移动、攻击、挖掘、冲刺和意识等实际玩法
 * @param presentationEnabled    是否生成客户端 HUD、画面和声音表现
 */
public record FeaturePolicy(
        boolean generationEnabled,
        boolean simulationEnabled,
        boolean gameplayEffectsEnabled,
        boolean presentationEnabled
) {
    /** 全部启用。 */
    public static final FeaturePolicy ALL_ON = new FeaturePolicy(true, true, true, true);
    /** 全部关闭。 */
    public static final FeaturePolicy ALL_OFF = new FeaturePolicy(false, false, false, false);
}
