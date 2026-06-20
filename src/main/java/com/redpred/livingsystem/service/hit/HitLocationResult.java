package com.redpred.livingsystem.service.hit;

import com.redpred.livingsystem.domain.body.BodyRegion;

import java.util.Optional;

/**
 * 命中部位解析结果（见开发文档 §6.3）。{@code region} 为空表示无法局部化、作用于全身。
 *
 * <p>注：开发文档引用 {@code HitLocationResult} 但未列字段，此处按“部位 + 证据质量”合理设计。</p>
 */
public record HitLocationResult(
        Optional<BodyRegion> region,
        HitEvidenceQuality quality
) {
    /** 全身作用结果。 */
    public static final HitLocationResult WHOLE_BODY =
            new HitLocationResult(Optional.empty(), HitEvidenceQuality.NOT_LOCALIZABLE);

    public static HitLocationResult of(BodyRegion region, HitEvidenceQuality quality) {
        return new HitLocationResult(Optional.of(region), quality);
    }
}
