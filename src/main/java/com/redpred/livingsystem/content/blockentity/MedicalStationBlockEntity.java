package com.redpred.livingsystem.content.blockentity;

import com.redpred.livingsystem.content.menu.MedicalStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 医疗工作站方块实体（见开发文档 §16.2）：持有 9 格医疗物资库存并随存档持久化，提供右键打开的菜单。
 */
public class MedicalStationBlockEntity extends BlockEntity implements MenuProvider {

    /** 库存格数。 */
    public static final int SLOT_COUNT = 9;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public MedicalStationBlockEntity(BlockPos pos, BlockState state) {
        super(MedicalBlockEntities.MEDICAL_STATION.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    /** 方块被移除时把库存掉落到世界。 */
    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Items", items.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items")) {
            items.deserializeNBT(registries, tag.getCompound("Items"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.livingsystem.medical_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MedicalStationMenu(containerId, playerInventory, items,
                ContainerLevelAccess.create(this.level, this.worldPosition));
    }
}
