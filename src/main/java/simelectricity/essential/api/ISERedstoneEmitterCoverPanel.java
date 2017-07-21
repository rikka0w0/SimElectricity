package simelectricity.essential.api;

import net.minecraft.tileentity.TileEntity;

public interface ISERedstoneEmitterCoverPanel extends ISECoverPanel{
	boolean isProvidingWeakPower();
	
	/**
	 * @return true if the redstone state has changed
	 */
	boolean checkRedStoneSignal(TileEntity te, double voltage);
}
