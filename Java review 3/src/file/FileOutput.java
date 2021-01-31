package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Used to write to files.
 * 
 * @author jorgejimenez
 *
 */
public class FileOutput {

	private File file;
	private FileWriter writer;
	private final String EXTENSION_TEXT = "txt";

	/**
	 * Creates a file based on the passed in parameter.
	 * 
	 * @param path
	 *            The path to the file. If it does not exist it is created.
	 */
	public FileOutput(String path) {

		this.file = new File(path);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		init();
	}

	// Creates a writer object.
	private void init() {
		try {
			writer = new FileWriter(file, true);

		} catch (IOException e) {
			System.out.println("There was a problem creating the writer.");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new file - untitled.txt
	 */
	public FileOutput() {
		this("untitled.txt");
	}

	/**
	 * Writes a line to a text file.
	 */
	public void writeLine() {
		String ext = file.getName().split("\\.")[1];

		if (!ext.equalsIgnoreCase(EXTENSION_TEXT))
			throw new IllegalArgumentException("The file is not a text file");

	}

	/**
	 * Writes a string to a text file.
	 */
	public void write(String output) {
		String ext = file.getName().split("\\.")[1];

		if (!ext.equalsIgnoreCase(EXTENSION_TEXT))
			throw new IllegalArgumentException("The file is not a text file");

		try {
			writer.write(output);
		} catch (IOException e) {
			System.out.println("There was a problem writing to the file.");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
