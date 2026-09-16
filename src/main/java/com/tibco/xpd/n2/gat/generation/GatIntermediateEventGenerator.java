/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatBoundaryEventElement;
import com.tibco.xpd.n2.gat.model.GatEventElement;
import com.tibco.xpd.n2.gat.model.GatIntermediateCatchEventElement;
import com.tibco.xpd.n2.gat.model.GatIntermediateThrowEventElement;
import com.tibco.xpd.n2.gat.util.GatEventObjectUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.CatchThrow;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for Intermediate Events of all kinds (catch, throw, boundary)
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatIntermediateEventGenerator extends GatEventGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatIntermediateEventGenerator(BaseGatGenerator parent, Activity xpdlActivity)
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
		/*
		 * Create appropriate Event model element type
		 */
		String attachedTaskId = GatEventObjectUtil.getTaskIdAttachedTo(xpdlActivity);

		/*
		 * BoundaryEvent
		 * */
		if (attachedTaskId != null && !attachedTaskId.isEmpty())
		{
			/* It's attached to an activity, so we need a BoundaryEvent */
			GatBoundaryEventElement boundaryEventElement = new GatBoundaryEventElement(xpdlActivity.getId(),
					xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));

			/* Set the boundary event properties. */
			boundaryEventElement.setAttachedToRef(attachedTaskId);

			boundaryEventElement.setCancelActivity(!GatEventObjectUtil.isNonCancellingEvent(xpdlActivity));

			return boundaryEventElement;
		}
		/*
		 * IntermediateThrowEvent
		 */
		else if (CatchThrow.THROW.equals(GatEventObjectUtil.getCatchThrowType(xpdlActivity)))
		{
			GatIntermediateThrowEventElement intermediateThrowEventElement = new GatIntermediateThrowEventElement(
					xpdlActivity.getId(), xpdlActivity.getName(), GatModelUtil.getDisplayNameOrName(xpdlActivity));

			return intermediateThrowEventElement;
		}
		/*
		 * IntermediateCatchEvent
		 */
		{
			GatIntermediateCatchEventElement intermediateCatchEventElement = new GatIntermediateCatchEventElement(
					xpdlActivity.getId(), xpdlActivity.getName(), GatModelUtil.getDisplayNameOrName(xpdlActivity));

			return intermediateCatchEventElement;
		}
	}

}
