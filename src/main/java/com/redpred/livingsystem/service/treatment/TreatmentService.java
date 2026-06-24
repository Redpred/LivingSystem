package com.redpred.livingsystem.service.treatment;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.treatment.TreatmentSession;
import com.redpred.livingsystem.service.context.TreatmentContext;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

/**
 * 治疗服务（见开发文档 §13、§24.3）。验证并推进治疗会话；服务端是治疗进度与结果的唯一权威。
 */
public interface TreatmentService {

    /** 校验治疗条件并创建治疗会话；条件不满足时返回空（附带中文原因由实现记录/反馈）。 */
    Optional<TreatmentSession> startTreatment(TreatmentContext context);

    /** 推进指定患者的全部活动治疗会话一刻：更新进度、按提交策略施加效果、处理中断与完成，并同步进度。 */
    void tickSessions(ServerPlayer patient, PlayerHealthData data);

    /** 取消指定会话（校验归属）；成功返回 {@code true}。 */
    boolean cancel(ServerPlayer patient, PlayerHealthData data, UUID sessionId);
}
