package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.domain.PlayerHealthData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 玩家健康数据附件注册。
 *
 * <p>全模组只注册这一个玩家附件，对应唯一聚合根 {@link PlayerHealthData}（见开发文档 §20）。
 * 使用 Codec 序列化以随玩家存档持久化；死亡/重生的精细克隆策略在后续阶段通过克隆事件实现，
 * 阶段一采用默认行为（不跨死亡复制）。</p>
 */
public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, LivingSystemMod.MOD_ID);

    /** 玩家健康数据附件。 */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerHealthData>> PLAYER_HEALTH =
            ATTACHMENT_TYPES.register("player_health", () -> AttachmentType
                    .<PlayerHealthData>builder(() -> new PlayerHealthData())
                    .serialize(PlayerHealthData.CODEC)
                    .build());

    private ModAttachments() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
