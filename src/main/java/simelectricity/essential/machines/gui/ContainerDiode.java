package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileDiode;

public class ContainerDiode extends ContainerNoInventoryTwoPort<TileDiode> implements IContainerWithGui {
	@ContainerSynchronizer.SyncField
    public double inputVoltage, outputVoltage;
    @ContainerSynchronizer.SyncField
    public EnumFacing inputSide, outputSide;

    public ContainerDiode(TileEntity tileEntity) {
        super(tileEntity);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiDiode(this);
    }
}
