package com.redpred.livingsystem.compatibility.vanilla;

/**
 * 原版资源桥接的事件接入点（见开发文档 §7.6、阶段任务 12）。
 *
 * <p>原版生命值、饥饿值、自然回血与溺水的取消/转换，以及死亡重入保护集中于此接入，由
 * {@code service.resource.VanillaResourceBridge} 提供逻辑。<b>阶段一不启用最终伤害替换</b>，
 * 仅保留接入骨架；实际事件挂载在后续阶段实现。</p>
 */
public final class VanillaResourceBridgeHooks {

    private VanillaResourceBridgeHooks() {
    }
}
