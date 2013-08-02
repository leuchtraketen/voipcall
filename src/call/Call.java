package call;

import java.util.ArrayList;
import java.util.List;


public class Call implements Activatable {

	List<Peer> peers = new ArrayList<Peer>();

	public boolean isActive() {
		return peers.size() > 0;
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public void add(Peer peer) {
		peers.add(peer);
		new Thread(peer).start();
	}

	public void close() {
		for (Peer peer : peers) {
			peer.close();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Call) {
			return hashCode() == obj.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (Peer peer : peers) {
			hash += peer.hashCode();
		}
		return hash;
	}
}
