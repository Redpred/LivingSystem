package com.redpred.livingsystem.persistence.migrator;

import com.redpred.livingsystem.domain.PlayerHealthData;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link PlayerHealthDataMigrationService} 默认实现。
 *
 * <p>持有按源版本索引的迁移器表，沿版本链迁移到 {@link PlayerHealthData#CURRENT_SCHEMA_VERSION}。
 * 阶段一无任何迁移器（当前 schema 版本为 1），因此恒原样返回。</p>
 */
public final class DefaultPlayerHealthDataMigrationService implements PlayerHealthDataMigrationService {

    private final Map<Integer, PlayerHealthDataMigrator> bySourceVersion = new HashMap<>();

    public DefaultPlayerHealthDataMigrationService() {
    }

    /** 注册一个迁移器。 */
    public void register(PlayerHealthDataMigrator migrator) {
        bySourceVersion.put(migrator.sourceVersion(), migrator);
    }

    @Override
    public CompoundTag migrateToCurrent(CompoundTag data, int fromVersion) {
        CompoundTag current = data;
        int version = fromVersion;
        while (version < PlayerHealthData.CURRENT_SCHEMA_VERSION) {
            PlayerHealthDataMigrator migrator = bySourceVersion.get(version);
            if (migrator == null) {
                break; // 无对应迁移器：保持现状，由调用方按缺失处理
            }
            current = migrator.migrate(current);
            version = migrator.targetVersion();
        }
        return current;
    }
}
