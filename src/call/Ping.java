package call;

public class Ping extends AbstractId {

	private final long average;
	private final long best;
	private final long worst;
	private final long uptime;

	public Ping(long average, long best, long worst, long uptime) {
		this.average = average;
		this.best = best;
		this.worst = worst;
		this.uptime = uptime;
	}

	public long getAverage() {
		return average;
	}

	public long getBest() {
		return best;
	}

	public long getWorst() {
		return worst;
	}

	public long getUptime() {
		return uptime;
	}

	@Override
	public String getId() {
		return "Ping<" + average + "," + best + "," + worst + ">";
	}

	@Override
	public String toString() {
		// return average + "ms(best=" + best + "ms/worst=" + worst + "ms)";
		long plusminus = (average - best) > (worst - average) ? (average - best) : (worst - average);
		return average + "ms \u00b1 " + plusminus + "ms";
	}
}
