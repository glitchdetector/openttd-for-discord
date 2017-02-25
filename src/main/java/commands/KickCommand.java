
package commands;

import java.util.List;

public class KickCommand extends Command {

	public KickCommand() {
		super("kick", "Kicks the designated user", "<client id>", 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		// TODO Auto-generated method stub
		getGameClient().sendAdminRcon("kick " + params.get(0));
		return "Kicked " + params.get(0);
	}

	@Override
	public boolean adminOnly() {
		return true;
	}

}
