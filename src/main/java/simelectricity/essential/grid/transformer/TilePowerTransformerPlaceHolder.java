package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.common.SETileEntity;
import simelectricity.essential.common.multiblock.ISEMultiBlockTile;
import simelectricity.essential.common.multiblock.MultiBlockTileInfo;
import simelectricity.essential.utils.Utils;

public class TilePowerTransformerPlaceHolder extends SETileEntity implements ISEMultiBlockTile{
	protected MultiBlockTileInfo mbInfo;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		mbInfo = new MultiBlockTileInfo(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		mbInfo.saveToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public MultiBlockTileInfo getMultiBlockTileInfo() {
		return this.mbInfo;
	}
	
	@Override
	public void onStructureCreating(MultiBlockTileInfo mbInfo) {
		this.mbInfo = mbInfo;
		this.markDirty();
	}

	@Override
	public void onStructureCreated() {}

	@Override
	public void onStructureRemoved() {}
	
	public static class Primary extends TilePowerTransformerPlaceHolder{
		public ISEGridNode getPrimaryTile() {
			BlockPos pos = mbInfo.getPartPos(EnumBlockType.Primary.offset);
			TileEntity te = world.getTileEntity(pos);
			return (te instanceof TilePowerTransformerWinding.Primary) ?
					((TilePowerTransformerWinding.Primary) te).getGridNode() : null;
		}
		
		public boolean canConnect() {
			BlockPos pos = mbInfo.getPartPos(EnumBlockType.Primary.offset);
			TileEntity te = world.getTileEntity(pos);
			return (te instanceof TilePowerTransformerWinding) ?
					((TilePowerTransformerWinding) te).canConnect() : false;
		}
	}
	
	public static class Secondary extends TilePowerTransformerPlaceHolder{
		public ISEGridNode getSecondaryTile() {
			BlockPos pos = mbInfo.getPartPos(EnumBlockType.Secondary.offset);
			TileEntity te = world.getTileEntity(pos);
			return (te instanceof TilePowerTransformerWinding.Secondary) ?
					((TilePowerTransformerWinding.Secondary) te).getGridNode() : null;
		}
		
		public boolean canConnect() {
			BlockPos pos = mbInfo.getPartPos(EnumBlockType.Secondary.offset);
			TileEntity te = world.getTileEntity(pos);
			return (te instanceof TilePowerTransformerWinding) ?
					((TilePowerTransformerWinding) te).canConnect() : false;
		}
	}
	
	public static class Render extends TilePowerTransformerPlaceHolder{
		@SideOnly(Side.CLIENT)
		private EnumFacing facing;
		@SideOnly(Side.CLIENT)
		private boolean mirrored;
		
		@Override
		public void prepareS2CPacketData(NBTTagCompound nbt) {
			Utils.saveToNbt(nbt, "facing", mbInfo.facing);
			nbt.setBoolean("mirrored", mbInfo.mirrored);
		}
		
		@Override
		@SideOnly(value = Side.CLIENT)
		public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
			this.facing = Utils.facingFromNbt(nbt, "facing");
			this.mirrored = nbt.getBoolean("mirrored");
			
			this.markForRenderUpdate();
			
			super.onSyncDataFromServerArrived(nbt);
		}
		
		public EnumFacing getFacing() {
			if (world.isRemote)
				return facing;
			else
				return mbInfo.facing;
		}
		
		public boolean isMirrored() {
			if (world.isRemote)
				return mirrored;
			else
				return mbInfo.mirrored;
		}
	}
}
