package simelectricity.essential.machines.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
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
import rikka.librikka.Utils;
import rikka.librikka.tileentity.INamedContainerProvider2;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.ItemStackHandlerInventory;
import simelectricity.essential.common.semachine.ISE2StateTile;
import simelectricity.essential.common.semachine.SESinglePortMachine;
import simelectricity.essential.machines.gui.ContainerElectricFurnace;

import java.util.Optional;

import javax.annotation.Nonnull;

public class TileElectricFurnace extends SESinglePortMachine<ISEVoltageSource> implements 
		ISEVoltageSource, ISE2StateTile, ISEEnergyNetUpdateHandler, ITickableTileEntity, INamedContainerProvider2 {
    public static float energyPerItem = 5000F;

    //Component parameters
    private double resistance = 100;
    private boolean isOn = false;

    //Calculated values
    public double voltage;          // V
    public double powerLevel;       // W
    public double bufferedEnergy;   // J

    public int progress;
    private FurnaceRecipe recipe = null;

    public boolean setWorking(boolean isOn) {
    	boolean changed = isOn!=this.isOn;
    	if (changed) {
    		this.isOn = isOn;
    		if (world.isRemote) {
    			// Client Side
    	        markForRenderUpdate();
    	        this.world.getLightManager().checkBlock(this.pos);	//checkLightFor
    		} else {
    			// Server Side
                updateTileParameter();
                markTileEntityForS2CSync();		
    		}
    	}
        return changed;
    }
    
    public double getEnergyRequired(FurnaceRecipe recipe) {
    	return recipe == null? -1 : recipe.getCookTime() * 25;
    }

    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (this.world.isRemote)
            return;

        if (this.isOn) {
            if (itemStackHandler.canWork(recipe)) {
                this.bufferedEnergy += this.powerLevel / 20.0;

                if (this.bufferedEnergy > getEnergyRequired(recipe)) {
                    this.bufferedEnergy = 0;
                    itemStackHandler.makeResult(recipe.getRecipeOutput().copy());
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
    public void onLoad() {
    	super.onLoad();
    	itemStackHandler.onContentsChanged(0);
    }
    
    @Override
    public void read(BlockState blockState, CompoundNBT tagCompound) {
        super.read(blockState, tagCompound);

        this.resistance = tagCompound.getDouble("resistance");
        this.bufferedEnergy = tagCompound.getDouble("bufferedEnergy");
        if (tagCompound.contains("inventory") && itemStackHandler != null)
            itemStackHandler.deserializeNBT(tagCompound.getCompound("inventory"));
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("resistance", this.resistance);
        tagCompound.putDouble("bufferedEnergy", this.bufferedEnergy);
        if (itemStackHandler != null)
            tagCompound.put("inventory", itemStackHandler.serializeNBT());

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
        this.setWorking(nbt.getBoolean("isWorking"));
        this.world.getLightManager().checkBlock(this.pos);	//checkLightFor
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    	if (!this.removed && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return itemStackHdlerCap.cast();
    	}
        return super.getCapability(capability, facing);
    }

    final TileElectricFurnace furnance = this;
    ItemStackHandlerImpl itemStackHandler = new ItemStackHandlerImpl(this);
    private final LazyOptional<?> itemStackHdlerCap = LazyOptional.of(()->itemStackHandler);
    public final ItemStackHandlerInventory inventory = new ItemStackHandlerInventory(itemStackHandler) {
		@Override
		public void markDirty() {
//			furnance.markDirty();
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity player) {
	        if (furnance.getWorld().getTileEntity(furnance.getPos()) != furnance)
	            return false;
	        
	        final double X_CENTRE_OFFSET = 0.5;
	        final double Y_CENTRE_OFFSET = 0.5;
	        final double Z_CENTRE_OFFSET = 0.5;
	        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;

	        return player.getDistanceSq(
	        		pos.getX() + X_CENTRE_OFFSET, 
	        		pos.getY() + Y_CENTRE_OFFSET, 
	        		pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
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

        public boolean canWork(FurnaceRecipe recipe) {
            if (recipe == null)
                return false;

            ItemStack recipeOutput = recipe.getRecipeOutput();
            ItemStack itemStack = getStackInSlot(1);
            if (!itemStack.isEmpty()) {
                if (!itemStack.isItemEqual(recipeOutput))
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
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerElectricFurnace(windowId, inv, this);
	}
	
	public FurnaceRecipe getSmeltingResult(ItemStack itemStack) {
		Optional<FurnaceRecipe> result = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemStack), world);
		return result.isPresent() ? result.get() : null;
	}
}
