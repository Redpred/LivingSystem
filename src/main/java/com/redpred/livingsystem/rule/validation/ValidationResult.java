package com.redpred.livingsystem.rule.validation;

import java.util.List;

/**
 * 一次校验的结果（见开发文档 §3.14）。不可变 {@code record}。
 */
public record ValidationResult(List<ValidationIssue> issues) {

    /** 无任何问题的结果。 */
    public static final ValidationResult OK = new ValidationResult(List.of());

    /** 是否通过：不含任何 {@link ValidationIssue.Severity#ERROR}。 */
    public boolean ok() {
        return issues.stream().noneMatch(issue -> issue.severity() == ValidationIssue.Severity.ERROR);
    }
}
