/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import java.util.List;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ConditionType;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Transition;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Base class for all Graphical Audit Trail Flow Node generators.
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
public abstract class GatFlowNodeGenerator extends GatFlowElementGenerator
{
	/**
	 * The source activity
	 */
	private Activity xpdlActivity;

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatFlowNodeGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent);

		this.xpdlActivity = xpdlActivity;
	}

	/**
	 * @return the xpdlActivity
	 */
	public Activity getXpdlActivity()
	{
		return xpdlActivity;
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatFlowNodeGenerator#generate()
	 *
	 * @return
	 */
	@Override
	public final GatFlowNodeElement generate()
	{
		/*
		 * Get sub-class to create appropriate model element for the activity
		 */
		GatFlowNodeElement flowNodeElement = generateFlowNode(xpdlActivity);

		/*
		 * Set the "default" sequence flow id property if there is an outgoing default sequence flow
		 */
		List<Transition> outgoingTransitions = GatModelUtil.getOutgoingTransitions(xpdlActivity.getId(),
				xpdlActivity.getFlowContainer());

		for (Transition transition : outgoingTransitions)
		{
			if (transition.getCondition() != null
					&& ConditionType.OTHERWISE_LITERAL.equals(transition.getCondition().getType()))
			{
				flowNodeElement.setDefault(transition.getId());
			}
		}

		return flowNodeElement;
	}

	/**
	 * Sub-class must generate a {@link GatFlowNodeElement} repreenting the XPDL activity.
	 *
	 * @param xpdlActivity
	 *
	 * @return {@link GatFlowNodeElement}
	 */
	public abstract GatFlowNodeElement generateFlowNode(Activity xpdlActivity);

	/**
	 * Generate the shape related to the context XPDL activity for which the given GAT flow node element.
	 *
	 * @param flowNodeElement
	 *
	 * @return {@link GatBPShapeElement}
	 */
	public GatBPShapeElement generateShape(GatFlowNodeElement flowNodeElement)
	{
		NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlActivity);

		Coordinates coords = ngi.getCoordinates();

		GatBPShapeElement shapeElement = new GatBPShapeElement(flowNodeElement);

		/*
		 * Convert activity x,y from 'centre of object' as is used in XPDL to 'top left' of activity
		 */
		DoublePoint elementCoords = new DoublePoint(coords.getXCoordinate() - (ngi.getWidth() / 2),
				coords.getYCoordinate() - (ngi.getHeight() / 2));

		/* Adjust the bounds from XPDL to GAT coordinate system */
		DoublePoint gatCoords = adjustToGatCoordSystem(elementCoords, GatModelUtil.getLaneAncestor(xpdlActivity));

		/*
		 * Set the adjusted coords
		 */
		GatBoundsElement boundsElement = new GatBoundsElement(gatCoords.getX(), gatCoords.getY(), ngi.getWidth(),
				ngi.getHeight());

		shapeElement.setBounds(boundsElement);

		return shapeElement;
	}


	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatFlowElementGenerator#getXpdlFlowContainer()
	 *
	 * @return
	 */
	@Override
	protected FlowContainer getXpdlFlowContainer()
	{
		return xpdlActivity.getFlowContainer();
	}

}
