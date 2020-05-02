package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableResistor;
import simelectricity.essential.machines.gui.ContainerElectricFurnace;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileElectricFurnace extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, ISEEnergyNetUpdateHandler, ISESocketProvider, ITickableTileEntity, ISidedInventory, INamedContainerProvider {
    public static float energyPerItem = 5000F;

    //Component parameters
    private double resistance = 100;
    private boolean isOn = false;

    //Calculated values
    public double voltage;          // V
    public double powerLevel;       // W
    public double bufferedEnergy;   // J

    public int progress;
    private ItemStack result = ItemStack.EMPTY;

    public boolean isWorking() {
        return this.isOn;
    }

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (this.world.isRemote)
            return;

        if (this.isOn) {
            if (!itemStackHandler.canWork(result)) {
                // Item used up or removed or cannot be smelt
                this.bufferedEnergy = 0;
                this.isOn = false;
                updateTileParameter();
                markTileEntityForS2CSync();
            } else {
                this.bufferedEnergy += this.powerLevel / 20.0;

                if (this.bufferedEnergy > energyPerItem) {
                    this.bufferedEnergy = 0;
                    itemStackHandler.makeResult(result.copy());
                }
            }

            this.progress = (int) (this.bufferedEnergy*100/energyPerItem);
        } else {
            // Furnace is not working (not using any electricity)
            if (itemStackHandler.canWork(result)) {
                this.isOn = true;
                updateTileParameter();
                markTileEntityForS2CSync();
            }
        }
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
        if (tagCompound.contains("inventory") && itemStackHandler != null)
            itemStackHandler.deserializeNBT(tagCompound.getCompound("inventory"));

        itemStackHandler.onContentsChanged(0);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("resistance", this.resistance);
        tagCompound.putDouble("bufferedEnergy", this.bufferedEnergy);
        if (itemStackHandler != null)
            tagCompound.put("inventory",itemStackHandler.serializeNBT());

        return super.write(tagCompound);
    }

    @Override
    public void prepareS2CPacketData(CompoundNBT nbt) {
        super.prepareS2CPacketData(nbt);
        nbt.putBoolean("isWorking", this.isOn);
    }

    @Override
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
        super.onSyncDataFromServerArrived(nbt);
        this.isOn = nbt.getBoolean("isWorking");
        markForRenderUpdate();
        this.world.getLightManager().checkBlock(this.pos);	//checkLightFor
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    	if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		// TODO: impl capability
    	}
        return super.getCapability(capability, facing);
    }

    ItemStackHandlerImpl itemStackHandler = new ItemStackHandlerImpl(this);
    static class ItemStackHandlerImpl extends ItemStackHandler {
        private final TileElectricFurnace owner;
        protected ItemStackHandlerImpl(TileElectricFurnace owner) {
            super(2);
            this.owner = owner;
        }

        public boolean isEmpty() {
            for (ItemStack itemstack : stacks) {
                if (!itemstack.isEmpty())
                    return false;
            }

            return true;
        }

        public void clear() {
            for (int slot = 0; slot<stacks.size(); slot++) {
                stacks.set(slot, ItemStack.EMPTY);
            }
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 1)
                return ItemStack.EMPTY;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot == 0)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        public boolean canWork(ItemStack result) {
            if (result.isEmpty())
                return false;

            ItemStack itemStack = getStackInSlot(1);
            if (!itemStack.isEmpty()) {
                if (!itemStack.isItemEqual(result))
                    return false;   // Different Type
                if (itemStack.getCount() + result.getCount() > getStackLimit(1, result))
                    return false;   // No more space
            }

            return true;
        }

        public void makeResult(ItemStack result) {
            super.extractItem(0, 1, false);
            super.insertItem(1, result,false);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0)
                owner.result = owner.getSmeltingResult(this.getStackInSlot(0));
        }
    }
    ///////////////////////////////////
    /// IInventory
    ///////////////////////////////////
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] {0, 1};
    }

    @Override
    public boolean canInsertItem(int slotIndex, ItemStack itemStackIn, Direction direction) {
        return slotIndex == 0 && !getSmeltingResult(itemStackIn).isEmpty();
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack stack, Direction direction) {
        return slotIndex == 1;
    }

    @Override
    public int getSizeInventory() {
        return itemStackHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return itemStackHandler.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return itemStackHandler.getStackInSlot(slotIndex);
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int count) {
        ItemStack itemStackInSlot = getStackInSlot(slotIndex);
        if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM

        ItemStack itemStackRemoved;
        if (itemStackInSlot.getCount() <= count) { //getStackSize
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
        } else {
            itemStackRemoved = itemStackInSlot.split(count);
            if (itemStackInSlot.getCount() == 0) //getStackSize
                setInventorySlotContents(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
        }
        markDirty();
        return itemStackRemoved;
    }

    /**
     * This method removes the entire contents of the given slot and returns it.
     * Used by containers such as crafting tables which return any items in their slots when you close the GUI
     * @param slotIndex
     * @return
     */
    @Override
    public ItemStack removeStackFromSlot(int slotIndex) {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (!itemStack.isEmpty())
            setInventorySlotContents(slotIndex, ItemStack.EMPTY);  //isEmpty();  EMPTY_ITEM
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit())  // isEmpty();  getStackSize()
            itemstack.setCount(getInventoryStackLimit());  //setStackSize()
        itemStackHandler.setStackInSlot(slotIndex, itemstack);

        if (itemStackHandler.getStackInSlot(0).isEmpty() && itemstack.isEmpty())
            itemStackHandler.onContentsChanged(slotIndex);

        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return itemStackHandler.getSlotLimit(0);
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this)
            return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;

        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    public void openInventory(PlayerEntity player) {}

    @Override
    public void closeInventory(PlayerEntity player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

     @Override
    public void clear() {
        itemStackHandler.clear();
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    ///////////////////////////////////
    /// ISEEnergyNetUpdateHandler
    ///////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
        this.voltage = this.circuit.getVoltage();
        this.powerLevel = this.voltage * this.voltage / this.cachedParam.getResistance();
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 0 : -1;
    }

    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerElectricFurnace(windowId, inv, this);
	}
	
	public ItemStack getSmeltingResult(ItemStack itemStack) {
		Optional<FurnaceRecipe> result = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemStack), world);
		return result.isPresent() ? result.get().getRecipeOutput() : ItemStack.EMPTY;
	}
}
