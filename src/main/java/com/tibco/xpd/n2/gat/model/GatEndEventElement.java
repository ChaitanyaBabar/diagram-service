/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail End Event model element
 *
 * @author Sid Allway
 * @since 22 Dec 2025
 */
@SuppressWarnings("nls")
public class GatEndEventElement extends GatEventElement
{
	private static final long serialVersionUID = -5588814297972078391L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatEndEventElement(String id, String internalName, String name)
	{
		super("EndEvent", id, internalName, name);
	}

}
