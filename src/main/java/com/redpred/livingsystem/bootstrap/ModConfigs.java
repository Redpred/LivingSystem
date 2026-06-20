package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.client.config.ClientConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置注册。
 *
 * <p>服务端配置控制会影响实际游戏结果的功能开关与全局倍率；客户端配置只控制画面、声音与界面
 * （见开发文档 §22、§3）。阶段一仅建立服务端总开关，完整开关与倍率在 {@code rule} 层接入
 * {@code FeatureGateService} 后扩展；客户端配置在 {@code client.config} 中建立。</p>
 */
public final class ModConfigs {

    /** 服务端配置规格。 */
    public static final ModConfigSpec SERVER_SPEC;

    /** 模组总开关。 */
    public static final ModConfigSpec.BooleanValue MASTER_ENABLED;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MASTER_ENABLED = builder
                .comment("模组总开关。关闭后不生成新的健康影响、不运行健康循环、不施加属性与操作限制；",
                        "但保留已有健康数据与全部注册项，以便重新开启后继续使用。")
                .define("masterEnabled", true);
        SERVER_SPEC = builder.build();
    }

    private ModConfigs() {
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
