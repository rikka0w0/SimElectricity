package simelectricity.essential.client.cable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.loader.IModelLoader;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.BlockWire;

@SideOnly(Side.CLIENT)
public class CableStateMapper extends StateMapperBase implements IModelLoader {
    public static final String VPATH = "virtual/blockstates/cablestatemapper";
    public final String domain;

    public CableStateMapper(String domain) {
        this.domain = domain;
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Block block = state.getBlock();

        if (block instanceof BlockCable) {
            BlockCable cable = (BlockCable) block;
            int meta = cable.getMetaFromState(state);
            String name = cable.getRegistryName().getResourcePath();
            String subName = cable.getSubBlockUnlocalizedNames()[meta];
            double thickness = cable.thickness[meta];

            //Encode relative information in the variant name part
            String varStr = "cable," + name + "_" + subName + "," + thickness;

            //The resource path indicates the loader
            ModelResourceLocation res = new ModelResourceLocation(
                    domain + ":" + CableStateMapper.VPATH,
                    varStr
            );
            return res;
        } else if (block instanceof BlockWire) {
            BlockWire wire = (BlockWire) block;
            String name = wire.getRegistryName().getResourcePath();
            float thickness = wire.thickness;

            String varStr = "wire," + name + "," + thickness;

            //The resource path indicates the loader
            ModelResourceLocation res = new ModelResourceLocation(
                    domain + ":" + CableStateMapper.VPATH,
                    varStr
            );

            return res;
        }

        return null;
    }

    @Override
    public boolean accepts(String resPath) {
        return resPath.startsWith(CableStateMapper.VPATH);
    }

    @Override
    public IModel loadModel(String domain, String resPath, String variantStr) throws Exception {
        String[] splited = variantStr.split(",");
        String type = splited[0];
        String name = splited[1];
        if (type.equals("cable")) {
            float thickness = Float.parseFloat(splited[2]);
            return new CableModel(domain, "cable/" + name, thickness);
        } else if (type.equals("wire")) {
            float thickness = Float.parseFloat(splited[2]);
            return new WireModel(domain, "wire/" + name, thickness);
        }
        return null;
    }

    public void register(Block block) {
        ModelLoader.setCustomStateMapper(block, this);
    }
}
