package simelectricity.essential;

import cpw.mods.fml.client.registry.ClientRegistry;


import simelectricity.essential.blocks.GuiVoltageMeter;
import simelectricity.essential.blocks.TileVoltageMeter;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.render.RenderBlockCable;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockTransmissionTowerTop;
import simelectricity.essential.grid.BlockTransmissionTowerBottom;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TileTransmissionTower;
import simelectricity.essential.grid.render.BlockRenderCableJoint;
import simelectricity.essential.grid.render.TileRenderTransmissionTower;
import simelectricity.essential.grid.render.BlockRenderTransmissionTowerTop;
import simelectricity.essential.grid.render.BlockRenderTransmissionTowerBottom;
import simelectricity.essential.grid.render.TileRenderTranmissionTowerBase;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy{
	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public World getClientWorld(){
		return Minecraft.getMinecraft().theWorld;
	}
	
	@Override
	public Object getClientThread() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public void registerRenders() {
		//Cable
		BlockCable.renderID = (new RenderBlockCable()).getRenderId();
		RenderBlockCable.bakeCableModel(BlockRegistry.blockCable);
		
		//Transmission Tower
		BlockTransmissionTowerTop.renderID = (new BlockRenderTransmissionTowerTop()).getRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileTransmissionTower.class, new TileRenderTransmissionTower());
		BlockTransmissionTowerBottom.renderID = (new BlockRenderTransmissionTowerBottom()).getRenderId();
		
		//Cable Joint
		BlockCableJoint.renderID = (new BlockRenderCableJoint()).getRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCableJoint.class, new TileRenderTranmissionTowerBase());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof TileVoltageMeter)
			return new GuiVoltageMeter(te);
		
		return null;
	}
}
