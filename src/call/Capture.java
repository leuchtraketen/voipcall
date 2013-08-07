package call;

import java.io.OutputStream;

public interface Capture extends Id {
	OutputStream getCaptureOutputStream();
}
