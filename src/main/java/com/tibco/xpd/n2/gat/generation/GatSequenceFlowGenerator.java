/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatSequenceFlowElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPEdgeElement;
import com.tibco.xpd.n2.gat.model.diagram.GatPointElement;
import com.tibco.xpd.n2.gat.util.GatModelUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.ConditionType;
import com.tibco.xpd.xpdl2.ConnectorGraphicsInfo;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Transition;

/**
 * Generator for Graphical Audit Trail Sequence Flow elements
 *
 * @author Sid Allway
 * @since 24 Dec 2025
 */
public class GatSequenceFlowGenerator extends GatFlowElementGenerator
{
	private Transition xpdlSequenceFlow;

	public GatSequenceFlowGenerator(BaseGatGenerator parent, Transition sequenceFlow)
	{
		super(parent);

		this.xpdlSequenceFlow = sequenceFlow;
	}

	@Override
	public GatSequenceFlowElement generate()
	{
		GatSequenceFlowElement sequenceFlowElement = new GatSequenceFlowElement(xpdlSequenceFlow.getId(),
				GatModelUtil.getDisplayName(xpdlSequenceFlow));

		sequenceFlowElement.setSourceRef(xpdlSequenceFlow.getFrom());
		sequenceFlowElement.setTargetRef(xpdlSequenceFlow.getTo());

		if (xpdlSequenceFlow.getCondition() != null
				&& !ConditionType.OTHERWISE_LITERAL.equals(xpdlSequenceFlow.getCondition().getType()))
		{
			sequenceFlowElement.setIsCondition();
		}

		return sequenceFlowElement;
	}

	public GatBPEdgeElement generateBPEdge(GatSequenceFlowElement sequenceFlowElement)
	{
		GatBPEdgeElement bpEdgeElement = new GatBPEdgeElement(sequenceFlowElement);

		if (isInsideCollapsedSubProcess(xpdlSequenceFlow.getFlowContainer()))
		{
			Activity sourceActivity = xpdlSequenceFlow.getFlowContainer()
					.getActivity(xpdlSequenceFlow.getFrom());
			NodeGraphicsInfo sourceNgi = GatModelUtil.getNodeGraphicsInfo(sourceActivity);

			Activity targetActivity = xpdlSequenceFlow.getFlowContainer()
					.getActivity(xpdlSequenceFlow.getTo());
			NodeGraphicsInfo targetNgi = GatModelUtil.getNodeGraphicsInfo(targetActivity);

			double sourceCenterX = sourceNgi.getCoordinates().getXCoordinate();
			double sourceCenterY = sourceNgi.getCoordinates().getYCoordinate();
			double targetCenterX = targetNgi.getCoordinates().getXCoordinate();
			double targetCenterY = targetNgi.getCoordinates().getYCoordinate();

			double dx = targetCenterX - sourceCenterX;
			double dy = targetCenterY - sourceCenterY;

			DoublePoint sourceExit;
			DoublePoint targetEntry;
			boolean horizontal;

			if (Math.abs(dx) >= Math.abs(dy))
			{
				horizontal = true;

				if (dx >= 0)
				{
					sourceExit = new DoublePoint(sourceCenterX + (sourceNgi.getWidth() / 2), sourceCenterY);
					targetEntry = new DoublePoint(targetCenterX - (targetNgi.getWidth() / 2), targetCenterY);
				}
				else
				{
					sourceExit = new DoublePoint(sourceCenterX - (sourceNgi.getWidth() / 2), sourceCenterY);
					targetEntry = new DoublePoint(targetCenterX + (targetNgi.getWidth() / 2), targetCenterY);
				}
			}
			else
			{
				horizontal = false;

				if (dy >= 0)
				{
					sourceExit = new DoublePoint(sourceCenterX, sourceCenterY + (sourceNgi.getHeight() / 2));
					targetEntry = new DoublePoint(targetCenterX, targetCenterY - (targetNgi.getHeight() / 2));
				}
				else
				{
					sourceExit = new DoublePoint(sourceCenterX, sourceCenterY - (sourceNgi.getHeight() / 2));
					targetEntry = new DoublePoint(targetCenterX, targetCenterY + (targetNgi.getHeight() / 2));
				}
			}

			DoublePoint sourceGat = adjustToGatCoordSystem(sourceExit, null);
			DoublePoint targetGat = adjustToGatCoordSystem(targetEntry, null);

			addManhattanWaypoints(bpEdgeElement, sourceGat.getX(), sourceGat.getY(), targetGat.getX(),
					targetGat.getY(), horizontal);
		}
		else
		{
			ConnectorGraphicsInfo cgi = GatModelUtil.getConnectorGraphicsInfo(xpdlSequenceFlow,
					GatModelUtil.LITERALPOINTS_INFO_IDSUFFIX);

			if (cgi != null)
			{
				FlowContainer flowContainer = xpdlSequenceFlow.getFlowContainer();

				if (flowContainer instanceof ActivitySet)
				{
					addWaypointsForExpandedSubProcess(bpEdgeElement, cgi, flowContainer);
				}
				else
				{
					for (Coordinates coords : cgi.getCoordinates())
					{
						bpEdgeElement.addWayPoint(
								new GatPointElement(coords.getXCoordinate() - 5,
										coords.getYCoordinate() - 5));
					}
				}
			}
		}

		return bpEdgeElement;
	}

	private void addWaypointsForExpandedSubProcess(GatBPEdgeElement bpEdgeElement, ConnectorGraphicsInfo cgi,
			FlowContainer flowContainer)
	{
		Activity sourceActivity = flowContainer.getActivity(xpdlSequenceFlow.getFrom());
		Activity targetActivity = flowContainer.getActivity(xpdlSequenceFlow.getTo());

		if (sourceActivity == null || targetActivity == null)
		{
			for (Coordinates coords : cgi.getCoordinates())
			{
				bpEdgeElement.addWayPoint(
						new GatPointElement(coords.getXCoordinate() - 5,
								coords.getYCoordinate() - 5));
			}
			return;
		}

		NodeGraphicsInfo sourceNgi = GatModelUtil.getNodeGraphicsInfo(sourceActivity);
		DoublePoint sourceTopLeft = new DoublePoint(
				sourceNgi.getCoordinates().getXCoordinate() - (sourceNgi.getWidth() / 2),
				sourceNgi.getCoordinates().getYCoordinate() - (sourceNgi.getHeight() / 2));
		DoublePoint sourceGat = adjustToGatCoordSystem(sourceTopLeft, GatModelUtil.getLaneAncestor(sourceActivity));

		NodeGraphicsInfo targetNgi = GatModelUtil.getNodeGraphicsInfo(targetActivity);
		DoublePoint targetTopLeft = new DoublePoint(
				targetNgi.getCoordinates().getXCoordinate() - (targetNgi.getWidth() / 2),
				targetNgi.getCoordinates().getYCoordinate() - (targetNgi.getHeight() / 2));
		DoublePoint targetGat = adjustToGatCoordSystem(targetTopLeft, GatModelUtil.getLaneAncestor(targetActivity));

		int count = cgi.getCoordinates().size();
		int lastIdx = count - 1;

		double sourceDockY = computeDockY(cgi, 0, Math.min(1, lastIdx), sourceGat.getY(), sourceNgi.getHeight(), true);
		double targetDockY = computeDockY(cgi, Math.max(0, lastIdx - 1), lastIdx, targetGat.getY(),
				targetNgi.getHeight(), false);

		double originalSourceY = cgi.getCoordinates().get(0).getYCoordinate() - 5;
		double originalTargetY = cgi.getCoordinates().get(lastIdx).getYCoordinate() - 5;

		double sourceYCorrection = sourceDockY - originalSourceY;
		double targetYCorrection = targetDockY - originalTargetY;

		if (Math.abs(sourceYCorrection - targetYCorrection) < 2.0)
		{
			for (int i = 0; i < count; i++)
			{
				Coordinates coords = cgi.getCoordinates().get(i);
				double wpX = coords.getXCoordinate() - 5;
				double wpY;

				if (i == 0)
				{
					wpY = sourceDockY;
				}
				else if (i == lastIdx)
				{
					wpY = targetDockY;
				}
				else
				{
					wpY = (coords.getYCoordinate() - 5) + sourceYCorrection;
				}

				bpEdgeElement.addWayPoint(new GatPointElement(wpX, wpY));
			}
		}
		else
		{
			double yCorrection = Math.abs(sourceYCorrection) <= Math.abs(targetYCorrection) ? sourceYCorrection
					: targetYCorrection;

			for (int i = 0; i < count; i++)
			{
				Coordinates coords = cgi.getCoordinates().get(i);
				double wpX = coords.getXCoordinate() - 5;
				double wpY = (coords.getYCoordinate() - 5) + yCorrection;

				bpEdgeElement.addWayPoint(new GatPointElement(wpX, wpY));
			}
		}
	}

	private double computeDockY(ConnectorGraphicsInfo cgi, int fromIdx, int toIdx, double shapeGatY,
			double shapeHeight, boolean isSource)
	{
		double dx = cgi.getCoordinates().get(toIdx).getXCoordinate()
				- cgi.getCoordinates().get(fromIdx).getXCoordinate();
		double dy = cgi.getCoordinates().get(toIdx).getYCoordinate()
				- cgi.getCoordinates().get(fromIdx).getYCoordinate();

		if (Math.abs(dx) >= Math.abs(dy))
		{
			return shapeGatY + (shapeHeight / 2);
		}

		if (isSource)
		{
			return dy > 0 ? shapeGatY + shapeHeight : shapeGatY;
		}
		else
		{
			return dy > 0 ? shapeGatY : shapeGatY + shapeHeight;
		}
	}

	@Override
	protected FlowContainer getXpdlFlowContainer()
	{
		return xpdlSequenceFlow.getFlowContainer();
	}

}
