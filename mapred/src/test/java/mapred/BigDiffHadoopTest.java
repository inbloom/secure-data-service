package mapred;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;


public class BigDiffHadoopTest {

	private static String outputDirectory = "target/bigdiff-hadoop-result";
	
	@Before
	public void setUp() throws IOException {
		// get rid of previous results, to avoid FileAlreadyExistsException
		FileUtils.deleteDirectory(new File(outputDirectory));
	}
	
	@Test
	public void testExecute() throws Exception {
		
		BigDiffHadoop bd = new BigDiffHadoop();
		bd.execute("src/main/resources/bigdiff/left.txt", "src/main/resources/bigdiff/right-sorted.txt", outputDirectory);
		System.out.println("completed Hadoop big diff");
		
	}	
}
