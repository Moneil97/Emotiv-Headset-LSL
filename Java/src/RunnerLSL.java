import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class RunnerLSL {
	public static void main(String[] args) throws IOException, URISyntaxException {
		String libs = new File(RunnerLSL.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\EmotivLibs";
		
		if (new File(libs).exists()) {
			if (new File(libs + "\\edk.dll").exists())
				Runtime.getRuntime().exec("java -Djava.library.path=\"" + libs + "\" -jar EEGImporterLSL.jar");
			else
				JOptionPane.showMessageDialog(null, "Could not find file in EmotivLibs: edk.dll");
		}
		else
			JOptionPane.showMessageDialog(null, "Could not find folder: EmotivLibs, please make sure it is in the same folder as this jar file");
		
	}
}
