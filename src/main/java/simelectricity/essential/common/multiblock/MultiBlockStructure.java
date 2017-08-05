package simelectricity.essential.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MultiBlockStructure {
	/**NSWE YZX*/
	private final BlockInfo[][][][] unmirrored = new BlockInfo[4][][][];
	private final BlockInfo[][][][] mirroredAboutZ = new BlockInfo[4][][][];
	
	
	private final int height;
	private final int searchAreaSize;
	
	/**
	 * @param config y,z,x facing NORTH(Z-), do not change 
	 */
	public MultiBlockStructure(BlockInfo[][][] configuration){
		this.height = configuration.length;
		
		//Find the bounding box
		int zSize = 0, xSize = 0;
		for (int y=0; y<height; y++){
			BlockInfo[][] zxc = configuration[y];
			for (int z=0; z<zxc.length; z++){
				BlockInfo[] xc = zxc[z];
				if (xc.length > xSize)
					xSize = xc.length;
			}
			
			if (zxc.length > zSize)
				zSize = zxc.length;
		}
		this.searchAreaSize = xSize>zSize?xSize:zSize;
		
		
		
		this.unmirrored[0] = new BlockInfo[height][zSize][xSize]; 	//North, Unmirrored
		this.unmirrored[3] = new BlockInfo[height][xSize][zSize];	//East, Unmirrored
		this.unmirrored[1] = new BlockInfo[height][zSize][xSize]; 	//South, Unmirrored
		this.unmirrored[2] = new BlockInfo[height][xSize][zSize];	//West, Unmirrored
		for (int y=0; y<height; y++){
			for (int z=0; z<configuration[y].length; z++){
				for (int x=0; x<configuration[y][z].length; x++){
					this.unmirrored[0][y][z][x] = configuration[y][z][x];					//North
					this.unmirrored[3][y][x][zSize-1-z] = configuration[y][z][x];			//East, newX = zSize-1 - oldZ, newZ = oldX
					this.unmirrored[1][y][zSize-1-z][xSize-1-x] = configuration[y][z][x];	//South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
					this.unmirrored[2][y][xSize-1-x][z] = configuration[y][z][x];			//West, newX = oldZ, newZ = xSize-1 - oldX;
				}
			}
		}
		
		for (int dir=0; dir<4; dir++){
			int zDim = this.unmirrored[dir][0].length;
			int xDim = this.unmirrored[dir][0][0].length;
			
			this.mirroredAboutZ[dir] = new BlockInfo[height][zDim][xDim];
			for (int y=0; y<height; y++){
				for (int z=0; z<zDim; z++){
					for (int x=0; x<xDim; x++){
						this.mirroredAboutZ[dir][y][z][xDim-1-x] = this.unmirrored[dir][y][z][x];	//newX = xDim-1 -oldX, newZ = oldZ
					}
				}
			}
		}
	}
	
	private boolean check(Block[][][] blocks, int[][][] meta, BlockInfo[][][] configuration, int xOrigin, int yOrigin, int zOrigin){
		for (int y=0; y<height; y++){
			for (int z=0; z<configuration[y].length; z++){
				for (int x=0; x<configuration[y][z].length; x++){
					BlockInfo config = configuration[y][z][x];
					if (config != null &&
						configuration[y][z][x].isDifferent
							(blocks[xOrigin+x][yOrigin+y][zOrigin+z], meta[xOrigin+x][yOrigin+y][zOrigin+z]))
						return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param blocks
	 * @param meta
	 * @param configuration
	 * @return	offset, null = mismatch
	 */
	private int[] check(Block[][][] blocks, int[][][] meta, BlockInfo[][][] configuration){
		for (int zOrigin=0; zOrigin<searchAreaSize; zOrigin++){
			for (int xOrigin=0; xOrigin<searchAreaSize; xOrigin++){
				for (int yOrigin=0; yOrigin<height; yOrigin++){
					if (check(blocks, meta, configuration, xOrigin, yOrigin, zOrigin))
						return new int[]{xOrigin, yOrigin, zOrigin};
				}
			}
		}
		
		return null;
	}
	
	public MultiBlockStructure.Result check(World world, int xStart, int yStart, int zStart){
		//XYZ
		Block[][][] blocks = new Block[searchAreaSize*2-1][height*2-1][searchAreaSize*2-1];
		int[][][] meta = new int[searchAreaSize*2-1][height*2-1][searchAreaSize*2-1];
		
		//Origin of the search area
		int xOrigin = xStart-searchAreaSize+1;
		int yOrigin = yStart-height+1;
		int zOrigin = zStart-searchAreaSize+1;
		
		for (int x=xOrigin, i=0; x<xStart+searchAreaSize; x++, i++){
			for (int y=yOrigin, j=0; y<yStart+height; y++, j++){
				for (int z=zOrigin, k=0; z<zStart+searchAreaSize; z++, k++){
					blocks[i][j][k] = world.getBlock(x, y, z);
					meta[i][j][k] = world.getBlockMetadata(x, y, z);
					
					if (blocks[i][j][k] == Blocks.air)
						blocks[i][j][k] = null;
				}
			}
		}
		
		Result result = null;
		
		//Check unmirrored
		for (int dir=0; dir<4; dir++){
			int[] offset = check(blocks, meta, unmirrored[dir]);
			if (offset != null)
				return new Result(this, dir, false, xOrigin+offset[0], yOrigin+offset[1], zOrigin+offset[2]);
		}
		
		//Check mirrored
		for (int dir=0; dir<4; dir++){
			int[] offset = check(blocks, meta, mirroredAboutZ[dir]);
			if (offset != null)
				return new Result(this, dir, true, xOrigin+offset[0], yOrigin+offset[1], zOrigin+offset[2]);
		}
		
		return null;
	}

	public static class BlockInfo{
		public final Block block;
		public final int meta;
		public final Block block2;
		public final int meta2;
		
		public BlockInfo(Block block, int meta, Block block2, int meta2){
			this.block = block;
			this.meta = meta;
			this.block2 = block2;
			this.meta2 = meta;
		}
		
		public boolean isDifferent(Block block, int meta){			
			return this.block != block || this.meta != meta;
		}
	}
	
	public static class Result{
		private final MultiBlockStructure structure;
		public final int rotation;
		public final boolean mirrored;

		
		
		/**NSWE YZX*/
		private final BlockInfo[][][] blockInfo;
		/** Origin of blockInfo, Actual location in the world */
		public final int xOrigin, yOrigin, zOrigin;
		private final int zSize, xSize;
		
		/** Actual location in the world */
		public final int xOriginActual, yOriginActual, zOriginActual;
		
		public Result(MultiBlockStructure structure, int rotation, boolean mirrored,
				int xOrigin, int yOrigin, int zOrigin){
			this.structure = structure;
			this.rotation = rotation;
			this.mirrored = mirrored;
			this.xOrigin = xOrigin;
			this.yOrigin = yOrigin;
			this.zOrigin = zOrigin;
			
			
			
			if (mirrored){
				this.blockInfo = structure.mirroredAboutZ[rotation];
			}else{
				this.blockInfo = structure.unmirrored[rotation];
			}
			
			this.zSize = this.blockInfo[0].length;
			this.xSize = this.blockInfo[0][0].length;
			
			int xOriginActual = this.xOrigin;
			int yOriginActual = this.yOrigin;
			int zOriginActual = this.zOrigin;
			

			
			switch(rotation){
			case 0:	//North
				if (mirrored)
					xOriginActual = xOriginActual + this.xSize-1;
				break;
			case 3: //East, newX = zSize-1 - oldZ, newZ = oldX
				xOriginActual += this.xSize - 1;
				if (mirrored)
					xOriginActual = xOriginActual - this.xSize+1;
				break;
			case 1:	//South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
				xOriginActual += this.xSize - 1;
				zOriginActual += this.zSize - 1;
				if (mirrored)
					xOriginActual = xOriginActual - this.xSize+1;
				break;
			case 2: //West, newX = oldZ, newZ = xSize-1 - oldX;
				if (mirrored)
					xOriginActual = xOriginActual + this.xSize-1;
				zOriginActual += this.zSize - 1;
				break;
			default:
				xOriginActual = -1;
				yOriginActual = -1;
				zOriginActual = -1;
				break;
			}
			
			this.xOriginActual = xOriginActual;
			this.yOriginActual = yOriginActual;
			this.zOriginActual = zOriginActual;
		}
		
		/**
		 * 
		 * @param x
		 * @param y
		 * @param z
		 * @return XYZ
		 */
		public int[] getActualOffset(int x, int y, int z){
			int[] ret = new int[3];
			
			switch(rotation){
			case 0:	//North
				ret =  new int[]{x,y,z};
				break;
			case 3: //East, newX = zSize-1 - oldZ, newZ = oldX
				ret =  new int[]{-z,y,x};
				break;
			case 1:	//South, newX = xSize-1 -newX, newZ = zSize-1 - newZ
				ret =  new int[]{-x,y,-z};
				break;
			case 2: //West, newX = oldZ, newZ = xSize-1 - oldX;
				ret =  new int[]{z,y,-x};
				break;
			default:
				ret = null;
				break;
			}
			
			if (ret == null)
				return null;
			
			if (mirrored)
				ret[0] = -ret[0];
			
			ret[0] += xOriginActual;
			ret[1] += yOriginActual;
			ret[2] += zOriginActual;
			
			return ret;
		}
	}
}
