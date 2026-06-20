package com.redpred.livingsystem.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 客户端个人配置（见开发文档 §3.2.3、§15.4）。只控制画面、声音与界面表现，不得改变服务端计算的
 * 实际游戏效果。本类不引用 {@code net.minecraft.client.*}，由 {@code ModConfigs} 以 CLIENT 类型注册。
 *
 * <p>阶段一仅含少量代表性项，完整客户端偏好在后续阶段扩充。</p>
 */
public final class ClientConfig {

    public static final ModConfigSpec SPEC;

    /** 是否显示健康 HUD。 */
    public static final ModConfigSpec.BooleanValue SHOW_HEALTH_HUD;
    /** 是否启用镜头晃动等画面效果（不影响服务端实际惩罚）。 */
    public static final ModConfigSpec.BooleanValue ENABLE_CAMERA_SWAY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SHOW_HEALTH_HUD = builder
                .comment("是否显示 LivingSystem 健康 HUD。仅影响本地显示，不改变服务端健康结果。")
                .define("showHealthHud", true);
        ENABLE_CAMERA_SWAY = builder
                .comment("是否启用镜头晃动等画面效果。关闭仅去除画面表现，不能消除服务端计算的操作惩罚。")
                .define("enableCameraSway", true);
        SPEC = builder.build();
    }

    private ClientConfig() {
    }
}
