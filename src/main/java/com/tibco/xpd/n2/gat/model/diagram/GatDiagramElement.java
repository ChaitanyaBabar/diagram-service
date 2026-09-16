/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model.diagram;

import java.util.LinkedHashMap;

import com.tibco.xpd.n2.gat.model.GatIdElement;

/**
 * The base Graphical Audit Trail Diagram model element for a given semantic model element
 *
 * @author Sid.Allway
 * @since Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatDiagramElement extends LinkedHashMap<String, Object>
{
	private static final long serialVersionUID = 1830797426578638899L;

	/**
	 * Constructor
	 *
	 * @param typeName
	 *            The diagram model object's class type name (without prefix)
	 * @param modelElement
	 *            The semantic model element that this diagram element represents.
	 */
	public GatDiagramElement(String typeName, GatIdElement modelElement)
	{
		this.put("$type", "bpdi:" + typeName);

		/* Add the back reference to semantic model */
		put("bpElement", modelElement.getId());
	}

	/**
	 * Get the diagram element id
	 *
	 * @return The id property value.
	 */
	public String getId()
	{
		return (String) get("id");
	}
}
