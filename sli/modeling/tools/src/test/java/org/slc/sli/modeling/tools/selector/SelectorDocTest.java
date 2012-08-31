/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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



package org.slc.sli.modeling.tools.selector;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import org.junit.Test;

/**
 * JUnit test for SelectorDoc class.
 *
 * @author wscott
 *
 */
public class SelectorDocTest {

	private final String[] args = new String[]{"../../domain/src/main/resources/sliModel/SLI.xmi", "output.xml"};
	private final SelectorDoc selectorDoc = new SelectorDoc(args[0], args[1]);
	private final SelectorDoc spy = spy(selectorDoc);
	
	@Test
    public void test() {
    	SelectorDoc.main(new String[]{});
    }

}
