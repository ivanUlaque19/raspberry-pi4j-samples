package nmeaproviders.client;

import nmea.api.Multiplexer;
import nmea.api.NMEAClient;
import nmea.api.NMEAEvent;
import nmeaproviders.reader.HTU21DFReader;

/**
 * Read a file containing logged data
 */
public class HTU21DFClient extends NMEAClient {
	public HTU21DFClient() {
		this(null, null, null);
	}

	public HTU21DFClient(Multiplexer mux) {
		this(null, null, mux);
	}

	public HTU21DFClient(String s, String[] sa) {
		this(s, sa, null);
	}

	public HTU21DFClient(String s, String[] sa, Multiplexer mux) {
		super(s, sa, mux);
		this.verbose = "true".equals(System.getProperty("htu21df.data.verbose", "false"));
	}

	@Override
	public void dataDetectedEvent(NMEAEvent e) {
		if (verbose)
			System.out.println("Received from HTU21DF:" + e.getContent());
		if (multiplexer != null) {
			multiplexer.onData(e.getContent());
		}
	}

	private static HTU21DFClient nmeaClient = null;

	public static class HTU21DFBean implements ClientBean {
		String cls;
		String type = "hut21df";
		boolean verbose;

		public HTU21DFBean(HTU21DFClient instance) {
			cls = instance.getClass().getName();
			verbose = instance.isVerbose();
		}

		@Override
		public String getType() {
			return this.type;
		}

		@Override
		public boolean getVerbose() {
			return this.verbose;
		}
	}

	@Override
	public Object getBean() {
		return new HTU21DFBean(this);
	}

	public static void main(String[] args) {
		System.out.println("HTU21DFClient invoked with " + args.length + " Parameter(s).");
		for (String s : args)
			System.out.println("HTU21DFClient prm:" + s);

		nmeaClient = new HTU21DFClient();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down nicely.");
				nmeaClient.stopDataRead();
			}
		});

//  nmeaClient.setEOS("\n"); // TASK Sure?
		nmeaClient.initClient();
		nmeaClient.setReader(new HTU21DFReader(nmeaClient.getListeners()));
		nmeaClient.startWorking();
	}
}