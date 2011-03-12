/*******************************************************************************
 * Copyright (c) 2011 Modular Mind, Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrick Paulin - initial implementation
 *******************************************************************************/
package com.example.app.bootstrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActivatorTest {

	@Test
	public void testGetMessage() {
		Activator activator = Activator.getDefault();
		assertEquals(activator.getMessage(), "Hello!");
	}
}
