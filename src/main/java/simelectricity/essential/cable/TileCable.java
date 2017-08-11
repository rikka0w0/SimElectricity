package simelectricity.essential.cable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.ISECrowbarTarget;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.ISEIuminousCoverPanelHost;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEElectricalCoverPanel;
import simelectricity.essential.api.coverpanel.ISEElectricalLoadCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISEIuminousCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;
import simelectricity.essential.common.ISEGuiProvider;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;

public class TileCable extends SEEnergyTile implements ISECrowbarTarget, ISEGenericCable, ISEIuminousCoverPanelHost, ISECableTile, IEnergyNetUpdateHandler, ISEGuiProvider{
	private ISESimulatable node = SEAPI.energyNetAgent.newCable(this, false);
    private int color = 0;
    private double resistance = 10;
    private double voltage;
    
    public boolean emitRedstoneSignal;
	
    /**
     * Accessible from client
     */
    public byte lightLevel;
    private boolean[] connections = new boolean[6];
    private ISECoverPanel[] installedCoverPanels = new ISECoverPanel[6];
    
	////////////////////////////////////////
	//Private functions
	////////////////////////////////////////
	private NBTTagList coverPanelsToNBT(){
		NBTTagList tagList = new NBTTagList();
		for (int i=0; i<installedCoverPanels.length; i++){
			ISECoverPanel coverPanel = installedCoverPanels[i];
			if (coverPanel != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("side", i);
				coverPanel.toNBT(tag);
				tagList.appendTag(tag);
			}
		}
		return tagList;
	}
	
	private void coverPanelsFromNBT(NBTTagList tagList){
		for (int i=0; i<6; i++)
			installedCoverPanels[i] = null;
		
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int side = tag.getInteger("side");
            if (side > -1 && side < installedCoverPanels.length){
            	ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromNBT(tag);
            	installedCoverPanels[side] = coverPanel;
            	
            	coverPanel.setHost(this, EnumFacing.getFront(side));
            }
        }
	}
    
	public void setResistanceOnPlace(double resistance){
		this.resistance = resistance;
	}
	
	////////////////////////////////////////
	//ISEGenericCable
	////////////////////////////////////////
    @Override
    public void onCableRenderingUpdateRequested(){
		//Update connection
        EnumFacing[] dirs = EnumFacing.VALUES;
        for (int i = 0; i < 6; i++) {
        	connections[i] = SEAPI.energyNetAgent.canConnectTo(this, dirs[i]);
        }
		
		
		//Initiate Server->Client synchronization
		markTileEntityForS2CSync();
    }
    
	@Override
	public boolean connectedOnSide(EnumFacing side) {
		return connections[side.ordinal()];
	}
	
	@Override
	public EnumFacing getSelectedSide(EntityPlayer player, EnumFacing side){
		EnumFacing selectedDirection = side;
		Block block = getBlockType();
		if (block instanceof BlockCable){
			//RaytraceResult result = ((BlockCable) block).doRayTrace(world, pos, player);
			//return result.hitCenter ? null : result.sideHit;
			return selectedDirection;	//TODO: need to be fixed!
		}
		
		return selectedDirection;
	}
	
	@Override
	public ISECoverPanel getCoverPanelOnSide(EnumFacing side){
		return installedCoverPanels[side.ordinal()];
	}
	
	@Override
	public boolean canInstallCoverPanelOnSide(EnumFacing side, ISECoverPanel coverPanel) {
		return installedCoverPanels[side.ordinal()] == null;
	}
	
	@Override
	public void installCoverPanel(EnumFacing side, ISECoverPanel coverPanel) {
		installedCoverPanels[side.ordinal()] = coverPanel;
		coverPanel.setHost(this, side);
		
		if (!coverPanel.isHollow()){
			//If the cover panel is not hollow, it may block some connection
			if (connectedOnSide(side))
				SEAPI.energyNetAgent.updateTileConnection(this);
		}
		
		if (coverPanel instanceof ISEElectricalCoverPanel)
			((ISEElectricalCoverPanel) coverPanel).onPlaced(voltage);
		
		if (coverPanel instanceof ISEElectricalLoadCoverPanel)
			SEAPI.energyNetAgent.updateTileConnection(this);
		
		onCableRenderingUpdateRequested();
	}

	///////////////////////////////////////
	///TileEntity
	///////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        return bb;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        color = tagCompound.getInteger("color");
        resistance = tagCompound.getDouble("resistance");
        coverPanelsFromNBT(tagCompound.getTagList("coverPanels", Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("color", color);
        tagCompound.setDouble("resistance", resistance);
        tagCompound.setTag("coverPanels", coverPanelsToNBT());
        
        return super.writeToNBT(tagCompound);
    }
    
	///////////////////////////////////////
	///ISECableTile
	///////////////////////////////////////
	@Override
	public int getColor() {return color;}

	@Override
	public double getResistance() {return resistance;}

	@Override
	public ISESimulatable getNode() {return node;}

	@Override
	public boolean canConnectOnSide(EnumFacing direction) {
		ISECoverPanel coverPanel = getCoverPanelOnSide(direction);
		if (coverPanel == null)
			return true;
		else 
			return coverPanel.isHollow();
	}

	@Override
	public boolean isGridLinkEnabled() {return false;}

	@Override
	public boolean hasShuntResistance() {
		boolean hasShuntResistance = false;
		for (ISECoverPanel coverPanel: this.installedCoverPanels)
			hasShuntResistance |= (coverPanel instanceof ISEElectricalLoadCoverPanel);
		return hasShuntResistance;
	}

	@Override
	public double getShuntResistance() {
		double shuntConductance = 0;
		for (ISECoverPanel coverPanel: this.installedCoverPanels)
			if (coverPanel instanceof ISEElectricalLoadCoverPanel)
				shuntConductance += 1.0D / ((ISEElectricalLoadCoverPanel) coverPanel).getResistance();
		
		return 1.0D / shuntConductance;
	}
	////////////////////////////////////////
	//Server->Client sync
	////////////////////////////////////////	
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt){	
		super.prepareS2CPacketData(nbt);
		
		byte bc = 0x00;
		if (connections[0]) bc |= 1;
		if (connections[1]) bc |= 2;
		if (connections[2]) bc |= 4;
		if (connections[3]) bc |= 8;
		if (connections[4]) bc |= 16;
		if (connections[5]) bc |= 32;
		
		nbt.setByte("connections", bc);
		
		nbt.setTag("coverPanels", coverPanelsToNBT());
		
		nbt.setByte("lightLevel", lightLevel);
	}
	
	@SideOnly(value = Side.CLIENT)
	@Override
	public void onSyncDataFromServerArrived(NBTTagCompound nbt){
		byte connectionsBinary = nbt.getByte("connections");
		
		this.connections[0] = (connectionsBinary & 1) > 0;
		this.connections[1] = (connectionsBinary & 2) > 0;
		this.connections[2] = (connectionsBinary & 4) > 0;
		this.connections[3] = (connectionsBinary & 8) > 0;
		this.connections[4] = (connectionsBinary & 16) > 0;
		this.connections[5] = (connectionsBinary & 32) > 0;
				
		coverPanelsFromNBT(nbt.getTagList("coverPanels", Constants.NBT.TAG_COMPOUND));
		
		byte lightLevel = nbt.getByte("lightLevel");
		if (this.lightLevel != lightLevel){
			this.lightLevel = lightLevel;
			//Detect change & proceed
			world.checkLight(pos);
			//world.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);	//checkLightFor
		}
		
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
	}

	////////////////////////////////////////
	//IEnergyNetUpdateHandler
	////////////////////////////////////////	
	@Override
	public void onEnergyNetUpdate() {
		voltage = SEAPI.energyNetAgent.getVoltage(this.node);
		
		for (ISECoverPanel coverPanel: this.installedCoverPanels){
			if (coverPanel instanceof ISEElectricalCoverPanel)
				((ISEElectricalCoverPanel) coverPanel).onEnergyNetUpdate(voltage);
		}
	}

	////////////////////////////////////////
	//ISECrowbarTarget
	////////////////////////////////////////	
	@Override
	public boolean canCrowbarBeUsed(EnumFacing side) {
		if (side == null)
			return false;
		
		ISECoverPanel coverPanel = installedCoverPanels[side.ordinal()];
		return coverPanel != null;
	}
	
	@Override
	public void onCrowbarAction(EnumFacing side, boolean isCreativePlayer) {
		if (side == null)
			return;
		
		ISECoverPanel coverPanel = installedCoverPanels[side.ordinal()];
		if (coverPanel == null)
			return;
		
		//Remove the panel
		installedCoverPanels[side.ordinal()] = null;
		
		if (coverPanel instanceof ISEElectricalLoadCoverPanel)
			SEAPI.energyNetAgent.updateTileConnection(this);
		
		onLightValueUpdated();
		
		//Notify neighbor block that this side no longer emits redstone signal
		if (coverPanel instanceof ISERedstoneEmitterCoverPanel)
			world.notifyNeighborsOfStateChange(pos, blockType, false);
		
		
		onCableRenderingUpdateRequested();
		
		//Spawn an item entity for player to pick up
		if (!isCreativePlayer)
			Utils.dropItemIntoWorld(world, pos, 
					SEEAPI.coverPanelRegistry.toItemStack(coverPanel));
	}
	
	///////////////////////
	///ISEGuiProvider
	///////////////////////
	@Override
	public Container getServerContainer(EnumFacing side) {
		ISECoverPanel coverPanel = installedCoverPanels[side.ordinal()];
		return coverPanel instanceof ISEGuiCoverPanel ? ((ISEGuiCoverPanel)coverPanel).getServerContainer(this) : null;
	}

	@Override
	public GuiContainer getClientGuiContainer(EnumFacing side) {
		ISECoverPanel coverPanel = installedCoverPanels[side.ordinal()];
		return coverPanel instanceof ISEGuiCoverPanel ? ((ISEGuiCoverPanel)coverPanel).getClientGuiContainer(this) : null;
	}

	/////////////////////////////////
	///ISEIuminousCoverPanelHost
	/////////////////////////////////
	@Override
	public void onLightValueUpdated() {
		byte lightLevel = 0;
		for (ISECoverPanel coverPanel: this.installedCoverPanels){
			if (coverPanel instanceof ISEIuminousCoverPanel){
				byte ll = ((ISEIuminousCoverPanel) coverPanel).getLightValue();
				if (ll > lightLevel)
					lightLevel = ll;
			}
		}
		
		if (this.lightLevel != lightLevel){
			this.lightLevel = lightLevel;
			this.markTileEntityForS2CSync();
		}
	}
}
