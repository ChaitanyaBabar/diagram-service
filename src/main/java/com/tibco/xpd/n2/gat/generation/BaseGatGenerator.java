/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatDefinitionsElement;
import com.tibco.xpd.n2.gat.model.GatModelElement;
import com.tibco.xpd.n2.gat.model.GatProcessElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPDiagramElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPEdgeElement;
import com.tibco.xpd.n2.gat.model.diagram.GatPointElement;
import com.tibco.xpd.n2.gat.util.GatModelUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.ViewType;

/**
 * Base class for all Graphical Audit Trail model generators
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
public abstract class BaseGatGenerator
{
	/**
	 * The width (in pixels) of the Pool header
	 */
	public static final int		POOL_HEADER_WIDTH			= 29;

	/**
	 * The Lane content margin X&Y (in pixels)
	 */
	public static final int		LANE_CONTENT_MARGIN	= 2;

	/**
	 * The width (in pixels) of the Lane header
	 */
	public static final int		LANE_HEADER_WIDTH	= 29;

	/**
	 * The Lane content margin (in pixels) on RHS of right-most activity
	 */
	public static final int		LANE_RHS_OBJECT_MARGIN_X	= 20;

	/**
	 * The minimum Lane width (in pixels)
	 */
	public static final int		MINIMUM_LANE_WIDTH	= 700 - POOL_HEADER_WIDTH
			- LANE_HEADER_WIDTH;

	/**
	 * The width (in pixels) of the embedded sub-process left/right borders
	 */
	public static final int		EMBEDDED_SUBPROCESS_BORDER_WIDTH		= 10;

	/**
	 * The height(in pixels) of the embedded sub-process top border
	 */
	public static final int		EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT	= 20;

	/**
	 * The parent object for this GAT model generator object
	 */
	private BaseGatGenerator	parent;

	/**
	 * Constructor
	 *
	 * @param parent
	 */
	public BaseGatGenerator(BaseGatGenerator parent)
	{
		super();
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public BaseGatGenerator getParent()
	{
		return parent;
	}

	/**
	 * @return The root {@link GatDefinitionsElement}
	 */
	public GatDefinitionsElement getGatDefinitionsElement()
	{
		return getParent().getGatDefinitionsElement();
	}

	/**
	 * @return The root {@link GatProcessElement}
	 */
	public GatProcessElement getGatProcessElement()
	{
		return getParent().getGatProcessElement();
	}

	/**
	 * @return The root {@link GatProcessElement}
	 */
	public GatBPDiagramElement getGatDiagramElement()
	{
		return getParent().getGatDiagramElement();
	}

	/**
	 * @return the xpdlProcess
	 */
	public Process getXpdlProcess()
	{
		return getParent().getXpdlProcess();
	}

	/**
	 * Generate the Graphical Audit Trail model.
	 *
	 * @return {@link GatModelElement}
	 */
	public abstract GatModelElement generate();

	/**
	 * Check whether the given FlowContainer is inside a collapsed embedded sub-process.
	 *
	 * @param flowContainer
	 * @return true if the FlowContainer has a collapsed sub-process ancestor
	 */
	protected boolean isInsideCollapsedSubProcess(FlowContainer flowContainer)
	{
		if (flowContainer == null || flowContainer instanceof Process)
		{
			return false;
		}

		FlowContainer current = flowContainer;

		while (!(current instanceof Process))
		{
			ActivitySet activitySet = (ActivitySet) current;

			Activity parentSubProc = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
					activitySet.getId());

			if (parentSubProc != null && parentSubProc.getBlockActivity() != null
					&& ViewType.COLLAPSED.equals(parentSubProc.getBlockActivity().getView()))
			{
				return true;
			}

			current = parentSubProc.getFlowContainer();
		}

		return false;
	}

	/**
	 * Convert a point from content-relative coordinates to GAT figure-relative coordinates for an element inside a
	 * collapsed sub-process.
	 *
	 * @param x
	 *            content-relative X coordinate
	 * @param y
	 *            content-relative Y coordinate
	 * @param flowContainer
	 *            the FlowContainer (ActivitySet) containing the element
	 * @return double array {gatX, gatY} in collapsed sub-process figure coordinates
	 */
	protected double[] adjustPointForCollapsedSubProcess(double x, double y, FlowContainer flowContainer)
	{
		double adjustedX = x;
		double adjustedY = y;

		FlowContainer current = flowContainer;

		while (!(current instanceof Process))
		{
			ActivitySet activitySet = (ActivitySet) current;

			Activity parentSubProc = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
					activitySet.getId());

			if (ViewType.COLLAPSED.equals(parentSubProc.getBlockActivity().getView()))
			{
				adjustedX += EMBEDDED_SUBPROCESS_BORDER_WIDTH;
				adjustedY += EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT;
				break;
			}

			NodeGraphicsInfo parentNgi = GatModelUtil.getNodeGraphicsInfo(parentSubProc);
			double parentTopLeftX = parentNgi.getCoordinates().getXCoordinate() - (parentNgi.getWidth() / 2);
			double parentTopLeftY = parentNgi.getCoordinates().getYCoordinate() - (parentNgi.getHeight() / 2);

			adjustedX += parentTopLeftX + EMBEDDED_SUBPROCESS_BORDER_WIDTH;
			adjustedY += parentTopLeftY + EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT;

			current = parentSubProc.getFlowContainer();
		}

		return new double[]{adjustedX, adjustedY};
	}

	/**
	 * Adjusts content-relative coordinates to GAT coordinates for a point inside an expanded sub-process.
	 *
	 * @param x
	 *            content-relative X coordinate
	 * @param y
	 *            content-relative Y coordinate
	 * @param flowContainer
	 *            the FlowContainer (ActivitySet) containing the element
	 * @return double array {gatX, gatY} in GAT coordinates
	 */
	protected double[] adjustToGatCoordSystemForExpandedSubProcess(double x, double y, FlowContainer flowContainer)
	{
		double adjustedX = x;
		double adjustedY = y;

		FlowContainer current = flowContainer;
		Activity outermostParent = null;

		while (!(current instanceof Process))
		{
			ActivitySet activitySet = (ActivitySet) current;

			Activity parentSubProc = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
					activitySet.getId());

			outermostParent = parentSubProc;

			NodeGraphicsInfo parentNgi = GatModelUtil.getNodeGraphicsInfo(parentSubProc);
			double parentTopLeftX = parentNgi.getCoordinates().getXCoordinate() - (parentNgi.getWidth() / 2);
			double parentTopLeftY = parentNgi.getCoordinates().getYCoordinate() - (parentNgi.getHeight() / 2);

			adjustedX += parentTopLeftX + EMBEDDED_SUBPROCESS_BORDER_WIDTH;
			adjustedY += parentTopLeftY + EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT;

			current = parentSubProc.getFlowContainer();
		}

		if (outermostParent != null)
		{
			Lane laneAncestor = GatModelUtil.getLaneAncestor(outermostParent);

			if (laneAncestor != null)
			{
				adjustedX += POOL_HEADER_WIDTH + LANE_HEADER_WIDTH + 2;

				Pool pool = laneAncestor.getParentPool();

				for (Lane lane : pool.getLanes())
				{
					if (lane.equals(laneAncestor))
					{
						break;
					}

					NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(lane);

					adjustedY += ngi.getHeight() + LANE_CONTENT_MARGIN + 1;
				}
			}
		}

		return new double[]{adjustedX, adjustedY};
	}

	/**
	 * Add Manhattan-style waypoints between source and target points to the given BPEdge element.
	 *
	 * @param bpEdgeElement
	 * @param sx source X in GAT coordinates
	 * @param sy source Y in GAT coordinates
	 * @param tx target X in GAT coordinates
	 * @param ty target Y in GAT coordinates
	 * @param horizontal true if the connection exits/enters horizontally
	 */
	protected void addManhattanWaypoints(GatBPEdgeElement bpEdgeElement, double sx, double sy, double tx, double ty,
			boolean horizontal)
	{
		if (Math.abs(sy - ty) < 1.0 || Math.abs(sx - tx) < 1.0)
		{
			bpEdgeElement.addWayPoint(new GatPointElement(sx, sy));
			bpEdgeElement.addWayPoint(new GatPointElement(tx, ty));
		}
		else if (horizontal)
		{
			double midX = (sx + tx) / 2;

			bpEdgeElement.addWayPoint(new GatPointElement(sx, sy));
			bpEdgeElement.addWayPoint(new GatPointElement(midX, sy));
			bpEdgeElement.addWayPoint(new GatPointElement(midX, ty));
			bpEdgeElement.addWayPoint(new GatPointElement(tx, ty));
		}
		else
		{
			double midY = (sy + ty) / 2;

			bpEdgeElement.addWayPoint(new GatPointElement(sx, sy));
			bpEdgeElement.addWayPoint(new GatPointElement(sx, midY));
			bpEdgeElement.addWayPoint(new GatPointElement(tx, midY));
			bpEdgeElement.addWayPoint(new GatPointElement(tx, ty));
		}
	}
}
