package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.InjuryDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link InjuryDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class InjuryDefinitionRegistry extends DefinitionRegistry<InjuryDefinition> {
    public InjuryDefinitionRegistry() {
        super();
    }

    public InjuryDefinitionRegistry(Map<ResourceLocation, InjuryDefinition> byId) {
        super(byId);
    }
}
