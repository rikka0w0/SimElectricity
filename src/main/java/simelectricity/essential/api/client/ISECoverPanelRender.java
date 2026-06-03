package simelectricity.essential.api.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.List;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
    @OnlyIn(Dist.CLIENT)
    void renderCoverPanel(TYPE coverPanel, Direction side, RandomSource random, List<BakedQuad> quads, RenderType renderType);
    
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
	default <X> X cast() {
    	return (X) this;
    }
}
