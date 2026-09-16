/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatComplexGatewayElement;
import com.tibco.xpd.n2.gat.model.GatEventBasedGatewayElement;
import com.tibco.xpd.n2.gat.model.GatExclusiveGatewayElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.GatInclusiveGatewayElement;
import com.tibco.xpd.n2.gat.model.GatParallelGatewayElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ExclusiveType;
import com.tibco.xpd.xpdl2.JoinSplitType;
import com.tibco.xpd.xpdl2.Route;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for Gateways
 *
 * @author Sid Allway
 * @since 23 Dec 2025
 */
@SuppressWarnings("nls")
public class GatGatewayGenerator extends GatFlowNodeGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatGatewayGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatActivityGenerator#generateFlowNode(com.tibco.xpd.xpdl2.Activity)
	 *
	 * @param xpdlActivity
	 * @return
	 */
	@Override
	public GatFlowNodeElement generateFlowNode(Activity xpdlActivity)
	{
		Route route = xpdlActivity.getRoute();

		if (route != null)
		{
			JoinSplitType gatewayType = route.getGatewayType();

			if (JoinSplitType.EXCLUSIVE_LITERAL.equals(gatewayType))
			{
				ExclusiveType exclusiveType = route.getExclusiveType();

				if (ExclusiveType.EVENT.equals(exclusiveType))
				{
					return new GatEventBasedGatewayElement(xpdlActivity.getId(), xpdlActivity.getName(),
							GatModelUtil.getDisplayNameOrName(xpdlActivity));
				}
				else
				{
					return new GatExclusiveGatewayElement(xpdlActivity.getId(), xpdlActivity.getName(),
							GatModelUtil.getDisplayNameOrName(xpdlActivity));
				}
			}
			else if (JoinSplitType.INCLUSIVE_LITERAL.equals(gatewayType))
			{
				return new GatInclusiveGatewayElement(xpdlActivity.getId(), xpdlActivity.getName(),
						GatModelUtil.getDisplayNameOrName(xpdlActivity));
			}
			else if (JoinSplitType.COMPLEX_LITERAL.equals(gatewayType))
			{
				return new GatComplexGatewayElement(xpdlActivity.getId(), xpdlActivity.getName(),
						GatModelUtil.getDisplayNameOrName(xpdlActivity));
			}
			else if (JoinSplitType.PARALLEL_LITERAL.equals(gatewayType))
			{
				return new GatParallelGatewayElement(xpdlActivity.getId(), xpdlActivity.getName(),
						GatModelUtil.getDisplayNameOrName(xpdlActivity));
			}

		}

		return null;
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatFlowNodeGenerator#generateShape(com.tibco.xpd.n2.gat.model.GatFlowNodeElement)
	 *
	 * @param flowNodeElement
	 * @return
	 */
	@Override
	public GatBPShapeElement generateShape(GatFlowNodeElement flowNodeElement)
	{
		/*
		 * Need to override default generate shape, because a defect in process editor _sometimes_ leave gateways with
		 * different sizes (size including label?) when they are moved inside embedded sub-process.
		 *
		 * So create the default shape and then overwrite the shape bounds with the default fixed size of events
		 */
		GatBPShapeElement shape = super.generateShape(flowNodeElement);

		GatBoundsElement bounds = shape.getBounds();

		if (bounds.getWidth() != 41.0)
		{
			/* X-coord will be top-left of object according to the current width, so adjust that first */
			bounds.setX((bounds.getX() + (bounds.getWidth() / 2)) - (41.0 / 2));
			bounds.setWidth(41.0);
		}

		if (bounds.getHeight() != 43.0)
		{
			/* Y-coord will be top-left of object according to the current width, so adjust that first */
			bounds.setY((bounds.getY() + (bounds.getHeight() / 2)) - (43.0 / 2));
			bounds.setHeight(43.0);
		}

		return shape;
	}

}
