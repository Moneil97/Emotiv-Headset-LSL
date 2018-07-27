import java.util.ArrayList;
import java.util.List;

import com.emotiv.Iedk.Edk;
import com.emotiv.Iedk.EdkErrorCode;
import com.emotiv.Iedk.EmoState;
import com.emotiv.Iedk.IEegData;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EmotivController3 {
	
	private IntByReference epocModeRef = new IntByReference(0), eegRateRef = new IntByReference(0), eegResRef = new IntByReference(0),
		       			   memsRateRef = new IntByReference(0), memsResRef = new IntByReference(0);
	private Pointer eEvent = Edk.INSTANCE.IEE_EmoEngineEventCreate();
	private Pointer eState = Edk.INSTANCE.IEE_EmoStateCreate();
	private IntByReference nSamplesTaken = new IntByReference(0);
	private IntByReference userID = new IntByReference(0);
	private int state = 0;
	private boolean readytocollect = false;
	private Pointer hData;
	private float secs = 1f;
	private IntByReference batteryLevel = new IntByReference(0), maxBatteryLevel = new IntByReference(0);
	private Thread t;
	
	public EmotivController3() {

		if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
			System.out.println("Emotiv Engine start up failed.");
			return;
		}
		System.out.println("Emotiv Engine started up.");
		hData = IEegData.INSTANCE.IEE_DataCreate();
		IEegData.INSTANCE.IEE_DataSetBufferSizeInSec(secs);
		System.out.println("Buffer size in secs: " + secs);
		System.out.println("Start receiving EEG Data!");
	}
	
	//skipSample1and2==true will return a List<double[18]> //use to more closely match original EPOC
	//skipSample1and2==false will return a List<double[20]>
	public List<double[]> getAvailableSamples(boolean skipSample1and2) {
		
		if (readytocollect) {
			
			List<double[]> samples = new ArrayList<double[]>();
			IEegData.INSTANCE.IEE_DataUpdateHandle(userID.getValue(), hData);
			IEegData.INSTANCE.IEE_DataGetNumberOfSample(hData, nSamplesTaken);
	
			if (nSamplesTaken != null && nSamplesTaken.getValue() != 0) {
	
				double[] data = new double[nSamplesTaken.getValue()];
				
				
				if (skipSample1and2) {
					for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) 
						samples.add(new double[18]);
					
					IEegData.INSTANCE.IEE_DataGet(hData, 0, data, nSamplesTaken.getValue());
					for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) 
						samples.get(sampleIdx)[0] = data[sampleIdx];
					
					for (int i = 3; i < 20; i++) {
						IEegData.INSTANCE.IEE_DataGet(hData, i, data, nSamplesTaken.getValue());
						for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) 
							samples.get(sampleIdx)[i-2] = data[sampleIdx];
					}
				}
				else {
					for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) 
						samples.add(new double[20]);
					
					for (int i = 0; i < 20; i++) {
						IEegData.INSTANCE.IEE_DataGet(hData, i, data, nSamplesTaken.getValue());
						for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) 
							samples.get(sampleIdx)[i] = data[sampleIdx];
					}
				}
				
				
				return samples;
			}
		}
		return null;
	}
	
	//Prints Headset settings to system.out
	
	public String[] getHeadsetSettings(){
		if (userID.getValue() == 0) 
			return null;
		else {
			Edk.INSTANCE.IEE_GetHeadsetSettings(userID.getValue(), epocModeRef, eegRateRef, eegResRef, memsRateRef, memsResRef);
			
			String[] out = new String[6];
			
			out[0] = String.valueOf(userID.getValue());
			out[1] = epocModeRef.getValue() == 0 ? "EPOC" : "EPOC+";
			out[2] = eegRateRef.getValue() == 0 ? "128Hz" : "256Hz";
			out[3] = eegResRef.getValue() == 0 ? "14bit" : "16bit";

			switch(memsRateRef.getValue()) {
				case 0:
					out[4] = "0Hz";
					break;
				case 1:
					out[4] = "32Hz";
					break;
				case 2:
					out[4] = "64Hz";
					break;
				case 3:
					out[4] = "128Hz";
					break;
			}
			switch(memsResRef.getValue()) {
				case 0:
					out[5] = "12bit";
					break;
				case 1:
					out[5] = "14bit";
					break;
				case 2:
					out[5] = "16bit";
					break;
			}
			return out;
		}
	}
	
	//Headset must be plugged in via USB
	public void changeSettings(Settings EPOC_MODE, Settings EEG_RATE, Settings EEG_RES, Settings MEMS_RATE, Settings MEMS_RES) {
		if (userID.getValue() == 0)
			System.err.println("no headset connected");
		else {
			System.out.println("new settings: " + userID.getValue() + " , " + EPOC_MODE.val + " , " + EEG_RATE.val + " , " +EEG_RES.val + " , " +MEMS_RATE.val + " , " +MEMS_RES.val);
			Edk.INSTANCE.IEE_SetHeadsetSettings(userID.getValue(), EPOC_MODE.val, EEG_RATE.val, EEG_RES.val, MEMS_RATE.val, MEMS_RES.val);
		}
	}
	
	public void startStateHandler() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					state = Edk.INSTANCE.IEE_EngineGetNextEvent(eEvent);
					
					// New event needs to be handled
					if (state == EdkErrorCode.EDK_OK.ToInt()) {
						int eventType = Edk.INSTANCE.IEE_EmoEngineEventGetType(eEvent);
						Edk.INSTANCE.IEE_EmoEngineEventGetUserId(eEvent, userID);
				
						if (eventType == Edk.IEE_Event_t.IEE_UserAdded.ToInt()) {
							if (userID != null) {
								System.out.println("User " + userID.getValue() + " added");
								IEegData.INSTANCE.IEE_DataAcquisitionEnable(userID.getValue(), true);
								readytocollect = true;
								userAdded(userID.getValue());
							}
						}
						else if (eventType == Edk.IEE_Event_t.IEE_UserRemoved.ToInt()) {
							//System.out.println("User " + userID.getValue() + " removed");
							//userID = null;
						}
						else if (eventType == Edk.IEE_Event_t.IEE_EmoStateUpdated.ToInt()) {
							Edk.INSTANCE.IEE_EmoEngineEventGetEmoState(eEvent, eState);
							EmoState.INSTANCE.IS_GetBatteryChargeLevel(eState, batteryLevel, maxBatteryLevel);
							
							int[] contactQuality = new int[18];
							for (int i = 0; i < 18; i++)
								contactQuality[i] = EmoState.INSTANCE.IS_GetContactQuality(eState, i);
							
							stateUpdated(EmoState.INSTANCE.IS_GetWirelessSignalStatus(eState), batteryLevel.getValue(), contactQuality);
						}
					}
					else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
						System.out.println("Internal error in Emotiv Engine!");
						break;
					}
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
					
					if (t.isInterrupted()) {
						break;
					}
				}
			}
		});
		t.start();
	}
	
	//override this method to get new state info when it is available
	public void stateUpdated(int wireless, int battery, int[] contactQuality) {}
	public void userAdded(int userID) {}
	
	public double getBattery() {
		return ((double)batteryLevel.getValue())/maxBatteryLevel.getValue();
	}
	
	public void stopHandlerThread() {
		t.interrupt();
	}
	
	public void disconnect() {
		t.interrupt();
		Edk.INSTANCE.IEE_EngineDisconnect();
		Edk.INSTANCE.IEE_EmoStateFree(eState);
		Edk.INSTANCE.IEE_EmoEngineEventFree(eEvent);
	}
	
	public static void main(String[] args) {
		System.loadLibrary("edk");
		EmotivController3 ec = new EmotivController3();
		
		//int last = 0;
		
		while (true) {
		//for (int a = 0; a < 10000; a++) {
			List<double[]> samples = ec.getAvailableSamples(false);
			if (samples != null) {
				for (int i = 0; i < samples.size(); i++) {
					for (int j=0; j < 20; j++) {
						//System.out.print(samples.get(i)[j] + " ");
					}
					//System.out.println();
					
//					if (last != (int)samples.get(i)[0]-1 && !(last == 255 && (int)samples.get(i)[0] == 0))
//						System.out.println("skipped from " + last + " to " + samples.get(i)[0]);
//					
//					last = (int) samples.get(i)[0];
				}
			}
		}
		
//		ec.printHeadsetSettings();
//		ec.changeSettings(Settings.EPOC_PLUS, Settings.EEG_256Hz, Settings.EEG_16Bit, Settings.MEMS_64Hz, Settings.MEMS_16Bit);
//		ec.printHeadsetSettings();
//		
//		ec.disconnect();
	}

}
