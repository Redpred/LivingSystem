package com.redpred.livingsystem.service;

import com.redpred.livingsystem.rule.snapshot.ResolvedRulesSnapshot;

/**
 * 规则快照服务（见开发文档 §23）。运行时服务通过它获取当前有效规则快照，不直接读取配置文件。
 */
public interface RulesSnapshotService {

    /** 当前有效规则快照。 */
    ResolvedRulesSnapshot current();
}
