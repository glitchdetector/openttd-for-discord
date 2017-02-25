package commands;

import java.util.List;

public class PauseCommand extends Command {
	
	public PauseCommand() {
		super("pause", "Pause the game.", "", 0);
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		if (getGameClient().getGame().isPaused()) {
			return "Game is already paused.";
		} else {
			getGameClient().sendAdminRcon("pause");
			return "Paused";
		}
	}

	@Override
	public boolean adminOnly() {
		return true;
	}
	
}
