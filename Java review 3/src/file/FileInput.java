package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * 
 * Used for reading files.
 * 
 * @author jorgejimenez
 *
 */
public class FileInput {

	private File file;

	public FileInput(String path) {

		this.file = new File(path);

		if (!file.exists())
			throw new IllegalArgumentException("The file " + path + " does not exists.");

	}

	/**
	 * @return A two dimensional array containing the values in the CSV file or null if the file extension is not csv
	 */
	public String[][] getCSVData() {
		String[][] results = null;

		String ext = file.getName().split("\\.")[1];

		if (!ext.equalsIgnoreCase("csv"))
			return null;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			int index = 0;
			int capacity = 25;
			String delimiter = ",";

			line = br.readLine();
			results = new String[capacity][line.split(delimiter).length];

			while (line != null) {

				if (index >= capacity) {
					capacity = 2 * capacity;
					results = resize(results, capacity);
				}

				results[index] = line.split(delimiter);

				index++;
				line = br.readLine();
			}

			//			System.out.println(index);

			if (index < capacity)
				results = resize(results, index);

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	private String[][] resize(String[][] arr, int newSize) {
		return java.util.Arrays.copyOf(arr, newSize);
	}

	// Test client 

	private int COLUMN_WIDTH_1 = 5;
	private int COLUMN_WIDTH_2 = 7;
	private int COLUMN_WIDTH_3 = 4;
	private int COLUMN_WIDTH_4 = 8;

	// Takes a String array and and builds a row for the table. 
	//@formatter:off
	public void printRow(String[] elements) {
		String out = String.format(
				"%-" + COLUMN_WIDTH_1     + "s " + 
						"%" + COLUMN_WIDTH_2   + "s " + 
						"%" + COLUMN_WIDTH_2   + "s " + 
						"%-" + COLUMN_WIDTH_4 + "s ", (Object[]) elements
				);
		printLine(94);
		System.out.println(out);
	}
	//@formatter:on

	private void printLine(int size) {
		char[] l = new char[size];
		Arrays.fill(l, '-');
		System.out.println(new StringBuilder().append(l).toString());
	}

	public static void main(String[] args) {
		String file = "/Users/jorgejimenez/eclipse-workspace/fileHelper/src/resources/CustomerData2.csv";
		String file2 = "/Users/jorgejimenez/eclipse-workspace/fileHelper/testFiles/CustomerData2.csv";
		String[][] data;

		FileInput fh = new FileInput(file2);

		data = fh.getCSVData();

		System.out.println(data.length);
		System.out.println(data[0].length);

		for (String[] row : data)
			fh.printRow(row);

	}

	public void close() {

	}
}
