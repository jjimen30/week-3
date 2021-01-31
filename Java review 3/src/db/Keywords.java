package db;

/**
 * 
 * A collection of keywords used to build query SQL statements.
 * 
 * @author jorgejimenez
 *
 */

public enum Keywords {

	//@formatter:off
	/*
		SELECT - extracts data from a database
		UPDATE - updates data in a database
		DELETE - deletes data from a database
		INSERT INTO - inserts new data into a database
		CREATE DATABASE - creates a new database
		ALTER DATABASE - modifies a database
		CREATE TABLE - creates a new table
		ALTER TABLE - modifies a table
		DROP TABLE - deletes a table
		CREATE INDEX - creates an index (search key)
		DROP INDEX - deletes an index
	
	 */
	
	SELECT("SELECT"), UPDATE("UPDATE"), DELETE("DELETE"), INSERT_INTO("INSERT INTO"), CREATE_DATABASE("CREATE DATABASE"),
	ALTER_DATABASE("ALTER DATABASE"), CREATE_TABLE("CREATE TABLE"), ALTER_TABLE("ALTER TABLE"), 
	//SETLECT DISTINCT
	DISTINCT("DISTINCT"), WHERE("WHERE"), ORDER_BY("ORDER BY"), 
	
	/*
	
	=	Equal	
	>	Greater than	
	<	Less than	
	>=	Greater than or equal	
	<=	Less than or equal	
	<>	Not equal. Note: In some versions of SQL this operator may be written as !=	
	BETWEEN	Between a certain range	
	LIKE	Search for a pattern	
	IN	To specify multiple possible values for a column
	
	
	 */
	FROM("FROM"), ALL("*"), AND("AND"), EQUAL("="), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="), 
	LESS_THAN("<"), LESS_THAN_OR_EQUAL("<="), NOT_EQUAL("<>"), BETWEEN("BETWEEN"), LIKE("LIKE"), IN("IN");
	//@formatter:on

	String name;

	private Keywords(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
