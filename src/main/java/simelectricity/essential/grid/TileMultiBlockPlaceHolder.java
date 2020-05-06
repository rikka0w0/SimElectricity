package simelectricity.essential.grid;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelDataMap;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import rikka.librikka.tileentity.TileEntityBase;
import simelectricity.essential.Essential;

public class TileMultiBlockPlaceHolder extends TileEntityBase implements IMultiBlockTile {
	public TileMultiBlockPlaceHolder() {
		super(Essential.MODID);
	}

	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.write(nbt);
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
    
    /////////////////////////////////////////////////////////
    /////Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundNBT nbt) {
        super.prepareS2CPacketData(nbt);
    	mbInfo.saveToNBT(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
    	mbInfo = new MultiBlockTileInfo(nbt);
        
        super.onSyncDataFromServerArrived(nbt);
    }
    
	@Override
    protected void collectModelData(ModelDataMap.Builder builder) {
		builder.withInitial(IMultiBlockTile.prop, this);
    }

    /////////////////////////////////////////////////////////
    /////Functions to be implemented
    /////////////////////////////////////////////////////////
    public void onStructureCreating() {
    	
    }
    
	@Override
	public void onStructureCreated() {

	}

	@Override
	public void onStructureRemoved() {

	}
}
