package com.redpred.livingsystem.service.structure;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;

/**
 * {@link StructureDamageService} 默认实现。阶段一为空操作，不改变任何结构完整度。
 */
public final class DefaultStructureDamageService implements StructureDamageService {

    @Override
    public void applyStructureDamage(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount) {
        // 阶段一占位
    }

    @Override
    public void applyStructureRepair(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount) {
        // 阶段一占位
    }
}
