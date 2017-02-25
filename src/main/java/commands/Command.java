
package commands;

import java.util.ArrayList;
import java.util.List;

import discord.DiscordClient;
import game.GameClient;
import main.Main;

public abstract class Command {
	private String					commandName;
	private String					commandDescription;
	private String					commandUsage;
	private int						paramCount;

	private GameClient				gameClient;
	private DiscordClient			discordClient;

	private static List<Command>	commands	= new ArrayList<Command>();

	public static List<Command> getCommands() {
		return commands;
	}

	public Command(String commandName, String commandDescription, String commandUsage, int paramCount) {
		this.commandName = commandName;
		this.commandDescription = commandDescription;
		this.commandUsage = commandUsage;
		this.paramCount = paramCount;

		gameClient = Main.getGameClient();
		discordClient = Main.getDiscordClient();

		commands.add(this);
	}

	public boolean canExecute(List<String> params) {
		return params.size() >= paramCount;
	}

	public String execute(List<String> params, boolean isAdmin) {
		if (canExecute(params)) {
			return command(params, isAdmin);
		} else {
			return getUsage();
		}
	}

	public abstract String command(List<String> params, boolean isAdmin);

	public String command(List<String> params) {
		return command(params, false);
	}
	
	public String getName() {
		return commandName;
	}

	public String getDescription() {
		return commandDescription;
	}

	public String getUsage() {
		return commandName + " " + commandUsage;
	}
	
	public boolean discordOnly() {
		return false;
	}

	public boolean adminOnly() {
		return false;
	}

	public GameClient getGameClient() {
		return gameClient;
	}

	public DiscordClient getDiscordClient() {
		return discordClient;
	}
}
