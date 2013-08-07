package call;

import java.awt.Color;

public interface MessageOutput {

	void append(String str, Color c);

	void close();

}
