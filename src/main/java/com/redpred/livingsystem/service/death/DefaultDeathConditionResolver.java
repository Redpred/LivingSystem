package com.redpred.livingsystem.service.death;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.domain.body.BodyRegionState;
import com.redpred.livingsystem.domain.body.StructureState;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@link DeathConditionResolver} 默认实现（唯一死亡判定入口，见开发文档 §7 死亡、§17 不变量 3）。
 *
 * <p>阶段二 2.1 判定：失血低于不可维持比例，或心脏/脑结构完整度归零。2.6 增加缺氧终末（氧债满）。
 * 其余条件（循环崩溃、毒素/感染致死等）随对应子里程碑接入。</p>
 */
public final class DefaultDeathConditionResolver implements DeathConditionResolver {

    @Override
    public boolean shouldDie(ServerPlayer player, PlayerHealthData data) {
        PhysiologyState p = data.physiology();
        float fraction = p.getMaxBloodVolume() > 0 ? p.getCurrentBloodVolume() / p.getMaxBloodVolume() : 1.0F;
        if (fraction < ModConfigs.UNSURVIVABLE_BLOOD_FRACTION.get().floatValue()) {
            return true;
        }
        // 缺氧终末：氧债累积至上限（取代原版溺水致死，见 §7.5/§8.6）。
        if (p.getOxygenDebt() >= 1.0F) {
            return true;
        }
        return destroyed(data, BodyRegion.CHEST, AnatomicalStructure.HEART)
                || destroyed(data, BodyRegion.HEAD_NECK, AnatomicalStructure.BRAIN);
    }

    private static boolean destroyed(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure) {
        BodyRegionState regionState = data.bodyRegions().get(region);
        if (regionState == null) {
            return false;
        }
        StructureState state = regionState.structure(structure);
        return state != null && state.getIntegrity() <= 0.0F;
    }
}
