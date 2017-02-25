
package main;

import commands.*;
import discord.DiscordClient;
import game.GameClient;
import sx.blah.discord.api.IDiscordClient;

public class Main {

	public static final String channel = "176593973484191748"; // #admin @ Latias
	public static final String botKey = ""; // Moneyshot

	public static final String adminRoleID = "168099884497502208"; // Eon Pokémon @ Latias
	public static final String adminRoleName = "Admin";

	public static final String modRoleID = "227091375492038656"; // HIOF @ Latias
	public static final String modRoleName = "Mod";

	public static final String vipRoleID = "221713169138188288"; // VIP @ Latias
	public static final String vipRoleName = "VIP";

	public static final String serverHost = "localhost";
	public static final int serverPort = 3977;
	public static final String serverPassword = "";

	public static final String botName = "DiscordRelay";
	public static final String botVersion = "1.0";

	private static GameClient gameClient;
	private static DiscordClient discordClient;

	public static void main(String[] main) {
		// Make new clients
		discordClient = new DiscordClient(botKey);
		gameClient = new GameClient(serverHost, serverPort, serverPassword, botName, botVersion);

		// Assign the opposing client to each client (for communication)
		gameClient.setDiscordClient(discordClient);
		discordClient.setGameClient(gameClient);

		// Add listener and broadcast channel ids
		discordClient.getListenChannels().add(channel);
		discordClient.getBroadcastChannels().add(channel);

		// Add Discord role tags. (For ingame tagging)
		discordClient.getRoleTags().put(adminRoleID, adminRoleName);
		discordClient.getRoleTags().put(modRoleID, modRoleName);
		discordClient.getRoleTags().put(vipRoleID, vipRoleName);

		// Commands (stores themselves so no need to keep them)
		new KickCommand();
		new BanCommand();
		new PauseCommand();
		new UnpauseCommand();
		new ClientsCommand();
		new OpCommand();
		new HelpCommand();
	}

	/** The OpenTTD AdminPort Client **/
	public static GameClient getGameClient() {
		return gameClient;
	}

	/** The Discord Bot Client **/
	public static DiscordClient getDiscordClient() {
		return discordClient;
	}
}
