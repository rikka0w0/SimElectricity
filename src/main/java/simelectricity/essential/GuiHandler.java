package simelectricity.essential;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import rikka.librikka.gui.ISEContainer;
import rikka.librikka.tileentity.ISEGuiProvider;
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

public class GuiHandler implements IGuiHandler{
    @Override
    public final Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                            int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (te instanceof ISEGuiProvider)
            return ((ISEGuiProvider) te).getContainer(player, EnumFacing.getFront(ID));

        return BlockRegistry.getContainer(te, player);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
    	Object guiContainer = getServerGuiElement(ID, player, world, x, y, z);
    	if (guiContainer instanceof ISEContainer)
    		return ((ISEContainer) guiContainer).createGui();
    	
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
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
