import java.util.HashMap;

//! Set headset setting for EPOC+ headset
/*!
 *  \param userId       - user ID
 *  \param EPOCmode     - If 0, then EPOC mode is EPOC.
 *                      - If 1, then EPOC mode is EPOC+.
 *  \param eegRate      - If 0, then EEG sample rate is 128Hz.
 *                      - If 1, then EEG sample rate is 256Hz.
 *  \param eegRes       - If 0, then EEG Resolution is 14bit.
 *                      - If 1, then EEG Resolution is 16bit.
 *  \param memsRate     - If 0, then MEMS sample rate is OFF.
 *                      - If 1, then MEMS sample rate is 32Hz.
 *                      - If 2, then MEMS sample rate is 64Hz.
 *                      - If 3, then MEMS sample rate is 128Hz.
 *  \param memsRes      - If 0, then MEMS Resolution is 12bit.
 *                      - If 1, then MEMS Resolution is 14bit.
 *                      - If 2, then MEMS Resolution is 16bit.
 *  \return EDK_ERROR_CODE 
 *                      - EDK_ERROR_CODE = EDK_OK if the command successful
*/

public enum Settings {
	
	EPOC(0, "Epoc"), EPOC_PLUS(1, "Epoc+"),
	EEG_128Hz(0, "128Hz"), EEG_256Hz(1, "256Hz"),
	EEG_14Bit(0, "14bit"), EEG_16Bit(1, "16bit"),
	MEMS_OFF(0, "0Hz"), MEMS_32Hz(1, "32Hz"), MEMS_64Hz(2, "64Hz"), MEMS_128Hz(3, "128Hz "),//Note the space at the end to avoid conflicts with EEG_Rate
	MEMS_12Bit(0, "12bit "), MEMS_14Bit(1, "14bit "), MEMS_16Bit(2, "16bit "); //Note the spaces at the end to avoid conflicts with EEG_Res
	
	int val;
	String string;
	private static HashMap<String, Settings> map = new HashMap<>();
	
	Settings(int val, String s) {
		this.val = val;
		this.string = s;
	}
	
	static {
		for (Settings s : Settings.values())
			map.put(s.string, s);
	}
	
	static Settings stringToEnum(String s) {
		return map.get(s);
	}
}
