package simelectricity.essential.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import rikka.librikka.tileentity.TileEntityBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;

public abstract class SEEnergyTile extends TileEntityBase {
	public SEEnergyTile(BlockPos pos, BlockState blockState) {
		super(Essential.MODID, pos, blockState);
	}

	protected boolean isAddedToEnergyNet;

    /**
     * Called just before joining the energyNet, do some initialization here </p>
     * Should not use clearRemoved() in client anyway!
     */
    @Override
    public void clearRemoved() {
    	super.clearRemoved();
        if (!this.level.isClientSide && !this.isAddedToEnergyNet) {
            SEAPI.energyNetAgent.attachTile(this);
            isAddedToEnergyNet = true;
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!this.level.isClientSide && this.isAddedToEnergyNet) {
            SEAPI.energyNetAgent.detachTile(this);
            isAddedToEnergyNet = false;
        }
    }

    /**
     * A helper function
     */
    protected final void updateTileParameter() {
        SEAPI.energyNetAgent.updateTileParameter(this);
    }

    protected final void updateTileConnection() {SEAPI.energyNetAgent.updateTileConnection(this); }
}
