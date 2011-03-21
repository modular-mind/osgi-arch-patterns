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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			if (installNewFeature()) return IApplication.EXIT_RESTART;
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}	
	
	private static final String JUSTUPDATED = "justUpdated";
	private static final String UPDATE_SITE_URL = "updateSiteUrl";

	/*
	 * NOTE: The P2 code here is not production-ready, but simply
	 * an example for how you could install Features from within your
	 * Application.
	 *
	 * Creating a fully-generic installer/updater is beyond the scope 
	 * of this Tutorial, but watch bug#337016 for details on an 
	 * initiative to make this easy.
	 */
	private boolean installNewFeature() throws ProvisionException {

		/*
		 * Check to make sure that we are not restarting after an update. If we
		 * are, then there is no need to check for updates again.
		 */
		final IPreferenceStore prefStore = Activator.getDefault()
				.getPreferenceStore();
		if (prefStore.getBoolean(JUSTUPDATED)) {
			prefStore.setValue(JUSTUPDATED, false);
			return false;
		}

		final IProvisioningAgent agent = this.getProvisioningAgent();

		/*
		 * Create a runnable to execute the update. We'll show a dialog during
		 * the process and then return when the runnable is complete.
		 */
		final boolean[] restartRequired = new boolean[] { false };
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				IStatus installStatus = doInstallOperation(agent, monitor);
				if (installStatus.getSeverity() != IStatus.ERROR) {
					prefStore.setValue(JUSTUPDATED, true);
					restartRequired[0] = true;
				} else {
					log(installStatus);
				}
			}
		};

		/*
		 * Execute the runnable and wait for it to complete.
		 */
		try {
			new ProgressMonitorDialog(null).run(true, true, runnable);
			return restartRequired[0];
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}
		return false;
	}

	/*
	 * An alternative way to do it. Needs error checking, cancel checking, etc.
	 */
	protected IStatus doInstallOperation(IProvisioningAgent agent,
			IProgressMonitor monitor) {
		ProvisioningSession session = new ProvisioningSession(agent);

		/*
		 * Get update site url from system argument.
		 */
		String updateSiteUrl = System.getProperty(UPDATE_SITE_URL);
		if (updateSiteUrl == null || updateSiteUrl.isEmpty()) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
					"No udpate site URL specified. Pass -DupdateSiteUrl as a system argument.");
		}

		/*
		 * Load the metadata repository to be searched for new feature.
		 */
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IMetadataRepository metadataRepo;
		try {
			metadataRepo = manager.loadRepository(new URI(updateSiteUrl),
					new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		}

		/*
		 * Load the artifact repository
		 */
		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		try {
			artifactManager.loadRepository(new URI(updateSiteUrl),
					new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(Status.ERROR, Activator.PLUGIN_ID,
					e.getMessage(), e);
		}

		/*
		 * Query the metadata repository for the feature to install.
		 */
		Collection<IInstallableUnit> toInstall = metadataRepo.query(
				QueryUtil.createIUAnyQuery(), new NullProgressMonitor())
				.toUnmodifiableSet();

		/*
		 * Run the install operation modally so that a status can be returned to
		 * the caller.
		 */
		SubMonitor sub = SubMonitor.convert(monitor,
				"Installing new features...", 200);

		InstallOperation operation = new InstallOperation(session, toInstall);
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.isOK()) {
			ProvisioningJob job = operation.getProvisioningJob(null);
			status = job.runModal(sub.newChild(100));
			if (status.getSeverity() == IStatus.CANCEL)
				throw new OperationCanceledException();
		}
		return status;
	}

	private IProvisioningAgent getProvisioningAgent() throws ProvisionException {
		ServiceReference serviceReference = Activator.getContext()
				.getServiceReference(IProvisioningAgentProvider.SERVICE_NAME);

		IProvisioningAgentProvider agentProvider = (IProvisioningAgentProvider) Activator
				.getContext().getService(serviceReference);
		return agentProvider.createAgent(null);
	}

	private void log(IStatus status) {
		Activator.getDefault().getLog().log(status);
	}
}
