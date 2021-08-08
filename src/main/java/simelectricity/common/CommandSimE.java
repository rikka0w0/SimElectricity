package simelectricity.common;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import simelectricity.SimElectricity;
import simelectricity.energynet.EnergyNet;
import simelectricity.energynet.EnergyNetAgent;

public class CommandSimE {
	private final static RequiredArgumentBuilder<CommandSourceStack, ?> argDimension = Commands.argument("dimension", DimensionArgument.dimension());
	private static ServerLevel parseDimension(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return DimensionArgument.getDimension(context, argDimension.getName());
	}

	private static LiteralArgumentBuilder<CommandSourceStack> consumeDimension(LiteralArgumentBuilder<CommandSourceStack> cmd, BiFunction<CommandSourceStack, ServerLevel, Integer> func) {
		return cmd.then(argDimension.executes(context -> func.apply(context.getSource(), parseDimension(context))))
				.executes(context -> func.apply(context.getSource(), context.getSource().getPlayerOrException().getLevel()));
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("sime")
				.requires(source -> source.hasPermission(2))
				.executes(context -> version(context.getSource()));

		literalargumentbuilder.then(Commands.literal("version").executes(context -> version(context.getSource())));
		literalargumentbuilder.then(consumeDimension(Commands.literal("info"), CommandSimE::info));
		literalargumentbuilder.then(consumeDimension(Commands.literal("refresh"), CommandSimE::refresh));

		dispatcher.register(literalargumentbuilder);
	}

	public static int version(CommandSourceStack sender) {
		sender.sendSuccess(new TextComponent("SimElectricity Version: " + SimElectricity.version), true);

		return 1;
	}

	private static int info(CommandSourceStack sender, ServerLevel world) {
		if (world == null) {
			sender.sendSuccess(new TextComponent("Dimension " + world.dimension().getRegistryName() + " is not loaded!"), true);
			return 0;
		}

		EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
		sender.sendSuccess(new TextComponent("-----------------------------------"), true);
		sender.sendSuccess(new TextComponent("EnergyNet for dimension " + world.dimension().getRegistryName() + ":"), true);

		for (String s : energyNet.info()) {
			sender.sendSuccess(new TextComponent(s), true);
		}

		return 1;
    }

    private static int refresh(CommandSourceStack sender, ServerLevel world) {
		if (world == null) {
			sender.sendSuccess(new TextComponent("Dimension " + world.dimension().getRegistryName() + " is not loaded!"), true);
			return 0;
		}

		EnergyNet energyNet = EnergyNetAgent.getEnergyNetForWorld(world);
		energyNet.reFresh();
		sender.sendSuccess(new TextComponent("EnergyNet for dimension " + world.dimension().getRegistryName() + " has been refreshed!"), true);

		return 1;
    }
}
