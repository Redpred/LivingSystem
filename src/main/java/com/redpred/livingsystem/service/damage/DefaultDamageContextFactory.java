package com.redpred.livingsystem.service.damage;

import com.redpred.livingsystem.service.context.DamageContext;
import com.redpred.livingsystem.service.hit.HitEvidence;
import com.redpred.livingsystem.service.hit.HitEvidenceQuality;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link DamageContextFactory} 默认实现：从 {@link DamageSource} 提取命中证据并生成确定性事件 ID。
 *
 * <p>命中证据按来源类型分级：投射物用其当前位置；近战攻击者用其眼位方向；间接来源用来源位置。
 * {@code sourceEventId} 由"玩家 UUID + 游戏时间 + DamageType msgId"确定性派生（见开发文档 §6.6），
 * 保证服务端重算/调试一致。</p>
 */
public final class DefaultDamageContextFactory implements DamageContextFactory {

    @Override
    public DamageContext create(ServerPlayer victim, DamageSource source, float amount, long gameTime) {
        return new DamageContext(victim, source, amount, deterministicEventId(victim, source, gameTime),
                gameTime, buildEvidence(source));
    }

    private static HitEvidence buildEvidence(DamageSource source) {
        Entity direct = source.getDirectEntity();
        Optional<Vec3> sourcePos = Optional.ofNullable(source.getSourcePosition());
        if (direct instanceof Projectile) {
            Vec3 pos = direct.position();
            return new HitEvidence(Optional.of(pos), Optional.empty(), Optional.of(pos), sourcePos,
                    HitEvidenceQuality.RAY_APPROXIMATION);
        }
        if (direct != null) {
            return new HitEvidence(Optional.empty(), Optional.of(direct.getEyePosition()), Optional.empty(),
                    sourcePos, HitEvidenceQuality.DIRECTION_ONLY);
        }
        return new HitEvidence(Optional.empty(), Optional.empty(), Optional.empty(), sourcePos,
                sourcePos.isPresent() ? HitEvidenceQuality.SOURCE_RULE : HitEvidenceQuality.NOT_LOCALIZABLE);
    }

    private static UUID deterministicEventId(ServerPlayer victim, DamageSource source, long gameTime) {
        String seed = victim.getUUID() + ":" + gameTime + ":" + source.getMsgId();
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}
