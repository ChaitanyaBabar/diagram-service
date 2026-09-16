/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model.diagram;

import java.util.LinkedHashMap;

/**
 * The base Graphical Audit Trail Bounds model element
 *
 * @author Sid.Allway
 * @since Dec 2025
 */
@SuppressWarnings("nls")
public class GatBoundsElement extends LinkedHashMap<String, Object>
{
	private static final long serialVersionUID = 7740620572125499015L;

	/**
	 * Constructor
	 */
	public GatBoundsElement(Double x, Double y, Double width, Double height)
	{
		this.put("$type", "dc:Bounds");

		this.put("x", x);
		this.put("y", y);
		this.put("width", width);
		this.put("height", height);
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


	/**
	 * @return The width
	 */
	public Double getWidth()
	{
		return (Double) get("width");
	}

	/**
	 * Set the width
	 *
	 * @param width
	 */
	public void setWidth(Double width)
	{
		this.put("width", width);
	}

	/**
	 * @return The height
	 */
	public Double getHeight()
	{
		return (Double) get("height");
	}

	/**
	 * Set the height
	 *
	 * @param height
	 */
	public void setHeight(Double height)
	{
		this.put("height", height);
	}

}
