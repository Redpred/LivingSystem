package com.redpred.livingsystem.service.exposure;

/**
 * {@link EnvironmentalHazardRegistry} 默认实现。阶段一无任何环境危害定义。
 */
public final class DefaultEnvironmentalHazardRegistry implements EnvironmentalHazardRegistry {

    @Override
    public int size() {
        return 0;
    }
}
