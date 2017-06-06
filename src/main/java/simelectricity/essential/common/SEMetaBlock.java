package simelectricity.essential.common;

import net.minecraft.block.material.Material;

public abstract class SEMetaBlock extends SEBlock{
	public SEMetaBlock(String unlocalizedName, Material material) {
		super(unlocalizedName, material, SEItemBlock.class);
	}

	/**
	 * Expose conventional meta data
	 */
	//public static IProperty<Integer> propertyMeta;
	
	/**
	 * @param properties use properties.add() to add custom blockStates
	 */
	/*
	public void registerBlockState(LinkedList<IProperty> properties){
		propertyMeta = PropertyInteger.create("meta", 0, 15);
		properties.add(propertyMeta);
	}
	*/
	
	/**
	 * Initialize properties and set their default value
	 * @param baseState use baseState.withProperty(name, value) to set default value for properties
	 * @return
	 */
	/*
	public IBlockState setDefaultBlockState(IBlockState baseState){
		return baseState.withProperty(propertyMeta, 0);
	}
	*/
	
	/**
	 * Set blockStates which related to meta data only, other blockStates are set in getActualState()
	 * </p>
	 * If you want to retrieve a block's state, call getActualState()
	 */
	/*
	@Override
    public final IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(propertyMeta, meta);
    }
	*/
	
	/**
	 * Convert blockState back to meta, the map between meta and blockStates MUST consistent!!!
	 */
	/*
	@Override
    public final int getMetaFromState(IBlockState state)
    {
		int meta = state.getValue(propertyMeta);
		return meta;
    }
    */
}