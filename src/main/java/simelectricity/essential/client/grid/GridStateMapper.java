package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.GhostModel;
import rikka.librikka.model.loader.IModelLoader;
import rikka.librikka.properties.Properties;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.client.grid.pole.*;
import simelectricity.essential.grid.BlockPowerPole2;
import simelectricity.essential.grid.BlockPowerPole3;
import simelectricity.essential.grid.EnumBlockTypePole3;

@SideOnly(Side.CLIENT)
public class GridStateMapper extends StateMapperBase implements IModelLoader {
    public static final String VPATH = "virtual/blockstates/grid";
    public final String domain;

    public GridStateMapper(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean accepts(String resPath) {
        return resPath.startsWith(GridStateMapper.VPATH);
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Block block = state.getBlock();
        String blockDomain = block.getRegistryName().getResourceDomain();
        String blockName = block.getRegistryName().getResourcePath();

        String varStr = "";

        if (block == BlockRegistry.cableJoint) {
            varStr = "" + state.getValue(Properties.facing3bit);
        } else if (block == BlockRegistry.powerPoleBottom) {

        } else if (block == BlockRegistry.powerPoleTop) {

        } else if (block == BlockRegistry.powerPoleCollisionBox) {

        } else if (block == BlockRegistry.powerPole2) {
            int type = state.getValue(Properties.type1bit);
            boolean isRod = state.getValue(BlockPowerPole2.propertyIsPole);
            int facing = state.getValue(Properties.facing2bit);
            varStr = facing + "," + type + "," + isRod;
        } else if (block == BlockRegistry.powerPole3) {
            EnumBlockTypePole3 blockType = state.getValue(EnumBlockTypePole3.property);
            int type = blockType.ordinal();
            int facing = state.getValue(Properties.facing3bit);

            if (blockType.ignoreFacing)
                facing = 0;

            varStr = facing + "," + type;
        }

        ModelResourceLocation res = new ModelResourceLocation(domain + ":" + GridStateMapper.VPATH,
                blockDomain + "," + blockName + "," + varStr);
        return res;
    }

    @Override
    public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
        String[] splited = variantStr.split(",");
        String blockDomain = splited[0];
        String blockName = splited[1];
        Block block = Block.getBlockFromName(blockDomain + ":" + blockName);

        if (block == BlockRegistry.cableJoint) {
            int facing = Integer.parseInt(splited[2]);
            return new CableJointModel(facing);
        } else if (block == BlockRegistry.powerPoleBottom) {
        	return new PowerPoleBottomModel();
        } else if (block == BlockRegistry.powerPoleTop) {
            return new PowerPoleTopModel();
        } else if (block == BlockRegistry.powerPoleCollisionBox) {
            return new GhostModel();
        } else if (block == BlockRegistry.powerPole2) {
            int facing = Integer.parseInt(splited[2]);
            int type = Integer.parseInt(splited[3]);
            boolean isRod = Boolean.parseBoolean(splited[4]);
            return new PowerPole2Model(facing, type, isRod);
        } else if (block == BlockRegistry.powerPole3) {
            int facing = Integer.parseInt(splited[2]);
            EnumBlockTypePole3 blockType = EnumBlockTypePole3.fromInt(Integer.parseInt(splited[3]));
            return new PowerPole3Model(blockType, facing);
        }

        return null;
    }

    public void register(Block block) {
        ModelLoader.setCustomStateMapper(block, this);
    }

    public void register(BlockPowerPole3 block) {
        ModelLoader.setCustomStateMapper(block, this);

        ItemBlock itemBlock = block.itemBlock;
        for (EnumBlockTypePole3 blockType : EnumBlockTypePole3.values) {
            ModelResourceLocation res = getModelResourceLocation(
                    block.getDefaultState().withProperty(EnumBlockTypePole3.property, blockType));
            //Also register inventory variants here
            ModelLoader.setCustomModelResourceLocation(itemBlock, blockType.ordinal(), res);
        }
    }
}
