/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Start Event model element
 *
 * @author Sid Allway
 * @since 22 Dec 2025
 */
@SuppressWarnings("nls")
public class GatStartEventElement extends GatEventElement
{
	private static final long serialVersionUID = -8444393817931232976L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatStartEventElement(String id, String internalName, String name)
	{
		super("StartEvent", id, internalName, name);
	}

	/**
	 * Set the isInterrupting property
	 *
	 * @param isInterrupting
	 */
	public void setIsInterrupting(Boolean isInterrupting)
	{
		put("isInterrupting", isInterrupting);
	}

}
