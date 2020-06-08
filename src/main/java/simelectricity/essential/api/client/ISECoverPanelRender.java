package simelectricity.essential.api.client;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.List;
import java.util.Random;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
    @OnlyIn(Dist.CLIENT)
    void renderCoverPanel(TYPE coverPanel, Direction side, Random random, List<BakedQuad> quads);
    
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
	default <X> X cast() {
    	return (X) this;
    }
}
