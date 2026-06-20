package com.redpred.livingsystem.service;

import net.minecraft.resources.ResourceLocation;

/**
 * {@link MedicalProductionService} 默认实现。阶段一无任何生产配方。
 */
public final class DefaultMedicalProductionService implements MedicalProductionService {

    @Override
    public boolean canProduce(ResourceLocation recipeId) {
        return false;
    }
}
