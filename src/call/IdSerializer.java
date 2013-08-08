package call;

public interface IdSerializer<A extends Id> {
	public String serialize(A deserialized);

	public A deserialize(String serialized) throws UnknownDefaultValueException;

	public String getConfigPrefix();

	public A getDefaultValue() throws UnknownDefaultValueException;
}
