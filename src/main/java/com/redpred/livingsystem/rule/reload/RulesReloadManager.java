package com.redpred.livingsystem.rule.reload;

import com.redpred.livingsystem.rule.snapshot.ResolvedRulesSnapshot;

/**
 * 规则快照的原子重载管理器（见开发文档 §3.2.4、§22）。
 *
 * <p>运行时服务通过 {@link #current()} 获取当前有效快照。重载流程在外部完整构建新快照后调用
 * {@link #install(ResolvedRulesSnapshot)} 原子替换；构建失败时不调用本方法，从而保留上一份有效快照。
 * 阶段一默认持有 {@link ResolvedRulesSnapshot#EMPTY}。</p>
 */
public final class RulesReloadManager {

    private static volatile ResolvedRulesSnapshot current = ResolvedRulesSnapshot.EMPTY;

    private RulesReloadManager() {
    }

    /** 当前有效规则快照。 */
    public static ResolvedRulesSnapshot current() {
        return current;
    }

    /** 原子替换当前快照。 */
    public static void install(ResolvedRulesSnapshot snapshot) {
        current = snapshot;
    }

    /** 重置为空快照。 */
    public static void reset() {
        current = ResolvedRulesSnapshot.EMPTY;
    }
}
