package simelectricity.essential.api.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;

public interface ISECoverPanelRegistry {
    /**
     * 
     * @param factory the factory which is able to create the ISECoverPanel from ItemStack
     * @param panelCls the class of the ISECoverPanel to be registered
     * @param name the name of the cover panel, this should only contains lower case letters, numbers and _. 
     * If null is supplied, the registry will automatically name the cover panel from its class name.
     */
    void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panelCls, @Nullable String name);

    /**
     * Instantiate a cover panel from ItemStack 
     * @param itemStack
     * @return the cover panel instance, null if no {@link ISECoverPanelFactory} accepts this ItemStack.
     */
    @Nullable
    ISECoverPanel fromItemStack(@Nonnull ItemStack itemStack);

    /**
     * Instantiate a cover panel from nbt 
     * @param nbt the nbt compound to hold the result, must not be null
     * @return the cover panel instance, null if nbt is null or invalid.
     */
    @Nullable
    ISECoverPanel fromNBT(CompoundNBT nbt);

    /**
     * Save a cover panel to NBT
     * @param panel the cover panel to be saved, it must be registered using {@link ISECoverPanelRegistry#register}
     * @param nbt the nbt compound to hold the result, must not be null
     */
    void saveToNBT(ISECoverPanel panel, @Nonnull CompoundNBT nbt);

    /**
     * @return the GenericFacadeRender which renders ISEFacadeCoverPanel and mimic the block specified by
     * {@link simelectricity.essential.api.coverpanel#getBlockState}
     */
    ISECoverPanelRender<ISEFacadeCoverPanel> getGenericFacadeRender();

    /**
     * In order to display facades of colored blocks properly using the "GenericFacadeRender",
     * the host block must be registered here. <p>
     * Call this function during Block Registration Event {@link net.minecraftforge.event.RegistryEvent.Register}
     * &lt;{@link net.minecraft.block.Block}&gt; or Common Setup Event
     * {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     */
    void registerColoredFacadeHost(Block... blocks);
}
