import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class Login {

	private JFrame frame;
	private JTextField userText;
	private JTextField urlText;
	private JTextField table1Text;
	private JTextField table2Text;
	private JPasswordField passwordField;
	private JRadioButton dumasButton;
	private Connection conn;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Login");
		JTabbedPane loginTab = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(loginTab);
		
		JPanel algorithmPanel = new JPanel();
		loginTab.addTab("Algorithm", null, algorithmPanel, null);
		algorithmPanel.setLayout(null);
		
		JLabel integrationLabel = new JLabel("Integration Method");
		integrationLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		integrationLabel.setBounds(10, 11, 170, 25);
		algorithmPanel.add(integrationLabel);
		
		dumasButton = new JRadioButton("DUMAS");
		dumasButton.setSelected(true);
		dumasButton.setBounds(20, 43, 109, 23);
		algorithmPanel.add(dumasButton);
		
		ButtonGroup group = new ButtonGroup();
		group.add(dumasButton);

		
		JPanel dataSourcePanel = new JPanel();
		loginTab.addTab("Data Source", null, dataSourcePanel, null);
		SpringLayout sl_dataSourcePanel = new SpringLayout();
		dataSourcePanel.setLayout(sl_dataSourcePanel);
		
		JLabel URLLabel = new JLabel("URL:");
		URLLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		dataSourcePanel.add(URLLabel);
		
		urlText = new JTextField();
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, URLLabel, 3, SpringLayout.NORTH, urlText);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, URLLabel, -6, SpringLayout.WEST, urlText);
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, urlText, 23, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, urlText, 86, SpringLayout.WEST, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, urlText, 419, SpringLayout.WEST, dataSourcePanel);
		urlText.setText("jdbc:postgresql://localhost:5432/DB");
		dataSourcePanel.add(urlText);
		urlText.setColumns(10);
		
		JLabel userLabel = new JLabel("User:");
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, userLabel, 0, SpringLayout.EAST, URLLabel);
		dataSourcePanel.add(userLabel);
		
		userText = new JTextField();
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, userLabel, 3, SpringLayout.NORTH, userText);
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, userText, 49, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, userText, 0, SpringLayout.WEST, urlText);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, userText, -10, SpringLayout.EAST, dataSourcePanel);
		userText.setText("postgres");
		dataSourcePanel.add(userText);
		userText.setColumns(10);
		
		JLabel passwordLabel = new JLabel("Password:");
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, passwordLabel, 78, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, passwordLabel, 20, SpringLayout.WEST, dataSourcePanel);
		dataSourcePanel.add(passwordLabel);
		
		passwordField = new JPasswordField();
		passwordField.setText("28408483");;
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, passwordField, 75, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, passwordField, 6, SpringLayout.EAST, passwordLabel);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, passwordField, -10, SpringLayout.EAST, dataSourcePanel);
		dataSourcePanel.add(passwordField);
		
		JLabel table1Label = new JLabel("Table 1:");
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, table1Label, 127, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, table1Label, 31, SpringLayout.WEST, dataSourcePanel);
		dataSourcePanel.add(table1Label);
		
		table1Text = new JTextField("sale1");
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, table1Text, 121, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, table1Text, 86, SpringLayout.WEST, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, table1Text, 419, SpringLayout.WEST, dataSourcePanel);
		dataSourcePanel.add(table1Text);
		table1Text.setColumns(10);
		
		JLabel table2Label = new JLabel("Table 2:");
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, table2Label, 150, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, table2Label, 31, SpringLayout.WEST, dataSourcePanel);
		dataSourcePanel.add(table2Label);
		
		table2Text = new JTextField("sale2");
		sl_dataSourcePanel.putConstraint(SpringLayout.NORTH, table2Text, 147, SpringLayout.NORTH, dataSourcePanel);
		sl_dataSourcePanel.putConstraint(SpringLayout.WEST, table2Text, 0, SpringLayout.WEST, urlText);
		sl_dataSourcePanel.putConstraint(SpringLayout.EAST, table2Text, 419, SpringLayout.WEST, dataSourcePanel);
		dataSourcePanel.add(table2Text);
		table2Text.setColumns(10);
		
		JPanel varPanel = new JPanel();
		loginTab.addTab("Variables", null, varPanel, null);
		SpringLayout sl_varPanel = new SpringLayout();
		varPanel.setLayout(sl_varPanel);
		
		JLabel lblDuplicates = new JLabel("Token Threshold:");
		sl_varPanel.putConstraint(SpringLayout.NORTH, lblDuplicates, 28, SpringLayout.NORTH, varPanel);
		sl_varPanel.putConstraint(SpringLayout.WEST, lblDuplicates, 55, SpringLayout.WEST, varPanel);
		varPanel.add(lblDuplicates);
		
		JSpinner tokenThresJsp = new JSpinner();
		sl_varPanel.putConstraint(SpringLayout.WEST, tokenThresJsp, 162, SpringLayout.WEST, varPanel);
		sl_varPanel.putConstraint(SpringLayout.EAST, lblDuplicates, -6, SpringLayout.WEST, tokenThresJsp);
		sl_varPanel.putConstraint(SpringLayout.NORTH, tokenThresJsp, -3, SpringLayout.NORTH, lblDuplicates);
		sl_varPanel.putConstraint(SpringLayout.EAST, tokenThresJsp, -229, SpringLayout.EAST, varPanel);
		tokenThresJsp.setModel(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
		((JSpinner.DefaultEditor) tokenThresJsp.getEditor()).getTextField().setEditable(false);
		varPanel.add(tokenThresJsp);
		
		JLabel numDupLabel = new JLabel("Number of Duplicates:");
		sl_varPanel.putConstraint(SpringLayout.NORTH, numDupLabel, 19, SpringLayout.SOUTH, lblDuplicates);
		sl_varPanel.putConstraint(SpringLayout.WEST, numDupLabel, 30, SpringLayout.WEST, varPanel);
		sl_varPanel.putConstraint(SpringLayout.EAST, numDupLabel, -273, SpringLayout.EAST, varPanel);
		varPanel.add(numDupLabel);
		
		JSpinner numDupJsp = new JSpinner();
		sl_varPanel.putConstraint(SpringLayout.NORTH, numDupJsp, 13, SpringLayout.SOUTH, tokenThresJsp);
		sl_varPanel.putConstraint(SpringLayout.WEST, numDupJsp, 0, SpringLayout.WEST, tokenThresJsp);
		sl_varPanel.putConstraint(SpringLayout.EAST, numDupJsp, -229, SpringLayout.EAST, varPanel);
		numDupJsp.setModel(new SpinnerNumberModel(new Integer(3), new Integer(3), null, new Integer(1)));
		((JSpinner.DefaultEditor) numDupJsp.getEditor()).getTextField().setEditable(false);
		varPanel.add(numDupJsp);
		
		JLabel duplicateThresLabel = new JLabel("Duplicate Threshold:");
		sl_varPanel.putConstraint(SpringLayout.NORTH, duplicateThresLabel, 16, SpringLayout.SOUTH, numDupLabel);
		sl_varPanel.putConstraint(SpringLayout.WEST, duplicateThresLabel, 40, SpringLayout.WEST, varPanel);
		sl_varPanel.putConstraint(SpringLayout.EAST, duplicateThresLabel, -273, SpringLayout.EAST, varPanel);
		varPanel.add(duplicateThresLabel);
		
		JSpinner dupThresJsp = new JSpinner();
		sl_varPanel.putConstraint(SpringLayout.NORTH, dupThresJsp, -3, SpringLayout.NORTH, duplicateThresLabel);
		sl_varPanel.putConstraint(SpringLayout.WEST, dupThresJsp, 162, SpringLayout.WEST, varPanel);
		sl_varPanel.putConstraint(SpringLayout.EAST, dupThresJsp, 0, SpringLayout.EAST, tokenThresJsp);
		dupThresJsp.setModel(new SpinnerNumberModel(1.0, 0.0, 1.0, 0.1));
		((JSpinner.DefaultEditor) dupThresJsp.getEditor()).getTextField().setEditable(false);
		varPanel.add(dupThresJsp);
		
		JPanel connectPanel = new JPanel();
		frame.getContentPane().add(connectPanel, BorderLayout.SOUTH);
		
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String url = urlText.getText();
				String password = passwordField.getText();
				String user = userText.getText();
				try (Connection connection = DriverManager.getConnection(url, user, password)){
					IntegrationView view = new IntegrationView(connection, table1Text.getText(), table2Text.getText(),
							(double)tokenThresJsp.getValue(), (int)numDupJsp.getValue(),(double)dupThresJsp.getValue());
					view.setConnectionInfo(url, user, password);
					
				
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, "Invalid Login Details", "Login Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
			}
		});
		connectPanel.add(connectButton);
		

		
	}
}
