/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.LinkedHashMap;

/**
 * The base Graphical Audit Trail model element
 *
 * @author Sid.Allway
 * @since Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatModelElement extends LinkedHashMap<String, Object>
{
	private static final long serialVersionUID = -7364306438757795187L;

	/**
	 * Constructor
	 */
	public GatModelElement(String typeName)
	{
		this.put("$type", "bp:" + typeName);
	}
}
