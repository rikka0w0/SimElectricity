package simelectricity.essential.grid;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockInfo {
    public final BlockState state;
    public final Vec3i offset;

    public BlockInfo(int x, int y, int z, BlockState state) {
        this(new Vec3i(x, y, z), state);
    }

    public BlockInfo(Vec3i offset, BlockState state) {
        this.offset = offset;
        this.state = state;
    }

    public BlockPos getRealPos(BlockPos origin) {
        return origin.add(this.offset);
    }
}
