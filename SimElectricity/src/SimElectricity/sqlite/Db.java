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

	public static void init() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection("jdbc:sqlite::memory:");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("create table Nodes(node UNIQUE, x, y, z, voltage, resistance, type)");
			stmt.executeUpdate("create table N2NRes(node1, node2, n1type, n2type, resistance)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Sqlite init.");
	}

	public static int addNode(int nodeHash, int x, int y, int z,
			double voltage, double resistance, int type) {
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into Nodes values(?, ?, ?, ?, ?, ?, ?)");
			prep.setInt(1, nodeHash);
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

	public static int delNode(int nodeHash) {
		try {
			PreparedStatement prep = conn
					.prepareStatement("DELETE FROM Nodes WHERE node = ?");
			prep.setInt(1, nodeHash);

			return prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static int addN2NRes(int node1, int node2, int n1type,
			int n2type, double resistance) {
		try {
			PreparedStatement prep = conn
					.prepareStatement("insert into N2NRes values(?, ?, ?, ?, ?)");
			prep.setInt(1, node1);
			prep.setInt(2, node2);
			prep.setInt(3, n1type);
			prep.setInt(4, n2type);
			prep.setDouble(5, resistance);

			return prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static int delN2NRes(int nodeHash) {
		try {
			PreparedStatement prep = conn
					.prepareStatement("DELETE FROM N2NRes WHERE (node1 = ?) or (node1 = ?)");
			prep.setInt(1, nodeHash);
			prep.setInt(2, nodeHash);

			return prep.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static ResultSet getNeighboringNodes(int x, int y, int z) {
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
