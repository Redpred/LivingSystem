package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.ToxinDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载 {@code data/<namespace>/toxin_definition/*.json} 为 {@link ToxinDefinition}，供毒素引擎按 ID 查询药代参数。
 * 单定义失败被隔离记录（见开发文档 §32）。
 */
public class ToxinDefinitionReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "toxin_definition";

    private static volatile Map<ResourceLocation, ToxinDefinition> byId = Map.of();

    public ToxinDefinitionReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 按毒素 ID 获取定义；不存在返回 {@code null}。 */
    @Nullable
    public static ToxinDefinition get(ResourceLocation id) {
        return byId.get(id);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, ToxinDefinition> loaded = new HashMap<>();
        object.forEach((location, json) -> ToxinDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("解析毒素定义 {} 失败：{}", location, error))
                .ifPresent(def -> loaded.put(def.id(), def)));
        byId = Map.copyOf(loaded);
        LOGGER.info("已加载 {} 个毒素定义。", byId.size());
    }
}
