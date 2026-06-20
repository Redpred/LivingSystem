package com.redpred.livingsystem.service.structure;

import com.redpred.livingsystem.domain.PlayerHealthData;
import com.redpred.livingsystem.domain.body.AnatomicalStructure;
import com.redpred.livingsystem.domain.body.BodyRegion;

/**
 * 结构损伤服务（见开发文档 §17 全局不变量 2）。是唯一允许修改结构完整度的服务，伤势类不得直接修改。
 */
public interface StructureDamageService {

    /** 对指定部位的指定结构施加损伤（降低完整度）。 */
    void applyStructureDamage(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount);

    /** 对指定部位的指定结构施加恢复（提高完整度，受可恢复上限约束）。 */
    void applyStructureRepair(PlayerHealthData data, BodyRegion region, AnatomicalStructure structure, float amount);
}
