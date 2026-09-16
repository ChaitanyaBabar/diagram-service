/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import org.eclipse.emf.ecore.EObject;

import com.tibco.xpd.n2.gat.model.GatEventElement;
import com.tibco.xpd.n2.gat.model.GatStartEventElement;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.BlockActivity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for Start Events
 *
 * @author Sid Allway
 * @since 22 Dec 2025
 */
@SuppressWarnings("nls")
public class GatStartEventGenerator extends GatEventGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatStartEventGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatEventGenerator#generateEventActivity(com.tibco.xpd.xpdl2.Activity)
	 *
	 * @param xpdlActivity
	 * @return
	 */
	@Override
	protected GatEventElement generateEventActivity(Activity xpdlActivity)
	{
		GatStartEventElement startEventElement = new GatStartEventElement(xpdlActivity.getId(), xpdlActivity.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlActivity));

		/*
		 * If contained in an event sub-process, then set the isInterrupting flag appropriately
		 */
		EObject container = GatModelUtil.getContainer(xpdlActivity);

		if (container instanceof ActivitySet)
		{
			Activity embeddedSubProcActivity = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
					((ActivitySet) container).getId());

			BlockActivity blockActivity = embeddedSubProcActivity.getBlockActivity();

			Object isEventSubProcess = GatModelUtil.getOtherAttribute(blockActivity,
					XpdExtensionPackage.eINSTANCE.getDocumentRoot_IsEventSubProcess());

			if (Boolean.TRUE.equals(isEventSubProcess)) {
				Object nonInterruptingEvent = GatModelUtil.getOtherAttribute(xpdlActivity.getEvent(),
						XpdExtensionPackage.eINSTANCE.getDocumentRoot_NonInterruptingEvent());

				if (Boolean.TRUE.equals(nonInterruptingEvent))
				{
					startEventElement.setIsInterrupting(Boolean.FALSE);
				}
				else
				{
					startEventElement.setIsInterrupting(Boolean.TRUE);
				}
			}
		}

		return startEventElement;
	}

}
