package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.PathogenDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link PathogenDefinition} 注册表（见开发文档 §3.2.4）。 */
public final class PathogenDefinitionRegistry extends DefinitionRegistry<PathogenDefinition> {
    public PathogenDefinitionRegistry() {
        super();
    }

    public PathogenDefinitionRegistry(Map<ResourceLocation, PathogenDefinition> byId) {
        super(byId);
    }
}
