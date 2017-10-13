package simelectricity.essential.client.grid.transformer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import rikka.librikka.model.GhostModel;
import rikka.librikka.model.loader.IModelLoader;
import rikka.librikka.properties.Properties;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerRenderPart;
import simelectricity.essential.grid.transformer.EnumPowerTransformerBlockType;

public class TransformerStateMapper extends StateMapperBase implements IModelLoader {
    public static final String VPATH = "virtual/blockstates/transformer";
    public final String domain;

    public TransformerStateMapper(String domain) {
        this.domain = domain;

        OBJLoader.INSTANCE.addDomain(domain);
    }

    @Override
    public boolean accepts(String resPath) {
        return resPath.startsWith(TransformerStateMapper.VPATH);
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Block block = state.getBlock();
        String blockDomain = block.getRegistryName().getResourceDomain();
        String blockName = block.getRegistryName().getResourcePath();

        String varStr = "";

        if (block == BlockRegistry.powerTransformer) {
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
        	if (blockType.formed) {
        		EnumDistributionTransformerRenderPart renderPart = state.getValue(EnumDistributionTransformerRenderPart.property);
                int facing = state.getValue(BlockHorizontal.FACING).ordinal() - 2;
                boolean mirrored = state.getValue(Properties.propertyMirrored);
                varStr = blockType.ordinal() + "," + renderPart.ordinal() + "," + facing + "," + mirrored;
        	} else {
        		varStr = blockType.ordinal() + "";
        	}
        }

        ModelResourceLocation res = new ModelResourceLocation(domain + ":" + TransformerStateMapper.VPATH,
                blockDomain + "," + blockName + "," + varStr);
        return res;
    }

    @Override
    public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
        String[] splited = variantStr.split(",");
        String blockDomain = splited[0];
        String blockName = splited[1];
        Block block = Block.getBlockFromName(blockDomain + ":" + blockName);

        if (block == BlockRegistry.powerTransformer) {
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
        		return new GhostModel();
        	}
        }

        return null;
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
