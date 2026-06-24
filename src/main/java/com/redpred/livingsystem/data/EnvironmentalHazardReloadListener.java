package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.EnvironmentalHazardProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加载 {@code data/<namespace>/environmental_hazard/*.json} 为 {@link EnvironmentalHazardProfile} 列表，
 * 供环境暴露采样器按方块/流体/群系匹配。单个定义解析失败被隔离记录，不影响其它定义（见开发文档 §32）。
 */
public class EnvironmentalHazardReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "environmental_hazard";

    /** 当前已启用的环境危害定义（只读快照）。 */
    private static volatile List<EnvironmentalHazardProfile> hazards = List.of();

    public EnvironmentalHazardReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 当前已启用的环境危害列表。 */
    public static List<EnvironmentalHazardProfile> all() {
        return hazards;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<EnvironmentalHazardProfile> loaded = new ArrayList<>();
        object.forEach((location, json) -> EnvironmentalHazardProfile.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("解析环境危害 {} 失败：{}", location, error))
                .ifPresent(profile -> {
                    if (profile.enabled()) {
                        loaded.add(profile);
                    }
                }));
        hazards = List.copyOf(loaded);
        LOGGER.info("已加载 {} 个环境危害定义。", hazards.size());
    }
}
