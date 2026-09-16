/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatSendTaskElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for SendTask.
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public class GatSendTaskGenerator extends GatActivityGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatSendTaskGenerator(BaseGatGenerator parent, Activity xpdlActivity)
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
		GatSendTaskElement sendTaskElement = new GatSendTaskElement(xpdlActivity.getId(),
				xpdlActivity.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlActivity));

		return sendTaskElement;
	}

}
