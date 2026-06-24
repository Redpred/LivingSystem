package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.data.EnvironmentalHazardReloadListener;
import com.redpred.livingsystem.rule.definition.EnvironmentalHazardProfile;

import java.util.List;

/**
 * {@link EnvironmentalHazardRegistry} 默认实现。委托数据包重载监听器持有的当前危害快照。
 */
public final class DefaultEnvironmentalHazardRegistry implements EnvironmentalHazardRegistry {

    @Override
    public int size() {
        return EnvironmentalHazardReloadListener.all().size();
    }

    @Override
    public List<EnvironmentalHazardProfile> all() {
        return EnvironmentalHazardReloadListener.all();
    }
}
