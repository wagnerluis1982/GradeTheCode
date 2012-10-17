package org.gtc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

	public static File[] listFilesRecursive(File dir, final String ...extensions) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (new File(dir.getAbsolutePath() + File.separator + name).isDirectory())
					return true;
				else {
					for (String ext : extensions)
						if (name.endsWith(ext))
							return true;
				}
				return false;
			}
		};

		List<File> files = listFilesRecursive(dir, new ArrayList<File>(), filter);
		return files.toArray(new File[0]);
	}

	private static List<File> listFilesRecursive(File dir, List<File> files,
			FilenameFilter filter) {
		File[] listFiles = dir.listFiles(filter);

		for (File f : listFiles) {
			if (f.isDirectory())
				listFilesRecursive(f, files, filter);
			else
				files.add(f);
		}

		return files;
	}

	public static <T> T[] filterNonNull(final T... items) {
		List<T> list = new ArrayList<T>();
		for (T e : items)
			if (e != null)
				list.add(e);

		if (list.size() == items.length)
			return items;

		return list.toArray(Arrays.copyOf(items, 0));
	}

	public static long nanoSeconds(long millis) {
		return millis * 1000000;
	}

	/**
	 * Atomically creates a new directory somewhere beneath the system's
	 * temporary directory (as defined by the {@code java.io.tmpdir} system
	 * property), and returns its name.
	 *
	 * <p>
	 * Use this method instead of {@link File#createTempFile(String, String)}
	 * when you wish to create a directory, not a regular file. A common pitfall
	 * is to call {@code createTempFile}, delete the file and create a directory
	 * in its place, but this leads a race condition which can be exploited to
	 * create security vulnerabilities, especially when executable files are to
	 * be written into the directory.
	 *
	 * <p>
	 * This method assumes that the temporary volume is writable, has free
	 * inodes and free blocks, and that it will not be called thousands of times
	 * per second.
	 *
	 * <p>
	 * NOTE: This method originally belongs to "com.google.common.io.Files", a
	 * class of the guava-libraries, which can be found at
	 * {@link https://code.google.com/p/guava-libraries/}.
	 *
	 * @return the newly-created directory
	 * @throws IllegalStateException
	 *             if the directory could not be created
	 */
	public static File createTempDir() {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}
		throw new IllegalStateException("Failed to create directory within "
			+ TEMP_DIR_ATTEMPTS + " attempts (tried "
			+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	/**
	 * Maximum loop count when creating temp directories.
	 * NOTE: This field is needed for static method createTempDir above.
	 */
	private static final int TEMP_DIR_ATTEMPTS = 10000;

}
