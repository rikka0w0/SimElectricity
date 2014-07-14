package simElectricity.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileIncandescentLamp extends TileStandardSEMachine {
	
	public float lightLevel = 0.0F;
	
    @Override
    public void updateEntity() {
        super.updateEntity();

        //Server only
        if (worldObj.isRemote)
            return;

        lightLevel = Energy.getPower(this) / 100.0F;
        Util.updateTileEntityField(this, "lightLevel");
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }

	@Override
	public float getMaxSafeVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverVoltage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getOutputVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getResistance() {
		// 100 watt at 230V
		return 529;
	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onOverloaded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getInventorySize() {
		// TODO Auto-generated method stub
		return 0;
	}
}
