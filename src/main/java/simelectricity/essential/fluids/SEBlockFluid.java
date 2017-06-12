package simelectricity.essential.fluids;

import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class SEBlockFluid extends BlockFluidClassic{	
    public SEBlockFluid(Fluid fluid, String name) {
		super(fluid, Material.water);
		this.setBlockName(name);
		this.registryName = name;
		this.setCreativeTab(SEAPI.SETab);
		GameRegistry.registerBlock(this, SEItemBlockFluid.class ,name);
	}
    
	@Deprecated
	protected String registryName;	//1.11.2 compatibility
    
    @Deprecated
	@SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister IconRegister)
    {
    	this.definedFluid.setFlowingIcon(
    			IconRegister.registerIcon(Essential.modID + ":fluids/" + registryName + "_flow")
    			).setStillIcon(
    			IconRegister.registerIcon(Essential.modID + ":fluids/" + registryName)
    			);
    }
	
    @Deprecated
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
    	return this.definedFluid.getStillIcon();
    }
}
