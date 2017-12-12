package simelectricity.essential.coverpanel;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.container.IContainerWithGui;
import simelectricity.essential.common.ContainerNoInvAutoSync;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

public class ContainerVoltageSensor extends ContainerNoInvAutoSync<VoltageSensorPanel> implements ISEButtonEventHandler, IContainerWithGui {
	@ContainerSynchronizer.SyncField
	public boolean emitRedStoneSignal;
    @ContainerSynchronizer.SyncField
    public boolean inverted;
    @ContainerSynchronizer.SyncField
    public double thresholdVoltage;

    public ContainerVoltageSensor(VoltageSensorPanel panel) {
    	super(panel);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen createGui() {
		return new GuiVoltageSensor(this);
	}

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double thresholdVoltage = this.thresholdVoltage;
        boolean inverted = this.inverted;

        switch (buttonID) {
            case 0:
                thresholdVoltage -= 100;
                break;
            case 1:
                thresholdVoltage -= 10;
                break;
            case 2:
                if (isCtrlPressed)
                    thresholdVoltage -= 0.1;
                else
                    thresholdVoltage -= 1;
                break;
            case 3:
                if (isCtrlPressed)
                    thresholdVoltage += 0.1;
                else
                    thresholdVoltage += 1;
                break;
            case 4:
                thresholdVoltage += 10;
                break;
            case 5:
                thresholdVoltage += 100;
                break;

            case 6:
                inverted = !inverted;
                break;
        }

        if (thresholdVoltage < 0.1)
            thresholdVoltage = 0.1;
        if (thresholdVoltage > 500)
            thresholdVoltage = 500;

        this.host.thresholdVoltage = thresholdVoltage;
        this.host.inverted = inverted;

        this.host.checkRedStoneSignal();
    }
}
