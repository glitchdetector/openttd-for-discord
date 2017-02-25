
package commands;

import java.util.List;

public class UnpauseCommand extends Command {

	public UnpauseCommand() {
		super("unpause", "Unpause the game.", "", 0);
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		if (getGameClient().getGame().isPaused()) {
			getGameClient().sendAdminRcon("unpause");
			return "Unpaused";
		} else {
			return "Game is not paused.";
		}
	}

	@Override
	public boolean adminOnly() {
		return true;
	}

}
