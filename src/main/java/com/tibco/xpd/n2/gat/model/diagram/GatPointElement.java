/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model.diagram;

import java.util.LinkedHashMap;

/**
 * The base Graphical Audit Trail Point model element
 *
 * @author Sid.Allway
 * @since Dec 2025
 */
@SuppressWarnings("nls")
public class GatPointElement extends LinkedHashMap<String, Object>
{
	private static final long serialVersionUID = 7740620572125499015L;

	/**
	 * Constructor
	 */
	public GatPointElement(Double x, Double y)
	{
		this.put("$type", "dc:Point");

		this.put("x", x);
		this.put("y", y);
	}

	/**
	 * @return The X coordinate
	 */
	public Double getX()
	{
		return (Double) get("x");
	}

	/**
	 * Set the X coordinate
	 *
	 * @param x
	 */
	public void setX(Double x)
	{
		this.put("x", x);
	}

	/**
	 * @return The Y coordinate
	 */
	public Double getY()
	{
		return (Double) get("y");
	}

	/**
	 * Set the Y coordinate
	 *
	 * @param y
	 */
	public void setY(Double y)
	{
		this.put("y", y);
	}

}
