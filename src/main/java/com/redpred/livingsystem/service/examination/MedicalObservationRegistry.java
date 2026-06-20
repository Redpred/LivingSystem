package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * 医疗检查结果注册/保存（见开发文档 §15.1.3、§23）。保存有限数量的玩家检查结果并向客户端同步。
 */
public interface MedicalObservationRegistry {

    /** 保存一次检查结果。 */
    void store(ServerPlayer patient, MedicalObservationSnapshot observation);

    /** 获取某玩家当前保存的检查结果。 */
    List<MedicalObservationSnapshot> get(ServerPlayer patient);
}
