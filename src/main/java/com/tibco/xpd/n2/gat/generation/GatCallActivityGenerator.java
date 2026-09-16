/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import org.eclipse.emf.ecore.EObject;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatAdHocCallActivityElement;
import com.tibco.xpd.n2.gat.model.GatCallActivityElement;
import com.tibco.xpd.n2.gat.util.GatTaskObjectUtil;
import com.tibco.xpd.n2.gat.util.GatModelUtil;
import com.tibco.xpd.xpdExtension.AsyncExecutionMode;
import com.tibco.xpd.xpdExtension.ProcessInterface;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ExecutionType;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.SubFlow;

/**
 * Graphical Audit Trail Generator for Call Activity (invoke sub-process) task.
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public class GatCallActivityGenerator extends GatActivityGenerator
{
	public GatCallActivityGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	@Override
	protected GatActivityElement generateActivity(Activity xpdlActivity)
	{
		GatCallActivityElement callActivityElement;

		Object adHocConfiguration = GatModelUtil.getOtherElement(xpdlActivity,
				XpdExtensionPackage.eINSTANCE.getDocumentRoot_AdHocTaskConfiguration());

		if (adHocConfiguration != null)
		{
			callActivityElement = new GatAdHocCallActivityElement(xpdlActivity.getId(),
				xpdlActivity.getName(), GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}
		else
		{
			callActivityElement = new GatCallActivityElement(xpdlActivity.getId(), xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}

		EObject subProcessOrInterface = GatTaskObjectUtil.getSubProcessOrInterface(xpdlActivity);

		if (subProcessOrInterface != null)
		{
			// Stubbed: WorkingCopyUtil/ProjectUtil require Eclipse workspace - applicationId left null

			if (subProcessOrInterface instanceof Process)
			{
				callActivityElement.setProcessArtifactId(((Process) subProcessOrInterface).getId());
			}
			else if (subProcessOrInterface instanceof ProcessInterface)
			{
				callActivityElement.setProcessArtifactId(((ProcessInterface) subProcessOrInterface).getId());
			}
		}

		if (xpdlActivity.getImplementation() instanceof SubFlow)
		{
			SubFlow xpdlSubFlow = (SubFlow) xpdlActivity.getImplementation();

			GatCallActivityElement.InvocationMode invocationMode = GatCallActivityElement.InvocationMode.Synchronous;

			if (ExecutionType.ASYNCHR_LITERAL.equals(xpdlSubFlow.getExecution()))
			{
				AsyncExecutionMode asynchMode = (AsyncExecutionMode) GatModelUtil.getOtherAttribute(xpdlSubFlow,
						XpdExtensionPackage.eINSTANCE.getDocumentRoot_AsyncExecutionMode());

				if (AsyncExecutionMode.ATTACHED.equals(asynchMode))
				{
					invocationMode = GatCallActivityElement.InvocationMode.AsynchronousAttached;
				}
				else
				{
					invocationMode = GatCallActivityElement.InvocationMode.AsynchronousDetached;
				}
			}

			callActivityElement.setInvocationMode(invocationMode);
		}

		return callActivityElement;
	}

}
