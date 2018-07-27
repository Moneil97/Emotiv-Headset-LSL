import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class EEG_LSL_GUI_Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private SendOverLSL eeg;
	private JButton btnStart, btnStop;
	private JProgressBar[] progressBars;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary("edk");
					EEG_LSL_GUI_Main frame = new EEG_LSL_GUI_Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EEG_LSL_GUI_Main() {
		setTitle("Emotiv Simulink EEG Importer (Lab-Streaming-Layer)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 600);
		setMinimumSize(new Dimension(500, 350));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		UIManager.put("ProgressBar.selectionBackground", Color.black);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		//contentPane.add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel centerPanel = new JPanel();
		panel_1.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		final JTextArea messagesTextArea = new JTextArea();
		messagesTextArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(messagesTextArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JLabel lblSystemMessages = new JLabel("System Messages:");
		centerPanel.add(lblSystemMessages, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		centerPanel.add(panel, BorderLayout.SOUTH);
		
		JPanel northPanel = new JPanel();
		panel_1.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_5 = new JPanel();
		northPanel.add(panel_5);
		
		JLabel lblSimulinkEegImporter = new JLabel("Emotiv Simulink EEG Importer");
		lblSimulinkEegImporter.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_5.add(lblSimulinkEegImporter);
		
		JPanel panel_7 = new JPanel();
		northPanel.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFrequency = new JLabel("Headset Frequency #:   ");
		panel_7.add(lblFrequency, BorderLayout.WEST);
		
		final JComboBox<String> frequencyInput = new JComboBox<>(new String[]{"128Hz","256Hz"});
		frequencyInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_7.add(frequencyInput, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		panel_1.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				frequencyInput.setEnabled(false);

				if (eeg == null) {
					eeg = new SendOverLSL(messagesTextArea) {
						@Override
						public void stateUpdated(int wireless, int battery, int[] contactQuality) {
							setProgressBar(progressBars[0], wireless);
							setProgressBar(progressBars[1], battery);
							for (int i = 2; i < progressBars.length; i++) 
								setProgressBar(progressBars[i], contactQuality[i-2]);
						}
					};
				}
				
				eeg.start(Integer.parseInt(frequencyInput.getSelectedItem().toString().substring(0,3)));
			}
		});
		southPanel.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				frequencyInput.setEnabled(true);
				
				eeg.stop();
				
				for (JProgressBar bar : progressBars)
					setProgressBar(bar,0);
				
				messagesTextArea.append("EEG Importer Stopped\n\n");
			}
		});
		southPanel.add(btnStop);
		
		JPanel panel_8 = new JPanel();
		contentPane.add(panel_8, BorderLayout.EAST);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_8.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EmptyBorder(0, 5, 0, 0));
		panel_2.add(panel_3, BorderLayout.WEST);
		panel_3.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblSignal = new JLabel("Signal:");
		panel_3.add(lblSignal);
		
		JLabel lblBattery = new JLabel("Battery:");
		panel_3.add(lblBattery);
		
		JLabel lblCms = new JLabel("CMS:");
		panel_3.add(lblCms);
		
		JLabel lblDrl = new JLabel("DRL:");
		panel_3.add(lblDrl);
		
		JLabel lblFp = new JLabel("FP1:");
		panel_3.add(lblFp);
		
		JLabel lblAf = new JLabel("AF3:");
		panel_3.add(lblAf);
		
		JLabel lblF = new JLabel("F7:");
		panel_3.add(lblF);
		
		JLabel lblF_1 = new JLabel("F3:");
		panel_3.add(lblF_1);
		
		JLabel lblFc = new JLabel("FC5:");
		panel_3.add(lblFc);
		
		JLabel lblT = new JLabel("T7:");
		panel_3.add(lblT);
		
		JLabel lblP = new JLabel("P7:");
		panel_3.add(lblP);
		
		JLabel lblPz = new JLabel("Pz/01:");
		panel_3.add(lblPz);
		
		JLabel label = new JLabel("02:");
		panel_3.add(label);
		
		JLabel lblP_1 = new JLabel("P8:");
		panel_3.add(lblP_1);
		
		JLabel lblT_1 = new JLabel("T8:");
		panel_3.add(lblT_1);
		
		JLabel lblFc_1 = new JLabel("FC6:");
		panel_3.add(lblFc_1);
		
		JLabel lblF_2 = new JLabel("F4:");
		panel_3.add(lblF_2);
		
		JLabel lblF_3 = new JLabel("F8:");
		panel_3.add(lblF_3);
		
		JLabel lblAf_1 = new JLabel("AF4:");
		panel_3.add(lblAf_1);
		
		JLabel lblFp_1 = new JLabel("FP2:");
		panel_3.add(lblFp_1);
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 1, 0, 0));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar.setMaximum(4);
		panel_4.add(progressBar);
		
		JProgressBar progressBar_1 = new JProgressBar();
		progressBar_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_1.setMaximum(5);
		panel_4.add(progressBar_1);
		
		JProgressBar progressBar_2 = new JProgressBar();
		progressBar_2.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_2.setMaximum(4);
		panel_4.add(progressBar_2);
		
		JProgressBar progressBar_3 = new JProgressBar();
		progressBar_3.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_3.setMaximum(4);
		panel_4.add(progressBar_3);
		
		JProgressBar progressBar_4 = new JProgressBar();
		progressBar_4.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_4.setMaximum(4);
		panel_4.add(progressBar_4);
		
		JProgressBar progressBar_5 = new JProgressBar();
		progressBar_5.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_5.setMaximum(4);
		panel_4.add(progressBar_5);
		
		JProgressBar progressBar_6 = new JProgressBar();
		progressBar_6.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_6.setMaximum(4);
		panel_4.add(progressBar_6);
		
		JProgressBar progressBar_7 = new JProgressBar();
		progressBar_7.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_7.setMaximum(4);
		panel_4.add(progressBar_7);
		
		JProgressBar progressBar_8 = new JProgressBar();
		progressBar_8.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_8.setMaximum(4);
		panel_4.add(progressBar_8);
		
		JProgressBar progressBar_9 = new JProgressBar();
		progressBar_9.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_9.setMaximum(4);
		panel_4.add(progressBar_9);
		
		JProgressBar progressBar_10 = new JProgressBar();
		progressBar_10.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_10.setMaximum(4);
		panel_4.add(progressBar_10);
		
		JProgressBar progressBar_11 = new JProgressBar();
		progressBar_11.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_11.setMaximum(4);
		panel_4.add(progressBar_11);
		
		JProgressBar progressBar_12 = new JProgressBar();
		progressBar_12.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_12.setMaximum(4);
		panel_4.add(progressBar_12);
		
		JProgressBar progressBar_13 = new JProgressBar();
		progressBar_13.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_13.setMaximum(4);
		panel_4.add(progressBar_13);
		
		JProgressBar progressBar_14 = new JProgressBar();
		progressBar_14.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_14.setMaximum(4);
		panel_4.add(progressBar_14);
		
		JProgressBar progressBar_15 = new JProgressBar();
		progressBar_15.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_15.setMaximum(4);
		panel_4.add(progressBar_15);
		
		JProgressBar progressBar_16 = new JProgressBar();
		progressBar_16.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_16.setMaximum(4);
		panel_4.add(progressBar_16);
		
		JProgressBar progressBar_17 = new JProgressBar();
		progressBar_17.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_17.setMaximum(4);
		panel_4.add(progressBar_17);
		
		JProgressBar progressBar_18 = new JProgressBar();
		progressBar_18.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_18.setMaximum(4);
		panel_4.add(progressBar_18);
		
		JProgressBar progressBar_19 = new JProgressBar();
		progressBar_19.setFont(new Font("Tahoma", Font.BOLD, 13));
		progressBar_19.setMaximum(4);
		panel_4.add(progressBar_19);
		
		
		
		JPanel panel_9 = new JPanel();
		panel_8.add(panel_9, BorderLayout.SOUTH);
		panel_9.setLayout(new BorderLayout(0, 0));
		
		JButton btnSettings = new JButton("Headset Settings");
		btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Please ensure that only one headset is connected the computer.\nAlso ensure that the headset is connected via USB only (no dongle).\nPress OK when you have done this.", "", 1);
				EEG_LSL_GUI_Settings settings = new EEG_LSL_GUI_Settings();
				settings.setDefaultCloseOperation(HIDE_ON_CLOSE);
				settings.addWindowListener(new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						EEG_LSL_GUI_Main.this.setVisible(true);
				    }
				});
				EEG_LSL_GUI_Main.this.setVisible(false);
				settings.setVisible(true);
			}
		});
		panel_9.add(btnSettings);
		
		progressBars = new JProgressBar[]{progressBar, progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBar_5, progressBar_6, 
									   progressBar_7, progressBar_8, progressBar_9, progressBar_10, progressBar_11, progressBar_12, progressBar_13, 
									   progressBar_14, progressBar_15, progressBar_16, progressBar_17, progressBar_18, progressBar_19};
		
		for (JProgressBar p : progressBars) {
			p.setStringPainted(true);
		}
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
		    	if (eeg != null)
		    		eeg.stop();
		    }
		});
		
	}
	
	private void setProgressBar(JProgressBar bar, int val) {
		bar.setValue(val);
		bar.setForeground(selectProgressBarColor(val));
	}

	private Color selectProgressBarColor(int val) {
		switch(val) {
			case 0:
				return Color.black;
			case 1:
				return Color.red;
			case 2:
				return Color.orange;
			case 3:
				return Color.yellow;
			default:
				return Color.green;
		}
	}
}
