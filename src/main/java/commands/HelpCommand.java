package commands;

import java.util.List;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help", "Shows a list of all commands and their usage.", "", 0);
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		String text = "";
		for (Command command : getCommands()) {
			if (command.adminOnly() && !isAdmin) continue; // don't print admin commands to normies
			text += "**" + command.getUsage() + "** *" + command.getDescription() + "" + (command.discordOnly() ? " (Discord only)*" : "*") + "\n";
		}
		return text;
	}

	@Override
	public boolean discordOnly() {
		return true;
	}

}
