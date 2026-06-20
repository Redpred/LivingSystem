package com.redpred.livingsystem.content.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * 医疗工作站菜单（见开发文档 §16.2、§27）。阶段一为空壳容器，仅验证菜单注册管线；
 * 具体槽位与治疗交互在后续阶段实现。
 */
public class MedicalStationMenu extends AbstractContainerMenu {

    public MedicalStationMenu(int containerId, Inventory playerInventory) {
        super(MedicalMenus.MEDICAL_STATION.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
