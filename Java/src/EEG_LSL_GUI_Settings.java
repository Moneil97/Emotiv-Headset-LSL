import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class EEG_LSL_GUI_Settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textID;
	private JTextField textMode;
	private JTextField textEEGRate;
	private JTextField textEEGRes;
	private JTextField textMEMSRate;
	private JTextField textMEMSRes;
	
	private EmotivController3 ec;
	private JButton btnApplySettings;
	private JComboBox<String> comboMode, comboEEGRate, comboEEGRes, comboMEMSRate, comboMEMSRes;
	private JTextField[] texts;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary("edk");
					EEG_LSL_GUI_Settings frame = new EEG_LSL_GUI_Settings();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EEG_LSL_GUI_Settings() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 457, 352);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4, BorderLayout.NORTH);
		
		JLabel lblCurrentSettings = new JLabel("Current Settings:");
		lblCurrentSettings.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_4.add(lblCurrentSettings);
		
		JPanel panel_8 = new JPanel();
		panel_2.add(panel_8, BorderLayout.CENTER);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_9 = new JPanel();
		panel_9.setBorder(new EmptyBorder(0, 5, 0, 5));
		panel_8.add(panel_9, BorderLayout.WEST);
		panel_9.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblUserId = new JLabel("Headset ID:");
		lblUserId.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblUserId);
		
		JLabel lblEpocMode = new JLabel("EPOC Mode:");
		lblEpocMode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblEpocMode);
		
		JLabel lblEegRate = new JLabel("EEG Rate:");
		lblEegRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblEegRate);
		
		JLabel lblEegRes = new JLabel("EEG Res:");
		lblEegRes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblEegRes);
		
		JLabel lblMemsRate = new JLabel("MEMS Rate:");
		lblMemsRate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblMemsRate);
		
		JLabel lblMemsRes = new JLabel("MEMS Res:");
		lblMemsRes.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_9.add(lblMemsRes);
		
		JPanel panel_10 = new JPanel();
		panel_8.add(panel_10, BorderLayout.CENTER);
		panel_10.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_11 = new JPanel();
		panel_10.add(panel_11, BorderLayout.WEST);
		panel_11.setLayout(new GridLayout(0, 1, 0, 0));
		
		textID = new JTextField();
		textID.setEditable(false);
		panel_11.add(textID);
		textID.setColumns(10);
		
		textMode = new JTextField();
		textMode.setEditable(false);
		textMode.setColumns(10);
		panel_11.add(textMode);
		
		textEEGRate = new JTextField();
		textEEGRate.setEditable(false);
		textEEGRate.setColumns(10);
		panel_11.add(textEEGRate);
		
		textEEGRes = new JTextField();
		textEEGRes.setEditable(false);
		textEEGRes.setColumns(10);
		panel_11.add(textEEGRes);
		
		textMEMSRate = new JTextField();
		textMEMSRate.setEditable(false);
		textMEMSRate.setColumns(10);
		panel_11.add(textMEMSRate);
		
		textMEMSRes = new JTextField();
		textMEMSRes.setEditable(false);
		textMEMSRes.setColumns(10);
		panel_11.add(textMEMSRes);
		
		texts = new JTextField[]{textID, textMode, textEEGRate, textEEGRes, textMEMSRate, textMEMSRes};
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5, BorderLayout.NORTH);
		
		JLabel lblChangeSettings = new JLabel("Change Settings:");
		panel_5.add(lblChangeSettings);
		lblChangeSettings.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JPanel panel_7 = new JPanel();
		panel_3.add(panel_7, BorderLayout.SOUTH);
		
		btnApplySettings = new JButton("Apply Settings");
		btnApplySettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				ec.changeSettings(Settings.stringToEnum((String)comboMode.getSelectedItem()), Settings.stringToEnum((String)comboEEGRate.getSelectedItem()),
								  Settings.stringToEnum((String)comboEEGRes.getSelectedItem()), Settings.stringToEnum((String)comboMEMSRate.getSelectedItem()),
								  Settings.stringToEnum((String)comboMEMSRes.getSelectedItem()));
				
				//ec.changeSettings(Settings.EPOC_PLUS, Settings.EEG_256Hz, Settings.EEG_16Bit, Settings.MEMS_64Hz, Settings.MEMS_16Bit);
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				updateSettingsText();
			}
		});
		panel_7.add(btnApplySettings);
		
		JPanel panel_12 = new JPanel();
		panel_3.add(panel_12, BorderLayout.CENTER);
		panel_12.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new EmptyBorder(0, 5, 0, 5));
		panel_12.add(panel_13, BorderLayout.WEST);
		panel_13.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel label_1 = new JLabel("EPOC Mode:");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_13.add(label_1);
		
		JLabel label_2 = new JLabel("EEG Rate:");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_13.add(label_2);
		
		JLabel label_3 = new JLabel("EEG Res:");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_13.add(label_3);
		
		JLabel label_4 = new JLabel("MEMS Rate:");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_13.add(label_4);
		
		JLabel label_5 = new JLabel("MEMS Res:");
		label_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_13.add(label_5);
		
		JPanel panel_14 = new JPanel();
		panel_12.add(panel_14, BorderLayout.CENTER);
		panel_14.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_15 = new JPanel();
		panel_14.add(panel_15, BorderLayout.WEST);
		panel_15.setLayout(new GridLayout(0, 1, 0, 0));
		
		comboMode = new JComboBox<String>();
		comboMode.setModel(new DefaultComboBoxModel<String>(new String[] {"Epoc", "Epoc+"}));
		panel_15.add(comboMode);
		
		comboEEGRate = new JComboBox<String>();
		comboEEGRate.setModel(new DefaultComboBoxModel<String>(new String[] {"128Hz", "256Hz"}));
		panel_15.add(comboEEGRate);
		
		comboEEGRes = new JComboBox<String>();
		comboEEGRes.setModel(new DefaultComboBoxModel<String>(new String[] {"14bit", "16bit"}));
		panel_15.add(comboEEGRes);
		
		comboMEMSRate = new JComboBox<String>();
		comboMEMSRate.setModel(new DefaultComboBoxModel<String>(new String[] {"0Hz", "32Hz", "64Hz", "128Hz "}));
		panel_15.add(comboMEMSRate);
		
		comboMEMSRes = new JComboBox<String>();
		comboMEMSRes.setModel(new DefaultComboBoxModel<String>(new String[] {"12bit ", "14bit ", "16bit "}));
		panel_15.add(comboMEMSRes);
		
		ec = new EmotivController3() {
			@Override
			public void userAdded(int userID) {
				updateSettingsText();
			}
		};
		ec.startStateHandler();
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
		    	ec.disconnect();
		    }
		});
		
	}
	
	private void updateSettingsText() {
		
		System.out.println(Arrays.toString(ec.getHeadsetSettings()));
		
		int i = 0;
		for (String s : ec.getHeadsetSettings()) {
			System.out.println(s);
			texts[i++].setText(s);
		}
		System.out.println();
		btnApplySettings.setEnabled(true);
	}

}
