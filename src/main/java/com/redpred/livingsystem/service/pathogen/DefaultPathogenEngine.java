package com.redpred.livingsystem.service.pathogen;

import com.redpred.livingsystem.data.PathogenDefinitionReloadListener;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.effect.CauseSnapshot;
import com.redpred.livingsystem.domain.effect.HealthEffectInstance;
import com.redpred.livingsystem.domain.effect.InfectionStage;
import com.redpred.livingsystem.domain.effect.PathogenState;
import com.redpred.livingsystem.domain.effect.TransmissionRoute;
import com.redpred.livingsystem.domain.effect.TraumaInjuryState;
import com.redpred.livingsystem.rule.definition.PathogenDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link PathogenEngine} 默认实现（阶段五）。
 *
 * <p>感染按阶段推进：潜伏（{@code INCUBATING}）到期转症状期（{@code SYMPTOMATIC}），载量按复制速率增长并受
 * 免疫储备清除对抗；载量换算严重度，达致死载量则致死。免疫压制载量后进入恢复（{@code RECOVERING}）直至
 * 清零痊愈（{@code CLEARED}）移除。开放且高污染的伤口按病原体定义概率继发感染。</p>
 */
public final class DefaultPathogenEngine implements PathogenEngine {

    /** 默认伤口感染所用病原体定义 ID。 */
    private static final ResourceLocation WOUND_INFECTION =
            ResourceLocation.fromNamespaceAndPath("livingsystem", "wound_infection");

    @Override
    public void infect(ServerPlayer player, ResourceLocation pathogenId, TransmissionRoute route, UUID sourceEventId) {
        PlayerHealthData data = com.redpred.livingsystem.service.LivingServices.REPOSITORY.get(player);
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof PathogenState ps && ps.active()
                    && pathogenId.equals(ps.getPathogenId()) && sourceEventId.equals(ps.sourceEventId())) {
                return; // 同源同病原体的感染已存在，避免重复创建。
            }
        }
        PathogenDefinition def = PathogenDefinitionReloadListener.get(pathogenId);
        PathogenState state = new PathogenState(UUID.randomUUID(), sourceEventId, CauseSnapshot.UNKNOWN,
                pathogenId, def != null ? def.type() : null, player.level().getGameTime());
        state.setTransmissionRoute(route);
        state.setStage(InfectionStage.INCUBATING);
        state.setPathogenLoad(0.1F);
        data.activeEffects().put(state.id(), state);
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        float immuneReserve = data.physiology().getImmuneReserve();
        long now = player.level().getGameTime();
        List<UUID> cleared = new ArrayList<>();
        for (HealthEffectInstance effect : new ArrayList<>(data.activeEffects().values())) {
            if (effect instanceof PathogenState ps && ps.active()) {
                advanceInfection(ps, immuneReserve, now);
                if (ps.getStage() == InfectionStage.CLEARED) {
                    ps.setActive(false);
                    cleared.add(ps.id());
                }
            }
        }
        cleared.forEach(id -> data.activeEffects().remove(id));
        attemptWoundInfections(player, data, immuneReserve, now);
    }

    private static void advanceInfection(PathogenState ps, float immuneReserve, long now) {
        PathogenDefinition def = PathogenDefinitionReloadListener.get(ps.getPathogenId());
        float replication = def != null ? def.replicationRate() : 0.08F;
        float virulence = def != null ? def.virulence() : 1.0F;
        float clear = (def != null ? def.immuneClearRate() : 0.05F) * Math.max(0.1F, immuneReserve);
        int incubation = def != null ? def.incubationTicks() : 1200;

        if (ps.getStage() == InfectionStage.INCUBATING && now - ps.createdGameTime() >= incubation) {
            ps.setStage(InfectionStage.SYMPTOMATIC);
        }
        float load = ps.getPathogenLoad();
        if (ps.getStage() == InfectionStage.SYMPTOMATIC || ps.getStage() == InfectionStage.INCUBATING) {
            load += load * replication - clear;
        } else {
            load -= clear;
        }
        load = Mth.clamp(load, 0.0F, 1.0F);
        ps.setPathogenLoad(load);
        ps.setImmuneControl(Mth.clamp(immuneReserve, 0.0F, 1.0F));
        ps.setSeverity(Mth.clamp(load * virulence, 0.0F, 1.0F));
        ps.setLastUpdatedGameTime(now);

        if (ps.getStage() == InfectionStage.SYMPTOMATIC && load < 0.2F && immuneReserve > 0.3F) {
            ps.setStage(InfectionStage.RECOVERING);
        }
        if (load <= 0.01F) {
            ps.setStage(InfectionStage.CLEARED);
        }
    }

    /** 开放且高污染的伤口按病原体定义概率继发伤口感染。 */
    private static void attemptWoundInfections(ServerPlayer player, PlayerHealthData data, float immuneReserve, long now) {
        PathogenDefinition def = PathogenDefinitionReloadListener.get(WOUND_INFECTION);
        if (def == null || def.woundInfectionChance() <= 0.0F) {
            return;
        }
        for (HealthEffectInstance effect : new ArrayList<>(data.activeEffects().values())) {
            if (effect instanceof TraumaInjuryState trauma
                    && trauma.getContamination().getContaminationLevel() > 0.5F
                    && !trauma.getContamination().isCleaned()) {
                float chance = def.woundInfectionChance()
                        * trauma.getContamination().getContaminationLevel() * (1.0F - immuneReserve * 0.5F);
                if (player.getRandom().nextFloat() < chance) {
                    infectInternal(data, trauma.id(), now);
                }
            }
        }
    }

    private static void infectInternal(PlayerHealthData data, UUID sourceEventId, long now) {
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof PathogenState ps && ps.active()
                    && WOUND_INFECTION.equals(ps.getPathogenId()) && sourceEventId.equals(ps.sourceEventId())) {
                return;
            }
        }
        PathogenDefinition def = PathogenDefinitionReloadListener.get(WOUND_INFECTION);
        PathogenState state = new PathogenState(UUID.randomUUID(), sourceEventId, CauseSnapshot.UNKNOWN,
                WOUND_INFECTION, def != null ? def.type() : null, now);
        state.setStage(InfectionStage.INCUBATING);
        state.setPathogenLoad(0.1F);
        data.activeEffects().put(state.id(), state);
    }

    @Override
    public float aggregateBurden(PlayerHealthData data) {
        float sum = 0.0F;
        for (HealthEffectInstance effect : data.activeEffects().values()) {
            if (effect instanceof PathogenState ps && ps.active()) {
                sum += ps.severity();
            }
        }
        return Mth.clamp(sum, 0.0F, 1.0F);
    }
}
