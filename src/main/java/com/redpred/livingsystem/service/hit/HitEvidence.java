package com.redpred.livingsystem.service.hit;

import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * 统一的命中信息适配结果（见开发文档 §6.2）。不同伤害来源能提供的命中信息不同，统一封装于此。
 *
 * <p>不得把 {@code DamageSource} 的来源位置直接当作实际命中点；来源位置以 {@link #sourcePosition} 单列。</p>
 */
public record HitEvidence(
        Optional<Vec3> exactHitPosition,
        Optional<Vec3> attackStart,
        Optional<Vec3> attackEnd,
        Optional<Vec3> sourcePosition,
        HitEvidenceQuality quality
) {
    /** 无可用命中证据。 */
    public static final HitEvidence NONE = new HitEvidence(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            HitEvidenceQuality.NOT_LOCALIZABLE);
}
