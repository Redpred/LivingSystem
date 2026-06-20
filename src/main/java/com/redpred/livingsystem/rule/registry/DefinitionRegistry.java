package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.RuleDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * 规则定义注册表基类。按 ID 持有一类不可变定义，构建后只读。
 *
 * @param <T> 定义类型
 */
public class DefinitionRegistry<T extends RuleDefinition> {

    private final Map<ResourceLocation, T> byId;

    public DefinitionRegistry() {
        this.byId = Map.of();
    }

    public DefinitionRegistry(Map<ResourceLocation, T> byId) {
        this.byId = Map.copyOf(byId);
    }

    /** 按 ID 获取定义，不存在返回 {@code null}。 */
    @Nullable
    public T get(ResourceLocation id) {
        return byId.get(id);
    }

    public boolean contains(ResourceLocation id) {
        return byId.containsKey(id);
    }

    public Collection<T> all() {
        return byId.values();
    }

    public int size() {
        return byId.size();
    }
}
