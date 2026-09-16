/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatAssociationElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPEdgeElement;
import com.tibco.xpd.n2.gat.model.diagram.GatPointElement;
import com.tibco.xpd.n2.gat.util.GatModelUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.Association;
import com.tibco.xpd.xpdl2.AssociationDirectionType;
import com.tibco.xpd.xpdl2.ConnectorGraphicsInfo;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;

/**
 * Graphical Audit Trail generator for Association edges.
 *
 * @author cbabar
 * @since Jul 16, 2026
 */
@SuppressWarnings("nls")
public class GatAssociationGenerator extends BaseGatGenerator
{
	private Association		xpdlAssociation;

	private FlowContainer	flowContainer;

	public GatAssociationGenerator(BaseGatGenerator parent, Association xpdlAssociation, FlowContainer flowContainer)
	{
		super(parent);
		this.xpdlAssociation = xpdlAssociation;
		this.flowContainer = flowContainer;
	}

	@Override
	public GatAssociationElement generate()
	{
		GatAssociationElement associationElement = new GatAssociationElement(xpdlAssociation.getId());

		associationElement.setSourceRef(xpdlAssociation.getSource());
		associationElement.setTargetRef(xpdlAssociation.getTarget());

		if (xpdlAssociation.isSetAssociationDirection())
		{
			AssociationDirectionType direction = xpdlAssociation.getAssociationDirection();

			if (AssociationDirectionType.TO_LITERAL == direction)
			{
				associationElement.setAssociationDirection("One");
			}
			else if (AssociationDirectionType.BOTH_LITERAL == direction)
			{
				associationElement.setAssociationDirection("Both");
			}
			else
			{
				associationElement.setAssociationDirection("None");
			}
		}

		return associationElement;
	}

	public GatBPEdgeElement generateBPEdge(GatAssociationElement associationElement)
	{
		GatBPEdgeElement bpEdgeElement = new GatBPEdgeElement(associationElement);

		if (flowContainer != null && isInsideCollapsedSubProcess(flowContainer))
		{
			double[] sourceInfo = getEndpointInfo(xpdlAssociation.getSource());
			double[] targetInfo = getEndpointInfo(xpdlAssociation.getTarget());

			if (sourceInfo != null && targetInfo != null)
			{
				double dx = targetInfo[0] - sourceInfo[0];
				double dy = targetInfo[1] - sourceInfo[1];

				double sourceExitX;
				double sourceExitY;
				double targetEntryX;
				double targetEntryY;
				boolean horizontal;

				if (Math.abs(dx) >= Math.abs(dy))
				{
					horizontal = true;

					if (dx >= 0)
					{
						sourceExitX = sourceInfo[0] + (sourceInfo[2] / 2);
						targetEntryX = targetInfo[0] - (targetInfo[2] / 2);
					}
					else
					{
						sourceExitX = sourceInfo[0] - (sourceInfo[2] / 2);
						targetEntryX = targetInfo[0] + (targetInfo[2] / 2);
					}

					sourceExitY = sourceInfo[1];
					targetEntryY = targetInfo[1];
				}
				else
				{
					horizontal = false;
					sourceExitX = sourceInfo[0];
					targetEntryX = targetInfo[0];

					if (dy >= 0)
					{
						sourceExitY = sourceInfo[1] + (sourceInfo[3] / 2);
						targetEntryY = targetInfo[1] - (targetInfo[3] / 2);
					}
					else
					{
						sourceExitY = sourceInfo[1] - (sourceInfo[3] / 2);
						targetEntryY = targetInfo[1] + (targetInfo[3] / 2);
					}
				}

				double[] sourceGat = adjustPointForCollapsedSubProcess(sourceExitX, sourceExitY, flowContainer);
				double[] targetGat = adjustPointForCollapsedSubProcess(targetEntryX, targetEntryY, flowContainer);

				addManhattanWaypoints(bpEdgeElement, sourceGat[0], sourceGat[1], targetGat[0], targetGat[1],
						horizontal);
			}
		}
		else
		{
			ConnectorGraphicsInfo cgi = GatModelUtil.getConnectorGraphicsInfo(xpdlAssociation,
					GatModelUtil.LITERALPOINTS_INFO_IDSUFFIX);

			if (cgi != null)
			{
				if (flowContainer instanceof ActivitySet)
				{
					addWaypointsForExpandedSubProcess(bpEdgeElement, cgi);
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

	private void addWaypointsForExpandedSubProcess(GatBPEdgeElement bpEdgeElement, ConnectorGraphicsInfo cgi)
	{
		double[] sourceInfo = getEndpointInfo(xpdlAssociation.getSource());
		double[] targetInfo = getEndpointInfo(xpdlAssociation.getTarget());

		if (sourceInfo == null || targetInfo == null)
		{
			for (Coordinates coords : cgi.getCoordinates())
			{
				bpEdgeElement.addWayPoint(
						new GatPointElement(coords.getXCoordinate() - 5,
								coords.getYCoordinate() - 5));
			}
			return;
		}

		double sourceTopLeftX = sourceInfo[0] - (sourceInfo[2] / 2);
		double sourceTopLeftY = sourceInfo[1] - (sourceInfo[3] / 2);
		double[] sourceGat = adjustToGatCoordSystemForExpandedSubProcess(sourceTopLeftX, sourceTopLeftY, flowContainer);

		double targetTopLeftX = targetInfo[0] - (targetInfo[2] / 2);
		double targetTopLeftY = targetInfo[1] - (targetInfo[3] / 2);
		double[] targetGat = adjustToGatCoordSystemForExpandedSubProcess(targetTopLeftX, targetTopLeftY, flowContainer);

		int count = cgi.getCoordinates().size();
		int lastIdx = count - 1;

		double sourceDockY = computeDockY(cgi, 0, Math.min(1, lastIdx), sourceGat[1], sourceInfo[3], true);
		double targetDockY = computeDockY(cgi, Math.max(0, lastIdx - 1), lastIdx, targetGat[1], targetInfo[3], false);

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

	private double[] getEndpointInfo(String elementId)
	{
		if (flowContainer instanceof ActivitySet)
		{
			Activity activity = ((ActivitySet) flowContainer).getActivity(elementId);

			if (activity != null)
			{
				NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(activity);

				return new double[]{ngi.getCoordinates().getXCoordinate(), ngi.getCoordinates().getYCoordinate(),
						ngi.getWidth(), ngi.getHeight()};
			}
		}

		for (Artifact artifact : GatModelUtil.getAllArtifactsInProcess(getXpdlProcess()))
		{
			if (elementId.equals(artifact.getId()))
			{
				NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(artifact);

				return new double[]{ngi.getCoordinates().getXCoordinate(), ngi.getCoordinates().getYCoordinate(),
						ngi.getWidth(), ngi.getHeight()};
			}
		}

		return null;
	}
}
