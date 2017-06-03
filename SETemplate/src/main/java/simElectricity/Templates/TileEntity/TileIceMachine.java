package simElectricity.Templates.TileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.SEAPI;
import simElectricity.Templates.SEItems;
import simElectricity.Templates.Common.TileStandardSEMachine;
import simElectricity.Templates.Utils.Utils;

public class TileIceMachine extends TileStandardSEMachine implements IFluidHandler, IEnergyNetUpdateHandler{
	public int maxCapacity = 10000;
	public float onResistance = 1000F;
	public float energyPerItem = 4000F;
	public static float waterPerIceIngot = 1000 / 9;
	
	public FluidTank tank=new FluidTank(maxCapacity);
	
	public float energyStored;
	public float resistance = Float.MAX_VALUE;
    
	public boolean isWorking;
	public boolean operationalVoltage;
	public int fluidID, amountP, progress, isPowered;
	
	
	void dowork(FluidStack fluid){
		if (this.tank.getFluidAmount() >= waterPerIceIngot && operationalVoltage && 	//Enough water
				(inv[2] != null ? inv[2].stackSize < inv[2].getMaxStackSize() : true)){	//Enough space
			if (resistance != onResistance){
				resistance = onResistance;
				SEAPI.energyNetAgent.updateTileParameter(this);
			}
			
			isWorking = true;
		}else{
			if (resistance != Float.MAX_VALUE){
				resistance = Float.MAX_VALUE;
				SEAPI.energyNetAgent.updateTileParameter(this);
			}
			
			isWorking = false;
		}
		
		energyStored += Math.pow(SEAPI.energyNetAgent.getVoltage(tile),2) / resistance;
			
		if (energyStored >= energyPerItem){
			energyStored -= energyPerItem;
			fluid.amount -= waterPerIceIngot;
				
			if (inv[2] == null)
				inv[2] = new ItemStack(SEItems.iceIngot,1);
			else
				inv[2].stackSize++;
		}
		
		
		progress = (int) (100 * energyStored / energyPerItem);
	}
	
    @Override
    public void updateEntity(){  	
        super.updateEntity();
                       
        if (worldObj.isRemote)
            return;
        
        FluidStack fluid = tank.getFluid();
        
        if(fluid!=null)
        	if (fluid.amount==0)
        		fluid=null;
        	else
        		dowork(fluid);
        	
        FluidStack l= Utils.drainContainer(maxCapacity, fluid, inv, 0, 1);
        if (l!=null){
        	if (fluid==null)
        		fluid=l;
        	else
        		fluid.amount+=l.amount;
        }
        
        tank.setFluid(fluid);
        
        if (fluid!=null){
        	fluidID=fluid.getFluidID();
        	amountP=fluid.amount*1000/maxCapacity;
        }
        else
        	fluidID=0;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound n) {
    	super.readFromNBT(n);
    	
    	isWorking = n.getBoolean("isWorking");
    	energyStored = n.getFloat("energyStored");
    	
    	tank.readFromNBT(n);
    }
	
    @Override
	public void writeToNBT(NBTTagCompound n) {
    	super.writeToNBT(n);
        
    	n.setBoolean("isWorking", isWorking);
    	n.setFloat("energyStored", energyStored);
    	
        tank.writeToNBT(n);
    }
	  
    
    
	@Override
	public void onEnergyNetUpdate() {
		operationalVoltage = SEAPI.energyNetAgent.getVoltage(tile) >= 200;
		isPowered = operationalVoltage ? 1 : 0;
	}
    
    
	
	
	
	@Override
	public double getResistance() {
		return resistance;
	}

    @Override
	public int getInventorySize(){
    	return 3;
    }
	
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] {0, 1, 2};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return slot == 0;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return slot == 1 || slot == 2;
    }
    
    
    
    
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())){
            return null;
        }
        return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tank.getInfo()};
	}
}
