package call;

import java.util.Map;

public interface IdMapSerializer<A extends Id, B> {
	public String serializeMap(Map<? extends A, ? extends B> value);

	public Map<A, B> deserializeMap(String serialized);

	public String getConfigPrefix();
}
