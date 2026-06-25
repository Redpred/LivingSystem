package com.redpred.livingsystem.content.menu;

import com.redpred.livingsystem.content.block.MedicalBlocks;
import com.redpred.livingsystem.content.blockentity.MedicalStationBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 医疗工作站菜单（见开发文档 §16.2、§27）：9 格工作站库存 + 玩家背包，支持 Shift 快速转移。
 */
public class MedicalStationMenu extends AbstractContainerMenu {

    private static final int STATION_SLOTS = MedicalStationBlockEntity.SLOT_COUNT;
    private final ContainerLevelAccess access;

    /** 客户端构造：库存由服务端同步覆盖，访问器为空。 */
    public MedicalStationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, new ItemStackHandler(STATION_SLOTS), ContainerLevelAccess.NULL);
    }

    public MedicalStationMenu(int containerId, Inventory playerInventory, IItemHandler handler, ContainerLevelAccess access) {
        super(MedicalMenus.MEDICAL_STATION.get(), containerId);
        this.access = access;

        // 工作站 9 格（一行）。
        for (int col = 0; col < STATION_SLOTS; col++) {
            this.addSlot(new SlotItemHandler(handler, col, 8 + col * 18, 36));
        }
        // 玩家主背包 3×9。
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 68 + row * 18));
            }
        }
        // 玩家快捷栏。
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 126));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < STATION_SLOTS) {
                // 从工作站移到玩家库存。
                if (!this.moveItemStackTo(stack, STATION_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, STATION_SLOTS, false)) {
                // 从玩家库存移入工作站。
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, MedicalBlocks.MEDICAL_STATION.get());
    }
}
