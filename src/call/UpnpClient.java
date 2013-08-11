package call;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

public class UpnpClient extends AbstractId implements Runnable {

	private final int[] ports;

	public UpnpClient(int[] ports) {
		this.ports = ports;
	}

	@Override
	public void run() {
		GatewayDevice gateway = discover();
		if (gateway != null) {
			String externalIPAddress;
			try {
				externalIPAddress = gateway.getExternalIPAddress();
			} catch (IOException | SAXException e1) {
				externalIPAddress = "unknown IP";
			}
			Util.log(this, "Gateway device found: " + gateway.getModelName() + " (" + externalIPAddress + ")");
			for (int port : ports)
				portmap(gateway, port);
		}
	}

	private void portmap(GatewayDevice d, int port) {
		String localAddress = d.getLocalAddress().getHostAddress();
		String externalIPAddress;
		try {
			externalIPAddress = d.getExternalIPAddress();
		} catch (IOException | SAXException e1) {
			Util.log(this, "External address: (unknown)");
			return;
		}

		for (int i = 0; i < 10; ++i) {
			int externalport = port + 10 * i;
			String portMappingStr = localAddress + ":" + port + " <-> " + externalIPAddress + ":"
					+ externalport;
			PortMappingEntry portMapping = new PortMappingEntry();
			try {
				if (!d.getSpecificPortMappingEntry(externalport, "TCP", portMapping)) {
					Util.log(this, "Attempting to map port: " + portMappingStr);
					if (d.addPortMapping(externalport, port, localAddress, "TCP", "test")) {
						Util.log(this, "Mapping successful!");
						break;

					} else {
						Util.log(this, "Mapping failed...");
					}
				} else {
					Util.log(this, "Port mapping entry already exists: " + portMappingStr);
					int portMappedLocalPort = portMapping.getInternalPort();
					String portMappedLocalAddress = portMapping.getInternalClient();
					if (!portMappedLocalAddress.equals(localAddress)) {
						Util.log(this, "Error: local IP in port map entry (" + portMappedLocalAddress
								+ ") does not match local IP (" + localAddress + ")!");
					} else if (portMappedLocalPort != port) {
						Util.log(this, "Error: local port in port map entry (" + portMappedLocalPort
								+ ") does not match local port (" + port + ")!");
					} else {
						break;
					}
				}
			} catch (IOException | SAXException e) {
				e.printStackTrace();
			}
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
		return "UpnpClient";
	}
}
