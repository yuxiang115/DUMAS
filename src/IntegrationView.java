import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import method.DUMAS;
import tableModel.QueryTableModel;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SpringLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class IntegrationView extends JFrame {

	private JPanel contentPane;
	private JTable table1;
	private JTable table2;
	private JTable integratedTable;
	private String table1Name = "table1";
	private String table2Name = "table2";
	private JTextArea duplicatesTextArea;
	private JLabel totalDuplicatesLabel;
	private JTextArea matchRes;
	private QueryTableModel tableModel1;
	private QueryTableModel tableModel2;
	private QueryTableModel tableModelIntegrated;
	private JTabbedPane tabbedPane;
	private int numWantedDuplicates = 0;
	private double tokenThreshold = 0.0;
	private double dupTreshold = 0.0;

	private String url;
	private String password;
	private String user;
	private Connection conn;
	/**
	 * Launch the application.
	 */


	/**
	 * Create the frame.
	 * @throws SQLException 
	 */
	public IntegrationView(Connection conn, String table1Name, String table2Name,
			double tokenThreshold, int numWantedDuplicates, double dupTreshold) throws SQLException {

		this.setTitle("Data Integration");
		if(table1Name != null || table1Name.length() > 0) this.table1Name = table1Name;
		if(table2Name != null || table2Name.length() > 0) this.table2Name = table2Name;

		this.conn = conn;
		this.numWantedDuplicates = numWantedDuplicates;
		this.tokenThreshold = tokenThreshold;
		this.dupTreshold = dupTreshold;


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		tableModel1 = new QueryTableModel(table1Name);
		tableModel2 = new QueryTableModel(table2Name);
		tableModelIntegrated = new QueryTableModel("Integrated");

		table1 = new JTable(tableModel1);
		tableModel1.setConnection(conn);
		tableModel1.setQuery("SELECT * FROM " + table1Name);
		JScrollPane table1scrollPane = new JScrollPane(table1);
		tabbedPane.addTab(table1Name, null, table1scrollPane, null);

		table2 = new JTable(tableModel2);
		tableModel2.setConnection(conn);
		tableModel2.setQuery("SELECT * FROM " + table2Name);
		JScrollPane table2ScrollPane = new JScrollPane(table2);
		tabbedPane.addTab(table2Name, null, table2ScrollPane, null);


		JPanel integratedPane = new JPanel();
		tabbedPane.addTab("Integrated Table", null, integratedPane, null);
		integratedPane.setLayout(new BorderLayout(0, 0));



		integratedTable = new JTable(this.tableModelIntegrated);
		JScrollPane integratedScrollPane = new JScrollPane(integratedTable);
		integratedPane.add(integratedScrollPane, BorderLayout.NORTH);

		JPanel integrationResPanle = new JPanel();
		integratedPane.add(integrationResPanle, BorderLayout.CENTER);
		SpringLayout sl_integrationResPanle = new SpringLayout();
		integrationResPanle.setLayout(sl_integrationResPanle);

		duplicatesTextArea = new JTextArea();
		duplicatesTextArea.setText("");
		JScrollPane duplicatesscrollPane = new JScrollPane(duplicatesTextArea);
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, duplicatesscrollPane, 38, SpringLayout.NORTH, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.WEST, duplicatesscrollPane, -524, SpringLayout.EAST, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.SOUTH, duplicatesscrollPane, 361, SpringLayout.NORTH, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.EAST, duplicatesscrollPane, -10, SpringLayout.EAST, integrationResPanle);
		integrationResPanle.add(duplicatesscrollPane);



		JLabel integratedResLabel = new JLabel("Result");
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, integratedResLabel, 10, SpringLayout.NORTH, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.WEST, integratedResLabel, 20, SpringLayout.WEST, integrationResPanle);
		integratedResLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		integrationResPanle.add(integratedResLabel);

		JLabel lblDuplicates = new JLabel("Duplicates:");
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, lblDuplicates, -28, SpringLayout.NORTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.WEST, lblDuplicates, -524, SpringLayout.EAST, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.SOUTH, lblDuplicates, -9, SpringLayout.NORTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.EAST, lblDuplicates, -440, SpringLayout.EAST, integrationResPanle);
		lblDuplicates.setFont(new Font("Tahoma", Font.BOLD, 15));
		integrationResPanle.add(lblDuplicates);

		totalDuplicatesLabel = new JLabel("Total: ");
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, totalDuplicatesLabel, 6, SpringLayout.SOUTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.WEST, totalDuplicatesLabel, -522, SpringLayout.EAST, integrationResPanle);
		sl_integrationResPanle.putConstraint(SpringLayout.SOUTH, totalDuplicatesLabel, 25, SpringLayout.SOUTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.EAST, totalDuplicatesLabel, -383, SpringLayout.EAST, integrationResPanle);
		totalDuplicatesLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		integrationResPanle.add(totalDuplicatesLabel);

		JButton importDBButton = new JButton("Import to DB");
		importDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tableModelIntegrated.getColumnCount() > 0)
					importTableToDB();
			}
		});
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, importDBButton, 0, SpringLayout.NORTH, totalDuplicatesLabel);
		sl_integrationResPanle.putConstraint(SpringLayout.EAST, importDBButton, 0, SpringLayout.EAST, duplicatesscrollPane);
		integrationResPanle.add(importDBButton);

		matchRes = new JTextArea();		
		JScrollPane matchResScoll = new JScrollPane(matchRes);
		sl_integrationResPanle.putConstraint(SpringLayout.NORTH, matchResScoll, 0, SpringLayout.NORTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.WEST, matchResScoll, 0, SpringLayout.WEST, integratedResLabel);
		sl_integrationResPanle.putConstraint(SpringLayout.SOUTH, matchResScoll, 323, SpringLayout.NORTH, duplicatesscrollPane);
		sl_integrationResPanle.putConstraint(SpringLayout.EAST, matchResScoll, 529, SpringLayout.WEST, integratedResLabel);
		integrationResPanle.add(matchResScoll);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		JButton resetButton = new JButton("Reset");
		resetButton.setIcon(new ImageIcon(IntegrationView.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		panel.add(resetButton, BorderLayout.WEST);

		JButton integrateButton = new JButton("Integrate");
		integrateButton.addActionListener(new IntegrateAllListener());

		panel.add(integrateButton, BorderLayout.EAST);

		this.setVisible(true);
		this.setSize(new Dimension(1280, 967));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private class IntegrateAllListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event){
			DUMAS dumas = new DUMAS();
			dumas.setTokenTreshold(tokenThreshold);
			dumas.setNumWantedDuplicates(numWantedDuplicates);
			dumas.setDupTreshold(dupTreshold);
			dumas.setFirstTable(tableModel1);
			dumas.setSecondTable(tableModel2);

			Vector<String[]> integreate = dumas.compareTables(dumas.getFirstTable(), dumas.getSecondTable());
			dumas.getDuplicatesRes();
			totalDuplicatesLabel.setText("Total: " + String.valueOf(dumas.getNumDuplicates()));
			duplicatesTextArea.setText(dumas.getDuplicatesRes());
			matchRes.setText("Matched: " + dumas.getSchemaRes());
			tableModelIntegrated.setData(integreate.remove(0), integreate);
			tabbedPane.setSelectedIndex(2);

		}
	}

	public void doDumas(){
		DUMAS dumas = new DUMAS();
		dumas.setFirstTable(tableModel1);
		Vector c = dumas.getIntegratedData();
		String[] headers = (String[]) c.remove(0);
		this.tableModelIntegrated.setData(headers, c);
		//testModel();
	}

	public void importTableToDB(){
		String[] headers = this.tableModelIntegrated.getHeadersName();

		String createTableSql = "CREATE TABLE " + this.table1Name + "_" + this.table2Name + "( \n";
		for(int i = 0; i < headers.length; i++){
			if(i < headers.length - 1)
				createTableSql += headers[i] +  "\t\t\t VARCHAR(255), \n";
			else
				createTableSql += headers[i] +  "\t\t\t VARCHAR(255)\n)";
		}
		Statement stmt;
		String inserSql = "";
		//creating table
		try (Connection connection = DriverManager.getConnection(url, user, password)){
			stmt = connection.createStatement();
			stmt.executeUpdate(createTableSql);
			stmt.close();
			connection.close();
			} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Creating table file: "+e.getMessage());
		}
		
		try (Connection connection = DriverManager.getConnection(url, user, password)){
			stmt = connection.createStatement();
			Iterator recIter = tableModelIntegrated.getRows().iterator();
			while(recIter.hasNext()){
				String[] rows = (String[]) recIter.next();
				inserSql = "INSERT INTO " + this.table1Name + "_" + this.table2Name + " VALUES(";
				for(int i = 0; i < rows.length; i++){
					if(i < rows.length - 1)
						inserSql += "?, ";
					else
						inserSql += "?);";
				}
				PreparedStatement stmtInsert = connection.prepareStatement(inserSql);
				for(int i = 0; i < rows.length; i++){
					stmtInsert.setString(i + 1, ((rows[i] == null || rows[i].length() < 1)? "": rows[i]));
				}

				stmtInsert.executeUpdate();
			}
			JOptionPane.showMessageDialog(null, "Insert Sucess", "Insert Sucess", JOptionPane.INFORMATION_MESSAGE);
			stmt.close();
			connection.close();
			} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(inserSql);
		}
		
	}
	
	public void setConnectionInfo(String url, String user, String password){
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void testModel(){
		String[] h = tableModelIntegrated.getHeadersName();
		for(int i = 0; i < tableModelIntegrated.getColumnCount(); i++){
			String[] row = (String[]) tableModelIntegrated.getValueAtRow(i);
			for(int j = 0; j < row.length; j++){
				System.out.print(row[j]);
			}

			System.out.println("");
		}
	}

}
