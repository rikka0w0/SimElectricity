package simelectricity.essential.machines.tile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SESinglePortMachine;

public class TileSolarPanel extends SESinglePortMachine implements ISEVoltageSource, ISESocketProvider, ITickable{
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
    public void update() {
        if (world.isRemote)
            return;

        //Server only
        if (!world.provider.isSurfaceWorld() || !world.canBlockSeeSky(pos.up())){
        	detectAndSendChange(STATE_CAVE);
        	return;
        }
        
        if (world.isDaytime())
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
    public boolean canWrenchBeUsed(EnumFacing newFunctionalSide) {
        return newFunctionalSide != EnumFacing.UP;
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
	public int getSocketIconIndex(EnumFacing side) {
		return side == functionalSide ? 1 : -1;
	}
}
