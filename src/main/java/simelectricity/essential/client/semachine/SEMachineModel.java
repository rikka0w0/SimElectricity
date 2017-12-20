package simelectricity.essential.client.semachine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.common.semachine.SEMachineBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SEMachineModel implements IBakedModel {
    private final IBakedModel bakedModel;

    public SEMachineModel(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.bakedModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.bakedModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.bakedModel.getParticleTexture();
    }

    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.bakedModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    private int[] getSocketIconArray(IExtendedBlockState exBlockState) {
        int[] ret = new int[6];
        int i = 0;
        for (IUnlistedProperty<Integer> prop : SEMachineBlock.propertySockets) {
            Integer val = exBlockState.getValue(prop);
            if (val == null)
                return null;
            ret[i] = val - 1;    //-1: no socket icon
            i++;
        }
        return ret;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState blockState, @Nullable EnumFacing side, long rand) {
    	if (side == null) {
            List<BakedQuad> generalQuads = this.bakedModel.getQuads(blockState, side, rand);

            if (!(blockState instanceof IExtendedBlockState))
                return generalQuads;   //Item Model

            IExtendedBlockState exBlockState = (IExtendedBlockState) blockState;
            int[] socketIcon = this.getSocketIconArray(exBlockState);

            if (socketIcon == null)
                return generalQuads;

            List<BakedQuad> quads = new ArrayList<BakedQuad>();
            quads.addAll(generalQuads);
            SocketRender.getBaked(quads, socketIcon);
            return quads;
    	} else {
    		return this.bakedModel.getQuads(blockState, side, rand); 
    	}
    }
}
