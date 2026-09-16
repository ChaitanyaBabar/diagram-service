/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatCaseTaskElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for CaseTask.
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public class GatCaseTaskGenerator extends GatActivityGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatCaseTaskGenerator(BaseGatGenerator parent, Activity xpdlActivity)
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
		GatCaseTaskElement caseTaskElement = new GatCaseTaskElement(xpdlActivity.getId(),
				xpdlActivity.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlActivity));

		return caseTaskElement;
	}

}
