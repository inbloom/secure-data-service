package mapred;

import org.junit.Test;


public class WordCountCascadingTest {

	@Test
	public void testExecute() {
		
		WordCountCascading wc = new WordCountCascading();
		wc.execute("src/main/resources/short.txt", "target/cascading-result");
		System.out.println("completed Cascading word count");
		
	}	
}
