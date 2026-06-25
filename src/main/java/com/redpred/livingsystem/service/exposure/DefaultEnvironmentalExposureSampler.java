package com.redpred.livingsystem.service.exposure;

import com.redpred.livingsystem.bootstrap.ModConfigs;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.exposure.ExposureCategory;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import com.redpred.livingsystem.rule.definition.EnvironmentalHazardProfile;
import com.redpred.livingsystem.service.LivingServices;
import com.redpred.livingsystem.service.context.ExposureContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * {@link EnvironmentalExposureSampler} 默认实现（阶段四 4.1/4.2）。
 *
 * <p>先按生物群系基础温度与岩浆/火/粉雪/浸水推算环境温度（护甲提供保暖），再遍历数据驱动的环境危害
 * （{@link EnvironmentalHazardProfile}）按方块/流体/群系匹配玩家所处环境：热/冷类叠加环境温度，呼吸类
 * 消耗呼吸储备并累积氧债，其余类别（毒素/辐射等）累积剂量待阶段五接入。最终把核心体温平滑趋近环境目标。</p>
 */
public final class DefaultEnvironmentalExposureSampler implements EnvironmentalExposureSampler {

    private static final ResourceLocation HEAT = ResourceLocation.fromNamespaceAndPath("livingsystem", "ambient_heat");
    private static final ResourceLocation COLD = ResourceLocation.fromNamespaceAndPath("livingsystem", "ambient_cold");

    @Override
    public void sample(ServerPlayer player) {
        if (!ModConfigs.TEMPERATURE_SYSTEM_ENABLED.get()) {
            return;
        }
        PlayerHealthData data = LivingServices.REPOSITORY.get(player);
        PhysiologyState p = data.physiology();
        float comfort = ModConfigs.COMFORT_TEMPERATURE.get().floatValue();
        float ambient = ambientTemperature(player, comfort);
        long now = player.level().getGameTime();

        // 数据驱动危害匹配：热/冷叠加温度、呼吸类消耗呼吸储备、其余累积剂量。
        float respiratoryHazard = 0.0F;
        for (EnvironmentalHazardProfile hazard : LivingServices.HAZARDS.all()) {
            if (!matches(player, hazard)) {
                continue;
            }
            accumulate(player, hazard.id(), hazard.category(), hazard.intensity(), now);
            if (hazard.category() == ExposureCategory.THERMAL_HEAT || hazard.category() == ExposureCategory.THERMAL_COLD) {
                ambient += hazard.temperatureDelta();
            } else if (hazard.category() == ExposureCategory.RESPIRATORY) {
                respiratoryHazard = Math.max(respiratoryHazard, hazard.intensity());
            } else if (hazard.category() == ExposureCategory.TOXIN) {
                // 毒气环境：把强度作为剂量注入毒素药代（危害 ID 约定对应同名毒素定义）。
                LivingServices.TOXIN.expose(player, hazard.id(),
                        com.redpred.livingsystem.domain.effect.ExposureRoute.INHALATION, hazard.intensity() * 0.5F);
            } else if (hazard.category() == ExposureCategory.PATHOGEN) {
                // 病原体环境：以危害 ID 为稳定来源键发起感染（去重避免每刻重复创建）。
                LivingServices.PATHOGEN.infect(player, hazard.id(),
                        com.redpred.livingsystem.domain.effect.TransmissionRoute.AIRBORNE,
                        java.util.UUID.nameUUIDFromBytes(hazard.id().toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            } else if (hazard.category() == ExposureCategory.RADIATION) {
                // 辐射环境：按强度累积伽马剂量（剂量经防护辐射屏蔽减免在引擎外不处理，最小实现用伽马）。
                LivingServices.RADIATION.irradiate(player,
                        com.redpred.livingsystem.domain.effect.RadiationType.GAMMA, hazard.intensity() * 0.05F);
            }
        }

        // 专用环境防护（防护服/面具）：隔热减少温度偏离，呼吸过滤/密封减少呼吸类危害。
        com.redpred.livingsystem.domain.protection.ProtectionResult exposureProtection = exposureProtection(player, now);
        ambient = comfort + (ambient - comfort) * exposureProtection.dermalPassThrough();
        respiratoryHazard *= exposureProtection.respiratoryPassThrough();

        // 核心体温向环境目标平滑趋近。
        float targetCore = Mth.clamp(37.0F + (ambient - comfort) * 0.05F, 25.0F, 45.0F);
        float rate = ModConfigs.TEMPERATURE_CHANGE_RATE.get().floatValue();
        p.setCoreTemperature(Mth.lerp(rate, p.getCoreTemperature(), targetCore));

        // 呼吸类危害（毒气/烟雾/低氧）消耗呼吸储备，耗尽则累积氧债。
        if (respiratoryHazard > 0.0F) {
            p.setRespiratoryReserve(Math.max(0.0F, p.getRespiratoryReserve() - respiratoryHazard * 0.2F));
            if (p.getRespiratoryReserve() <= 0.0F) {
                p.setOxygenDebt(Math.min(1.0F, p.getOxygenDebt() + respiratoryHazard * 0.1F));
            }
        }

        // 偏离舒适区的环境温度本身作为热/冷暴露累积。
        if (ambient > comfort + 15.0F) {
            accumulate(player, HEAT, ExposureCategory.THERMAL_HEAT, Mth.clamp((ambient - comfort) / 100.0F, 0.0F, 1.0F), now);
        } else if (ambient < comfort - 15.0F) {
            accumulate(player, COLD, ExposureCategory.THERMAL_COLD, Mth.clamp((comfort - ambient) / 50.0F, 0.0F, 1.0F), now);
        }
    }

    /** 推算玩家当前所处环境温度（摄氏度），含岩浆/火/粉雪/浸水修正与护甲保暖。 */
    private static float ambientTemperature(ServerPlayer player, float comfort) {
        float biomeTemp = player.level().getBiome(player.blockPosition()).value().getBaseTemperature();
        float ambient = comfort + (biomeTemp - 0.8F) * 18.0F;
        if (player.isInLava() || player.isOnFire()) {
            ambient = Math.max(ambient, 120.0F);
        } else if (player.isInPowderSnow || player.isFullyFrozen()) {
            ambient = Math.min(ambient, -15.0F);
        }
        if (player.isInWater()) {
            ambient -= 5.0F;
        }
        if (ambient < comfort) {
            int pieces = 0;
            for (ItemStack stack : player.getArmorSlots()) {
                if (!stack.isEmpty()) {
                    pieces++;
                }
            }
            ambient = Math.min(comfort, ambient + pieces * 2.5F);
        }
        return ambient;
    }

    /** 按触发方式判断玩家是否处于某环境危害中。 */
    private static boolean matches(ServerPlayer player, EnvironmentalHazardProfile hazard) {
        ServerLevel level = player.serverLevel();
        BlockPos feet = player.blockPosition();
        return switch (hazard.trigger()) {
            case AREA_OCCUPANCY -> level.getBiome(feet).unwrapKey()
                    .map(key -> hazard.biomes().contains(key.location())).orElse(false);
            case CONTACT -> hazard.blocks().contains(
                    BuiltInRegistries.BLOCK.getKey(level.getBlockState(feet).getBlock()));
            case IMMERSION -> hazard.fluids().contains(
                    BuiltInRegistries.FLUID.getKey(level.getFluidState(feet).getType()));
            case INHALATION -> {
                BlockPos eye = BlockPos.containing(player.getEyePosition());
                yield hazard.blocks().contains(BuiltInRegistries.BLOCK.getKey(level.getBlockState(eye).getBlock()))
                        || hazard.fluids().contains(BuiltInRegistries.FLUID.getKey(level.getFluidState(eye).getType()));
            }
            default -> false;
        };
    }

    private static void accumulate(ServerPlayer player, ResourceLocation id, ExposureCategory category,
                                   float intensity, long gameTime) {
        LivingServices.EXPOSURE.accumulate(new ExposureContext(player, id, category, intensity, gameTime));
    }

    /** 计算一次穿戴装备对环境暴露的防护（隔热/呼吸过滤/辐射屏蔽）。 */
    private static com.redpred.livingsystem.domain.protection.ProtectionResult exposureProtection(ServerPlayer player, long now) {
        java.util.List<ItemStack> armor = new java.util.ArrayList<>();
        player.getArmorSlots().forEach(armor::add);
        return LivingServices.PROTECTION.resolveExposureProtection(
                new ExposureContext(player, HEAT, ExposureCategory.THERMAL_HEAT, 0.0F, now), armor);
    }
}
