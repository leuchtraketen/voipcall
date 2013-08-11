package call;

import java.io.IOException;
import java.net.InetAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

public class UPNPClient extends AbstractId implements Runnable {

	private final int[] ports;

	public UPNPClient(int[] ports) {
		this.ports = ports;
	}

	@Override
	public void run() {
		GatewayDevice d = discover();
		if (d != null) {
			Util.log(this, "Gateway device found: " + d.getModelName() + " (" + d.getModelDescription() + ")");
			for (int port : ports)
				portmap(d, port);
		}
	}

	private void portmap(GatewayDevice d, int port) {
		InetAddress localAddress = d.getLocalAddress();
		Util.log(this, "Using local address: " + localAddress);
		String externalIPAddress;
		try {
			externalIPAddress = d.getExternalIPAddress();
			Util.log(this, "External address: " + externalIPAddress);
		} catch (IOException | SAXException e1) {
			Util.log(this, "External address: (unknown)");
		}
		PortMappingEntry portMapping = new PortMappingEntry();

		Util.log(this, "Attempting to map port " + port);
		Util.log(this, "Querying device to see if mapping for port " + port + " already exists");

		try {
			if (!d.getSpecificPortMappingEntry(port, "TCP", portMapping)) {
				Util.log(this, "Sending port mapping request");

				if (d.addPortMapping(port, port, localAddress.getHostAddress(), "TCP", "test")) {
					Util.log(this, "Mapping succesful");
				} else {
					Util.log(this, "Mapping failed");
				}
			}
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}

	private GatewayDevice discover() {
		GatewayDiscover discover = new GatewayDiscover();
		try {
			discover.discover();
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		GatewayDevice d = discover.getValidGateway();
		return d;
	}

	@Override
	public String getId() {
		return "UPNPClient";
	}
}
