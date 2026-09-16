/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Audit Trail Embedded Sub-Process model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatSubProcessElement extends GatActivityElement
{
	private static final long serialVersionUID = 6512703498617870475L;

	/**
	 * The flowElements property
	 */
	private List<GatIdElement>	flowElements;

	/**
	 * The artifacts property (lazy-initialized)
	 */
	private List<GatIdElement>	artifacts	= null;

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatSubProcessElement(String id, String internalName, String name)
	{
		this("SubProcess", id, internalName, name);
	}

	/**
	 * Constructor for sub-classes, allowing override of type name.
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	protected GatSubProcessElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);

		flowElements = new ArrayList<GatIdElement>();
		put("flowElements", flowElements);
	}

	/**
	 * Set the triggeredByEvent property
	 *
	 * @param triggeredByEvent
	 */
	public void setTriggeredByEvent(Boolean triggeredByEvent)
	{
		put("triggeredByEvent", triggeredByEvent);
	}

	/**
	 * Add a flow element to the GAT sub-process
	 *
	 * @param flowElement
	 */
	public void addFlowElement(GatFlowElement flowElement)
	{
		flowElements.add(flowElement);
	}

	/**
	 * Add a DataObject element to the sub-process flowElements
	 *
	 * @param dataObjectElement
	 */
	public void addFlowElement(GatDataObjectElement dataObjectElement)
	{
		flowElements.add(dataObjectElement);
	}

	/**
	 * Add a DataObjectReference element to the sub-process flowElements
	 *
	 * @param dataObjectReferenceElement
	 */
	public void addFlowElement(GatDataObjectReferenceElement dataObjectReferenceElement)
	{
		flowElements.add(dataObjectReferenceElement);
	}

	/**
	 * Add a TextAnnotation element to the sub-process artifacts
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
	 * Add an Association element to the sub-process artifacts
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

}
