package com.redpred.livingsystem.bootstrap;

import com.redpred.livingsystem.client.config.ClientConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置注册。
 *
 * <p>服务端配置控制会影响实际游戏结果的功能开关与全局倍率；客户端配置只控制画面、声音与界面
 * （见开发文档 §22、§3）。阶段二起逐步加入生理与桥接相关的服务端数值。</p>
 */
public final class ModConfigs {

    /** 服务端配置规格。 */
    public static final ModConfigSpec SERVER_SPEC;

    /** 模组总开关。 */
    public static final ModConfigSpec.BooleanValue MASTER_ENABLED;
    /** 健康循环 tick 间隔（游戏刻）。 */
    public static final ModConfigSpec.IntValue TICK_INTERVAL;
    /** 最大血液容量（毫升），见开发文档 §7.1。 */
    public static final ModConfigSpec.DoubleValue MAX_BLOOD_VOLUME;
    /** 不可维持血量比例：当前血量低于 最大血量×该比例 即进入致死条件，见开发文档 §7.1。 */
    public static final ModConfigSpec.DoubleValue UNSURVIVABLE_BLOOD_FRACTION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MASTER_ENABLED = builder
                .comment("模组总开关。关闭后不生成新的健康影响、不运行健康循环、不施加属性与操作限制；",
                        "但保留已有健康数据与全部注册项，以便重新开启后继续使用。")
                .define("masterEnabled", true);
        TICK_INTERVAL = builder
                .comment("健康循环的更新间隔（游戏刻），默认每 5 刻一次（见开发文档 §29 调度）。")
                .defineInRange("tickInterval", 5, 1, 100);
        MAX_BLOOD_VOLUME = builder
                .comment("玩家最大血液容量（毫升），默认 5000。")
                .defineInRange("maxBloodVolume", 5000.0, 100.0, 100000.0);
        UNSURVIVABLE_BLOOD_FRACTION = builder
                .comment("不可维持血量比例：当前血量低于 最大×该值 即满足失血致死条件。")
                .defineInRange("unsurvivableBloodFraction", 0.15, 0.0, 1.0);
        SERVER_SPEC = builder.build();
    }

    private ModConfigs() {
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
