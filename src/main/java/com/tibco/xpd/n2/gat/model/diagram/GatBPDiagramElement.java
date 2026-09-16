/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model.diagram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatIdElement;

/**
 * The Graphical Audit Trail model BPDiagram element
 *
 * @author Sid Allway
 * @since 16 Dec 2025
 */
@SuppressWarnings("nls")
public class GatBPDiagramElement extends LinkedHashMap<String, Object>
{
	private static final long	serialVersionUID	= 4255202088630487283L;

	/**
	 * The "plane" element in the BPDiagram. This contains all the {@link GatDiagramElement}'s representing objects in
	 * the diagram.
	 */
	private GatPlaneElement		planeElement;

	/**
	 * Constructor
	 *
	 * @param flowContainerElement
	 *            The process or collapsed embedded sub-process element that this is the diagram model for.
	 */
	public GatBPDiagramElement(GatIdElement flowContainerElement)
	{
		this.put("$type", "bpdi:BPDiagram");

		/*
		 * Each diagram has a single "plane" with a "planeElement" array of elements representing the elements of the
		 * diagram. No need to expose this to the consumer of the class, we'll just set things up so that the consumer
		 * creates this diagram and adds elements directly here.
		 */
		planeElement = new GatPlaneElement(flowContainerElement);
		put("plane", planeElement);
	}

	/**
	 * Add a diagram element representing a diagram object (activity, flow etc) to the diagram plane
	 *
	 * @param diagramElement
	 */
	public void addDiagramElement(GatDiagramElement diagramElement)
	{
		planeElement.addDiagramElement(diagramElement);
	}

	/**
	 * Get the shape for the given bpmnElementId
	 *
	 * @param bpmnElementId
	 *
	 * @return the shape for the given bpmnElementId or <code>null</code> if not found
	 */
	public GatBPShapeElement getGatDiagramShape(String bpmnElementId)
	{
		return planeElement.getGatDiagramShape(bpmnElementId);
	}

	/**
	 * Get the bounds of the shape for the given bpmnElementId
	 *
	 * @param bpmnElementId
	 *
	 * @return the bounds of the shape for the given bpmnElementId
	 */
	public GatBoundsElement getGatDiagramShapeBounds(String bpmnElementId)
	{
		return planeElement.getGatDiagramShapeBounds(bpmnElementId);
	}

	/**
	 * Graphical Audit Trail diagram "plane" element
	 */
	class GatPlaneElement extends GatDiagramElement
	{
		private static final long		serialVersionUID	= -5837836866065503334L;

		private List<GatDiagramElement>	planeElements;

		/**
		 *
		 * @param typeName
		 * @param flowContainerElement
		 *            The process or collapsed embedded sub-process element that this is the diagram model for.
		 */
		public GatPlaneElement(GatIdElement flowContainerElement)
		{
			super("BPPlane", flowContainerElement);

			planeElements = new ArrayList<>();
			put("planeElement", planeElements);
		}

		/**
		 * Add a diagram element to the diagram plane
		 *
		 * @param diagramElement
		 */
		public void addDiagramElement(GatDiagramElement diagramElement)
		{
			planeElements.add(diagramElement);
		}

		/**
		 * Get the shape for the given bpmnElementId
		 *
		 * @param bpmnElementId
		 *
		 * @return the shape for the given bpmnElementId or <code>null</code> if not found
		 */
		public GatBPShapeElement getGatDiagramShape(String bpmnElementId)
		{
			for (GatDiagramElement diagramElement : planeElements)
			{
				if (diagramElement instanceof GatBPShapeElement)
				{
					if (bpmnElementId.equals(diagramElement.getId()))
					{
						return (GatBPShapeElement) diagramElement;
					}
				}
			}
			return null;
		}

		/**
		 * Get the bounds of the shape for the given bpmnElementId
		 *
		 * @param bpmnElementId
		 *
		 * @return the bounds of the shape for the given bpmnElementId
		 */
		public GatBoundsElement getGatDiagramShapeBounds(String bpmnElementId)
		{
			GatBPShapeElement diagramShape = getGatDiagramShape(bpmnElementId);

			if (diagramShape != null)
			{
				GatBoundsElement bounds = diagramShape.getBounds();
				return bounds;
			}

			return null;
		}
	}

}
