/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatEndEventElement;
import com.tibco.xpd.n2.gat.model.GatEventElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for End Events
 *
 * @author Sid Allway
 * @since 22 Dec 2025
 */
@SuppressWarnings("nls")
public class GatEndEventGenerator extends GatEventGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatEndEventGenerator(BaseGatGenerator parent, Activity xpdlActivity)
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
		GatEndEventElement endEventElement = new GatEndEventElement(xpdlActivity.getId(), xpdlActivity.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlActivity));

		return endEventElement;
	}

}
