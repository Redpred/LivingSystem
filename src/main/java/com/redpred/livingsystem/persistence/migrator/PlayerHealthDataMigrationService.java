package com.redpred.livingsystem.persistence.migrator;

import net.minecraft.nbt.CompoundTag;

/**
 * 逐版本执行玩家健康数据迁移（见开发文档 §25.2、§23）。
 *
 * <p>按 {@code schemaVersion} 顺序链式执行已注册的 {@link PlayerHealthDataMigrator}，不得跳过中间迁移。
 * 迁移失败时由调用方保存备份、记录中文错误并使用安全默认值恢复，不静默丢弃数据。</p>
 */
public interface PlayerHealthDataMigrationService {

    /** 把指定源版本的数据迁移到当前版本并返回。 */
    CompoundTag migrateToCurrent(CompoundTag data, int fromVersion);
}
