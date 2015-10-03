package simElectricity.Common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import simElectricity.Common.EnergyNet.EnergyNet;
import simElectricity.Common.EnergyNet.WorldData;
import simElectricity.SimElectricity;

public class CommandSimE extends CommandBase {

    @Override
    public String getCommandName() {
        return "sime";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sime (info | refresh) [dimensionID]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("SimElectricity Version: " + SimElectricity.version));
            info(sender, sender.getEntityWorld().provider.getDimensionId());
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length == 1) {
                info(sender, sender.getEntityWorld().provider.getDimensionId());
            } else {
                info(sender, Integer.valueOf(args[1]));
            }
        } else if (args[0].equalsIgnoreCase("refresh")) {
            if (args.length == 1) {
                refresh(sender, sender.getEntityWorld().provider.getDimensionId());
            } else {
                refresh(sender, Integer.valueOf(args[1]));
            }
        }
    }

    private void info(ICommandSender sender, int dim) {
        World world = DimensionManager.getWorld(dim);
        EnergyNet energyNet = WorldData.getEnergyNetForWorld(world);
        sender.addChatMessage(new ChatComponentText("-----------------------------------"));
        sender.addChatMessage(new ChatComponentText("EnergyNet for dimension " + dim + ":"));
        for (String s : energyNet.info())
            sender.addChatMessage(new ChatComponentText(s));
    }

    private void refresh(ICommandSender sender, int dim) {
        EnergyNet energyNet = WorldData.getEnergyNetForWorld(DimensionManager.getWorld(dim));
        energyNet.reFresh();
        sender.addChatMessage(new ChatComponentText("EnergyNet for dimension " + dim + " has been refreshed!"));
    }

    @Override
    public int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }

    @Override
    public int compareTo(Object command) {
        return this.compareTo((ICommand) command);
    }
}
