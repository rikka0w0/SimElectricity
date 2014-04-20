package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.*;

public class TileWire extends TileEntity implements IConductor,ISyncPacketHandler{
	protected boolean isAddedToEnergyNet = false;
	public boolean[] renderSides = new boolean[6];
	
    public float resistance=100;
    @SideOnly(Side.CLIENT)
	public float width=0.1F;
    @SideOnly(Side.CLIENT)
    public String textureString;
	
    public TileWire(int meta){
    	super();
    	resistance=BlockWire.resistanceList[meta];
    	width=BlockWire.renderingWidthList[meta];
    	textureString=BlockWire.subNames[meta];
    }
    
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {}

	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		updateSides();
	}
    
	public void updateSides() {
		ForgeDirection[] dirs = ForgeDirection.values();
		for (int i = 0; i < 6; i++) {
			TileEntity ent = worldObj.getTileEntity(xCoord
				+ dirs[i].offsetX, yCoord + dirs[i].offsetY, zCoord
				+ dirs[i].offsetZ);
			if (ent != null && ent instanceof IBaseComponent) {
				if(ent instanceof IConductor){
					renderSides[i] = true;
				}else if (ent instanceof IEnergyTile){
					ForgeDirection functionalSide=((IEnergyTile)ent).getFunctionalSide();
					ForgeDirection curDirection=dirs[i];
					
					if(curDirection==functionalSide.getOpposite())
						renderSides[i] = true;	

				}
			} else
				renderSides[i] = false;
		}
	}

    @Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return bb;
    }

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileAttachEvent(this));
			this.isAddedToEnergyNet=true;
			Util.scheduleBlockUpdate(this,20);
		}	
	}

	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new TileDetachEvent(this));
		}
	}

	
	@Override
	public float getResistance() {
		return resistance;
	}

	@Override
	public void onOverloaded() {
		worldObj.createExplosion(null, xCoord, yCoord, zCoord, 0, true);
	}

	@Override
	public int getMaxPowerDissipation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInsulationBreakdownVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInsulationBreakdown() {
		// TODO Auto-generated method stub

	}
}
