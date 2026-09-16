/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatEmptyTaskElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for Empty Task.
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
public class GatEmptyTaskGenerator extends GatActivityGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 */
	public GatEmptyTaskGenerator(BaseGatGenerator parent, Activity xpdlActivity)
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
		GatEmptyTaskElement emptyTaskElement = new GatEmptyTaskElement(xpdlActivity.getId(), xpdlActivity.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlActivity));

		return emptyTaskElement;
	}

}
