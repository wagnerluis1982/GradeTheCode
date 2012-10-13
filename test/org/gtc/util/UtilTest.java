package org.gtc.util;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testListFilesRecursive() throws URISyntaxException {
		File stuff = new File(getClass().getResource("stuff").toURI());
		HashSet<File> expected = new HashSet<File>(Arrays.asList(new File[] {
				new File(stuff.getAbsolutePath() + "/folder_01/file_01.abc"),
				new File(stuff.getAbsolutePath() + "/folder_01/file_02.abc"),
				new File(stuff.getAbsolutePath() + "/folder_01/file_03.abc"),
				new File(stuff.getAbsolutePath() + "/folder_03/file_01.abc")
			}));

		File[] files = Util.listFilesRecursive(stuff, "abc");
		HashSet<File> actual = new HashSet<File>(Arrays.asList(files));

		assertEquals(expected, actual);
	}

}
