package simElectricity.Network;

import simElectricity.API.IEnergyTile;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**This packet performs server<->client side synchronization for tileEntity fields~*/
public class PacketTileEntitySideUpdate extends AbstractPacket{
	int x,z,hash;
	short y;
	byte value,type;
	
	public PacketTileEntitySideUpdate(){}
	
	/** type: 0-facing 1-functionalSide */
	public PacketTileEntitySideUpdate(TileEntity te,byte _type){
		if (te==null)
			return;
		
		if (te.getWorldObj()==null)
			return;
			
		x=te.xCoord;
		y=(short) te.yCoord;
		z=te.zCoord;
		hash=te.getClass().hashCode();
		
		type=_type;
		
		if(type==0){
			value=Util.direction2Byte(((ISidedFacing) te).getFacing());
		}else if(type==1){
			value=Util.direction2Byte(((IEnergyTile) te).getFunctionalSide());
		}else{
			System.out.println("TileEntity "+te.toString()+" is trying to update its facing/functional side, but it's not an instance of ISidedFacing nor IEnergyTile, this must be a bug!");
		}
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeShort(y);
		buffer.writeInt(z);
		buffer.writeInt(hash);
		buffer.writeByte(value);
		buffer.writeByte(type);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readShort();
        z = buffer.readInt();
        hash=buffer.readInt();
        value=buffer.readByte();
        type=buffer.readByte();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		World world = player.worldObj;
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te == null) 
			return;
		if (te.getClass().hashCode()!=hash)
			return;
		if (!world.isRemote)  //Client processing only!
			return;
		
	
		if(type==0){
			if (!(te instanceof ISidedFacing))
				return;
		
			((ISidedFacing) te).setFacing(Util.byte2Direction(value));
			world.markBlockForUpdate(x, y, z);
		}else if(type==1){
			if (!(te instanceof IEnergyTile))
				return;			
			
			((IEnergyTile) te).setFunctionalSide(Util.byte2Direction(value));
		}
	}
	
	@Override
	public void handleServerSide(EntityPlayer player) {}
}
