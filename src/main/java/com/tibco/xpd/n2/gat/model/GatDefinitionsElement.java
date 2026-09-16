/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

import com.tibco.xpd.n2.gat.model.diagram.GatBPDiagramElement;

/**
 * Graphical Audit Trail root Definitions element
 *
 * @author Sid Allway
 * @since 15 Dec 2025
 */
@SuppressWarnings("nls")
public class GatDefinitionsElement extends GatIdElement
{
	private static final long			serialVersionUID	= -7971700309962412907L;

	/**
	 * The rootElements property
	 */
	private List<GatProcessElement>		rootElements		= null;

	/**
	 * The diagrams property
	 */
	private List<GatBPDiagramElement>	diagrams			= null;

	/**
	 * Constructor
	 *
	 * @param typeName
	 * @param id
	 */
	public GatDefinitionsElement(String id)
	{
		super("Definitions", id);
	}

	/**
	 * Add the process element to the model
	 *
	 * @param processElement
	 */
	public void setProcess(GatProcessElement processElement)
	{
		if (rootElements == null)
		{
			rootElements = new ArrayList<GatProcessElement>();
			put("rootElements", rootElements);
		}

		/*
		 * Whilst the GAT model has a list of root elements, it should only ever contain one process.
		 */
		rootElements.add(processElement);
	}

	/**
	 * Add the diagram element to the model
	 *
	 * @param diagramElement
	 */
	public void addDiagram(GatBPDiagramElement diagramElement)
	{
		if (diagrams == null)
		{
			diagrams = new ArrayList<GatBPDiagramElement>();
			put("diagrams", diagrams);
		}

		diagrams.add(diagramElement);
	}

	/**
	 * Set the collaboration property
	 *
	 * @param collaborationElement
	 */
	public void setCollaboration(GatCollaborationElement collaborationElement)
	{
		put("collaboration", collaborationElement);
	}

}
