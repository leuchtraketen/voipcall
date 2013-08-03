package call;

public abstract class AbstractId implements Id {

	@Override
	public abstract String getId();

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return hashCode() == obj.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return getId();
	}
}
