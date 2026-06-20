package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.RadiationDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link RadiationDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class RadiationDefinitionRegistry extends DefinitionRegistry<RadiationDefinition> {
    public RadiationDefinitionRegistry() {
        super();
    }

    public RadiationDefinitionRegistry(Map<ResourceLocation, RadiationDefinition> byId) {
        super(byId);
    }
}
