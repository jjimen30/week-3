package javaReview;

import java.util.Arrays;
import java.util.Scanner;

import db.Keywords;
import db.Query;
import file.FileOutput;

/**
 * 
 * Assignment: Java Review 3 Description:
 * 
 * CSIS2451
 * 
 * This assignment is an extension of the Java Review 2 assignment. In this
 * assignment you are asked to modify your Revew 2 assignment code to obtain the
 * data for the temperatures report from a database instead of a csv file.
 * 
 * Resources: {@code Query}, A library used to send queries to a database.
 * {@code FileOutput}, Used to output to a text file.
 * 
 * All of the libraries were written by me except the mysql-connector-java API.
 * 	
 * @author jorgejimenez
 *
 */
public class JavaReview3 {

	// Indexes for the data array.
	private static int iMONTH = 0;
	private static int iDAY = 1;
	private static int iYEAR = 2;
	private static int iHIGH = 3;
	private static int iLOW = 4;

	// Some miscellaneous constants used to build the table output.
	private static int LINE = 62;
	private static int COLUMN_DATE = 11;
	private static int COLUMN_HIGH = 5;
	private static int COLUMN_LOW = 4;
	private static int COLUMN_VARIANCE = 8;

	private static final String HEADER_TEMPLATE = "Date  High Low  Variance";
	private static final String NEWLINE = "\n";
	private static String outputPath = "/Users/jorgejimenez/eclipse-workspace/Java review 3/src/resources/TemperaturesReport.txt";
	private static String[][] data;

	// Output and input.
	private static FileOutput out;
	private static StringBuilder sb;
	private static String line;
	private static String titleMonth;
	private static String monthNumber;

	public static void main(String[] arg) {

		userinput();

		String connectionString = "jdbc:mysql://localhost:3306/practice";
		String dbLogin = "javauser";
		String dbPassword = "Oreo4321";
		String dbTable = "temperatures";
		String[] columns = {"month", "day", "year", "hi", "lo"};

		Query query = new Query(connectionString, dbLogin, dbPassword);

		out = new FileOutput(outputPath);
		// in = new FileInput(path);

		// Form SQL statement and get results
		// in an two dimensional array.
		query.select(columns).from(dbTable).where("month", Keywords.EQUAL, monthNumber);
		data = query.getQueryResults();

		// Create the reports and print.
		reportOne();
		reportTow();

		System.out.println(sb.toString());
		out.write(sb.toString());
		out.close();
	}

	// Lists all days and temperatures in a tabular format.
	private static void reportOne() {

		sb = new StringBuilder();

		String title = " 2020: Temperatures in Utah \n";
		int highestIndex = 0;
		int lowestIndex = 0;
		line = createLine(LINE, '-');

		float sumHigh = 0;
		float sumLow = 0;

		// Add the title.
		sb.append(line);
		sb.append(titleMonth + title);
		sb.append(line);

		// Create the header for the table. Print out and add to file.
		String header = createRow(HEADER_TEMPLATE.split("\s+"), COLUMN_DATE, COLUMN_HIGH, COLUMN_LOW, COLUMN_VARIANCE);

		sb.append(header);
		sb.append(line);

		// Indexes for the data.
		int iDataLow = 3;
		int iDataHigh = 4;

		// row indexes.
		//@formatter:off
		int iDate = 0;
		int iHigh = 1;
		int iLow  = 2;
		int iVar  = 3;
		//@formatter:on

		String[] row = new String[HEADER_TEMPLATE.split("\s+").length];

		// String[] row = new String[];
		// Create each row entry.
		for (int i = 0; i < data.length; i++) {

			// Check for the highest and lowest.
			if (Integer.parseInt(data[i][iHIGH]) > Integer.parseInt(data[highestIndex][iHIGH]))
				highestIndex = i;

			if (Integer.parseInt(data[i][iLOW]) < Integer.parseInt(data[lowestIndex][iLOW]))
				lowestIndex = i;

			row[iDate] = data[i][iMONTH] + "/" + data[i][iDAY] + "/" + data[i][iYEAR];
			row[iHigh] = data[i][iHIGH];
			row[iLow] = data[i][iLOW];
			row[iVar] = variance(Integer.parseInt(data[i][iHIGH]), Integer.parseInt(data[i][iLOW]));

			// Get the row and add it to the StringBuilder.
			String rowString = createRow(row, COLUMN_DATE, COLUMN_HIGH, COLUMN_LOW, COLUMN_VARIANCE);
			sb.append(rowString);

			sumHigh += Float.parseFloat(data[i][iHIGH]);
			sumLow += Float.parseFloat(data[i][iLOW]);

		}

		sb.append(line);

		// The final report at the end.
		String highest = String.format("%s Highest Temperature: %s/%s: %s Average Hi: %.1f", titleMonth, monthNumber,
				data[highestIndex][iDAY], data[highestIndex][iHIGH], sumHigh / data.length);

		String lowest = String.format("%s Lowest Temperature: %s/%s: %s Average Lo: %.1f", titleMonth, monthNumber,
				data[lowestIndex][iDAY], data[lowestIndex][iLOW], sumLow / data.length);

		sb.append(highest + NEWLINE);
		sb.append(NEWLINE);
		sb.append(lowest + NEWLINE);

	}

	private static void reportTow() {

		sb.append(line);
		sb.append("Graph" + NEWLINE);

		sb.append(line);
		sb.append("      1   5    10   15   20   25   30   35   40   45   50\n");
		sb.append("      |   |    |    |    |    |    |    |    |    |    |\n");
		sb.append(line);

		// Iterate through the data to the create the second report.
		for (int i = 0; i < data.length; i++) {

			sb.append(String.format("%-3sHI %s", data[i][iDAY], createLine(Integer.parseInt(data[i][iHIGH]), '+')));
			sb.append(String.format("%-3sLO %s", "", createLine(Integer.parseInt(data[i][iLOW]), '-')));
			// sb.append(" LO " + createLine(Integer.parseInt(data[i][high]),
			// '+') + NEWLINE);
		}

		sb.append(line);
		sb.append("      |   |    |    |    |    |    |    |    |    |    |\n");
		sb.append("      1   5    10   15   20   25   30   35   40   45   50\n");
		sb.append(line + NEWLINE);
	}

	// --------------------------------------------
	// SOME HELPER METHODS
	// --------------------------------------------

	private static void userinput() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("What month would you like to create a report for?");
		String userIn = scanner.nextLine();

		// Check for december or november.
		while (!userIn.equalsIgnoreCase("december") && !userIn.equalsIgnoreCase("november")
				&& !userIn.equalsIgnoreCase("12") && !userIn.equalsIgnoreCase("11")) {

			System.out.println("What month would you like to create a report for?");
			userIn = scanner.nextLine();
		}

		titleMonth = null;
		monthNumber = null;

		switch (userIn.toLowerCase()) {
			case "11" :
				titleMonth = "November";
				monthNumber = "11";
				break;
			case "november" :
				titleMonth = "November";
				monthNumber = "11";
				break;
			case "12" :
				titleMonth = "December";
				monthNumber = "12";
			case "december" :
				titleMonth = "December";
				monthNumber = "12";
				break;

		}
	}

	// Calculates the variance between two numbers.
	private static String variance(int a, int b) {
		if ((a - b) < 0)
			return Integer.toString(b - a);

		return Integer.toString(a - b);
	}

	// Takes a String array and and prints a row for the table.
	private static void printRow(String[] elements, int... widths) {

		System.out.print(createRow(elements, widths));
	}

	// Creates a row based on the string array passed in and with the
	// corresponding column widths.
	private static String createRow(String[] elements, int... widths) {

		StringBuilder sb = new StringBuilder();
		int totalWidth = 0;

		for (int i = 0; i < elements.length; i++) {
			totalWidth += widths[i];
			sb.append(String.format("%-" + widths[i] + "s ", elements[i]));
		}

		sb.append(NEWLINE);

		return sb.toString();
	}

	// Prints the amount of dashes passed in.
	private static void printLine(int size, char c) {
		System.out.print(createLine(size, c));
	}

	// Creates a line of a size and characters c.
	private static String createLine(int size, char c) {
		char[] l = new char[size];
		Arrays.fill(l, c);
		StringBuilder sb = new StringBuilder();
		sb.append(l);
		sb.append(NEWLINE);
		return sb.toString();
	}
}
