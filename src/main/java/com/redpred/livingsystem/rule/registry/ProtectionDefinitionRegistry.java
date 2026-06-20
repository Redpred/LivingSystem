package com.redpred.livingsystem.rule.registry;

import com.redpred.livingsystem.rule.definition.ProtectionProfile;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/** {@link ProtectionProfile} 注册表（见开发文档 §3.2.4）。 */
public final class ProtectionDefinitionRegistry extends DefinitionRegistry<ProtectionProfile> {
    public ProtectionDefinitionRegistry() {
        super();
    }

    public ProtectionDefinitionRegistry(Map<ResourceLocation, ProtectionProfile> byId) {
        super(byId);
    }
}
