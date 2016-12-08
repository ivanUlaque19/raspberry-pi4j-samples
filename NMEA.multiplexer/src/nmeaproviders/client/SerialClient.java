package nmeaproviders.client;

import nmea.api.Multiplexer;
import nmea.api.NMEAClient;
import nmea.api.NMEAEvent;
import nmeaproviders.reader.SerialReader;

/**
 * Read NMEA Data from a Serial port
 */
public class SerialClient extends NMEAClient {
	public SerialClient() {
		this(null, null, null);
	}

	public SerialClient(Multiplexer mux) {
		this(null, null, mux);
	}

	public SerialClient(String s, String[] sa) {
		this(s, sa, null);
	}

	public SerialClient(String s, String[] sa, Multiplexer mux) {
		super(s, sa, mux);
		this.verbose = "true".equals(System.getProperty("serial.data.verbose", "false"));
	}

	@Override
	public void dataDetectedEvent(NMEAEvent e) {
		if (verbose)
			System.out.println("Received from Serial:" + e.getContent());
		if (multiplexer != null) {
			multiplexer.onData(e.getContent());
		}
	}

	private static SerialClient nmeaClient = null;

	public static class SerialBean implements ClientBean {
		String cls;
		String type = "serial";
		String port;
		int br;
		boolean verbose;

		public SerialBean(SerialClient instance) {
			cls = instance.getClass().getName();
			port = ((SerialReader) instance.getReader()).getPort();
			br = ((SerialReader) instance.getReader()).getBr();
			verbose = instance.isVerbose();
		}

		@Override
		public String getType() {
			return this.type;
		}

		public String getPort() {
			return port;
		}

		public int getBr() {
			return br;
		}

		@Override
		public boolean getVerbose() {
			return this.verbose;
		}
	}

	@Override
	public Object getBean() {
		return new SerialBean(this);
	}

	public static void main(String[] args) {
		System.out.println("CustomSerialClient invoked with " + args.length + " Parameter(s).");
		for (String s : args)
			System.out.println("CustomSerialClient prm:" + s);

//  String commPort = "/dev/ttyUSB0"; // "COM1";
		String commPort = "/dev/tty.usbserial"; // Mac
		if (args.length > 0)
			commPort = args[0];

		nmeaClient = new SerialClient();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down nicely.");
				nmeaClient.stopDataRead();
			}
		});
//  nmeaClient.setEOS("\n"); // TASK Sure?
		nmeaClient.initClient();
		nmeaClient.setReader(new SerialReader(nmeaClient.getListeners(), commPort, 4800));
		nmeaClient.startWorking();
	}
}