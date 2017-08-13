package simelectricity.extension.buildcraft;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

@SideOnly(Side.CLIENT)
public class BlockColorHandler implements IBlockColor{
	public final static IBlockColor colorHandler = new BlockColorHandler();
	
	@Override
	public int colorMultiplier(IBlockState blockState, IBlockAccess world, BlockPos pos, int tintIndex) {
		if (world != null && pos != null) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof ISECoverPanelHost) {
				ISECoverPanelHost cable = (ISECoverPanelHost)te;
				
				EnumFacing side = BCFacadeRender.getFacing(tintIndex);
				ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
				
				if (coverPanel instanceof BCFacadePanel) {
					BCFacadePanel bcFacade = (BCFacadePanel) coverPanel;
					IBlockState state = bcFacade.getBlockState();
					tintIndex = BCFacadeRender.getTint(tintIndex);
					return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, pos, tintIndex);
				}
				
				return -1;
			}
		}
		
		return -1;
	}

}
