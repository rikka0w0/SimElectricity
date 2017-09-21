package simelectricity.essential.utils.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;

@SideOnly(Side.CLIENT)
public class GuiDirectionSelector {
    private static final int[][] rotationMatrix = {
            {2, 3, 4, 5},
            {3, 2, 5, 4},
            {4, 5, 3, 2},
            {5, 4, 2, 3}
    };
    private final GuiDirectionSelector.GuiDirectionSelectorButton[] buttons;

    public GuiDirectionSelector(int x, int y) {
    	this(x, y, Utils.getPlayerSightHorizontal(Minecraft.getMinecraft().player));
    }
    
    public GuiDirectionSelector(int x, int y, EnumFacing playerSight) {
        int sight;
        if (playerSight == null)
            sight = 4;
        else if (playerSight == EnumFacing.NORTH ||
                playerSight == EnumFacing.SOUTH ||
                playerSight == EnumFacing.WEST ||
                playerSight == EnumFacing.EAST)
            sight = playerSight.ordinal() - 2 & 3;
        else
            sight = 4;

        this.buttons = new GuiDirectionSelector.GuiDirectionSelectorButton[6];

        buttons[0] = new GuiDirectionSelector.GuiDirectionSelectorButton(x + 23, y + 6, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_DOWN, EnumFacing.DOWN);        //D
        buttons[1] = new GuiDirectionSelector.GuiDirectionSelectorButton(x + 6, y + 6, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_UP, EnumFacing.UP);            //U
        buttons[2] = new GuiDirectionSelector.GuiDirectionSelectorButton(x + 3, y, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_HORIZONTAL, EnumFacing.getFront(rotationMatrix[sight][0]));    //N
        buttons[3] = new GuiDirectionSelector.GuiDirectionSelectorButton(x + 3, y + 17, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_HORIZONTAL, EnumFacing.getFront(rotationMatrix[sight][1]));    //S
        buttons[4] = new GuiDirectionSelector.GuiDirectionSelectorButton(x, y + 3, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_VERTICAL, EnumFacing.getFront(rotationMatrix[sight][2]));    //W
        buttons[5] = new GuiDirectionSelector.GuiDirectionSelectorButton(x + 17, y + 3, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_VERTICAL, EnumFacing.getFront(rotationMatrix[sight][3]));    //E
    }

    public void draw(EnumFacing red, EnumFacing green) {
        for (int i = 0; i < 6; i++) {
            GuiDirectionSelector.GuiDirectionSelectorButton button = buttons[i];
            if (button.actualDirection == red)
                buttons[i].state = GuiDirectionSelector.GuiDirectionSelectorButton.STATE_RED;
            else if (button.actualDirection == green)
                buttons[i].state = GuiDirectionSelector.GuiDirectionSelectorButton.STATE_GREEN;
            else
                buttons[i].state = GuiDirectionSelector.GuiDirectionSelectorButton.STATE_NO_SELECTION;
            this.buttons[i].draw();
        }
    }

    public EnumFacing onMouseClick(int x, int y) {
        for (int i = 0; i < 6; i++) {
            GuiDirectionSelector.GuiDirectionSelectorButton button = buttons[i];
            if (button.isMouseOver(x, y))
                return button.actualDirection;
        }
        return null;
    }

    public static final class GuiDirectionSelectorButton extends Gui {
        public static final byte TYPE_HORIZONTAL = 0;
        public static final byte TYPE_VERTICAL = 1;
        public static final byte TYPE_UP = 2;
        public static final byte TYPE_DOWN = 3;
        public static final byte STATE_NO_SELECTION = 0;
        public static final byte STATE_RED = 1;
        public static final byte STATE_GREEN = 2;
        private static final ResourceLocation texture = new ResourceLocation("sime_essential:textures/gui/direction_selector.png");
        private static final int[] widthList = {14, 3, 8, 8};
        private static final int[] heightList = {3, 14, 8, 8};
        private static final int[][] uList = new int[3][];
        private static final int[][] vList = new int[3][];

        static {
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_NO_SELECTION] = new int[]{0, 0, 9, 9};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_NO_SELECTION] = new int[]{0, 3, 3, 11};
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_RED] = new int[]{14, 3, 17, 17};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_RED] = new int[]{0, 3, 3, 11};
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_GREEN] = new int[]{28, 6, 25, 25};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_GREEN] = new int[]{0, 3, 3, 11};
        }

        protected final EnumFacing actualDirection;
        protected final byte type;
        protected final int xPos, yPos, width, height;
        protected boolean enabled, visible;
        protected byte state;

        protected GuiDirectionSelectorButton(int xPos, int yPos, byte type, EnumFacing actualDirection) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.width = GuiDirectionSelector.GuiDirectionSelectorButton.widthList[type];
            this.height = GuiDirectionSelector.GuiDirectionSelectorButton.heightList[type];
            this.type = type;
            this.actualDirection = actualDirection;

            enabled = true;
            visible = true;
            state = 0;
        }

        protected boolean isMouseOver(int xMouse, int yMouse) {
            return enabled && visible && xMouse >= xPos && yMouse >= yPos && xMouse < xPos + width && yMouse < yPos + height;
        }

        protected void draw() {
            if (!visible)
                return;

            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiDirectionSelector.GuiDirectionSelectorButton.texture);

            int u = GuiDirectionSelector.GuiDirectionSelectorButton.uList[state][type];
            int v = GuiDirectionSelector.GuiDirectionSelectorButton.vList[state][type];
            drawTexturedModalRect(xPos, yPos, u, v, width, height);
        }
    }
}
