
/*
 *  Class that represents a connected player. To check for ingame adminship 
 */

package game;

import org.openttd.Client;

public class GamePlayer {

	private Client	client;
	private boolean	isAdmin	= false;

	public GamePlayer(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isAdmin() {
		return isAdmin;
	}
	
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
