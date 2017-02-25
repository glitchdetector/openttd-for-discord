
package discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commands.Command;
import game.GameClient;
import main.Main;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.DiscordStatus;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.DiscordClientImpl;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordClient {
	private IDiscordClient		client;
	private GameClient			gameClient;
	private List<String>		listenChannels;
	private List<String>		broadcastChannels;
	private Map<String, String>	roleTags;

	public DiscordClient(String token) {
		client = createClient(token);
		client.getDispatcher().registerListener(this);

		listenChannels = new ArrayList<String>();
		broadcastChannels = new ArrayList<String>();
		roleTags = new HashMap<String, String>();
	}

	public DiscordClient() {
		// Literally throwaway to prevent nulls
	}

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event) {
		IMessage msg = event.getMessage();
		String channelID = msg.getChannel().getID();
		
		if (msg.getChannel().isPrivate()) {
			try {
				msg.getChannel().sendMessage("There are no easter eggs here, go away.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// DM's would crash the bot when it tried to get guild and channel stuff (fixed)
		if (listenChannels.contains(channelID)) {
			// Channel is one of the listener channels
			String name = msg.getAuthor().getName();
			String channel = msg.getChannel().getName();
			String content = msg.getFormattedContent();
			
			IUser author = msg.getAuthor();
			IGuild guild = msg.getGuild();

			boolean isAdmin = isAdmin(author, guild);

			String message = formatMessage(name, channel, content);
			if (hasRoleTag(author, guild)) {
				// User has a prefix tag
				message = formatMessage(name, channel, content, author.getRolesForGuild(guild));
			}
			if (content.startsWith("!")) {
				// User is doing a command
				String returnString = tryCommand(content, isAdmin);
				sendMessageToChannel(channelID, returnString);
				try {
					msg.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Normal message
				gameClient.broadcastMessage(message);
			}
		}
	}

	/** Check if user has any of the administrive roles **/
	public static boolean isAdmin(IUser user, IGuild guild) {
		return hasRole(user, guild, Main.adminRoleID) || hasRole(user, guild, Main.modRoleID);
	}

	/** Checks if the certain user has a role in a guild W**/
	public static boolean hasRole(IUser user, IGuild guild, String roleID) {
		return user.getRolesForGuild(guild).contains(guild.getRoleByID(roleID));
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
				if (command.canExecute(params)) {
					returnString = command.execute(params, isAdmin);
					break;
				}
				return "Usage: " + command.getUsage();
			}
		}
		return returnString;
	}

	/** Format the message, with a name, channel and content. */
	public String formatMessage(String name, String channel, String content) {
		return String.format("[%s @ #%s]: %s", name, channel, content);
	}

	/** Format the message with a prefixed role. */
	public String formatMessage(String name, String channel, String content, List<IRole> roles) {
		String roleText = "(";
		for (IRole role : roles) {
			if (roleTags.containsKey(role.getID())) {
				roleText += roleTags.get(role.getID()) + "";
				break;
			}
		}
		roleText += ")";
		return String.format("%s[%s @ #%s]: %s", roleText, name, channel, content);
	}

	/** Check if the user has a valid role that the game uses */
	public boolean hasRoleTag(IUser user, IGuild guild) {
		for (IRole role : user.getRolesForGuild(guild)) {
			if (roleTags.containsKey(role.getID())) return true;
		}
		return false;
	}

	private IDiscordClient createClient(String token) {
		ClientBuilder cb = new ClientBuilder();
		cb.withToken(token);
		try {
			return cb.login();
		} catch (DiscordException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void sendMessageToChannel(String channelID, String message) {
		try {
			new MessageBuilder(client).withChannel(client.getChannelByID(channelID)).withContent(message).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void broadcastMessage(String message) {
		for (String channelID : broadcastChannels) {
			sendMessageToChannel(channelID, message);
		}
	}

	public IDiscordClient getClient() {
		return client;
	}

	public List<String> getListenChannels() {
		return listenChannels;
	}

	public List<String> getBroadcastChannels() {
		return broadcastChannels;
	}

	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}

	public Map<String, String> getRoleTags() {
		return roleTags;
	}

}
