package call;

import java.util.ArrayList;
import java.util.List;

public interface CloseListener extends Activatable {
	void onClose();

	public static class CloseListeners implements CloseListener {
		private List<CloseListener> listeners = new ArrayList<CloseListener>();

		@Override
		public void onClose() {
			for (CloseListener cl : listeners) {
				cl.onClose();
			}
		}

		public void add(CloseListener cl) {
			listeners.add(cl);
		}

		@Override
		public boolean isActive() {
			throw new RuntimeException("Fuck! This should never happen!");
		}

		@Override
		public void close() {}

	}

	public static interface CloseListening extends Activatable {

		CloseListeners getCloseListeners();

	}
}
