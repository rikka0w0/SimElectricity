package simelectricity.essential.client;

import simelectricity.essential.machines.render.SEMachineStateMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomModelLoader implements ICustomModelLoader{
	public final static String PATH_STI = "virtual/sti/";
	
	public final String domain;
	public CustomModelLoader(String domain){
		this.domain = domain;
		
		ModelLoaderRegistry.registerLoader(this);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// TODO Do nothing?
	}
	
	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		String domain = modelLocation.getResourceDomain();
		if (!this.domain.equals(domain))
			return false;
		
		String resPath = modelLocation.getResourcePath();
		if (resPath.startsWith(PATH_STI)){
			return true;	//SimpleTextureItem
		}
		
		else if (SEMachineStateMapper.accepts(resPath)){
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
		if (resPath.startsWith(PATH_STI)){
			resPath = resPath.substring(PATH_STI.length());
			return new SingleTextureModel(this.domain, resPath, false);	//SimpleTextureItem
		}
		
		else if (SEMachineStateMapper.accepts(resPath)){
			String variantStr = ((ModelResourceLocation)modelLocation).getVariant();
			return SEMachineStateMapper.loadModel(domain, resPath, variantStr);
		}
		
		return null;
	}
	
	public void registerIconsFor(Item item) {
		if (item instanceof ISESimpleTextureItem) {
			ISESimpleTextureItem simpleTextureItem = (ISESimpleTextureItem) item;
			
			NonNullList<ItemStack> itemStacks = NonNullList.create();
			item.getSubItems(item, null, itemStacks);
			for (ItemStack itemStack : itemStacks){
				int damage = itemStack.getItemDamage();
				String textureName = simpleTextureItem.getIconName(damage);
				ModelResourceLocation res = new ModelResourceLocation(
						this.domain + ":" + PATH_STI + textureName, "inventory");
				ModelLoader.setCustomModelResourceLocation(item, damage, res);
			}
		}
	}
}
