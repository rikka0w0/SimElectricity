package simElectricity.Blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.IEnergyTile;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.TileStandardSEMachine;
import simElectricity.API.Util;

public class TileVoltageMeter extends TileStandardSEMachine{
	public float voltage=0;

    @Override
	public float getResistance() {return 1000000;}

	@Override
	public void onOverloaded() {}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public float getOutputVoltage() {return 0;}

	@Override
	public float getMaxSafeVoltage() {return 0;}

	@Override
	public void onOverVoltage() {}
}
