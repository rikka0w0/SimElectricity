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

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import simelectricity.Templates.Container.*;
import simelectricity.Templates.TileEntity.*;

public class CommonProxy implements IGuiHandler {
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public Object getCurrentGui(){
    	return null;
    }
    
    public World getClientWorld() {
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileQuantumGenerator)
            return new ContainerQuantumGenerator(player.inventory, te);
        if (te instanceof TileVoltageMeter)
            return new ContainerVoltageMeter(player.inventory, te);
        if (te instanceof TileAdjustableResistor)
            return new ContainerAdjustableResistor(player.inventory, te);
        if (te instanceof TileAdjustableTransformer)
            return new ContainerAdjustableTransformer(player.inventory, te);
        if (te instanceof TileSwitch)
            return new ContainerSwitch(player.inventory, te);
        if (te instanceof TileSolarInverter)
        	return new ContainerSolarInverter(player.inventory, te);
        if (te instanceof TileIceMachine)
        	return new ContainerIceMachine(player.inventory, te);   
        
        return null;
    }
}
