package simelectricity.essential.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiDirectionSelector {
	public final static class GuiDirectionSelectorButton extends Gui{
		private static final ResourceLocation texture = new ResourceLocation("sime_essential:textures/gui/direction_selector.png");
		private static final int[] widthList = new int[]{14,3,8,8};
		private static final int[] heightList = new int[]{3,14,8,8};
		private static final int[][] uList = new int[3][];
		private static final int[][] vList = new int[3][];
		
		public static final byte TYPE_HORIZONTAL = 0;
		public static final byte TYPE_VERTICAL = 1;
		public static final byte TYPE_UP = 2;
		public static final byte TYPE_DOWN = 3;
		
		public static final byte STATE_NO_SELECTION = 0;
		public static final byte STATE_RED = 1;
		public static final byte STATE_GREEN = 2;
		
		static{
			uList[STATE_NO_SELECTION] = new int[]{0,0,9,9};
			vList[STATE_NO_SELECTION] = new int[]{0,3,3,11};
			uList[STATE_RED] = new int[]{14,3,17,17};
			vList[STATE_RED] = new int[]{0,3,3,11};
			uList[STATE_GREEN] = new int[]{28,6,25,25};
			vList[STATE_GREEN] = new int[]{0,3,3,11};
		}

		protected final EnumFacing actualDirection;
		protected final byte type;
		protected final int xPos, yPos, width, height;
		protected boolean enabled, visible;
		protected byte state;
		
		protected GuiDirectionSelectorButton(int xPos, int yPos, byte type, EnumFacing actualDirection){
			this.xPos = xPos;
			this.yPos = yPos;
			this.width = this.widthList[type];
			this.height = this.heightList[type];
			this.type = type;
			this.actualDirection = actualDirection;
			
			this.enabled = true;
			this.visible = true;
			this.state = 0;
		}
		
		protected boolean isMouseOver(int xMouse, int yMouse) {
	        return this.enabled && this.visible && xMouse >= this.xPos && yMouse >= this.yPos && xMouse < this.xPos + this.width && yMouse < this.yPos + this.height;
	    }
	    
		protected void draw(){
	    	if (!this.visible)
	    		return;
	    	
	    	Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	    	
	    	int u = uList[this.state][this.type];
	    	int v = vList[this.state][this.type];
	    	this.drawTexturedModalRect(this.xPos, this.yPos, u, v, this.width, this.height);
	    }
	}
	
    private final static int[][] rotationMatrix = new int[][]{
    	{2,3,4,5},
		{3,2,5,4},
		{4,5,3,2},
		{5,4,2,3}
    };
	
	private final GuiDirectionSelectorButton[] buttons;
	public GuiDirectionSelector(int x, int y, EnumFacing playerSight){
		int sight;
		if (playerSight == null)
			sight = 4;
		else if (	playerSight == EnumFacing.NORTH ||
					playerSight == EnumFacing.SOUTH ||
					playerSight == EnumFacing.WEST ||
					playerSight == EnumFacing.EAST)
			sight = (playerSight.ordinal() - 2)&3;
		else
			sight = 4;
		
		this.buttons = new GuiDirectionSelectorButton[6];
		
		buttons[0] = new GuiDirectionSelectorButton(x+23, y+6, GuiDirectionSelectorButton.TYPE_DOWN, EnumFacing.DOWN);		//D
		buttons[1] = new GuiDirectionSelectorButton(x+6, y+6, GuiDirectionSelectorButton.TYPE_UP, EnumFacing.UP);			//U
		buttons[2] = new GuiDirectionSelectorButton(x + 3, y, GuiDirectionSelectorButton.TYPE_HORIZONTAL, EnumFacing.getFront(rotationMatrix[sight][0]));	//N
		buttons[3] = new GuiDirectionSelectorButton(x + 3, y+17, GuiDirectionSelectorButton.TYPE_HORIZONTAL, EnumFacing.getFront(rotationMatrix[sight][1]));	//S
		buttons[4] = new GuiDirectionSelectorButton(x, y+3, GuiDirectionSelectorButton.TYPE_VERTICAL, EnumFacing.getFront(rotationMatrix[sight][2]));	//W
		buttons[5] = new GuiDirectionSelectorButton(x+17, y+3, GuiDirectionSelectorButton.TYPE_VERTICAL, EnumFacing.getFront(rotationMatrix[sight][3]));	//E	
	}
	
	public void draw(EnumFacing red, EnumFacing green){		
		for (int i=0; i<6; i++){
			GuiDirectionSelectorButton button = buttons[i];
			if (button.actualDirection == red)
				buttons[i].state = GuiDirectionSelectorButton.STATE_RED;
			else if (button.actualDirection == green)
				buttons[i].state = GuiDirectionSelectorButton.STATE_GREEN;
			else
				buttons[i].state = GuiDirectionSelectorButton.STATE_NO_SELECTION;
			buttons[i].draw();
		}
	}
	
	public EnumFacing onMouseClick(int x, int y){
		for (int i=0; i<6; i++){
			GuiDirectionSelectorButton button = buttons[i];
			if (button.isMouseOver(x, y))
				return button.actualDirection;
		}
		return null;
	}
}
