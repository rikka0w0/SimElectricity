package SimElectricity.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import SimElectricity.API.IBaseComponent;

public class Db {
	private static Connection conn;

	public static void init() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite::memory:");

		Statement stmt = conn.createStatement();
		stmt.executeUpdate("create table Nodes(node UNIQUE, x, y, z, voltage, resistance, type)");
		stmt.executeUpdate("create table N2NRes(node1, node2, n1type, n2type, resistance, isResSet, resSet)");

		System.out.println("Sqlite init.");
	}

	public static int addNode(String node, int x, int y, int z,
			double voltage, double resistance, int type) {
		ResultSet result = null;
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into Nodes values(?, ?, ?, ?, ?, ?, ?)");
			prep.setString(1, node);
			prep.setInt(2, x);
			prep.setInt(3, y);
			prep.setInt(4, z);
			prep.setDouble(5, voltage);
			prep.setDouble(6, resistance);
			prep.setInt(7, type);
			
			return prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static void addN2NRes(String node1, String node2, int n1type,
			int n2type, double resistance, int isResSet, String resSet) {

	}

	public static ResultSet getNeighboringNode(int x, int y, int z) {
		ResultSet result = null;
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("select * from Nodes where "
					+ "(x=? and y="
					+ y
					+ " and z="
					+ z
					+ ") or "
					+ "(x=? and y="
					+ y
					+ " and z="
					+ z
					+ ") or "
					+ "(x="
					+ x
					+ " and y=? and z="
					+ z
					+ ") or "
					+ "(x="
					+ x
					+ " and y=? and z="
					+ z
					+ ") or "
					+ "(x="
					+ x
					+ " and y="
					+ y
					+ " and z=?) or "
					+ "(x="
					+ x
					+ " and y=" + y + " and z=?)");
			prep.setInt(1, x + 1);
			prep.setInt(2, x - 1);
			prep.setInt(3, y + 1);
			prep.setInt(4, y - 1);
			prep.setInt(5, z + 1);
			prep.setInt(6, z - 1);
			result = prep.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
