package com.redpred.livingsystem.service.examination;

import com.redpred.livingsystem.domain.examination.MedicalObservationSnapshot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * 医疗检查服务（见开发文档 §15.1、§23）。执行检查并生成带时效的观察快照；检查其他玩家须满足服务端条件。
 */
public interface MedicalExaminationService {

    /** 执行一次检查，返回观察快照；条件不满足时返回空。 */
    Optional<MedicalObservationSnapshot> examine(ServerPlayer examiner, ServerPlayer patient, ResourceLocation examinationId);
}
