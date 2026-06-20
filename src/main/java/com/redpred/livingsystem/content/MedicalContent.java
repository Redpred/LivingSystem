package com.redpred.livingsystem.content;

import com.redpred.livingsystem.content.block.MedicalBlocks;
import com.redpred.livingsystem.content.blockentity.MedicalBlockEntities;
import com.redpred.livingsystem.content.item.MedicalItems;
import com.redpred.livingsystem.content.menu.MedicalMenus;

/**
 * 内容注册聚合入口。触发各内容注册类的静态初始化，使其条目登记到对应 {@code DeferredRegister}。
 *
 * <p>必须在 {@code DeferredRegister} 绑定到事件总线之前调用 {@link #init()}。</p>
 */
public final class MedicalContent {

    private MedicalContent() {
    }

    public static void init() {
        MedicalItems.init();
        MedicalBlocks.init();
        MedicalBlockEntities.init();
        MedicalMenus.init();
    }
}
