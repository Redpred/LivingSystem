package com.redpred.livingsystem.domain.body;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 单个身体部位的可变状态。
 *
 * <p>身体部位不具有独立生命值，只保存：该部位拥有的结构状态、关联的活动健康影响 ID，
 * 以及（后续阶段加入的）必要局部治疗状态。部位功能由结构、伤势与疼痛动态推导，不在此持久化。</p>
 */
public final class BodyRegionState {
    /** 该部位拥有的解剖结构及其状态。 */
    private final EnumMap<AnatomicalStructure, StructureState> structures = new EnumMap<>(AnatomicalStructure.class);
    /** 关联到该部位的活动健康影响 ID。 */
    private final Set<UUID> activeEffectIds = new HashSet<>();

    public BodyRegionState() {
    }

    public EnumMap<AnatomicalStructure, StructureState> getStructures() {
        return structures;
    }

    /** 获取指定结构的状态，不存在时返回 {@code null}。 */
    public StructureState structure(AnatomicalStructure structure) {
        return structures.get(structure);
    }

    /** 获取指定结构的状态，不存在时创建默认状态。 */
    public StructureState getOrCreateStructure(AnatomicalStructure structure) {
        return structures.computeIfAbsent(structure, key -> new StructureState());
    }

    public Set<UUID> getActiveEffectIds() {
        return activeEffectIds;
    }
}
