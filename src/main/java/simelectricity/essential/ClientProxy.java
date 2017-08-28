package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import rikka.librikka.model.loader.AdvancedModelLoader;
import rikka.librikka.tileentity.ISEGuiProvider;

import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.cable.CableStateMapper;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.grid.GridRenderMonitor;
import simelectricity.essential.client.grid.GridStateMapper;
import simelectricity.essential.client.grid.TileRenderPowerPole;
import simelectricity.essential.client.grid.transformer.PowerTransformerStateMapper;
import simelectricity.essential.client.semachine.SEMachineStateMapper;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.grid.TilePowerPole2;
import simelectricity.essential.grid.TilePowerPole3.Pole10KvType0;
import simelectricity.essential.grid.TilePowerPole3.Pole10KvType1;
import simelectricity.essential.grid.TilePowerPole3.Pole415vType0;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;
import simelectricity.essential.machines.gui.*;
import simelectricity.essential.machines.tile.*;

import java.util.LinkedList;

public class ClientProxy extends CommonProxy {
    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public IThreadListener getClientThread() {
        return Minecraft.getMinecraft();
    }

    @Override
    public void preInit() {
        //Initialize the client-side API
        SEEAPI.coloredBlocks = new LinkedList<Block>();

        AdvancedModelLoader loader = new AdvancedModelLoader(Essential.modID);
        loader.registerInventoryIcon(ItemRegistry.itemHVCable);
        loader.registerInventoryIcon(ItemRegistry.itemVitaTea);
        loader.registerInventoryIcon(ItemRegistry.itemMisc);
        loader.registerInventoryIcon(ItemRegistry.itemTools);

        SEMachineStateMapper semStateMapper = new SEMachineStateMapper(Essential.modID);
        loader.registerModelLoader(semStateMapper);
        semStateMapper.register(BlockRegistry.blockElectronics);
        semStateMapper.register(BlockRegistry.blockTwoPortElectronics);

        CableStateMapper cStateMapper = new CableStateMapper(Essential.modID);
        loader.registerModelLoader(cStateMapper);
        cStateMapper.register(BlockRegistry.blockCable);
        loader.registerInventoryIcon(BlockRegistry.blockCable.itemBlock);

        GridStateMapper gStateMapper = new GridStateMapper(Essential.modID);
        loader.registerModelLoader(gStateMapper);
        gStateMapper.register(BlockRegistry.cableJoint);
        loader.registerInventoryIcon(BlockRegistry.cableJoint.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleBottom);
        loader.registerInventoryIcon(BlockRegistry.powerPoleBottom.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleCollisionBox);
        loader.registerInventoryIcon(BlockRegistry.powerPoleCollisionBox.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleTop);
        loader.registerInventoryIcon(BlockRegistry.powerPoleTop.itemBlock);
        gStateMapper.register(BlockRegistry.powerPole2);
        loader.registerInventoryIcon(BlockRegistry.powerPole2.itemBlock);
        gStateMapper.register(BlockRegistry.powerPole3);

        PowerTransformerStateMapper ptStateMapper = new PowerTransformerStateMapper(Essential.modID);
        loader.registerModelLoader(ptStateMapper);
        ptStateMapper.register(BlockRegistry.powerTransformer);

        //Initialize socket render and support render
        new SocketRender();
        new SupportRender();

        //Initialize coverpanel render
        new VoltageSensorRender();
        new LedPanelRender();
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new GridRenderMonitor());

        SEEAPI.coloredBlocks.add(BlockRegistry.blockCable);

        TileRenderPowerPole.register(TileCableJoint.class);
        TileRenderPowerPole.register(TilePowerPole.class);
        TileRenderPowerPole.register(TilePowerPole2.class);
        TileRenderPowerPole.register(Primary.class);
        TileRenderPowerPole.register(Secondary.class);
        TileRenderPowerPole.register(Pole10KvType0.class);
        TileRenderPowerPole.register(Pole10KvType1.class);
        TileRenderPowerPole.register(Pole415vType0.class);
    }

    @Override
    public void postInit() {

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (te instanceof ISEGuiProvider)
            return ((ISEGuiProvider) te).getClientGuiContainer(EnumFacing.getFront(ID));

        Container container = BlockRegistry.getContainer(te, player);

        if (te instanceof TileVoltageMeter)
            return new GuiVoltageMeter(container);
        if (te instanceof TileQuantumGenerator)
            return new GuiQuantumGenerator(container);
        if (te instanceof TileAdjustableResistor)
            return new GuiAdjustableResistor(container);

        if (te instanceof TileAdjustableTransformer)
            return new GuiAdjustableTransformer(container);
        if (te instanceof TileCurrentSensor)
            return new GuiCurrentSensor(container);
        if (te instanceof TileDiode)
            return new GuiDiode(container);
        if (te instanceof TileSwitch)
            return new GuiSwitch(container);

        return null;
    }
}
