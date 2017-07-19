package simelectricity.essential.machines.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileSolarPanel extends SESinglePortMachine implements ISEVoltageSource, ISESocketProvider{
    //Component parameters
	public double internalVoltage = 230;
    public double resistance = 0.1;
	
    private static byte STATE_DAY=0;
    private static byte STATE_NIGHT=1;
    private static byte STATE_CAVE=2;
    private byte state = -1;
    
	///////////////////////////////////
    /// TileEntity
	///////////////////////////////////
	@Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        //Server only
        if (!worldObj.provider.isSurfaceWorld() || !worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)){
        	detectAndSendChange(STATE_CAVE);
        	return;
        }
        
        if (worldObj.isDaytime())
            detectAndSendChange(STATE_DAY);
        else 
            detectAndSendChange(STATE_NIGHT);
        
    }
	
	void detectAndSendChange(byte state){
		if (this.state != state){
			this.state = state;
			
			if (state == STATE_DAY){
				this.internalVoltage = 22;
				this.resistance = 0.8;
			}else if (state == STATE_NIGHT){
				this.internalVoltage = 18;
				this.resistance = 10;
			}else{
				this.internalVoltage = 10;
				this.resistance = 100;
			}
			
			SEAPI.energyNetAgent.updateTileParameter(this);
		}
	}

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public boolean canWrenchBeUsed(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }
    
    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
	@Override
	public double getOutputVoltage() {
		return internalVoltage;
	}

	@Override
	public double getResistance() {
		return resistance;
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		return side == functionalSide ? 1 : -1;
	}
}
