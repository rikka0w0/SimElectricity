package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import simelectricity.essential.common.SEEnergyTile;

public abstract class TilePowerTransformer extends SEEnergyTile implements IMultiBlockTile {
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
    public MultiBlockTileInfo getMultiBlockTileInfo() {
        return mbInfo;
    }
}
