package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
        @OnlyIn(Dist.CLIENT)
        public void onSyncDataFromServerArrived(CompoundNBT nbt) {
        	super.onSyncDataFromServerArrived(nbt);
            markForRenderUpdate();
        }
        
        //////////////////////////////
        /////TileEntity
        //////////////////////////////
        @Override
        @OnlyIn(Dist.CLIENT)
        public double getMaxRenderDistanceSquared() {
            return 100000;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public AxisAlignedBB getRenderBoundingBox() {
            return TileEntity.INFINITE_EXTENT_AABB;
        }
        
        public boolean hasFastRenderer() {
            return true;
        }
    }
}
