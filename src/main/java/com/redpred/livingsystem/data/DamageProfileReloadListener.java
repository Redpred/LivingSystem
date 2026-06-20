package com.redpred.livingsystem.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.redpred.livingsystem.rule.definition.DamageProfile;
import com.redpred.livingsystem.rule.registry.DamageProfileRegistry;
import com.redpred.livingsystem.rule.registry.InjuryDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.PathogenDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ProtectionDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.RadiationDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.SymptomDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.ToxinDefinitionRegistry;
import com.redpred.livingsystem.rule.registry.TreatmentDefinitionRegistry;
import com.redpred.livingsystem.rule.reload.RulesReloadManager;
import com.redpred.livingsystem.rule.snapshot.FeaturePolicyRegistry;
import com.redpred.livingsystem.rule.snapshot.GlobalMultipliers;
import com.redpred.livingsystem.rule.snapshot.ResolvedRulesSnapshot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 加载 {@code data/<namespace>/damage_profile/*.json}（键为 DamageType 的 msgId，即文件名），
 * 并在每次重载后重建只读规则快照（{@link ResolvedRulesSnapshot}）原子装入 {@link RulesReloadManager}。
 *
 * <p>这是阶段二首次真正装配规则快照（阶段一恒为 EMPTY）。阶段二 2.1 仅装入伤害画像，其余注册表留空，
 * 后续子里程碑接入。</p>
 */
public class DamageProfileReloadListener extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final AtomicLong VERSION = new AtomicLong();
    public static final String DIRECTORY = "damage_profile";

    /** 按 DamageType msgId（小写）索引，供伤害管线直接查询。 */
    private static volatile Map<String, DamageProfile> byMsgId = Map.of();

    public DamageProfileReloadListener() {
        super(GSON, DIRECTORY);
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
        object.forEach((location, json) -> DamageProfile.CODEC
                .parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> LOGGER.error("Failed to parse damage_profile {}: {}", location, error))
                .ifPresent(profile -> {
                    loaded.put(location.getPath().toLowerCase(), profile);
                    byId.put(profile.id(), profile);
                }));
        byMsgId = Map.copyOf(loaded);

        ResolvedRulesSnapshot snapshot = new ResolvedRulesSnapshot(
                FeaturePolicyRegistry.EMPTY,
                GlobalMultipliers.DEFAULT,
                new DamageProfileRegistry(byId),
                new InjuryDefinitionRegistry(),
                new SymptomDefinitionRegistry(),
                new ToxinDefinitionRegistry(),
                new PathogenDefinitionRegistry(),
                new RadiationDefinitionRegistry(),
                new TreatmentDefinitionRegistry(),
                new ProtectionDefinitionRegistry(),
                VERSION.incrementAndGet());
        RulesReloadManager.install(snapshot);
        LOGGER.info("Loaded {} damage profile(s).", byMsgId.size());
    }
}
