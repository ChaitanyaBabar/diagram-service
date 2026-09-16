/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.stubs.Point;
import com.tibco.xpd.n2.gat.stubs.PointList;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatCancelEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatCompensateEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatErrorEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatEventElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.GatLinkEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatMessageEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatSignalEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatTerminateEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.GatTimerEventDefinitionElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.n2.gat.util.GatEventObjectUtil;
import com.tibco.xpd.n2.gat.adapters.EventTriggerType;
import com.tibco.xpd.n2.gat.util.GatLineUtilities;
import com.tibco.xpd.n2.gat.util.GatModelUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;

/**
 * Base Graphical Audit Trail Generator for Events
 *
 * @author Sid Allway
 * @since Jan 2025
 */
@SuppressWarnings("nls")
public abstract class GatEventGenerator extends GatFlowNodeGenerator
{
	protected GatEventGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	protected abstract GatEventElement generateEventActivity(Activity xpdlActivity);

	@Override
	public final GatFlowNodeElement generateFlowNode(Activity xpdlActivity)
	{
		GatEventElement eventElement = generateEventActivity(xpdlActivity);

		EventTriggerType eventTriggerType = GatEventObjectUtil.getEventTriggerType(xpdlActivity);

		if (EventTriggerType.EVENT_SIGNAL_CATCH_LITERAL.equals(eventTriggerType)
				|| EventTriggerType.EVENT_SIGNAL_THROW_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatSignalEventDefinitionElement("SignalEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_TIMER_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatTimerEventDefinitionElement("TimerEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_ERROR_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatErrorEventDefinitionElement("ErrorEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_COMPENSATION_CATCH_LITERAL.equals(eventTriggerType)
				|| EventTriggerType.EVENT_COMPENSATION_THROW_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatCompensateEventDefinitionElement("CompensateEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_CANCEL_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatCancelEventDefinitionElement("CancelEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_LINK_CATCH_LITERAL.equals(eventTriggerType)
				|| EventTriggerType.EVENT_LINK_THROW_LITERAL.equals(eventTriggerType))
		{
			GatLinkEventDefinitionElement linkEventDefinition = new GatLinkEventDefinitionElement(
					"LinkEventDefinition_" + xpdlActivity.getId());
			eventElement.addEventDefintion(linkEventDefinition);

			if (EventTriggerType.EVENT_LINK_CATCH_LITERAL.equals(eventTriggerType))
			{
				linkEventDefinition.setName("Link_" + xpdlActivity.getId());
			}
			else
			{
				linkEventDefinition.setName("Link_" + GatEventObjectUtil.getLinkEventId(xpdlActivity));
			}
		}
		else if (EventTriggerType.EVENT_TERMINATE_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatTerminateEventDefinitionElement("TerminateEventDefinition_" + xpdlActivity.getId()));
		}
		else if (EventTriggerType.EVENT_MESSAGE_CATCH_LITERAL.equals(eventTriggerType)
				|| EventTriggerType.EVENT_MESSAGE_THROW_LITERAL.equals(eventTriggerType))
		{
			eventElement.addEventDefintion(
					new GatMessageEventDefinitionElement("MessageEventDefinition_" + xpdlActivity.getId()));
		}

		return eventElement;
	}

	@Override
	public GatBPShapeElement generateShape(GatFlowNodeElement flowNodeElement)
	{
		Activity xpdlActivity = getXpdlActivity();

		if (GatEventObjectUtil.isAttachedToTask(xpdlActivity))
		{
			return generateBoundaryEventShape(flowNodeElement, xpdlActivity);
		}

		GatBPShapeElement shape = super.generateShape(flowNodeElement);

		GatBoundsElement bounds = shape.getBounds();

		if (bounds.getWidth() != 27)
		{
			bounds.setX((bounds.getX() + (bounds.getWidth() / 2))
					- ((double) 27 / 2));
			bounds.setWidth((double) 27);
		}

		if (bounds.getHeight() != 27)
		{
			bounds.setY((bounds.getY() + (bounds.getHeight() / 2))
					- ((double) 27 / 2));
			bounds.setHeight((double) 27);
		}

		return shape;
	}

	private GatBPShapeElement generateBoundaryEventShape(GatFlowNodeElement flowNodeElement, Activity xpdlActivity)
	{
		double borderPosition = 50.0;

		NodeGraphicsInfo eventNgi = GatModelUtil.getNodeGraphicsInfo(xpdlActivity,
				GatModelUtil.BORDER_EVENTPOS_IDSUFFIX);

		if (eventNgi != null)
		{
			Coordinates eventCoords = eventNgi.getCoordinates();

			if (eventCoords != null && eventCoords.isSetXCoordinate())
			{
				borderPosition = eventCoords.getXCoordinate();
			}
		}

		Activity taskAttachedTo = GatEventObjectUtil.getTaskAttachedTo(xpdlActivity);
		NodeGraphicsInfo taskNgi = GatModelUtil.getNodeGraphicsInfo(taskAttachedTo);
		Coordinates taskCoords = taskNgi.getCoordinates();

		double taskX = taskCoords.getXCoordinate() - (taskNgi.getWidth() / 2);
		double taskY = taskCoords.getYCoordinate() - (taskNgi.getHeight() / 2);

		double taskWidth = taskNgi.getWidth();
		double taskHeight = taskNgi.getHeight();

		PointList taskBorderPoints = new PointList(5);
		taskBorderPoints.addPoint(new Point(taskX + taskWidth, taskY));
		taskBorderPoints.addPoint(new Point(taskX + taskWidth, taskY + taskHeight));
		taskBorderPoints.addPoint(new Point(taskX, taskY + taskHeight));
		taskBorderPoints.addPoint(new Point(taskX, taskY));
		taskBorderPoints.addPoint(new Point(taskX + taskWidth, taskY));

		Point eventPosition = GatLineUtilities.getLinePointFromPortion(taskBorderPoints, borderPosition);

		DoublePoint gatCoords = adjustToGatCoordSystem(
				new DoublePoint(
						eventPosition.preciseX() - ((double) 27 / 2),
						eventPosition.preciseY() - ((double) 27 / 2)),
				GatModelUtil.getLaneAncestor(taskAttachedTo));

		GatBoundsElement gatBounds = new GatBoundsElement(gatCoords.getX(), gatCoords.getY(),
				(double) 27,
				(double) 27);
		GatBPShapeElement gatShape = new GatBPShapeElement(flowNodeElement);
		gatShape.setBounds(gatBounds);

		return gatShape;
	}

}
