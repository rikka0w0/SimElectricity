package simelectricity.energybridge.ic2.cable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.Resistivity;
import simelectricity.essential.cable.TileCable;
import simelectricity.essential.common.SEItemBlock;

public class BlockIc2Cable extends BlockCable {

	public BlockIc2Cable() {
		super("essential_ic2_cable", Material.glass, ItemBlock.class, 
				new String[]{"copper", "gold", "iron", "glass", "tin"},
				new double[]{0.22, 0.22, 0.22, 0.22, 0.22},
				new double[]{Resistivity.copper, Resistivity.gold, Resistivity.iron, Resistivity.glass, Resistivity.tin},
				TileCable.class);
		setHardness(0.2F);	
	}
	
}
