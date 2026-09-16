/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.stubs;

/**
 * Standalone stub for {@code org.eclipse.draw2d.geometry.Point}.
 *
 * <p>The original Draw2D Point class is used only in {@code GatEventGenerator}
 * for boundary event shape calculations. This stub provides the minimal API
 * surface needed by the GAT generation code.</p>
 *
 * <p>Only the following features are used:</p>
 * <ul>
 *   <li>{@code x, y} fields (int coordinates)</li>
 *   <li>{@code preciseX(), preciseY()} methods (double precision access)</li>
 *   <li>Constructors from int and double coordinates</li>
 * </ul>
 */
public class Point
{
    public int x;
    public int y;

    public Point()
    {
        this.x = 0;
        this.y = 0;
    }

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y)
    {
        this.x = (int) x;
        this.y = (int) y;
    }

    public double preciseX()
    {
        return x;
    }

    public double preciseY()
    {
        return y;
    }

    public Point getCopy()
    {
        return new Point(x, y);
    }

    @Override
    public String toString()
    {
        return "Point(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode()
    {
        return x * 31 + y;
    }
}
