package simelectricity.common;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import simelectricity.SimElectricity;
import simelectricity.energynet.EnergyNet;
import simelectricity.energynet.EnergyNetAgent;

public class CommandSimE {
	private final static RequiredArgumentBuilder<CommandSource, ?> argDimension = Commands.argument("dimension", DimensionArgument.getDimension());
	private static ServerWorld parseDimension(CommandContext<CommandSource> context) throws CommandSyntaxException {
		return DimensionArgument.getDimensionArgument(context, argDimension.getName());
	}

	private static LiteralArgumentBuilder<CommandSource> consumeDimension(LiteralArgumentBuilder<CommandSource> cmd, BiFunction<CommandSource, ServerWorld, Integer> func) {
		return cmd.then(argDimension.executes(context -> func.apply(context.getSource(), parseDimension(context))))
				.executes(context -> func.apply(context.getSource(), context.getSource().asPlayer().getServerWorld()));
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("sime")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> version(context.getSource()));

		literalargumentbuilder.then(Commands.literal("version").executes(context -> version(context.getSource())));
		literalargumentbuilder.then(consumeDimension(Commands.literal("info"), CommandSimE::info));
		literalargumentbuilder.then(consumeDimension(Commands.literal("refresh"), CommandSimE::refresh));

		dispatcher.register(literalargumentbuilder);
	}
	
	public static int version(CommandSource sender) {
		sender.sendFeedback(new StringTextComponent("SimElectricity Version: " + SimElectricity.version), true);

		return 1;
	}
	
	private static int info(CommandSource sender, ServerWorld world) {
		if (world == null) {
			sender.sendFeedback(new StringTextComponent("Dimension " + world.getDimensionKey().getRegistryName() + " is not loaded!"), true);
			return 0;
		}

		EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
		sender.sendFeedback(new StringTextComponent("-----------------------------------"), true);
		sender.sendFeedback(new StringTextComponent("EnergyNet for dimension " + world.getDimensionKey().getRegistryName() + ":"), true);

		for (String s : energyNet.info()) {
			sender.sendFeedback(new StringTextComponent(s), true);
		}

		return 1;
    }

    private static int refresh(CommandSource sender, ServerWorld world) {
		if (world == null) {
			sender.sendFeedback(new StringTextComponent("Dimension " + world.getDimensionKey().getRegistryName() + " is not loaded!"), true);
			return 0;
		}

		EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
		energyNet.reFresh();
		sender.sendFeedback(new StringTextComponent("EnergyNet for dimension " + world.getDimensionKey().getRegistryName() + " has been refreshed!"), true);

		return 1;
    }
}
