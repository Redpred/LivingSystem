package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.SymptomDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link SymptomDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class SymptomDefinitionRegistry extends DefinitionRegistry<SymptomDefinition> {
    public SymptomDefinitionRegistry() {
        super();
    }

    public SymptomDefinitionRegistry(Map<ResourceLocation, SymptomDefinition> byId) {
        super(byId);
    }
}
