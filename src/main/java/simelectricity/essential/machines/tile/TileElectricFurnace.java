package simelectricity.essential.machines.tile;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rikka.librikka.Utils;
import rikka.librikka.blockentity.INamedMenuProvider;
import rikka.librikka.blockentity.ITickableBlockEntity;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.ItemStackHandlerInventory;
import simelectricity.essential.common.semachine.ISE2StateTile;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerElectricFurnace;

import java.util.Optional;

import javax.annotation.Nonnull;

public class TileElectricFurnace extends SESinglePortMachine<ISEVoltageSource> implements
		ISEVoltageSource, ISE2StateTile, ISEEnergyNetUpdateHandler, ITickableBlockEntity, INamedMenuProvider {
    public TileElectricFurnace(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	public static float energyPerItem = 5000F;

    //Component parameters
    private double resistance = 100;
    private boolean isOn = false;

    //Calculated values
    public double voltage;          // V
    public double powerLevel;       // W
    public double bufferedEnergy;   // J

    public int progress;
    private SmeltingRecipe recipe = null;

    public boolean setWorking(boolean isOn) {
    	boolean changed = isOn!=this.isOn;
    	if (changed) {
    		this.isOn = isOn;
    		if (level.isClientSide) {
    			// Client Side
    	        markForRenderUpdate();
    	        this.level.getLightEngine().checkBlock(this.worldPosition);	//checkLightFor
    		} else {
    			// Server Side
                updateTileParameter();
                markTileEntityForS2CSync();
    		}
    	}
        return changed;
    }

    public double getEnergyRequired(SmeltingRecipe recipe) {
    	return recipe == null? -1 : recipe.getCookingTime() * 25;
    }

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;

        if (this.isOn) {
            if (itemStackHandler.canWork(recipe)) {
                this.bufferedEnergy += this.powerLevel / 20.0;

                if (this.bufferedEnergy > getEnergyRequired(recipe)) {
                    this.bufferedEnergy = 0;
                    itemStackHandler.makeResult(recipe.getResultItem().copy());
                }
            } else {
                // Item used up or removed or cannot be smelt
                this.bufferedEnergy = 0;
                setWorking(false);
            }

            this.progress = (int) (this.bufferedEnergy*100/getEnergyRequired(recipe));
        } else {
            // Furnace is not working (not using any electricity)
            if (itemStackHandler.canWork(recipe)) {
                setWorking(true);
            }
        }
    }

    @Override
    public void clearRemoved() {
    	super.clearRemoved();
    	itemStackHandler.onContentsChanged(0);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
        if (tagCompound.contains("inventory") && itemStackHandler != null)
            itemStackHandler.deserializeNBT(tagCompound.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
    	nbt.putDouble("resistance", this.resistance);
        nbt.putDouble("bufferedEnergy", this.bufferedEnergy);
        if (itemStackHandler != null)
        	nbt.put("inventory", itemStackHandler.serializeNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);
        nbt.putBoolean("isWorking", this.isOn);
    }

    @Override
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        super.onSyncDataFromServerArrived(nbt);
        this.setWorking(nbt.getBoolean("isWorking"));
        this.level.getLightEngine().checkBlock(this.worldPosition);	//checkLightFor
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    	if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return itemStackHdlerCap.cast();
    	}
        return super.getCapability(capability, facing);
    }

    ItemStackHandlerImpl itemStackHandler = new ItemStackHandlerImpl(this);
    private final LazyOptional<?> itemStackHdlerCap = LazyOptional.of(()->itemStackHandler);
    public final ItemStackHandlerInventory inventory = new ItemStackHandlerInventory(itemStackHandler) {
		@Override
		public void setChanged() {
//			furnance.markDirty();
		}

        @Override
        public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        	return slot == 0 && TileElectricFurnace.this.getSmeltingResult(stack) != null;
        }

		@Override
		public boolean stillValid(Player player) {
	        if (TileElectricFurnace.this.getLevel().getBlockEntity(TileElectricFurnace.this.getBlockPos()) != TileElectricFurnace.this)
	            return false;

	        final double X_CENTRE_OFFSET = 0.5;
	        final double Y_CENTRE_OFFSET = 0.5;
	        final double Z_CENTRE_OFFSET = 0.5;
	        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;

	        return player.distanceToSqr(
	        		worldPosition.getX() + X_CENTRE_OFFSET,
	        		worldPosition.getY() + Y_CENTRE_OFFSET,
	        		worldPosition.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
		}
    };

    static class ItemStackHandlerImpl extends ItemStackHandler {
        private final TileElectricFurnace owner;
        protected ItemStackHandlerImpl(TileElectricFurnace owner) {
            super(2);
            this.owner = owner;
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot == 0)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        public boolean canWork(SmeltingRecipe recipe) {
            if (recipe == null)
                return false;

            ItemStack recipeOutput = recipe.getResultItem();
            ItemStack itemStack = getStackInSlot(1);
            if (!itemStack.isEmpty()) {
                if (!itemStack.sameItem(recipeOutput))
                    return false;   // Different Type
                if (itemStack.getCount() + recipeOutput.getCount() > getStackLimit(1, recipeOutput))
                    return false;   // No more space
            }

            return true;
        }

        public void makeResult(ItemStack result) {
            super.extractItem(0, 1, false);	// Consume the ingredient

            ItemStack resultStack = this.getStackInSlot(1);
            // We assume that resultStack is valid for insert and replace

            if (resultStack.isEmpty()) {
                this.stacks.set(1, result);
            } else {
            	resultStack.grow(result.getCount());
            }
            onContentsChanged(1);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0)
                owner.recipe = owner.getSmeltingResult(this.getStackInSlot(0));
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        	return slot == 0 && owner.getSmeltingResult(stack) != null;
        }
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
        this.powerLevel = this.cachedParam.isOn() ? this.voltage * this.voltage / this.cachedParam.getResistance() : 0;


    	Utils.enqueueServerWork(()->{
			this.setSecondState(this.powerLevel > 100);
    	});
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
    /// MenuProvider
    ///////////////////////////////////
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerElectricFurnace(windowId, inv, this);
	}

	public SmeltingRecipe getSmeltingResult(ItemStack itemStack) {
		Optional<SmeltingRecipe> result = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(itemStack), level);
		return result.isPresent() ? result.get() : null;
	}
}
