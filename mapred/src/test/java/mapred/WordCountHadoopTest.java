package mapred;

import org.junit.Test;


public class WordCountHadoopTest {

	@Test
	public void testExecute() throws Exception {
		
		WordCountHadoop wc = new WordCountHadoop();
		wc.execute("src/main/resources/short.txt", "target/hadoop-result");
		System.out.println("completed Hadoop word count");
		
	}	
}
