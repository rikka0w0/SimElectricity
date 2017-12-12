package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerQuantumGenerator extends ContainerNoInvAutoSync<TileQuantumGenerator> implements ISEButtonEventHandler, IContainerWithGui {
	@ContainerSynchronizer.SyncField
	public double internalVoltage;
    @ContainerSynchronizer.SyncField
    public double resistance;
    @ContainerSynchronizer.SyncField
    public double voltage;
    @ContainerSynchronizer.SyncField
    public double current;

    public ContainerQuantumGenerator(TileEntity tileEntity) {
        super(tileEntity);
    }
    
    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double resistance = host.resistance;
        double internalVoltage = host.internalVoltage;

        switch (buttonID) {
            case 0:
                resistance -= 1;
                break;
            case 1:
                resistance -= 0.1;
                break;
            case 2:
                if (isCtrlPressed)
                    resistance -= 0.001;
                else
                    resistance -= 0.01;
                break;
            case 3:
                if (isCtrlPressed)
                    resistance += 0.001;
                else
                    resistance += 0.01;
                break;
            case 4:
                resistance += 0.1;
                break;
            case 5:
                resistance += 1;
                break;


            case 6:
                if (isCtrlPressed)
                    internalVoltage -= 1000;
                else
                    internalVoltage -= 100;
                break;
            case 7:
                internalVoltage -= 10;
                break;
            case 8:
                internalVoltage -= 1;
                break;
            case 9:
                internalVoltage += 1;
                break;
            case 10:
                internalVoltage += 10;
                break;
            case 11:
                if (isCtrlPressed)
                    internalVoltage += 1000;
                else
                    internalVoltage += 100;
                break;
        }

        if (resistance < 0.001)
            resistance = 0.001;
        if (resistance > 100)
            resistance = 100;

        if (internalVoltage < 0.1)
            internalVoltage = 0.1;
        if (internalVoltage > 10000)
            internalVoltage = 10000;

        host.resistance = resistance;
        host.internalVoltage = internalVoltage;

        SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
    	return new GuiQuantumGenerator(this);
    }
}
