package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.ProtectionProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载 {@code data/<namespace>/protection_profile/*.json} 为 {@link ProtectionProfile}，按匹配物品建立索引，
 * 供防护解析器按穿戴装备查询。单定义失败被隔离记录（见开发文档 §32）。
 */
public class ProtectionProfileReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "protection_profile";

    /** 按匹配物品 ID 索引的防护定义（一个物品命中一个定义）。 */
    private static volatile Map<ResourceLocation, ProtectionProfile> byItem = Map.of();
    private static final java.util.List<ProtectionProfile> CODE_REGISTERED = new java.util.concurrent.CopyOnWriteArrayList<>();

    public ProtectionProfileReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 供公开 API 在代码中注册防护画像；下次数据包重载时并入（数据包同物品优先）。 */
    public static void registerFromCode(ProtectionProfile profile) {
        CODE_REGISTERED.add(profile);
    }

    /** 按物品 ID 获取防护定义；无匹配返回 {@code null}。 */
    public static ProtectionProfile forItem(ResourceLocation itemId) {
        return byItem.get(itemId);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, ProtectionProfile> byItemMap = new HashMap<>();
        // 先并入代码注册（兜底），随后数据包同物品覆盖。
        CODE_REGISTERED.forEach(profile -> {
            if (profile.enabled()) {
                profile.items().forEach(item -> byItemMap.put(item, profile));
            }
        });
        object.forEach((location, json) -> ProtectionProfile.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("解析防护定义 {} 失败：{}", location, error))
                .ifPresent(profile -> {
                    if (profile.enabled()) {
                        profile.items().forEach(item -> byItemMap.put(item, profile));
                    }
                }));
        byItem = Map.copyOf(byItemMap);
        LOGGER.info("已加载 {} 条物品防护映射。", byItem.size());
    }
}
