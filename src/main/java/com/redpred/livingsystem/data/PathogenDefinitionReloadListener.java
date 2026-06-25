package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.PathogenDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载 {@code data/<namespace>/pathogen_definition/*.json} 为 {@link PathogenDefinition}，供病原体引擎查询。
 * 单定义失败被隔离记录（见开发文档 §32）。
 */
public class PathogenDefinitionReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "pathogen_definition";

    private static volatile Map<ResourceLocation, PathogenDefinition> byId = Map.of();

    public PathogenDefinitionReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 按病原体 ID 获取定义；不存在返回 {@code null}。 */
    @Nullable
    public static PathogenDefinition get(ResourceLocation id) {
        return byId.get(id);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, PathogenDefinition> loaded = new HashMap<>();
        object.forEach((location, json) -> PathogenDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("解析病原体定义 {} 失败：{}", location, error))
                .ifPresent(def -> loaded.put(def.id(), def)));
        byId = Map.copyOf(loaded);
        LOGGER.info("已加载 {} 个病原体定义。", byId.size());
    }
}
