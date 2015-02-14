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

package simElectricity.Common.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.*;
import simElectricity.API.Util;

public class ItemUltimateMultimeter extends ItemSE {
    public ItemUltimateMultimeter() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("UltimateMultimeter");
        setMaxDamage(256);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(blockPos);

        if (tile instanceof ITransformer && (!(world.isRemote))) {
            ITransformer transformer = (ITransformer) tile;
            ITransformer.ITransformerWinding primary = transformer.getPrimary();
            ITransformer.ITransformerWinding secondary = transformer.getSecondary();

            Util.chat(player, "-----------------------");
            Util.chat(player, "Transformer");
            Util.chat(player, "Primary Voltage: " + String.valueOf(Energy.getVoltage(primary)) + "V");
            Util.chat(player, "Secondary Voltage: " + String.valueOf(Energy.getVoltage(secondary)) + "V");
            Util.chat(player, "PS ratio: 1:" + String.valueOf(transformer.getRatio()));

            double secondaryResistance = transformer.getResistance();
            double secondaryCurrent = (transformer.getRatio() * Energy.getVoltage(primary) - Energy.getVoltage(secondary)) / secondaryResistance;
            Util.chat(player, "Internal Resistance: " + secondaryResistance + "\u03a9");
            Util.chat(player, "Secondary current: " + String.valueOf(secondaryCurrent) + "A");
            Util.chat(player, "Power loss: " + String.valueOf(secondaryCurrent * secondaryCurrent * secondaryResistance) + "A");
        }

        if ((tile instanceof IBaseComponent || tile instanceof IComplexTile) && (!(world.isRemote))) {
            IBaseComponent te = null;
            if (tile instanceof IBaseComponent) {
                te = (IBaseComponent) tile;
            } else if (tile instanceof IComplexTile) {
                te = ((IComplexTile) tile).getCircuitComponent(Util.getPlayerSight(player, false).getOpposite());
            }

            if (te == null)
                return false;

            String tileType = "Unknown";
            double outputVoltage = 0;


            Util.chat(player, "-----------------------");
            if (te instanceof ICircuitComponent) {
                if (((ICircuitComponent) te).getOutputVoltage() == 0)
                    tileType = "Energy Sink";
                else {
                    tileType = "Energy Source";
                    outputVoltage = ((ICircuitComponent) te).getOutputVoltage();
                }

                if (!(te instanceof IEnergyTile))
                    tileType += "(SubComponent)";
            }

            if (te instanceof IConductor) {
                tileType = "Energy Conductor";
            }

            if (te instanceof IManualJunction) {
                tileType = "Manual Junction";
            }

            //Print out information here
            Util.chat(player, "Type: " + tileType);
            if (te instanceof IEnergyTile)
                Util.chat(player, "FunctionalSide: " + ((IEnergyTile) te).getFunctionalSide().toString());

            if (te instanceof ICircuitComponent && outputVoltage > 0)
                Util.chat(player, "Internal resistance: " + String.valueOf(te.getResistance()) + "\u03a9");
            else if (te instanceof IConductor ||
                    (te instanceof IManualJunction && ((IManualJunction) te).getResistance() != 0))
                Util.chat(player, "Resistance (Per Block) : " + String.valueOf(te.getResistance() * 2) + "\u03a9");
            else if (te instanceof ICircuitComponent)
                Util.chat(player, "Resistance : " + String.valueOf(te.getResistance()) + "\u03a9");

            if (te instanceof ICircuitComponent) {
                Util.chat(player, "Current: " + String.valueOf(Energy.getCurrent((ICircuitComponent) te, tile.getWorld())) + "A");
                Util.chat(player, "Power rate: " + String.valueOf(Energy.getPower((ICircuitComponent) te, tile.getWorld())) + "W");
            }
            Util.chat(player, "Voltage: " + String.valueOf(Energy.getVoltage(te, tile.getWorld())) + "V");
            if (outputVoltage > 0) { //Energy Source
                Util.chat(player, "Internal voltage: " + String.valueOf(outputVoltage) + "V");
                Util.chat(player, "Output rate: " + String.valueOf(outputVoltage * Energy.getCurrent((ICircuitComponent) te, tile.getWorld())) + "W");
            }
            if (te instanceof IConductor) {
                Util.chat(player, "Color: " + String.valueOf(((IConductor) te).getColor()));
            }

            return true;
        } else {
            return false;
        }
    }
}
