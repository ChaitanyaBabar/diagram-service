/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Audit Trail Process element
 *
 * @author Sid Allway
 * @since 15 Dec 2025
 */
@SuppressWarnings("nls")
public class GatProcessElement extends GatIdElement
{
	private static final long				serialVersionUID	= 2610144129950305044L;

	/**
	 * The flowElements property
	 */
	private List<GatIdElement>	flowElements;

	/**
	 * The artifacts property
	 */
	private List<GatIdElement>		artifacts			= null;

	/**
	 * The laneSets property
	 */
	private List<GatLaneSetElement>	laneSets			= null;

	/**
	 * Constructor
	 *
	 * @param typeName
	 * @param id
	 */
	public GatProcessElement(String id)
	{
		super("Process", id);

		flowElements = new ArrayList<GatIdElement>();
		put("flowElements", flowElements);
	}

	/**
	 * Add a flow element to the GAT process
	 *
	 * @param flowElement
	 */
	public void addFlowElement(GatFlowElement flowElement)
	{
		flowElements.add(flowElement);
	}

	/**
	 * Add a DataObject element to the GAT process flowElements
	 *
	 * @param dataObjectElement
	 */
	public void addFlowElement(GatDataObjectElement dataObjectElement)
	{
		flowElements.add(dataObjectElement);
	}

	/**
	 * Add a DataObjectReference element to the GAT process flowElements
	 *
	 * @param dataObjectReferenceElement
	 */
	public void addFlowElement(GatDataObjectReferenceElement dataObjectReferenceElement)
	{
		flowElements.add(dataObjectReferenceElement);
	}

	/**
	 * Add a TextAnnotation element to the GAT process artifacts
	 *
	 * @param annotationElement
	 */
	public void addArtifact(GatTextAnnotationElement annotationElement)
	{
		if (artifacts == null)
		{
			artifacts = new ArrayList<>();
			put("artifacts", artifacts);
		}

		artifacts.add(annotationElement);
	}

	/**
	 * Add an Association element to the GAT process artifacts
	 *
	 * @param associationElement
	 */
	public void addArtifact(GatAssociationElement associationElement)
	{
		if (artifacts == null)
		{
			artifacts = new ArrayList<>();
			put("artifacts", artifacts);
		}

		artifacts.add(associationElement);
	}

	/**
	 * Add a Group element to the GAT process artifacts
	 *
	 * @param groupElement
	 */
	public void addArtifact(GatGroupElement groupElement)
	{
		if (artifacts == null)
		{
			artifacts = new ArrayList<>();
			put("artifacts", artifacts);
		}

		artifacts.add(groupElement);
	}

	/**
	 * Add a laneSet to the GAT process
	 *
	 * @param laneSetElement
	 */
	public void addLaneSetElement(GatLaneSetElement laneSetElement)
	{
		if (laneSets == null)
		{
			laneSets = new ArrayList<>();
			put("laneSets", laneSets);
		}

		laneSets.add(laneSetElement);
	}
}
