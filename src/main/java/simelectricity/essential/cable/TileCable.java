package simelectricity.essential.cable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.common.SEEnergyTile;

public class TileCable extends SEEnergyTile implements ISEGenericCable, ISECableTile, IEnergyNetUpdateHandler{
	private ISESimulatable node = SEAPI.energyNetAgent.newCable(this, false);
    private int color = 0;
    private double resistance = 10;
	public byte lightLevel;
    
    /**
     * Accessible from client
     */
    private boolean[] connections = new boolean[6];
    private ISECoverPanel[] installedCoverPanels = new ISECoverPanel[6];
    private boolean hasLedPanel;
    
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
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int side = tag.getInteger("side");
            if (side > -1 && side < installedCoverPanels.length){
            	ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromNBT(tag);
            	installedCoverPanels[side] = coverPanel;
            	
        		if (coverPanel instanceof LedPanel)
        			hasLedPanel = true;
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
        ForgeDirection[] dirs = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i < 6; i++) {
        	connections[i] = SEAPI.energyNetAgent.canConnectTo(this, dirs[i]);
        }
		
		
		//Initiate Server->Client synchronization
		markTileEntityForS2CSync();
    }
    
	@Override
	public boolean connectedOnSide(ForgeDirection side) {
		return connections[side.ordinal()];
	}
	
	@Override
	public ISECoverPanel getCoverPanelOnSide(ForgeDirection side){
		return installedCoverPanels[side.ordinal()];
	}
	
	@Override
	public void installCoverPanel(ForgeDirection side, ISECoverPanel coverPanel) {
		installedCoverPanels[side.ordinal()] = coverPanel;
		
		if (!coverPanel.isHollow()){
			//If the cover panel is not hollow, it may block some connection
			if (connectedOnSide(side))
				SEAPI.energyNetAgent.updateTileConnection(this);
		}
		
		if (coverPanel instanceof LedPanel){
			hasLedPanel = true;
			
			SEAPI.energyNetAgent.updateTileConnection(this);
		}
		
		onCableRenderingUpdateRequested();
	}

	///////////////////////////////////////
	///TileEntity
	///////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("color", color);
        tagCompound.setDouble("resistance", resistance);
        tagCompound.setTag("coverPanels", coverPanelsToNBT());
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
	public boolean canConnectOnSide(ForgeDirection direction) {
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
		return hasLedPanel;
	}

	@Override
	public double getShuntResistance() {
		return hasLedPanel ? LedPanel.getResistance() : 0;
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
		byte bc = nbt.getByte("connections");
		
		connections[0] = (bc & 1) > 0;
		connections[1] = (bc & 2) > 0;
		connections[2] = (bc & 4) > 0;
		connections[3] = (bc & 8) > 0;
		connections[4] = (bc & 16) > 0;
		connections[5] = (bc & 32) > 0;
				
		coverPanelsFromNBT(nbt.getTagList("coverPanels", Constants.NBT.TAG_COMPOUND));
		
		byte lightLevel = nbt.getByte("lightLevel");
		if (this.lightLevel != lightLevel){
			this.lightLevel = lightLevel;
			//Detect change & proceed
			worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);	//checkLightFor
		}
		
		// Flag 1 - update Rendering Only!
		markForRenderUpdate();
	}

	////////////////////////////////////////
	//IEnergyNetUpdateHandler
	////////////////////////////////////////	
	@Override
	public void onEnergyNetUpdate() {
		if (!hasLedPanel)
			return;
		
		double voltage = SEAPI.energyNetAgent.getVoltage(this.node);
		double power = (voltage*voltage/LedPanel.getResistance());
		byte lightLevel = LedPanel.getLightValue(power);
		
		if (this.lightLevel != lightLevel){
			//If light value changes, send a sync. packet to client
			this.lightLevel = lightLevel;
			
			this.markTileEntityForS2CSync();
		}
	}
}
