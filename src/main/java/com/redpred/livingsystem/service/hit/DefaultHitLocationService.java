package com.redpred.livingsystem.service.hit;

import com.redpred.livingsystem.domain.body.BodyRegion;
import com.redpred.livingsystem.service.context.DamageContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.Vec3;

/**
 * {@link HitLocationService} 默认实现（见开发文档 §6）。
 *
 * <p>非局部来源（溺水、饥饿、魔法等）→ 全身；坠落/石笋 → 腿部；否则用命中点高度比例分区（§6.4）选
 * 头/胸/腹/腿，并以"玩家右向量点积"判左右与臂/躯干；无命中点 → 轮廓加权随机。随机均基于确定性
 * {@code sourceEventId} 种子。</p>
 */
public final class DefaultHitLocationService implements HitLocationService {

    @Override
    public HitLocationResult resolve(DamageContext context) {
        DamageSource src = context.source();
        if (isWholeBody(src)) {
            return HitLocationResult.WHOLE_BODY;
        }
        RandomSource random = RandomSource.create(context.sourceEventId().getMostSignificantBits() ^ context.gameTime());

        if (src.is(DamageTypes.FALL) || src.is(DamageTypes.STALAGMITE)) {
            return HitLocationResult.of(random.nextBoolean() ? BodyRegion.LEFT_LEG : BodyRegion.RIGHT_LEG,
                    HitEvidenceQuality.SOURCE_RULE);
        }

        Vec3 hit = context.hitEvidence().exactHitPosition()
                .orElse(context.hitEvidence().sourcePosition().orElse(null));
        ServerPlayer victim = context.victim();
        if (hit == null) {
            return HitLocationResult.of(weighted(random), HitEvidenceQuality.RANDOM_FALLBACK);
        }

        double height = Math.max(0.1, victim.getBbHeight());
        double frac = (hit.y - victim.getY()) / height;
        double yaw = Math.toRadians(victim.getYRot());
        double lateral = (hit.x - victim.getX()) * (-Math.cos(yaw)) + (hit.z - victim.getZ()) * (-Math.sin(yaw));
        double shoulder = victim.getBbWidth() * 0.33;

        BodyRegion region;
        if (frac > 0.80) {
            region = BodyRegion.HEAD_NECK;
        } else if (frac > 0.55) {
            region = Math.abs(lateral) > shoulder ? (lateral > 0 ? BodyRegion.RIGHT_ARM : BodyRegion.LEFT_ARM) : BodyRegion.CHEST;
        } else if (frac > 0.38) {
            region = Math.abs(lateral) > shoulder ? (lateral > 0 ? BodyRegion.RIGHT_ARM : BodyRegion.LEFT_ARM) : BodyRegion.ABDOMEN;
        } else {
            region = lateral >= 0 ? BodyRegion.RIGHT_LEG : BodyRegion.LEFT_LEG;
        }
        return HitLocationResult.of(region, context.hitEvidence().quality());
    }

    private static boolean isWholeBody(DamageSource s) {
        return s.is(DamageTypes.DROWN) || s.is(DamageTypes.STARVE) || s.is(DamageTypes.WITHER)
                || s.is(DamageTypes.MAGIC) || s.is(DamageTypes.INDIRECT_MAGIC) || s.is(DamageTypes.IN_WALL)
                || s.is(DamageTypes.FREEZE) || s.is(DamageTypes.FELL_OUT_OF_WORLD) || s.is(DamageTypes.GENERIC_KILL);
    }

    /** 无命中点时按人体轮廓面积加权随机（和为 100）。 */
    private static BodyRegion weighted(RandomSource r) {
        int roll = r.nextInt(100);
        if (roll < 10) return BodyRegion.HEAD_NECK;
        if (roll < 35) return BodyRegion.CHEST;
        if (roll < 50) return BodyRegion.ABDOMEN;
        if (roll < 63) return BodyRegion.LEFT_ARM;
        if (roll < 76) return BodyRegion.RIGHT_ARM;
        if (roll < 88) return BodyRegion.LEFT_LEG;
        return BodyRegion.RIGHT_LEG;
    }
}
