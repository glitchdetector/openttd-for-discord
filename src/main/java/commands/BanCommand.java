
package commands;

import java.util.List;

public class BanCommand extends Command {

	public BanCommand() {
		super("ban", "Bans the specified client", "<client id>", 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String command(List<String> params, boolean isAdmin) {
		// TODO Auto-generated method stub
		return "Banned client";
	}

	@Override
	public boolean adminOnly() {
		return true;
	}

}
