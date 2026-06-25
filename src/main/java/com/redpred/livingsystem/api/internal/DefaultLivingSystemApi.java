package com.redpred.livingsystem.api.internal;

import com.redpred.livingsystem.LivingSystemMod;
import com.redpred.livingsystem.api.LivingSystemApi;
import com.redpred.livingsystem.api.damage.DamageApi;
import com.redpred.livingsystem.api.event.EventApi;
import com.redpred.livingsystem.api.examination.ExaminationApi;
import com.redpred.livingsystem.api.exposure.ExposureApi;
import com.redpred.livingsystem.api.protection.ProtectionApi;
import com.redpred.livingsystem.api.treatment.TreatmentApi;
import com.redpred.livingsystem.data.DamageProfileReloadListener;
import com.redpred.livingsystem.data.ProtectionProfileReloadListener;
import com.redpred.livingsystem.data.TreatmentDefinitionReloadListener;

/**
 * 公开 API 门面默认实现（见开发文档 §30）。各注册方法把定义并入对应的代码注册区，下次数据包重载时合并
 * （数据包同 ID 优先）；事件订阅委托给 {@link ApiEventBus}。不暴露可变健康数据引用，所有变更经受校验服务执行。
 */
public final class DefaultLivingSystemApi implements LivingSystemApi {

    private final DamageApi damage = DamageProfileReloadListener::registerFromCode;
    private final ProtectionApi protection = ProtectionProfileReloadListener::registerFromCode;
    private final TreatmentApi treatment = TreatmentDefinitionReloadListener::registerFromCode;
    private final ExposureApi exposure =
            id -> LivingSystemMod.LOGGER.info("[API] 注册动态环境危害发射源（待实现）：{}", id);
    private final ExaminationApi examination =
            id -> LivingSystemMod.LOGGER.info("[API] 注册医疗检查定义（待实现）：{}", id);
    private final EventApi events = ApiEventBus::addDeathReportListener;

    @Override
    public DamageApi damage() {
        return damage;
    }

    @Override
    public ExposureApi exposure() {
        return exposure;
    }

    @Override
    public ProtectionApi protection() {
        return protection;
    }

    @Override
    public TreatmentApi treatment() {
        return treatment;
    }

    @Override
    public ExaminationApi examination() {
        return examination;
    }

    @Override
    public EventApi events() {
        return events;
    }
}
