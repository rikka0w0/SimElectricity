package simelectricity.essential;

import simelectricity.essential.client.CustomModelLoader;
import simelectricity.essential.common.ISEGuiProvider;
import simelectricity.essential.machines.gui.GuiAdjustableResistor;
import simelectricity.essential.machines.gui.GuiAdjustableTransformer;
import simelectricity.essential.machines.gui.GuiCurrentSensor;
import simelectricity.essential.machines.gui.GuiDiode;
import simelectricity.essential.machines.gui.GuiQuantumGenerator;
import simelectricity.essential.machines.gui.GuiSwitch;
import simelectricity.essential.machines.gui.GuiVoltageMeter;
import simelectricity.essential.machines.render.SEMachineStateMapper;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.machines.tile.TileVoltageMeter;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

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
	public Object getClientThread() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public void registerModel(){
		CustomModelLoader loader = new CustomModelLoader(Essential.modID);		
		loader.registerIconsFor(ItemRegistry.itemHVCable);
		loader.registerIconsFor(ItemRegistry.itemVitaTea);
		loader.registerIconsFor(ItemRegistry.itemMisc);
		loader.registerIconsFor(ItemRegistry.itemTools);
		
		ModelLoader.setCustomStateMapper(BlockRegistry.blockElectronics, new SEMachineStateMapper(Essential.modID));
	}
	
	@Override
	public void registerRenders() {

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
