package simelectricity.essential.client;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISEModelLoader {
    boolean accepts(String resPath);

    IModel loadModel(String domain, String resPath, String variantStr) throws Exception;
}
