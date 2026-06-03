package simelectricity.essential.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Level getClientWorld() {
        return Minecraft.getInstance().level;
    }
}
