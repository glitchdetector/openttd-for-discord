
package commands;

import java.util.List;
import java.util.Map;

import org.openttd.Client;

import game.GamePlayer;

public class ClientsCommand extends Command {

	public ClientsCommand() {
		super("clients", "Shows a list of connected clients", "", 0);
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		String text = "";
		for (Map.Entry<Long, GamePlayer> entry : getGameClient().getGamePlayers().entrySet()) {
			Client c = entry.getValue().getClient();
			text += "#" + c.id + ": " + c.name + "\n";
		}
		return text;
	}

	@Override
	public boolean discordOnly() {
		return true;
	}
	
	
}
