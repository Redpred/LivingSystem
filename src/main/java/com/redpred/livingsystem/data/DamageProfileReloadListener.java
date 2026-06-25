package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.DamageProfile;
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
 * 加载 {@code data/<namespace>/damage_profile/*.json}（键为 DamageType 的 msgId，即文件名），
 * 并把解析结果写入共享的 {@link RulesSnapshotBuilder} 伤害画像分区，触发只读规则快照重建。
 *
 * <p>阶段二装入伤害画像；治疗等其它分区由各自监听器写入同一构建器（见 {@link RulesSnapshotBuilder}）。</p>
 */
public class DamageProfileReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    public static final String DIRECTORY = "damage_profile";

    /** 按 DamageType msgId（小写）索引，供伤害管线直接查询。 */
    private static volatile Map<String, DamageProfile> byMsgId = Map.of();
    /** 其它模组经公开 API 注册的伤害画像（代码兜底，重载时与数据包合并，数据包同 ID 覆盖之）。 */
    private static final Map<ResourceLocation, DamageProfile> CODE_REGISTERED = new java.util.concurrent.ConcurrentHashMap<>();

    public DamageProfileReloadListener() {
        super(GSON, DIRECTORY);
    }

    /** 供公开 API 在代码中注册伤害画像；下次数据包重载时并入（数据包同 ID 优先）。 */
    public static void registerFromCode(DamageProfile profile) {
        CODE_REGISTERED.put(profile.id(), profile);
    }

    /** 按 DamageType 的 msgId 获取伤害画像；无对应配置返回 {@code null}（调用方使用通用默认）。 */
    @Nullable
    public static DamageProfile get(String damageTypeMsgId) {
        return byMsgId.get(damageTypeMsgId.toLowerCase());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, DamageProfile> loaded = new HashMap<>();
        Map<ResourceLocation, DamageProfile> byId = new HashMap<>();
        // 先并入代码注册（兜底），随后数据包同 ID 覆盖。
        CODE_REGISTERED.forEach((id, profile) -> {
            loaded.put(id.getPath().toLowerCase(), profile);
            byId.put(id, profile);
        });
        object.forEach((location, json) -> DamageProfile.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("Failed to parse damage_profile {}: {}", location, error))
                .ifPresent(profile -> {
                    loaded.put(location.getPath().toLowerCase(), profile);
                    byId.put(profile.id(), profile);
                }));
        byMsgId = Map.copyOf(loaded);
        RulesSnapshotBuilder.setDamageProfiles(byId);
        LOGGER.info("Loaded {} damage profile(s).", byMsgId.size());
    }
}
