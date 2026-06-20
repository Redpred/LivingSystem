package com.redpred.livingsystem.service.exposure;

import net.minecraft.server.level.ServerPlayer;

/**
 * 环境暴露采样器（见开发文档 §12、§24.2）。按固定间隔检查玩家周围环境并计算当前暴露，
 * 必须限制扫描半径、分帧处理并缓存静态来源，不每 tick 全量扫描方块。
 */
public interface EnvironmentalExposureSampler {

    /** 对一名玩家执行一次环境采样。 */
    void sample(ServerPlayer player);
}
