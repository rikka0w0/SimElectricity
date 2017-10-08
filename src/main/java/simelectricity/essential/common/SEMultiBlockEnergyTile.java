package simelectricity.essential.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public abstract class SEMultiBlockEnergyTile extends SEEnergyTile implements IMultiBlockTile {
	@SideOnly(Side.CLIENT)
    protected EnumFacing facing;
    @SideOnly(Side.CLIENT)
    protected boolean mirrored;
	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.writeToNBT(nbt);
    }
    
    @Override
    public final void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        markDirty();
        
        onStructureCreating();
    }
        
    @Override
    public MultiBlockTileInfo getMultiBlockTileInfo() {
        return mbInfo;
    }
    
    protected abstract void onStructureCreating();
    
    /////////////////////////////////////////////////////////
    /////Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "facing", this.mbInfo.facing);
        nbt.setBoolean("mirrored", this.mbInfo.mirrored);
        
        super.prepareS2CPacketData(nbt);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        facing = Utils.facingFromNbt(nbt, "facing");
        mirrored = nbt.getBoolean("mirrored");
        
        super.onSyncDataFromServerArrived(nbt);
    }
    
    /////////////////////////////////////////////////////////
    /////Utils
    /////////////////////////////////////////////////////////
    public boolean isMirrored() {
    	return world.isRemote ? this.mirrored : (this.mbInfo==null? null : this.mbInfo.mirrored);
    }
    
    public EnumFacing getFacing() {
    	return world.isRemote ? this.facing : (this.mbInfo==null? null : this.mbInfo.facing);
    }
}
