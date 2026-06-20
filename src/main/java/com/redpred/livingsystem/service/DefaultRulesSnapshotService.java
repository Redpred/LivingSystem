package com.redpred.livingsystem.service;

import com.redpred.livingsystem.rule.reload.RulesReloadManager;
import com.redpred.livingsystem.rule.snapshot.ResolvedRulesSnapshot;

/**
 * {@link RulesSnapshotService} 默认实现，委托 {@link RulesReloadManager} 的原子快照。
 */
public final class DefaultRulesSnapshotService implements RulesSnapshotService {

    @Override
    public ResolvedRulesSnapshot current() {
        return RulesReloadManager.current();
    }
}
