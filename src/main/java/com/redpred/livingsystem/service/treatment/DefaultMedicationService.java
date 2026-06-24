package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.medication.MedicationEffectInstance;
import com.redpred.livingsystem.domain.medication.MedicationRoute;
import com.redpred.livingsystem.domain.physiology.PhysiologyState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.Iterator;

/**
 * {@link MedicationService} 默认实现（阶段三 3.3）。
 *
 * <p>药物不在使用时立即改最终状态，而是创建 {@link MedicationEffectInstance}，随药代过程逐周期
 * 吸收（贮存→已吸收）、起效与代谢衰减。每周期汇总全部活动药物的镇痛/镇静贡献写入
 * {@link PhysiologyState}（镇痛随后被疼痛汇总扣减）。完整的数据驱动药物定义（多种药效、靶器官、
 * 副作用）随阶段五毒素/药物系统统一接入；此处先内置一种镇痛药（止痛药）。</p>
 */
public final class DefaultMedicationService implements MedicationService {

    /** 止痛药定义 ID（与物品同名）。 */
    public static final ResourceLocation PAINKILLER =
            ResourceLocation.fromNamespaceAndPath(LivingSystemMod.MOD_ID, "painkiller");

    private static final float ABSORPTION_RATE = 0.25F;
    private static final float METABOLISM_RATE = 0.04F;
    /** 止痛药满强度时的镇痛系数。 */
    private static final float PAINKILLER_ANALGESIA = 0.6F;

    @Override
    public void administer(ServerPlayer player, ResourceLocation medicationId, MedicationRoute route, float dose) {
        if (dose <= 0.0F) {
            return;
        }
        PlayerHealthData data = com.redpred.livingsystem.service.LivingServices.REPOSITORY.get(player);
        data.medications().add(new MedicationEffectInstance(
                medicationId, route, dose, player.level().getGameTime()));
    }

    @Override
    public void tick(ServerPlayer player, PlayerHealthData data) {
        float analgesia = 0.0F;
        float sedation = 0.0F;
        Iterator<MedicationEffectInstance> it = data.medications().iterator();
        while (it.hasNext()) {
            MedicationEffectInstance med = it.next();
            // 吸收：贮存按比例进入已吸收。
            float absorbStep = med.getAbsorptionReservoir() * ABSORPTION_RATE;
            med.setAbsorptionReservoir(med.getAbsorptionReservoir() - absorbStep);
            med.setAbsorbedAmount(med.getAbsorbedAmount() + absorbStep);
            // 代谢：已吸收量随时间衰减，有效强度跟随已吸收量。
            med.setAbsorbedAmount(med.getAbsorbedAmount() * (1.0F - METABOLISM_RATE));
            med.setActiveStrength(med.getAbsorbedAmount());

            if (med.getAbsorptionReservoir() < 0.01F && med.getActiveStrength() < 0.01F) {
                med.setActive(false);
                it.remove();
                continue;
            }
            analgesia += analgesiaContribution(med);
        }
        PhysiologyState physiology = data.physiology();
        physiology.setAnalgesiaLevel(Mth.clamp(analgesia, 0.0F, 1.0F));
        physiology.setSedationLevel(Mth.clamp(sedation, 0.0F, 1.0F));
    }

    /** 单个药物对镇痛的贡献（内置药效表，后续由药物定义数据驱动）。 */
    private static float analgesiaContribution(MedicationEffectInstance med) {
        if (PAINKILLER.equals(med.getMedicationId())) {
            return med.getActiveStrength() * PAINKILLER_ANALGESIA;
        }
        return 0.0F;
    }
}
