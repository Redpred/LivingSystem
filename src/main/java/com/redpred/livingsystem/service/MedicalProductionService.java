package com.redpred.livingsystem.service;

import net.minecraft.resources.ResourceLocation;

/**
 * 医疗物资生产服务（见开发文档 §16.2.6、§31）。把生产配方抽象为机器角色，核心治疗逻辑不依赖特定机器。
 *
 * <p>阶段一为骨架：配方与机器角色绑定在内容阶段建立后再充实。</p>
 */
public interface MedicalProductionService {

    /** 指定配方当前是否可生产（取决于机器角色与配方启用状态）。 */
    boolean canProduce(ResourceLocation recipeId);
}
