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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		String perspectiveId = System.getProperty("initialWindowPerspectiveId");
		if (getWorkbenchConfigurer().getWorkbench().getPerspectiveRegistry()
				.findPerspectiveWithId(perspectiveId) != null) {
			return perspectiveId;
		} else {
			MessageDialog
					.openWarning(
							null,
							"Startup Warning",
							"The initial perspective has either not been specified or is not valid.");
			return null;
		}
	}
}
