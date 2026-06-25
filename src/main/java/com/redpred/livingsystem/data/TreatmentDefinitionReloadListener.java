package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import com.redpred.livingsystem.rule.reload.RulesSnapshotBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 加载 {@code data/<namespace>/treatment_definition/*.json}，解析为 {@link TreatmentDefinition} 并写入
 * 共享 {@link RulesSnapshotBuilder} 的治疗分区，触发只读规则快照重建。
 *
 * <p>约定治疗定义 ID 与触发治疗的医疗物品 ID 相同，便于"右键医疗物品 → 按物品 ID 查治疗定义"。
 * 单个定义解析失败被隔离并记录中文错误，不影响其它定义加载（见开发文档 §32）。</p>
 */
public class TreatmentDefinitionReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "treatment_definition";

    /** 按定义 ID 索引，供治疗服务直接查询。 */
    private static volatile Map<ResourceLocation, TreatmentDefinition> byId = Map.of();
    private static final Map<ResourceLocation, TreatmentDefinition> CODE_REGISTERED = new java.util.concurrent.ConcurrentHashMap<>();

    public TreatmentDefinitionReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 供公开 API 在代码中注册治疗定义；下次数据包重载时并入（数据包同 ID 优先）。 */
    public static void registerFromCode(TreatmentDefinition definition) {
        CODE_REGISTERED.put(definition.id(), definition);
    }

    /** 按定义 ID 获取治疗定义；不存在返回 {@code null}。 */
    @Nullable
    public static TreatmentDefinition get(ResourceLocation id) {
        return byId.get(id);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, TreatmentDefinition> loaded = new HashMap<>();
        loaded.putAll(CODE_REGISTERED);
        object.forEach((location, json) -> TreatmentDefinition.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("解析治疗定义 {} 失败：{}", location, error))
                .ifPresent(def -> loaded.put(def.id(), def)));
        byId = Map.copyOf(loaded);
        RulesSnapshotBuilder.setTreatments(loaded);
        LOGGER.info("已加载 {} 个治疗定义。", byId.size());
    }
}
