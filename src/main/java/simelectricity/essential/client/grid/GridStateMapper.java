package simelectricity.essential.client.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.GhostModel;
import rikka.librikka.model.loader.IModelLoader;
import rikka.librikka.properties.Properties;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.client.grid.pole.*;
import simelectricity.essential.client.grid.transformer.DistributionTransformerComponentModel;
import simelectricity.essential.client.grid.transformer.DistributionTransformerFormedModel;
import simelectricity.essential.client.grid.transformer.PowerTransformerModel;
import simelectricity.essential.grid.BlockPowerPole2;
import simelectricity.essential.grid.BlockPowerPole3;
import simelectricity.essential.grid.EnumBlockTypePole3;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerRenderPart;
import simelectricity.essential.grid.transformer.EnumPowerTransformerBlockType;

@SideOnly(Side.CLIENT)
public class GridStateMapper extends StateMapperBase implements IModelLoader, IResourceManagerReloadListener {
    public static final String VPATH = "virtual/blockstates/grid";
    public final String domain;

    public GridStateMapper(String domain) {
        this.domain = domain;
        
        OBJLoader.INSTANCE.addDomain(domain);
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
            varStr = state.getValue(Properties.facing3bit) + "," + state.getValue(Properties.type1bit);
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
        
        
        //Transformers
        else if (block == BlockRegistry.powerTransformer) {
            EnumPowerTransformerBlockType blockType = state.getValue(EnumPowerTransformerBlockType.property);

            if (blockType == EnumPowerTransformerBlockType.Render) {
                int facing = state.getValue(BlockHorizontal.FACING).ordinal() - 2;
                boolean mirrored = state.getValue(Properties.propertyMirrored);
                varStr = blockType.ordinal() + "," + facing + "," + mirrored;
            } else {
                varStr = "" + blockType.ordinal();
            }
        } else if (block == BlockRegistry.distributionTransformer) {
        	EnumDistributionTransformerBlockType blockType = state.getValue(EnumDistributionTransformerBlockType.property);
            boolean mirrored = state.getValue(Properties.propertyMirrored);
        	if (blockType.formed) {
        		EnumDistributionTransformerRenderPart renderPart = state.getValue(EnumDistributionTransformerRenderPart.property);
                int facing = state.getValue(BlockHorizontal.FACING).ordinal() - 2;
                varStr = blockType.ordinal() + "," + renderPart.ordinal() + "," + facing + "," + mirrored;
        	} else {
        		varStr = blockType.ordinal() + "," + mirrored;
        	}
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
            int type = Integer.parseInt(splited[3]);
            return type==0? new CableJointModel.Type10kV(facing): new CableJointModel.Type415V(facing);
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

        //Transformers
        else if (block == BlockRegistry.powerTransformer) {
            EnumPowerTransformerBlockType blockType = EnumPowerTransformerBlockType.fromInt(Integer.parseInt(splited[2]));

            if (blockType == EnumPowerTransformerBlockType.Render) {
                int facing = Integer.parseInt(splited[3]);
                boolean mirrored = Boolean.parseBoolean(splited[4]);
                return new PowerTransformerModel(facing, mirrored);
            } else if (blockType.formed){
            	return new GhostModel();
            }else {
            	return ModelLoaderRegistry.getModel(new ResourceLocation(domain + ":block/powertransformer_" + blockType.getName()));
            }
        }  else if (block == BlockRegistry.distributionTransformer) {
        	EnumDistributionTransformerBlockType blockType = EnumDistributionTransformerBlockType.fromInt(Integer.parseInt(splited[2]));
        	if (blockType.formed) {
        		EnumDistributionTransformerRenderPart renderPart = EnumDistributionTransformerRenderPart.fromInt(Integer.parseInt(splited[3]));
            	int facing = Integer.parseInt(splited[4]);
                boolean mirrored = Boolean.parseBoolean(splited[5]);
                return new DistributionTransformerFormedModel(renderPart, facing, mirrored);
        	} else {
                boolean mirrored = Boolean.parseBoolean(splited[3]);
        		return new DistributionTransformerComponentModel(blockType, mirrored);
        	}
        }
        
        return null;
    }
    
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		WorldClient theWorld = Minecraft.getMinecraft().world;
		
		if (theWorld == null)
			return;
		
		GridRenderMonitor.instance.markLoadedPowerPoleForRenderingUpdate();
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
    
    public void register(BlockPowerTransformer block) {
        ModelLoader.setCustomStateMapper(block, this);

        ItemBlock itemBlock = ((BlockPowerTransformer) block).itemBlock;
        for (EnumPowerTransformerBlockType blockType : EnumPowerTransformerBlockType.values) {
            IBlockState blockState = ((BlockPowerTransformer) block).stateFromType(blockType);
            int meta = block.getMetaFromState(blockState);
            ModelResourceLocation res = getModelResourceLocation(blockState);
            //Also register inventory variants here
            ModelLoader.setCustomModelResourceLocation(itemBlock, meta, res);
        }
    }
    
    public void register(BlockDistributionTransformer block) {
        ModelLoader.setCustomStateMapper(block, this);

        ItemBlock itemBlock = ((BlockDistributionTransformer) block).itemBlock;
        for (EnumDistributionTransformerBlockType blockType : EnumDistributionTransformerBlockType.values) {
            IBlockState blockState = ((BlockDistributionTransformer) block).stateFromType(blockType);
            int meta = block.getMetaFromState(blockState);
            ModelResourceLocation res = getModelResourceLocation(blockState);
            //Also register inventory variants here
            ModelLoader.setCustomModelResourceLocation(itemBlock, meta, res);
        }
    }
}
