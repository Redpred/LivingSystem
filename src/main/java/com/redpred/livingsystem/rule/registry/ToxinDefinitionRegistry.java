package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.ToxinDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link ToxinDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class ToxinDefinitionRegistry extends DefinitionRegistry<ToxinDefinition> {
    public ToxinDefinitionRegistry() {
        super();
    }

    public ToxinDefinitionRegistry(Map<ResourceLocation, ToxinDefinition> byId) {
        super(byId);
    }
}
