package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.DamageProfile;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link DamageProfile} 注册表（见开发文档 §3.2.4）。 */
public final class DamageProfileRegistry extends DefinitionRegistry<DamageProfile> {
    public DamageProfileRegistry() {
        super();
    }

    public DamageProfileRegistry(Map<ResourceLocation, DamageProfile> byId) {
        super(byId);
    }
}
