package simelectricity.essential.common;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class SEBlock extends Block{
	protected SEItemBlock itemBlock;
	
	@Deprecated
	protected String registryName;	//1.11.2 compatibility
	
    public SEBlock(String unlocalizedName, Material material, Class<? extends SEItemBlock> itemBlockClass) {
        super(material);
        this.setBlockName(unlocalizedName);
        //this.setUnlocalizedName(unlocalizedName);
        registryName = unlocalizedName;
        //this.setRegistryName(unlocalizedName);				//Key!
        
        //this.setDefaultState(setDefaultBlockState(this.blockState.getBaseState()));
        this.beforeRegister();
        
        GameRegistry.registerBlock(this, itemBlockClass, unlocalizedName);
        itemBlock = (SEItemBlock) Item.getItemFromBlock(this);
        //GameRegistry.register(this);
        //itemBlock = new SEItemBlock(this);
        //GameRegistry.register(itemBlock, this.getRegistryName());
        //new SEItemRenderRegistery(this);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    //public final void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
    public final void getSubBlocks(Item itemIn, CreativeTabs tab, List subItems){
    	if (itemBlock.getHasSubtypes()){
            for (int ix = 0; ix < ((ISESubBlock)this).getSubBlockUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
    	}else{
    		super.getSubBlocks(itemIn, tab, subItems);
    	}

    }
    
	@Override
	public int damageDropped(int meta){
		return itemBlock.getHasSubtypes() ? meta : 0;
	//public final int damageDropped(IBlockState state) {
	    //return getMetaFromState(state);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
	//public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
	    //return new ItemStack(itemBlock, 1, this.getMetaFromState(world.getBlockState(pos)));
		return itemBlock.getHasSubtypes() ? new ItemStack(itemBlock, 1, world.getBlockMetadata(x, y, z))
											: super.getPickBlock(target, world, x, y, z, player);
	}
    
    public final SEItemBlock getItemBlock(){
    	return this.itemBlock;
    }
    
    public void beforeRegister(){}
    
	//BlockState --------------------------------------------------------------------
	/**
	 * @param properties use properties.add() to add custom blockStates
	 */
	//public void registerBlockState(LinkedList<IProperty> properties){}
	
	/**
	 * Initialize properties and set their default value
	 * @param baseState use baseState.withProperty(name, value) to set default value for properties
	 * @return
	 */
	/*public IBlockState setDefaultBlockState(IBlockState baseState){return baseState;}
	
	@Override
	protected final BlockStateContainer createBlockState(){
		LinkedList<IProperty> customProperties = new LinkedList<IProperty>();
		registerBlockState(customProperties);
		
		IProperty[] properties = new IProperty[customProperties.size()];
		
		int i = 0;
		for (IProperty p: customProperties){
			properties[i] = p;
			i++;
		}
			
		return new BlockStateContainer(this, properties);
	}*/
}
