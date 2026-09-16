/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Base class representing a Graphical Audit Trail model element that has id, internalName and and name (label)
 * properties.
 *
 * @author Sid Allway
 * @since Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatNamedElement extends GatIdElement
{
	private static final long serialVersionUID = -4229057779206137451L;

	/**
	 * Construct named model element and set the id, internal-name and name properties.
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 *            Label name, optional.
	 */
	public GatNamedElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id);

		if (internalName != null && !internalName.isEmpty())
		{
			put("internalName", internalName);
		}

		if (name != null && !name.isEmpty())
		{
			put("name", name);
		}
	}

	/**
	 * @return The internalName property
	 */
	public String getInternalName()
	{
		return (String) get("internalName");
	}

	/**
	 * @return The name property
	 */
	public String getName()
	{
		return (String) get("name");
	}

}
