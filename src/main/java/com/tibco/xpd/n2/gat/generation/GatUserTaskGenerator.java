/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */
package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatAdHocUserTaskElement;
import com.tibco.xpd.n2.gat.model.GatUserTaskElement;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for User Task.
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public class GatUserTaskGenerator extends GatActivityGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatUserTaskGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatActivityGenerator#generateActivity(com.tibco.xpd.xpdl2.Activity)
	 *
	 * @param xpdlActivity
	 * @return
	 */
	@Override
	protected GatActivityElement generateActivity(Activity xpdlActivity)
	{
		/* Normal and Ad-Hoc user tasks are different types */
		Object adHocConfiguration = GatModelUtil.getOtherElement(xpdlActivity,
				XpdExtensionPackage.eINSTANCE.getDocumentRoot_AdHocTaskConfiguration());

		if (adHocConfiguration != null)
		{
			return new GatAdHocUserTaskElement(xpdlActivity.getId(), xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}
		else
		{
			return new GatUserTaskElement(xpdlActivity.getId(), xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}
	}

}
