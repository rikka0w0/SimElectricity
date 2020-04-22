package simelectricity.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import simelectricity.SimElectricity;
import simelectricity.energynet.EnergyNet;
import simelectricity.energynet.EnergyNetAgent;

public class CommandSimE {
	private final static String[] actionNames = {"version", "info", "refresh"};
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("sime")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> execAction(context.getSource(), actionNames[0], ""));
	
		for (int id=0; id<actionNames.length; id++) {
			String actionName = actionNames[id];
			literalargumentbuilder.then(
					Commands.literal(actionName).executes(context -> execAction(context.getSource(), actionName, ""))
					.then(Commands.argument("args", StringArgumentType.string()).executes(context -> execAction(context.getSource(), actionName, StringArgumentType.getString(context, "args"))))
			);
		}

		dispatcher.register(literalargumentbuilder);
	}
	
	public static int execAction(CommandSource sender, String action, String args) {
		if (action.contentEquals("version")) {
			sender.sendFeedback(new StringTextComponent("SimElectricity Version: " + SimElectricity.version), true);
			CommandSimE.info(sender, sender.getWorld().getDimension().getType());
		} else if (action.contentEquals("info")) {
			if (args.length() == 0) {
				CommandSimE.info(sender, sender.getWorld().getDimension().getType());
			} else {
				int dimID = Integer.valueOf(args);
				CommandSimE.info(sender, DimensionType.getById(dimID));
			}
		} else if (action.contentEquals("refresh")) {
			if (args.length() == 0) {
				CommandSimE.refresh(sender, sender.getWorld().getDimension().getType());
			} else {
				int dimID = Integer.valueOf(args);
				CommandSimE.refresh(sender, DimensionType.getById(dimID));
			}
		}
		return 1;
	}
	
	private static void info(CommandSource sender, DimensionType dim) {
		ServerWorld world = DimensionManager.getWorld(sender.getServer(), dim, false, false);
		if (world == null) {
			sender.sendFeedback(new StringTextComponent("Dimension " + dim.getId() + " is not loaded!"), true);
			return;
		}
		
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        sender.sendFeedback(new StringTextComponent("-----------------------------------"), true);
        sender.sendFeedback(new StringTextComponent("EnergyNet for dimension " + dim.getId() + ":"), true);
        for (String s : energyNet.info())
            sender.sendFeedback(new StringTextComponent(s), true);
    }

    private static void refresh(CommandSource sender, DimensionType dim) {
		ServerWorld world = DimensionManager.getWorld(sender.getServer(), dim, false, false);
		if (world == null) {
			sender.sendFeedback(new StringTextComponent("Dimension " + dim.getId() + " is not loaded!"), true);
			return;
		}
		
        EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
        energyNet.reFresh();
        sender.sendFeedback(new StringTextComponent("EnergyNet for dimension " + dim.getId() + " has been refreshed!"), true);
    }
}
