package call;

import java.util.Collection;

public interface IdSerializer<A extends Id> {
	public String serialize(A deserialized);

	public A deserialize(String serialized) throws UnknownDefaultValueException;

	public String serializeAll(Collection<A> deserialized);

	public Collection<A> deserializeAll(String serialized) throws UnknownDefaultValueException;

	public String getConfigPrefix();

	public A getDefaultValue() throws UnknownDefaultValueException;
}
