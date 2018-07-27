import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.sun.jna.Platform;

public class EEG_LSL_GUI_Login extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userNameInput;
	private JPasswordField passwordInput;
	
	private String userName, password;
	private boolean loggedIn = false;
	private JCheckBox saveLoginCheckBox;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary("edk");
					System.loadLibrary(Platform.is64Bit() ? "liblsl64" : "liblsl32");
					EEG_LSL_GUI_Login frame = new EEG_LSL_GUI_Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EEG_LSL_GUI_Login() {
		setTitle("Simulink EEG Importer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 405);
		setMinimumSize(new Dimension(420, 300));
		setLocationRelativeTo(null);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
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
		
		JPanel northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_5 = new JPanel();
		northPanel.add(panel_5);
		
		JLabel lblSimulinkEegImporter = new JLabel("Emotiv Simulink EEG Importer (UDP)");
		lblSimulinkEegImporter.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_5.add(lblSimulinkEegImporter);
		
		JPanel panel_2 = new JPanel();
		northPanel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblUsername = new JLabel("Emotiv Username:   ");
		panel_2.add(lblUsername, BorderLayout.WEST);
		
		userNameInput = new JTextField();
		userNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_2.add(userNameInput, BorderLayout.CENTER);
		userNameInput.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		northPanel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPassword = new JLabel("Emotiv Password:    ");
		panel_3.add(lblPassword, BorderLayout.WEST);
		
		passwordInput = new JPasswordField();
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_3.add(passwordInput, BorderLayout.CENTER);
		
		final JCheckBox hideCheckBox = new JCheckBox("Hide");
		hideCheckBox.setToolTipText("Hide your password input");
		hideCheckBox.setSelected(true);
		hideCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hideCheckBox.isSelected())
					passwordInput.setEchoChar('*');
				else 
					passwordInput.setEchoChar((char) 0);
			}
		});
		panel_3.add(hideCheckBox, BorderLayout.EAST);
		
		JPanel panel_4 = new JPanel();
		northPanel.add(panel_4);
		
		JLabel lblSessionsToAdd = new JLabel("Sessions to add:");
		panel_4.add(lblSessionsToAdd);
		
		final JSpinner spinnerSessions = new JSpinner();
		spinnerSessions.setPreferredSize(new Dimension(50, 20));
		spinnerSessions.setModel(new SpinnerNumberModel(1, 1, null, 1));
		panel_4.add(spinnerSessions);
		
		saveLoginCheckBox = new JCheckBox("Save Login");
		saveLoginCheckBox.setToolTipText("WARNING: username and password will not be encrypted, do not use on a public machine");
		panel_4.add(saveLoginCheckBox);
		
		JButton deleteSavedLoginButton = new JButton("Delete Saved Login");
		deleteSavedLoginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteLogin();
			}
		});
		panel_4.add(deleteSavedLoginButton);
		
		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		
		final JButton btnLogIn = new JButton("Log In");
		btnLogIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!loggedIn) {
					userName = userNameInput.getText();
					password = new String(passwordInput.getPassword());
					
					EmotivLicenseActivator ela = new EmotivLicenseActivator(userNameInput.getText(), new String(passwordInput.getPassword()));
					
					String loginMessage = ela.login() + "\n";
					messagesTextArea.append(loginMessage);
					messagesTextArea.append(ela.getSessionsInfo() + "\n");
					messagesTextArea.append(ela.addSessions((int)spinnerSessions.getValue()) + "\n");
					messagesTextArea.append(ela.getLicenseInfo() + "\n\n\n");
					ela.disconnect();
					
					if (saveLoginCheckBox.isSelected()) {
						saveLogin();
					}
					else {
						deleteLogin();
					}
					
					if (loginMessage.startsWith("Logged in as")) {
						loggedIn = true;
						btnLogIn.setText("continue");
					}
				}
				else {
					openMainWindow();
				}
			}
		});
		southPanel.add(btnLogIn);
		
		JButton btnSkipLogin = new JButton("Skip Login");
		btnSkipLogin.setToolTipText("Skips login and will not add any new sessions");
		btnSkipLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openMainWindow();
			}
		});
		southPanel.add(btnSkipLogin);
		
		readLogin();
	}
	
	private void deleteLogin() {
		if (new File("EmotivLogin").exists())
			new File("EmotivLogin").delete();
	}
	
	private void saveLogin() {
		try {
			byte data[] = (userName + "\n" + password + "\n").getBytes();
			for (int i = 0; i < data.length; i++)
				data[i] += 128;
			Path file = Paths.get("EmotivLogin");
			Files.write(file, data);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void readLogin() {
		try {
			if (new File("EmotivLogin").exists()) {
				byte[] data = Files.readAllBytes(Paths.get("EmotivLogin"));
				for (int i = 0; i < data.length; i++)
					data[i] -= 128;
				
				String in = new String(data);
				userName = in.substring(0, in.indexOf("\n")).trim();
				password = in.substring(in.indexOf("\n")).trim();
				
				userNameInput.setText(userName);
				passwordInput.setText(password);
				saveLoginCheckBox.setSelected(true);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void openMainWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EEG_LSL_GUI_Login.this.setVisible(false);
					EEG_LSL_GUI_Main frame = new EEG_LSL_GUI_Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
