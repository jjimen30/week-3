package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An API used to send SQL statement.
 * 
 * Sample usage:
 * 
 * <pre>
 * <code>
String connectionString = "...";
String dbLogin = "user";
String dbPassword = "1234";
String dbTable = "temperatures";
String[] rows = {"month", "day", "year", "hi", "lo"};

Query query = new Query(connectionString, dbLogin, dbPassword);
query.select(rows).from(dbTable).where("month", Keywords.EQUAL, "12");
query.select(rows);

query.from(dbTable);
query.where("month", Keywords.EQUAL, "12");

String[][] a = query.getQueryResults();

for (String[] el : a) {
	System.out.println(Arrays.toString(el));
}
 * </code>
 * </pre>
 * 
 * Output from above:
 * 
 * <pre>

SELECT month, day, year, hi, lo FROM temperatures 

[12, 1, 2020, 40, 25]
[12, 2, 2020, 41, 21]
[12, 3, 2020, 39, 20]
[12, 4, 2020, 37, 18]
[12, 5, 2020, 40, 19]
[12, 6, 2020, 42, 19]
[12, 7, 2020, 43, 19]
[12, 8, 2020, 42, 20]
[12, 9, 2020, 39, 19]
[12, 10, 2020, 36, 20]
[12, 11, 2020, 35, 20]
[12, 12, 2020, 32, 18]
[12, 13, 2020, 31, 16]
 * </pre>
 * 
 * @author jorgejimenez
 *
 */
public class Query {

	private Connection conn;
	private StringBuilder currentStatement;
	private Keywords currentState = null;
	private String[] columns;
	private TreeSet<String> columnMap;

	/**
	 * Creates a connection to an SQL database.
	 * 
	 * @param connectionString
	 * @param dbLogin
	 * @param dbPassword
	 */
	public Query(String connectionString, String dbLogin, String dbPassword) {

		initConnection(connectionString, dbLogin, dbPassword);

	}

	private void initConnection(String connectionString, String dbLogin, String dbPassword) {
		try {
			conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
		} catch (SQLException e) {
			System.err.println("There was a problem with the connection. Check the loging information.");
			e.printStackTrace();
		}
	}

	/**
	 * Releases this Connection object's database and JDBC resources immediately
	 * instead of waiting for them to be automatically released.
	 * 
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The SQL SELECT DISTINCT Statement The SELECT DISTINCT statement is used
	 * to return only distinct (different) values.
	 * 
	 * Inside a table, a column often contains many duplicate values; and
	 * sometimes you only want to list the different (distinct) values.
	 * 
	 * 
	 * @param columns
	 * @return
	 */
	public Query selectDistinct(String... columns) {

		currentStatement = new StringBuilder();

		currentStatement.append(Keywords.SELECT + " " + Keywords.DISTINCT + " ");
		currentState = Keywords.DISTINCT;

		select(columns);
		return this;
	}

	public Query select(String... columns) {

		if (currentState != Keywords.DISTINCT) {
			currentStatement = new StringBuilder();
			currentStatement.append(Keywords.SELECT.toString() + " ");
		}

		columnMap = new TreeSet<>();
		this.columns = columns.clone();

		boolean first = true;

		for (int i = 0; i < columns.length; i++) {
			columnMap.add(columns[i]);

			if (first)
				currentStatement.append(columns[i]);
			else
				currentStatement.append(", " + columns[i]);

			first = false;

		}

		currentStatement.append(" ");

		currentState = Keywords.SELECT;

		// System.out.println(currentStatement.toString());
		return this;
	}

	public Query from(String... tables) {

		if (currentState != Keywords.SELECT)
			throw new InvalidStatement("The order the statemene was built is wrong.");

		currentStatement.append(Keywords.FROM.toString() + " ");

		boolean first = true;

		for (int i = 0; i < tables.length; i++) {

			if (first)
				currentStatement.append(tables[i]);
			else
				currentStatement.append(", " + tables[i]);

			first = false;
		}

		currentStatement.append(" ");

		currentState = Keywords.FROM;

		System.out.println(currentStatement.toString());
		return this;
	}

	public Query selectAll(Keywords keyword) {

		return select("*");
	}

	public String[][] getQueryResults() {
		String[][] results = null;

		if (currentStatement == null)
			throw new IncompleteStatement("The order the statemene was built is wrong.");

		try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt.executeQuery(currentStatement.toString())) {

			int numRows;
			int numCols = rs.getMetaData().getColumnCount();

			rs.last();
			numRows = rs.getRow();
			rs.first();
			results = new String[numRows][numCols];

			for (int i = 0; i < numRows; i++) {
				for (int col = 0; col < columnMap.size(); col++) {
					results[i][col] = rs.getString(columns[col]);
				}
				rs.next();
			}

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return results;
	}

	public void where(String column, Keywords condition, String value) {

		currentStatement.append(Keywords.WHERE + " ");
		switch (condition) {

			/*
			 * = Equal > Greater than < Less than >= Greater than or equal <=
			 * Less than or equal <> Not equal. Note: In some versions of SQL
			 * this operator may be written as != BETWEEN Between a certain
			 * range LIKE Search for a pattern IN To specify multiple possible
			 * values for a column
			 */

			case EQUAL :
				currentStatement.append(column + " " + Keywords.EQUAL + " " + value);
				break;
			case GREATER_THAN :
				break;
			case GREATER_THAN_OR_EQUAL :
				break;
			case LESS_THAN :
				break;
			case LESS_THAN_OR_EQUAL :
				break;
			case NOT_EQUAL :
				break;
			case BETWEEN :
				break;
			case LIKE :
				break;
			case IN :
				break;
			default :
				break;
		}

		if (currentState != Keywords.FROM)
			throw new InvalidStatement("Invalid statement: table not specyfied.");

	}
	// __________________________________
	//
	// SOME RUNTIME EXCEPTIONS
	// __________________________________

	private class InvalidStatement extends RuntimeException {
		private static final long serialVersionUID = 536587772853913861L;

		public InvalidStatement(String errorMessage) {
			super(errorMessage);
		}
	}

	private class IncompleteStatement extends RuntimeException {
		private static final long serialVersionUID = 536587772853913861L;

		public IncompleteStatement(String errorMessage) {
			super(errorMessage);
		}
	}

	public static void main(String[] arg) {
		String connectionString = "jdbc:mysql://localhost:3306/practice";
		String dbLogin = "javauser";
		String dbPassword = "Oreo4321";
		String dbTable = "temperatures";
		String[] rows = {"month", "day", "year", "hi", "lo"};

		Query query = new Query(connectionString, dbLogin, dbPassword);
		query.select(rows);

		query.from(dbTable);
		query.where("month", Keywords.EQUAL, "12");

		// query.selectDistinct("hi").from("temperatures");
		String[][] a = query.getQueryResults();

		for (String[] el : a) {
			System.out.println(Arrays.toString(el));
		}
		// query.where();

		// query.select().from()
	}
}
