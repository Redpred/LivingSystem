package com.redpred.livingsystem.service.pathogen;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.TransmissionRoute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 病原体引擎（见开发文档 §5.8、§23）。管理感染的阶段推进、载量与免疫对抗，并汇总全身感染负担。
 */
public interface PathogenEngine {

    /** 使玩家暴露于某病原体（无同源感染时创建处于潜伏的感染实例）。 */
    void infect(ServerPlayer player, ResourceLocation pathogenId, TransmissionRoute route, java.util.UUID sourceEventId);

    /** 推进玩家全部感染的阶段/载量，并对开放污染伤口做继发感染判定，移除已痊愈者。 */
    void tick(ServerPlayer player, PlayerHealthData data);

    /** 汇总当前全身感染负担（全部活动感染严重度之和，夹取 0~1）。 */
    float aggregateBurden(PlayerHealthData data);
}
