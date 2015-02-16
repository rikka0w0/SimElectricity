package simElectricity.Client.Render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simElectricity.API.Client.RenderHVTowerBase;
import simElectricity.Common.Blocks.TileEntity.TileTower;

@SideOnly(Side.CLIENT)
public class RenderCableClamp extends RenderHVTowerBase {

    @Override
    public void bindTexture(int index, int side) {
        switch (index) {
            case 1:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/thin_side.png"));
                return;
            case 2:
                bindTexture(new ResourceLocation("simelectricity", "textures/render/HvInsulator.png"));
                return;
            case 3:
                if (side == 5)
                    bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/thin_head.png"));
                else
                    bindTexture(new ResourceLocation("simelectricity", "textures/blocks/Wiring/thin_side.png"));
                return;
            default:
                bindTexture(new ResourceLocation("simelectricity", "textures/blocks/AdjustableResistor_Top.png"));
        }
    }

    @Override
    public void renderTower(TileEntity tower, double x, double y, double z) {
        GL11.glPushMatrix();
        render.render_cube(0.25, 1, 0.25);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.3, 0.2, 0);
        render.render_cube(0.4, 0.6, 0.6, 3);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.4, 1.2, 0.8);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glRotated(15, 0, 0, 1);
        GL11.glRotated(45, 1, 0, 0);
        renderInsulator(4, 0.8);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.6, 1.5, 0);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glRotated(25, 0, 0, 1);
        renderInsulator(4, 0.8);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(0.4, 1.2, -0.8);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glRotated(15, 0, 0, 1);
        GL11.glRotated(-45, 1, 0, 0);
        renderInsulator(4, 0.8);
        GL11.glPopMatrix();
    }

    @Override
    public void renderCable(TileEntity tileEntity, double x, double y, double z) {
        TileEntity neighbor = tileEntity.getWorld().getTileEntity(new BlockPos(tileEntity.getPos().getX(), tileEntity.getPos().getY() + 2, tileEntity.getPos().getZ()));
        if (neighbor instanceof TileTower && neighbor.getBlockMetadata() == 1)
            renderCableTo(tileEntity, neighbor, x, y, z, 0.4);
    }
}
