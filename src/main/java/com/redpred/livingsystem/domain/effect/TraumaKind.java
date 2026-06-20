package com.redpred.livingsystem.domain.effect;

/**
 * 局部创伤类型。出血、疼痛、血管/神经损伤、感染、休克、气胸、器官功能下降等都不是创伤类型，
 * 它们分别属于伤势组件、结构损伤、继发健康影响或派生生理状态（见开发文档 §5.3）。
 */
public enum TraumaKind {
    ABRASION,
    CUT_WOUND,
    PUNCTURE_WOUND,
    PENETRATING_WOUND,
    BALLISTIC_WOUND,
    CONTUSION,
    CRUSH_INJURY,
    FRACTURE,
    CONCUSSION,
    BLAST_TRAUMA
}
