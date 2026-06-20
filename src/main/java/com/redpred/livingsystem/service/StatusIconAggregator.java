package com.redpred.livingsystem.service;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 状态图标汇总（见开发文档 §15.3.6、§23）。按优先级汇总应在状态图标列表中显示的条目。
 * 阶段一以图标 ID 列表表示，后续阶段扩展为带严重度/时间/部位的图标条目。
 */
public interface StatusIconAggregator {

    /** 汇总当前应显示的状态图标 ID 列表（按优先级排序）。 */
    List<ResourceLocation> aggregate(PlayerHealthData data);
}
