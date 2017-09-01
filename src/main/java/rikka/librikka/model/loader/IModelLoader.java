package rikka.librikka.model.loader;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IModelLoader {
    boolean accepts(String resPath);

    IModel loadModel(String domain, String resPath, String variantStr) throws Exception;
}
