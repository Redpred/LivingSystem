package com.redpred.livingsystem.api;

import com.redpred.livingsystem.api.damage.DamageApi;
import com.redpred.livingsystem.api.event.EventApi;
import com.redpred.livingsystem.api.examination.ExaminationApi;
import com.redpred.livingsystem.api.exposure.ExposureApi;
import com.redpred.livingsystem.api.protection.ProtectionApi;
import com.redpred.livingsystem.api.treatment.TreatmentApi;

/**
 * LivingSystem 公开 API 门面（见开发文档 §30）。
 *
 * <p>其他模组通过此门面注册兼容数据、查询只读健康摘要并订阅事件。API 不暴露可任意修改
 * {@code PlayerHealthData} 的公共可变引用，所有变更通过受校验的命令对象与服务执行。</p>
 */
public interface LivingSystemApi {

    DamageApi damage();

    ExposureApi exposure();

    ProtectionApi protection();

    TreatmentApi treatment();

    ExaminationApi examination();

    EventApi events();
}
