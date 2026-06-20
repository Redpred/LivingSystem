package com.redpred.livingsystem.client.animation;

import com.redpred.livingsystem.network.payload.TreatmentProgressPayload;

/**
 * 治疗动画服务（见开发文档 §14.2、§27）。根据治疗行为定义驱动第一/第三人称动画、音效与粒子，
 * 共享归一化进度；动画只负责显示，不决定治疗是否成功（服务端的治疗会话才是权威）。
 *
 * <p>阶段一为接口骨架，根据治疗进度快照更新动画状态。</p>
 */
public interface TreatmentAnimationService {

    /** 根据最新治疗进度更新动画状态。 */
    void update(TreatmentProgressPayload progress);
}
