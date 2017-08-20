package simelectricity.essential;

import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.CustomModelLoader;
import simelectricity.essential.client.cable.CableStateMapper;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.grid.GridStateMapper;
import simelectricity.essential.client.grid.TileRenderPowerPole;
import simelectricity.essential.client.grid.transformer.PowerTransformerStateMapper;
import simelectricity.essential.client.semachine.SEMachineStateMapper;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.common.ISEGuiProvider;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.grid.TilePowerPole2;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding;
import simelectricity.essential.machines.gui.GuiAdjustableResistor;
import simelectricity.essential.machines.gui.GuiAdjustableTransformer;
import simelectricity.essential.machines.gui.GuiCurrentSensor;
import simelectricity.essential.machines.gui.GuiDiode;
import simelectricity.essential.machines.gui.GuiQuantumGenerator;
import simelectricity.essential.machines.gui.GuiSwitch;
import simelectricity.essential.machines.gui.GuiVoltageMeter;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.machines.tile.TileVoltageMeter;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy{
	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().player;
	}
	
	@Override
	public World getClientWorld(){
		return Minecraft.getMinecraft().world;
	}
	
	@Override
	public IThreadListener getClientThread() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public void preInit(){
		//Initialize the client-side API
		SEEAPI.coloredBlocks = new LinkedList<Block>();
		
		CustomModelLoader loader = new CustomModelLoader(Essential.modID);		
		loader.registerInventoryIcon(ItemRegistry.itemHVCable);
		loader.registerInventoryIcon(ItemRegistry.itemVitaTea);
		loader.registerInventoryIcon(ItemRegistry.itemMisc);
		loader.registerInventoryIcon(ItemRegistry.itemTools);
		
		SEMachineStateMapper semStateMapper = new SEMachineStateMapper(Essential.modID);
		loader.registerModelLoader(semStateMapper);
		semStateMapper.register(BlockRegistry.blockElectronics);
		semStateMapper.register(BlockRegistry.blockTwoPortElectronics);
		
		CableStateMapper cStateMapper = new CableStateMapper(Essential.modID);
		loader.registerModelLoader(cStateMapper);
		cStateMapper.register(BlockRegistry.blockCable);
		loader.registerInventoryIcon(BlockRegistry.blockCable.itemBlock);
		
		GridStateMapper gStateMapper = new GridStateMapper(Essential.modID);
		loader.registerModelLoader(gStateMapper);
		gStateMapper.register(BlockRegistry.cableJoint);
		loader.registerInventoryIcon(BlockRegistry.cableJoint.itemBlock);
		gStateMapper.register(BlockRegistry.powerPoleBottom);
		loader.registerInventoryIcon(BlockRegistry.powerPoleBottom.itemBlock);
		gStateMapper.register(BlockRegistry.powerPoleCollisionBox);
		loader.registerInventoryIcon(BlockRegistry.powerPoleCollisionBox.itemBlock);
		gStateMapper.register(BlockRegistry.powerPoleTop);
		loader.registerInventoryIcon(BlockRegistry.powerPoleTop.itemBlock);
		gStateMapper.register(BlockRegistry.powerPole2);
		loader.registerInventoryIcon(BlockRegistry.powerPole2.itemBlock);
		
		PowerTransformerStateMapper ptStateMapper = new PowerTransformerStateMapper(Essential.modID);
		loader.registerModelLoader(ptStateMapper);
		ptStateMapper.register(BlockRegistry.powerTransformer);
		
		//Initialize socket render and support render
		new SocketRender();
		new SupportRender();
		
		//Initialize coverpanel render
		new VoltageSensorRender();
		new LedPanelRender();
	}
	
	@Override
	public void init() {
		SEEAPI.coloredBlocks.add(BlockRegistry.blockCable);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileCableJoint.class, new TileRenderPowerPole());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerPole.class, new TileRenderPowerPole());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerPole2.class, new TileRenderPowerPole());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerTransformerWinding.Primary.class, new TileRenderPowerPole());
		ClientRegistry.bindTileEntitySpecialRenderer(TilePowerTransformerWinding.Secondary.class, new TileRenderPowerPole());
	}
	
	@Override
	public void postInit() {

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		
		if (te instanceof ISEGuiProvider)
			return ((ISEGuiProvider) te).getClientGuiContainer(EnumFacing.getFront(ID));
		
		Container container = BlockRegistry.getContainer(te, player);
		
		if (te instanceof TileVoltageMeter)
			return new GuiVoltageMeter(container);
		if (te instanceof TileQuantumGenerator)
			return new GuiQuantumGenerator(container);
		if (te instanceof TileAdjustableResistor)
			return new GuiAdjustableResistor(container);
		
		if (te instanceof TileAdjustableTransformer)
			return new GuiAdjustableTransformer(container);
		if (te instanceof TileCurrentSensor)
			return new GuiCurrentSensor(container);
		if (te instanceof TileDiode)
			return new GuiDiode(container);
		if (te instanceof TileSwitch)
			return new GuiSwitch(container);
		
		return null;
	}
}
