package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import com.redpred.livingsystem.service.context.TreatmentContext;

import java.util.Optional;
import java.util.UUID;

/**
 * 治疗服务（见开发文档 §13、§24.3）。验证并推进治疗会话；服务端是治疗进度与结果的唯一权威。
 */
public interface TreatmentService {

    /** 校验治疗条件并创建治疗会话；条件不满足时返回空（附带中文原因由实现记录/反馈）。 */
    Optional<TreatmentSession> startTreatment(TreatmentContext context);

    /** 推进一次治疗会话进度。 */
    void advance(UUID sessionId);

    /** 取消一次治疗会话。 */
    void cancel(UUID sessionId);
}
