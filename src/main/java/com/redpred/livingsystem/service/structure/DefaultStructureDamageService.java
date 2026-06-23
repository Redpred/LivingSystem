package com.redpred.livingsystem.service.structure;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;

/**
 * {@link StructureDamageService} 默认实现：是唯一修改结构完整度的入口（见开发文档 §17 不变量 2）。
 * 结构按需创建（未存在视为完整 1.0），{@code integrity} 的上下限由 {@link StructureState#setIntegrity} 保证。
 */
public final class DefaultStructureDamageService implements StructureDamageService {

    @Override
    public void applyStructureDamage(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount) {
        if (amount <= 0) {
            return;
        }
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState == null) {
            return;
        }
        StructureState state = regionState.getOrCreateStructure(structure);
        state.setIntegrity(state.getIntegrity() - amount);
    }

    @Override
    public void applyStructureRepair(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount) {
        if (amount <= 0) {
            return;
        }
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState == null) {
            return;
        }
        StructureState state = regionState.getOrCreateStructure(structure);
        state.setIntegrity(state.getIntegrity() + amount);
    }
}
