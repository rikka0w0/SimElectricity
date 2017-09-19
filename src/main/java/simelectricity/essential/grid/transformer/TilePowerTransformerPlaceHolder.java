package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public class TilePowerTransformerPlaceHolder extends TilePowerTransformer {
    @Override
    public void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        markDirty();
    }

    @Override
    public void onStructureCreated() {
    }

    @Override
    public void onStructureRemoved() {
    }
	
    public static class Primary extends TilePowerTransformerPlaceHolder {
        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumBlockType.Primary.offset);
            TileEntity te = this.world.getTileEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding)te : null;
        }
    }

    public static class Secondary extends TilePowerTransformerPlaceHolder {
        public TilePowerTransformerWinding getWinding() {
            BlockPos pos = this.mbInfo.getPartPos(EnumBlockType.Secondary.offset);
            TileEntity te = this.world.getTileEntity(pos);
            return te instanceof TilePowerTransformerWinding ? (TilePowerTransformerWinding) te : null;
        }
    }

    public static class Render extends TilePowerTransformerPlaceHolder {
        @SideOnly(Side.CLIENT)
        private EnumFacing facing;
        @SideOnly(Side.CLIENT)
        private boolean mirrored;

        @Override
        public void prepareS2CPacketData(NBTTagCompound nbt) {
            Utils.saveToNbt(nbt, "facing", this.mbInfo.facing);
            nbt.setBoolean("mirrored", this.mbInfo.mirrored);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
            facing = Utils.facingFromNbt(nbt, "facing");
            mirrored = nbt.getBoolean("mirrored");

            markForRenderUpdate();

            super.onSyncDataFromServerArrived(nbt);
        }

        public EnumFacing getFacing() {
            if (this.world.isRemote)
                return this.facing;
            else
                return this.mbInfo.facing;
        }

        public boolean isMirrored() {
            if (this.world.isRemote)
                return this.mirrored;
            else
                return this.mbInfo.mirrored;
        }
    }
}
