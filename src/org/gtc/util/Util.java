package org.gtc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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

}
