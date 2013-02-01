/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
