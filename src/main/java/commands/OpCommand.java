package commands;

import java.util.List;

public class OpCommand extends Command {
	public OpCommand() {
		super("op", "Permit admin rights to a client.", "<client id>", 1);
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		try {
			getGameClient().getGamePlayers().get(Long.parseLong(params.get(0))).setAdmin(true);
			return "Permitted " + params.get(0);
		} catch (Exception e) {
			return "Failed";
		}
	}

	@Override
	public boolean discordOnly() {
		return true;
	}

	@Override
	public boolean adminOnly() {
		return true;
	}
	
}
