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

package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CaseInsensitiveStringTest {
	private static final String string1 = "i am a string";
	private static final String string2 = "I aM a StRiNg";
	
	private static final CaseInsensitiveString cis1 = new CaseInsensitiveString(string1);
	private static final CaseInsensitiveString cis2 = new CaseInsensitiveString(string2);

	@Test
	public void testCompareTo() {
		assertFalse(string1.compareTo(string2) == 0);
		assertTrue(cis1.compareTo(cis2) == 0);
	}
	
	@Test
	public void testEquals() {
		assertFalse(cis1.equals(string1));
		assertTrue(cis1.equals(new CaseInsensitiveString(string2)));
	}
	
	@Test
	public void testHashCode() {
		assertTrue(cis1.hashCode() == cis2.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals(cis1.toString(), cis2.toString());
	}
}
