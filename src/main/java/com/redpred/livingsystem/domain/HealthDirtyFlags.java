package com.redpred.livingsystem.domain;

/**
 * 运行时脏标记（见开发文档 §29.4）。状态无变化时不重复计算全部派生数据。不持久化。
 */
public final class HealthDirtyFlags {
    private boolean injuries;
    private boolean structure;
    private boolean physiology;
    private boolean treatment;
    private boolean protection;
    private boolean rules;

    public void markInjuries() { this.injuries = true; }
    public void markStructure() { this.structure = true; }
    public void markPhysiology() { this.physiology = true; }
    public void markTreatment() { this.treatment = true; }
    public void markProtection() { this.protection = true; }
    public void markRules() { this.rules = true; }

    public boolean isInjuriesDirty() { return injuries; }
    public boolean isStructureDirty() { return structure; }
    public boolean isPhysiologyDirty() { return physiology; }
    public boolean isTreatmentDirty() { return treatment; }
    public boolean isProtectionDirty() { return protection; }
    public boolean isRulesDirty() { return rules; }

    /** 是否存在任一脏标记。 */
    public boolean any() {
        return injuries || structure || physiology || treatment || protection || rules;
    }

    /** 清除全部脏标记。 */
    public void clearAll() {
        injuries = false;
        structure = false;
        physiology = false;
        treatment = false;
        protection = false;
        rules = false;
    }
}
