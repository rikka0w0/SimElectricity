package rikka.librikka.model.loader;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.model.SingleTextureModel;

/**
 * Load simple texture for items/itemblocks and also load code based block models
 * @author Rikka0_0
 *
 */
@SideOnly(Side.CLIENT)
public class AdvancedModelLoader implements ICustomModelLoader {
	//STI - simple texture item
	private static final String PATH_STI = "virtual/sti/";
    public final String domain;
    private final Set<IModelLoader> registeredLoaders = new HashSet();
    
    public AdvancedModelLoader(String domain) {
        this.domain = domain;

        ModelLoaderRegistry.registerLoader(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    	for (IModelLoader loader : this.registeredLoaders)
    		if (loader instanceof IResourceManagerReloadListener)
    			((IResourceManagerReloadListener) loader).onResourceManagerReload(resourceManager);
    }
    
    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        String domain = modelLocation.getResourceDomain();
        if (!this.domain.equals(domain))
            return false;

        String resPath = modelLocation.getResourcePath();
        if (resPath.startsWith(PATH_STI)) {
            return true;    //SimpleTextureItem
        } else {
            for (IModelLoader loader : this.registeredLoaders)
                if (loader.accepts(resPath))
                    return true;
        }

        return false;
    }
    
    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        String domain = modelLocation.getResourceDomain();
        if (!this.domain.equals(domain))
            return null;

        String resPath = modelLocation.getResourcePath();
        if (resPath.startsWith(PATH_STI)) {
            resPath = resPath.substring(PATH_STI.length());
            return new SingleTextureModel(this.domain, resPath, false);    //SimpleTextureItem
        } else {
            for (IModelLoader loader : this.registeredLoaders)
                if (loader.accepts(resPath)) {
                    String variantStr = ((ModelResourceLocation) modelLocation).getVariant();
                    return loader.loadModel(domain, resPath, variantStr);
                }
        }

        return null;
    }
    
    ///////////////////////////
    /// Utils
    ///////////////////////////
    public void registerModelLoader(IModelLoader loader) {
        this.registeredLoaders.add(loader);
    }

    public void registerInventoryIcon(BlockBase block) {
    	if (block instanceof ISimpleTexture) {
    		ISimpleTexture simpleTextureItem = (ISimpleTexture) block;

            NonNullList<ItemStack> itemStacks = NonNullList.create();
            block.itemBlock.getSubItems(block.itemBlock, null, itemStacks);
            for (ItemStack itemStack : itemStacks) {
                int damage = itemStack.getItemDamage();
                String textureName = simpleTextureItem.getIconName(damage);
                ModelResourceLocation res = new ModelResourceLocation(
                        domain + ":" + PATH_STI + textureName, "inventory");
                ModelLoader.setCustomModelResourceLocation(block.itemBlock, damage, res);
            }
    	} else {
    		registerInventoryIcon(block.itemBlock);
    	}
    }
    
    public void registerInventoryIcon(Item item) {
        if (item instanceof ISimpleTexture) {
            ISimpleTexture simpleTextureItem = (ISimpleTexture) item;

            NonNullList<ItemStack> itemStacks = NonNullList.create();
            item.getSubItems(item, null, itemStacks);
            for (ItemStack itemStack : itemStacks) {
                int damage = itemStack.getItemDamage();
                String textureName = simpleTextureItem.getIconName(damage);
                ModelResourceLocation res = new ModelResourceLocation(
                        domain + ":" + PATH_STI + textureName, "inventory");
                ModelLoader.setCustomModelResourceLocation(item, damage, res);
            }
        } else if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            if (block instanceof ISimpleTexture) {
                ISimpleTexture simpleTextureItem = (ISimpleTexture) block;

                NonNullList<ItemStack> itemStacks = NonNullList.create();
                item.getSubItems(item, null, itemStacks);
                for (ItemStack itemStack : itemStacks) {
                    int damage = itemStack.getItemDamage();
                    String textureName = simpleTextureItem.getIconName(damage);
                    ModelResourceLocation res = new ModelResourceLocation(
                            domain + ":" + PATH_STI + textureName, "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, damage, res);
                }
            }
        }
    }
}
