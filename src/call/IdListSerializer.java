package call;

import java.util.Collection;

public interface IdListSerializer<A extends Id> {
	public String serializeAll(Collection<? extends A> value);

	public Collection<A> deserializeAll(String serialized);

	public String getConfigPrefix();
}
