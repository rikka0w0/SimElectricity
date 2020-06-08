package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.gui.AutoGuiHandler;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.utils.network.ISEButtonEventHandler;

@AutoGuiHandler.Marker(GuiAdjustableTransformer.class)
public class ContainerAdjustableTransformer extends ContainerNoInventoryTwoPort<TileAdjustableTransformer> implements ISEButtonEventHandler {
	@ContainerSynchronizer.SyncField
	public double ratio, outputResistance;
	@ContainerSynchronizer.SyncField
    public Direction inputSide, outputSide;
	@ContainerSynchronizer.SyncField
    public double vPri, vSec;

	// Server side
    public ContainerAdjustableTransformer(TileAdjustableTransformer tileEntity, int windowID) {
        super(tileEntity, windowID);
    }
    
    // Client side
    public ContainerAdjustableTransformer(int windowId, PlayerInventory inv, PacketBuffer data) {
    	this(null, windowId);
    }

    @Override
    public void onButtonPressed(int buttonID, boolean isCtrlPressed) {
        double ratio = this.host.ratio, outputResistance = this.host.outputResistance;

        switch (buttonID) {
            case 6:
                if (isCtrlPressed)
                    outputResistance -= 10;
                else
                    outputResistance -= 1;
                break;
            case 7:
                outputResistance -= 0.1;
                break;
            case 8:
                if (isCtrlPressed)
                    outputResistance -= 0.001;
                else
                    outputResistance -= 0.01;
                break;
            case 9:
                if (isCtrlPressed)
                    outputResistance += 0.001;
                else
                    outputResistance += 0.01;
                break;
            case 10:
                outputResistance += 0.1;
                break;
            case 11:
                if (isCtrlPressed)
                    outputResistance += 10;
                else
                    outputResistance += 1;
                break;


            case 0:
                if (isCtrlPressed)
                    ratio -= 10;
                else
                    ratio -= 1;
                break;
            case 1:
                ratio -= 0.1;
                break;
            case 2:
                if (isCtrlPressed)
                    ratio -= 0.001;
                else
                    ratio -= 0.01;
                break;
            case 3:
                if (isCtrlPressed)
                    ratio += 0.001;
                else
                    ratio += 0.01;
                break;
            case 4:
                ratio += 0.1;
                break;
            case 5:
                if (isCtrlPressed)
                    ratio += 10;
                else
                    ratio += 1;
                break;
        }

        if (outputResistance < 0.001)
            outputResistance = 0.001;
        if (outputResistance > 100)
            outputResistance = 100;


        if (ratio < 0.001)
            ratio = 0.001;
        if (ratio > 100)
            ratio = 100;

        this.host.ratio = ratio;
        this.host.outputResistance = outputResistance;

        SEAPI.energyNetAgent.updateTileParameter(this.host);
    }
}
