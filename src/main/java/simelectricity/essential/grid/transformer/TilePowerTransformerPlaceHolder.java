package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public class TilePowerTransformerPlaceHolder extends SEMultiBlockEnergyTile {
    @Override
    public void onLoad() {}
	@Override
	protected void onStructureCreating() {}
    @Override
    public void onStructureCreated() {}
    @Override
    public void onStructureRemoved() {}	
    
    public static class Primary extends TilePowerTransformerPlaceHolder {
        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Primary.offset);
            TileEntity te = this.world.getTileEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding)te : null;
        }
    }

    public static class Secondary extends TilePowerTransformerPlaceHolder {
        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Secondary.offset);
            TileEntity te = this.world.getTileEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding) te : null;
        }
    }

    public static class Render extends TilePowerTransformerPlaceHolder {
        @Override
        @SideOnly(Side.CLIENT)
        public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        	super.onSyncDataFromServerArrived(nbt);
            markForRenderUpdate();
        }
    }
}
