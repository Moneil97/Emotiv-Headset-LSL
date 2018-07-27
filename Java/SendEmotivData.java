package examples;
import edu.ucsd.sccn.LSL;
import emotiv.EmotivController3;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public class SendEmotivData {
	
	final static int channels = 18, sampleRate = 128, TIMESTAMP = 0;
	
    public static void main(String[] args) throws IOException, InterruptedException  {
        System.out.println("Creating a new StreamInfo...");
        LSL.StreamInfo info = new LSL.StreamInfo("Emotiv","EEG",channels,sampleRate,LSL.ChannelFormat.double64,"emotivHeadset1");

        System.out.println("Creating an outlet...");
        LSL.StreamOutlet outlet = new LSL.StreamOutlet(info);
        
        ////
        System.loadLibrary("edk");
        EmotivController3 ec = new EmotivController3();
        ec.startStateHandler();
        /////
        
        System.out.println("Sending data...");
        //float[] sample = new float[channels];
        int last = -1;
        while (true) {
			List<double[]> samples = ec.getAvailableSamples(true);
			if (samples != null) {
				
				for (int s = 0; s < samples.size();  s++) {
					double[] sample = samples.get(s);
					//System.out.println(Arrays.toString(sample));
					
					//Check for skipped packets
					if (last == -1)
						;
					else if (last != (int)sample[TIMESTAMP]-1 && !(last == sampleRate-1 && (int)sample[TIMESTAMP] == 0))
						System.out.println("skipped from " + last + " to " + sample[TIMESTAMP]);
					last = (int) sample[TIMESTAMP];
					
		            outlet.push_sample(sample);
				}
			}
        	
			Thread.sleep(1);
        }
        
        //ec.disconnect();
        //outlet.close();
        //info.destroy();
    }
}