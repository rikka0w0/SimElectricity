package simelectricity.essential.common;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.utils.network.ISEDirectionSelectorEventHandler;

public abstract class ContainerNoInventoryTwoPort<HOST extends SETwoPortMachine<?>> extends ContainerNoInvAutoSync<HOST> implements ISEDirectionSelectorEventHandler {
	public ContainerNoInventoryTwoPort(@Nullable HOST host, int windowID, Player player) {
		this(host, Essential.MODID, windowID, player);
	}

    public ContainerNoInventoryTwoPort(@Nullable HOST host, String namespace, int windowID, Player player) {
		super(host, namespace, windowID, player);
	}

    @Override
    public void onDirectionSelected(Direction direction, int mouseButton) {
        Direction inputSide = this.host.inputSide, outputSide = this.host.outputSide;

        if (mouseButton == 0) {        //Left key
            if (outputSide == direction)
                outputSide = inputSide;
            inputSide = direction;
        } else if (mouseButton == 1) { //Right key
            if (inputSide == direction)
                inputSide = outputSide;
            outputSide = direction;
        }

        SEAPI.energyNetAgent.updateTileConnection(this.host);
        this.host.setFunctionalSide(inputSide, outputSide);
    }
}
