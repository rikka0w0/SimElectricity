package simElectricity.Common.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.TileEntity.TileTower;

import java.util.HashMap;
import java.util.Map;

public class ItemHVWire extends ItemSE {
    public static Map<EntityPlayer, int[]> lastCoordinates = new HashMap<EntityPlayer, int[]>();
    ;

    public ItemHVWire() {
        super();
        setUnlocalizedName("HVWire");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!lastCoordinates.containsKey(player))
                lastCoordinates.put(player, new int[] { 0, -1, 0 });

            int[] lastCoordinate = lastCoordinates.get(player);

            if (lastCoordinate[1] == -1) {
                lastCoordinate[0] = x;
                lastCoordinate[1] = y;
                lastCoordinate[2] = z;

                Util.chat(player, StatCollector.translateToLocal("sime.TwSelect"));
            } else {
                if (!(lastCoordinate[0] == x && lastCoordinate[1] == y && lastCoordinate[2] == z) &&
                        world.getTileEntity(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]) instanceof TileTower) {
                    TileTower tower1 = (TileTower) world.getTileEntity(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
                    TileTower tower2 = (TileTower) world.getTileEntity(x, y, z);

                    if (tower1.hasVacant() && tower2.hasVacant()) {
                        tower1.addNeighbor(tower2);
                        tower2.addNeighbor(tower1);

                        Util.updateTileEntityField(tower1, "neighborsInfo");
                        Util.updateTileEntityField(tower2, "neighborsInfo");

                        Util.chat(player, StatCollector.translateToLocal("sime.TwConnect"));
                    } else
                        Util.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));
                } else
                    Util.chat(player, StatCollector.translateToLocal("sime.ActionCancel"));

                lastCoordinate[0] = 0;
                lastCoordinate[1] = -1;
                lastCoordinate[2] = 0;
            }
        }

        return true;
    }
}
