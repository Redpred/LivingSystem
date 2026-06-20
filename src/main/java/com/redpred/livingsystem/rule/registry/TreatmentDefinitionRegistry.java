package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.TreatmentDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link TreatmentDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class TreatmentDefinitionRegistry extends DefinitionRegistry<TreatmentDefinition> {
    public TreatmentDefinitionRegistry() {
        super();
    }

    public TreatmentDefinitionRegistry(Map<ResourceLocation, TreatmentDefinition> byId) {
        super(byId);
    }
}
