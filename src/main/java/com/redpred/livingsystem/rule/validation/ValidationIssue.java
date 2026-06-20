package com.redpred.livingsystem.rule.validation;

/**
 * 一条配置校验问题（见开发文档 §3.14、§32）。保留来源路径便于定位与中文报错。不可变 {@code record}。
 */
public record ValidationIssue(Severity severity, String messageZhCn, String sourcePath) {

    /** 问题严重程度。 */
    public enum Severity {
        /** 错误：导致该定义或整份快照被拒绝。 */
        ERROR,
        /** 警告：记录但不阻止加载。 */
        WARNING
    }

    public static ValidationIssue error(String messageZhCn, String sourcePath) {
        return new ValidationIssue(Severity.ERROR, messageZhCn, sourcePath);
    }

    public static ValidationIssue warning(String messageZhCn, String sourcePath) {
        return new ValidationIssue(Severity.WARNING, messageZhCn, sourcePath);
    }
}
