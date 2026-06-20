package com.redpred.livingsystem.service.hit;

import com.redpred.livingsystem.service.context.DamageContext;

/**
 * 命中部位解析服务（见开发文档 §6.3、§23）。仅在命中成立后判断命中哪个身体部位，不接管原版碰撞。
 */
public interface HitLocationService {

    /** 根据证据链解析命中部位。 */
    HitLocationResult resolve(DamageContext context);
}
