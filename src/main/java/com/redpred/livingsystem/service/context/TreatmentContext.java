package com.redpred.livingsystem.service.context;

import com.redpred.livingsystem.domain.body.BodyRegion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 一次治疗请求的瞬时上下文（见开发文档 §18、§24.3）。瞬时输入对象，不持久化。
 *
 * <p>服务端必须基于此上下文重新验证全部条件，不信任客户端提交的伤势状态与治疗结果。</p>
 */
public record TreatmentContext(
        ServerPlayer practitioner,
        ServerPlayer patient,
        ResourceLocation treatmentActionId,
        ResourceLocation sourceItemId,
        @Nullable UUID targetEffectId,
        @Nullable BodyRegion targetRegion,
        long gameTime
) {
}
