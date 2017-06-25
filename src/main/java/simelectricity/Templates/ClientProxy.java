/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simelectricity.Templates;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import simelectricity.Templates.Client.Gui.*;
import simelectricity.Templates.TileEntity.*;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
    public Object getCurrentGui(){
    	return FMLClientHandler.instance().getClient().currentScreen;
    }
	
    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
    
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileSwitch)
            return new GuiSwitch(player.inventory, te);
        if (te instanceof TileSolarInverter)
            return new GuiSolarInverter(player.inventory, te);
        if (te instanceof TileIceMachine)
            return new GuiIceMachine(player.inventory, te); 
        

        return null;
    }
}