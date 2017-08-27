package simelectricity.essential.client.semachine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.ISEModelLoader;
import simelectricity.essential.common.semachine.ExtendedProperties;
import simelectricity.essential.common.semachine.SEMachineBlock;

@SideOnly(Side.CLIENT)
public class SEMachineStateMapper extends StateMapperBase implements ISEModelLoader {
    public static final String VPATH = "virtual/blockstates/semachine";
    public final String domain;

    public SEMachineStateMapper(String domain) {
        this.domain = domain;
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Block block = state.getBlock();

        if (block instanceof ISESidedTextureBlock) {
            ISESidedTextureBlock stBlock = (ISESidedTextureBlock) block;

            String modelName = stBlock.getModelNameFrom(state);
            EnumFacing facing = state.getValue(ExtendedProperties.propertyFacing);
            boolean is2State = state.getValue(ExtendedProperties.propertyIs2state);

            if (!stBlock.hasSecondState(state))
                is2State = false;

            String varStr = modelName + "," + facing.ordinal() + "," + is2State;

            ModelResourceLocation res = new ModelResourceLocation(domain + ":" + SEMachineStateMapper.VPATH, varStr);
            return res;
        }

        return null;
    }

    @Override
    public boolean accepts(String resPath) {
        return resPath.startsWith(SEMachineStateMapper.VPATH);
    }

    @Override
    public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
        String[] splited = variantStr.split(",");
        String blockName = splited[0];
        int facing = Integer.parseInt(splited[1]);
        boolean is2State = Boolean.parseBoolean(splited[2]);

        IModel model = new SEMachineRawModel(domain, blockName, EnumFacing.getFront(facing), is2State);
        return model;
    }

    public void register(SEMachineBlock block) {
        ModelLoader.setCustomStateMapper(block, this);

        ItemBlock itemBlock = block.itemBlock;
        for (int meta : block.propertyMeta.getAllowedValues()) {
            IBlockState blockState = block.getStateFromMeta(meta);
            blockState = blockState.withProperty(ExtendedProperties.propertyFacing, EnumFacing.NORTH);
            ModelResourceLocation res = getModelResourceLocation(blockState);
            //Also register inventory variants here
            ModelLoader.setCustomModelResourceLocation(itemBlock, meta, res);
        }
    }
}
