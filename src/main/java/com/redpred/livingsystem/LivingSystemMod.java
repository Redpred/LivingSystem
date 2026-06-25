package com.redpred.livingsystem;

import com.mojang.logging.LogUtils;
import com.redpred.livingsystem.bootstrap.ModAttachments;
import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.bootstrap.ModContent;
import com.redpred.livingsystem.bootstrap.ModDataComponents;
import com.redpred.livingsystem.bootstrap.ModPayloads;
import com.redpred.livingsystem.bootstrap.ModRegistries;
import com.redpred.livingsystem.content.MedicalContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * LivingSystem 模组主入口。
 *
 * <p>仅负责把各注册聚合类挂载到模组事件总线，并注册服务端配置。具体业务逻辑位于领域服务层，
 * 主类不直接承载任何健康计算（见开发文档 §18 分层约束）。</p>
 */
@Mod(LivingSystemMod.MOD_ID)
public final class LivingSystemMod {

    /** 模组与资源命名空间标识。 */
    public static final String MOD_ID = "livingsystem";

    /** 全模组共享的 SLF4J 日志器。 */
    public static final Logger LOGGER = LogUtils.getLogger();

    public LivingSystemMod(IEventBus modBus, ModContainer container) {
        // 先触发内容静态注册，再将各 DeferredRegister 绑定到事件总线。
        MedicalContent.init();
        ModContent.register(modBus);
        ModRegistries.register(modBus);
        ModAttachments.register(modBus);
        ModDataComponents.register(modBus);
        ModPayloads.register(modBus);
        ModConfigs.register(container);
        com.redpred.livingsystem.api.LivingSystemApiHolder.set(
                new com.redpred.livingsystem.api.internal.DefaultLivingSystemApi());
        LOGGER.info("LivingSystem 已构造。");
    }
}
