/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail element with id property
 *
 * @author Sid Allway
 * @since 15 Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatIdElement extends GatModelElement
{
	private static final long serialVersionUID = -8285909329543127737L;

	/**
	 * @param typeName
	 */
	public GatIdElement(String typeName, String id)
	{
		super(typeName);

		put("id", id);
	}

	/**
	 * @return The id property
	 */
	public String getId()
	{
		return (String) get("id");
	}

}
