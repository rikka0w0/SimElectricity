package simelectricity.essential.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.essential.utils.Utils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public abstract class SEMachineBlock extends SEBlock implements ITileEntityProvider, ISESubBlock{
	protected final String[] subNames;
	
	//[meta][side]
	protected final IIcon[][] iconBuffer;
	protected final IIcon[][] iconBuffer2;
	
	
	public SEMachineBlock(String unlocalizedName, String[] subNames) {
		super(unlocalizedName, Material.rock, SEItemBlock.class);
		this.iconBuffer = new IIcon[subNames.length][6];
		this.iconBuffer2 = new IIcon[subNames.length][];
		
		this.subNames = new String[subNames.length];
		for (int i=0; i<subNames.length; i++)
			this.subNames[i] = subNames[i];
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
    
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    @Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
    	TileEntity te = world.getTileEntity(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	if (te instanceof ISidedFacing){
    		int facing = ((ISidedFacing)te).getFacing().ordinal();
    		
    		if (isSecondState(te))
    			return iconBuffer2[meta][Utils.sideAndFacingToSpriteOffset[side][facing]];
    		else
    			return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][facing]];
		}else{
			return iconBuffer[meta][3];
		}
	}
	
	@Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
		return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][3]];	//2 - North, Default facing
	}
	
	@Deprecated
	public static int renderID = 0; 	//Definition has changed from 1.8
	@Override
    public int getRenderType()
    {
        return renderID;
    }
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	
	protected abstract boolean isSecondState(TileEntity te);
}
