
package game;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openttd.Client;
import org.openttd.Company;
import org.openttd.Game;
import org.openttd.OpenTTD;
import org.openttd.enums.AdminCompanyRemoveReason;
import org.openttd.enums.AdminUpdateFrequency;
import org.openttd.enums.AdminUpdateType;
import org.openttd.enums.DestType;
import org.openttd.enums.NetworkAction;
import org.openttd.enums.NetworkErrorCode;

import commands.Command;
import discord.DiscordClient;

public class GameClient extends OpenTTD {
	/** The discord interface */
	private DiscordClient			discordClient;

	/* To keep track of players and companies (data stored on info calls) */
	private Map<Long, GamePlayer>	gamePlayers		= new HashMap<Long, GamePlayer>();
	private Map<Integer, Company>	gameCompanies	= new HashMap<Integer, Company>();

	public GameClient(String serverHost, int serverPort, String serverPassword, String botName, String botVersion) {
		setHostname(serverHost);
		setPort(serverPort);
		setPassword(serverPassword);

		setBotName(botName);
		setBotVersion(botVersion);

		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Ran when the bot successfully connects to the game */
	private void setup() {
		// Register all game related update frequencies
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_DATE, AdminUpdateFrequency.ADMIN_FREQUENCY_MONTHLY);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_CLIENT_INFO, AdminUpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_COMPANY_INFO, AdminUpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_COMPANY_ECONOMY, AdminUpdateFrequency.ADMIN_FREQUENCY_MONTHLY);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_COMPANY_STATS, AdminUpdateFrequency.ADMIN_FREQUENCY_MONTHLY);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_CHAT, AdminUpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_CONSOLE, AdminUpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC);
		registerUpdateFrequency(AdminUpdateType.ADMIN_UPDATE_CMD_LOGGING, AdminUpdateFrequency.ADMIN_FREQUENCY_AUTOMATIC);
		pollAll();
	}

	/** Ran when the a message is sent to the chat ingame. */
	@Override
	public void onChat(NetworkAction action, DestType desttype, Client client, String message, BigInteger data) {
		// TODO Auto-generated method stub
		System.out.println("onChat - " + client.name + ": " + message + " | " + action.name() + " | " + desttype.name() + " | " + data);

		boolean isAdmin = gamePlayers.get(client.id).isAdmin();
		
		// Only send a message if its sent to the public chat channel.
		if (desttype == DestType.DESTTYPE_BROADCAST && action == NetworkAction.NETWORK_ACTION_CHAT) {
			if (message.startsWith("!")) {

				/** The user is trying to issue a command **/
				String returnString = tryCommand(message, isAdmin);
				chatPrivate(client.id, returnString); // Send the result back

			} else {

				/** The user is sending a message **/
				discordClient.broadcastMessage(formatMessage(client.name, message));

			}
		} else if (desttype == DestType.DESTTYPE_BROADCAST) {

			/** Another type of action is happening **/
			switch (action) {
				case NETWORK_ACTION_COMPANY_SPECTATOR:
					// Client joined spectator
					break;
				case NETWORK_ACTION_CHAT_CLIENT:
				case NETWORK_ACTION_CHAT_COMPANY:
					// shh theyre pming
					break;
				case NETWORK_ACTION_COMPANY_JOIN:
					// Client joined a company
					discordClient.broadcastMessage(client.name + " joined company #" + client.companyId + ".");
					break;
				case NETWORK_ACTION_COMPANY_NEW:
					// Client made a new company
					discordClient.broadcastMessage(client.name + " created a new company (#" + client.companyId + ").");
					break;
				case NETWORK_ACTION_GIVE_MONEY:
					// client gave monies
					break;
				case NETWORK_ACTION_JOIN:
					// Client joined
					break;
				case NETWORK_ACTION_LEAVE:
					// Client left
					break;
				case NETWORK_ACTION_NAME_CHANGE:
					// Client changed name
					break;
				case NETWORK_ACTION_SERVER_MESSAGE:
					// A server message?
					break;
				default:
					break;
			}

		}
		super.onChat(action, desttype, client, message, data);
	}

	private String tryCommand(String message, boolean isAdmin) {
		String[] msg = message.split(" ");
		List<String> params = new ArrayList<String>();
		for (int i = 1; i < msg.length; i++) {
			params.add(msg[i]);
		}
		String returnString = "Invalid command";
		for (Command command : Command.getCommands()) {
			if (("!" + command.getName()).equals(msg[0])) {
				if (command.adminOnly() && !isAdmin) return "No permission";
				if (command.discordOnly()) return "Command can only be ran in Discord";
				if (command.canExecute(params)) {
					returnString = command.execute(params, isAdmin);
					break;
				}
				return "Usage: " + command.getUsage();
			}
		}
		return returnString;
	}

	@Override
	public void onServerWelcome(Game game) {
		System.out.println("Logged in");
		setup();
		super.onServerWelcome(game);
	}

	public void onConsole(String origin, String message) {
		System.out.println(message);
		super.onConsole(origin, message);
	}

	public String formatMessage(String username, String message) {
		return String.format("[%s]: %s", username, message);
	}

	public void broadcastMessage(String message) {
		System.out.println(message);
		chatPublic(message);
		adminMessage(message);
	}

	public void adminMessage(String message) {
		System.out.println(message);
		// TODO make a system to send messages to a dedicated admin channel. so we can spy on ppls pms and do shady admin stuff behind their backs
	}

	public DiscordClient getDiscordClient() {
		return (discordClient == null ? new DiscordClient() : discordClient);
	}

	public void setDiscordClient(DiscordClient discordClient) {
		this.discordClient = discordClient;
	}

	@Override
	public void onShutdown() {
		// TODO Auto-generated method stub
		System.out.println("Shutdown");
		super.onShutdown();
	}
	
	

	@Override
	public void onClientQuit(Client client) {
		// TODO Auto-generated method stub
		System.out.println("client quit");
		if (gamePlayers.containsKey(client.id)) {
			gamePlayers.remove(client.id);
		}
		super.onClientQuit(client);
	}

	@Override
	public void onClientInfo(Client client) {
		// TODO Auto-generated method stub
		System.out.println("client info");
		if (gamePlayers.containsKey(client.id)) {
			gamePlayers.get(client.id).setClient(client);
		} else {
			gamePlayers.put(client.id, new GamePlayer(client));
		}
		super.onClientInfo(client);
	}

	@Override
	public void onCompanyRemove(Company company, AdminCompanyRemoveReason crr) {
		// TODO Auto-generated method stub
		System.out.println("company remove");
		if (gameCompanies.containsKey(company.id)) {
			gameCompanies.remove(company.id);
		}
		super.onCompanyRemove(company, crr);
	}

	@Override
	public void onCompanyInfo(Company company) {
		// TODO Auto-generated method stub
		System.out.println("company info");
		if (gameCompanies.containsKey(company.id)) {
			gameCompanies.remove(company.id);
		}
		gameCompanies.put(company.id, company);
		super.onCompanyInfo(company);
	}

	public Map<Long, GamePlayer> getGamePlayers() {
		return gamePlayers;
	}

	public Map<Integer, Company> getGameCompanies() {
		return gameCompanies;
	}

	@Override
	public void onClientError(Client client, NetworkErrorCode error) {
		// TODO Auto-generated method stub
		System.out.println("client error");
		if (gamePlayers.containsKey(client.id)) {
			gamePlayers.remove(client.id);
		}
		super.onClientError(client, error);
	}
	
	
}
