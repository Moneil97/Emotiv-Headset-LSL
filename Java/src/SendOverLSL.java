import java.util.List;

import javax.swing.JTextArea;

import edu.ucsd.sccn.LSL;

public class SendOverLSL {
	
	final static int channels = 18, CLOCK = 0;
	private Thread t;
	private JTextArea messagesTextArea;
	
	public SendOverLSL(JTextArea messagesTextArea) {
		this.messagesTextArea = messagesTextArea;
	}
	
	//override to get state data
	public void stateUpdated(int wireless, int battery, int[] contactQuality) {}
	
	public void start(int frequency) {
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				messagesTextArea.append("Loading Emotiv Controller...\n");
		        System.loadLibrary("edk");
		        EmotivController3 ec = new EmotivController3() {
		        	@Override
					public void stateUpdated(int wireless, int battery, int[] contactQuality) {
						//System.out.println(wireless + " " + battery + " " + Arrays.toString(contactQuality));
		        		SendOverLSL.this.stateUpdated(wireless, battery, contactQuality);
					}
		        };
		        ec.startStateHandler();
		        	
				messagesTextArea.append("Creating new StreamInfo @ " + frequency + "Hz...\n");
		        LSL.StreamInfo info = new LSL.StreamInfo("Emotiv","EEG",channels, frequency, LSL.ChannelFormat.double64,"emotivHeadset1");

		        messagesTextArea.append("Creating an outlet...\n");
		        LSL.StreamOutlet outlet = new LSL.StreamOutlet(info);
				
				messagesTextArea.append("Sending data...\n");
				int lastClock = -1;
		        while (true) {
					List<double[]> samples = ec.getAvailableSamples(true);
					if (samples != null) {
						
						for (int s = 0; s < samples.size();  s++) {
							double[] sample = samples.get(s);
							//System.out.println(Arrays.toString(sample));
							
							//Check for skipped packets
							if (lastClock == -1)
								;
							else if (lastClock != (int)sample[CLOCK]-1 && !(lastClock == frequency-1 && (int)sample[CLOCK] == 0))
								messagesTextArea.append("Lost Packets: Skipped from " + lastClock + " to " + sample[CLOCK] + "\n");
							lastClock = (int) sample[CLOCK];
							
				            outlet.push_sample(sample);
						}
					}
					
					if (t.isInterrupted()) {
						break;
					}
		        	
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						break;
					}
		        }
		        
		        //Loop end
		        ec.disconnect();
		        outlet.close();
		        info.destroy();
			}
		});
		
		t.start();
	}
	
	public void stop() {
		t.interrupt();
	}

	
}