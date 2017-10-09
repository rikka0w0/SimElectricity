package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public class TileDistributionTransformerPole extends SEMultiBlockEnergyTile{

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        super.onSyncDataFromServerArrived(nbt);
        
        this.markForRenderUpdate();
    }
	
	@Override
	public void onLoad() {}
	
	@Override
	public void onStructureCreated() {
		System.out.println("onStructureCreated");
	}

	@Override
	public void onStructureRemoved() {
		System.out.println("onStructureRemoved");
		
	}

	@Override
	protected void onStructureCreating() {
		System.out.println("onStructureCreating");
	}
}
