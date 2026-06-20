package com.redpred.livingsystem.domain.body;

/**
 * 通用解剖结构类型。所有身体部位默认具有通用结构（皮肤、软组织、骨骼、血管、神经），
 * 头部、胸腔、腹部额外具有各自的关键结构。
 *
 * <p>设计约束（见开发文档 §4.2）：不区分具体骨骼；脏器按实/空腔归类；上、下呼吸道必须分开；
 * 腹部使用游戏抽象的 {@link #LUMBOSACRAL_NERVE_PATHWAY} 代替“腰段脊髓”。</p>
 */
public enum AnatomicalStructure {
    // —— 通用结构 ——
    /** 皮肤。 */
    SKIN,
    /** 软组织：肌肉、脂肪、肌腱、韧带等统一抽象。 */
    SOFT_TISSUE,
    /** 该部位的整体骨骼。 */
    BONE,
    /** 该部位的血管系统。 */
    VASCULAR,
    /** 该部位的周围神经系统。 */
    NERVE,

    // —— 头部关键结构 ——
    /** 脑。 */
    BRAIN,
    /** 感觉系统。 */
    SENSORY_SYSTEM,
    /** 上呼吸道。 */
    UPPER_AIRWAY,
    /** 颈段脊髓通路。 */
    CERVICAL_SPINAL_PATHWAY,

    // —— 胸腔关键结构 ——
    /** 心脏。 */
    HEART,
    /** 左肺。 */
    LEFT_LUNG,
    /** 右肺。 */
    RIGHT_LUNG,
    /** 下呼吸道。 */
    LOWER_AIRWAY,
    /** 胸段脊髓通路。 */
    THORACIC_SPINAL_PATHWAY,

    // —— 腹部关键结构 ——
    /** 实质脏器：肝、脾、肾等。 */
    SOLID_ORGANS,
    /** 空腔脏器：胃、肠道、膀胱等。 */
    HOLLOW_ORGANS,
    /** 腰骶神经通路（游戏抽象，代替腰段脊髓）。 */
    LUMBOSACRAL_NERVE_PATHWAY;

    /** 缓存数组，避免每次调用 {@link #values()} 产生分配。 */
    public static final AnatomicalStructure[] VALUES = values();
}
