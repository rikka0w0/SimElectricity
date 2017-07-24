package simelectricity.essential;

import cpw.mods.fml.client.registry.ClientRegistry;

import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.render.RenderBlockCable;
import simelectricity.essential.cable.render.RenderLedPanel;
import simelectricity.essential.cable.render.RenderVoltageSensorPanel;
import simelectricity.essential.common.ISEGuiProvider;
import simelectricity.essential.extensions.buildcraft.client.BCFacadeRender;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockTransmissionTower2;
import simelectricity.essential.grid.BlockTransmissionTowerTop;
import simelectricity.essential.grid.BlockTransmissionTowerBottom;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TileTransmissionTower;
import simelectricity.essential.grid.TileTransmissionTower2;
import simelectricity.essential.grid.render.BlockRenderCableJoint;
import simelectricity.essential.grid.render.BlockRenderTransmissionTower2;
import simelectricity.essential.grid.render.ModelBaker;
import simelectricity.essential.grid.render.TileRenderTransmissionTower;
import simelectricity.essential.grid.render.BlockRenderTransmissionTowerTop;
import simelectricity.essential.grid.render.BlockRenderTransmissionTowerBottom;
import simelectricity.essential.grid.render.TileRenderTranmissionTowerBase;
import simelectricity.essential.grid.render.TileRenderTransmissionTower2;
import simelectricity.essential.machines.gui.GuiAdjustableResistor;
import simelectricity.essential.machines.gui.GuiAdjustableTransformer;
import simelectricity.essential.machines.gui.GuiDiode;
import simelectricity.essential.machines.gui.GuiQuantumGenerator;
import simelectricity.essential.machines.gui.GuiSwitch;
import simelectricity.essential.machines.gui.GuiVoltageMeter;
import simelectricity.essential.machines.render.BlockRenderMachine;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.machines.tile.TileVoltageMeter;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
		new ModelBaker();
		BlockTransmissionTowerTop.renderID = (new BlockRenderTransmissionTowerTop()).getRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileTransmissionTower.class, new TileRenderTransmissionTower());
		BlockTransmissionTowerBottom.renderID = (new BlockRenderTransmissionTowerBottom()).getRenderId();
		BlockTransmissionTower2.renderID = (new BlockRenderTransmissionTower2()).getRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileTransmissionTower2.class, new TileRenderTransmissionTower2());
		
		//Cable Joint
		BlockCableJoint.renderID = (new BlockRenderCableJoint()).getRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCableJoint.class, new TileRenderTranmissionTowerBase());
		
		BlockRegistry.blockElectronics.renderID = (new BlockRenderMachine()).getRenderId();
		BlockRegistry.blockTwoPortElectronics.renderID = BlockRegistry.blockElectronics.renderID;
		
		//BCFacadeRender
		new BCFacadeRender();
		new RenderLedPanel();
		new RenderVoltageSensorPanel();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof ISEGuiProvider)
			return ((ISEGuiProvider) te).getClientGuiContainer(ForgeDirection.getOrientation(ID));
		
		Container container = BlockRegistry.getContainer(te, player);
		
		if (te instanceof TileVoltageMeter)
			return new GuiVoltageMeter(container);
		if (te instanceof TileQuantumGenerator)
			return new GuiQuantumGenerator(container);
		if (te instanceof TileAdjustableResistor)
			return new GuiAdjustableResistor(container);
		
		if (te instanceof TileAdjustableTransformer)
			return new GuiAdjustableTransformer(container);
		if (te instanceof TileDiode)
			return new GuiDiode(container);
		if (te instanceof TileSwitch)
			return new GuiSwitch(container);
		
		return null;
	}
}
