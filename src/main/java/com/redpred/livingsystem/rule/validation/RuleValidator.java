package com.redpred.livingsystem.rule.validation;

import com.redpred.livingsystem.rule.definition.RuleDefinition;

import java.util.List;

/**
 * 单类规则定义的校验器（见开发文档 §3.14）。
 *
 * <p>校验内容包括数值范围、概率区间、引用存在性、依赖与循环引用等。单个定义无效时应被隔离并记录
 * 中文错误，不阻止其他有效定义加载。</p>
 *
 * @param <T> 被校验的定义类型
 */
public interface RuleValidator<T extends RuleDefinition> {

    /** 校验单个定义，返回问题列表（空列表表示通过）。 */
    List<ValidationIssue> validate(T definition);
}
