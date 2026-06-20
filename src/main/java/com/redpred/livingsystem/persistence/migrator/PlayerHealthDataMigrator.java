package com.redpred.livingsystem.persistence.migrator;

import net.minecraft.nbt.CompoundTag;

/**
 * 将某一旧版本健康数据迁移到下一版本（见开发文档 §25.3）。
 *
 * <p>所有迁移器实现必须用中文注释说明：旧字段含义、新字段含义、默认值来源、是否存在数据丢失。
 * 迁移必须可重复执行而不继续破坏数据。</p>
 */
public interface PlayerHealthDataMigrator {

    /** 源版本。 */
    int sourceVersion();

    /** 目标版本。 */
    int targetVersion();

    /** 执行迁移并返回新版本数据标签。 */
    CompoundTag migrate(CompoundTag oldData);
}
