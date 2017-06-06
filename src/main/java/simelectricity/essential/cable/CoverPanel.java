package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanel;

public class CoverPanel implements ISECoverPanel{
	private boolean isBCFacade;
	private boolean isHollow;
	private int meta;
	private Block block;
	
	public CoverPanel(boolean isBCFacade, boolean isHollow, int meta, Block block){
		this.isBCFacade = isBCFacade;
		this.isHollow = isHollow;
		this.meta = meta;
		this.block = block;
	}
	
	@Override
	public boolean isBuildCraftFacade() {return isBCFacade;}

	@Override
	public boolean isHollow() {return isHollow;}

	@Override
	public int getBlockMeta() {return meta;}

	@Override
	public Block getBlock() {return block;}

	@Override
	public void toNBT(NBTTagCompound nbt) {
		nbt.setBoolean("isBCFacade", isBCFacade);
		nbt.setBoolean("isHollow", isHollow);
		nbt.setInteger("meta", meta);
		nbt.setInteger("blockID", Block.getIdFromBlock(block));
	}
}
