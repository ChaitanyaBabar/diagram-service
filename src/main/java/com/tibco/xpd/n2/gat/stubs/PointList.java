/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.stubs;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone stub for {@code org.eclipse.draw2d.geometry.PointList}.
 *
 * <p>The original Draw2D PointList stores points as a flat int array for
 * performance. This stub uses a simple ArrayList since the GAT generation
 * code only processes small point lists (boundary event waypoints, typically
 * 2-10 points).</p>
 *
 * <p>Only the following features are used by GAT generation:</p>
 * <ul>
 *   <li>{@code addPoint(Point)} - add a point</li>
 *   <li>{@code getPoint(int)} - get point at index</li>
 *   <li>{@code getFirstPoint(), getLastPoint()} - convenience accessors</li>
 *   <li>{@code size()} - number of points</li>
 * </ul>
 */
public class PointList
{
    private final List<Point> points;

    public PointList()
    {
        this.points = new ArrayList<>();
    }

    public PointList(int capacity)
    {
        this.points = new ArrayList<>(capacity);
    }

    public void addPoint(Point p)
    {
        points.add(p);
    }

    public void addPoint(int x, int y)
    {
        points.add(new Point(x, y));
    }

    public Point getPoint(int index)
    {
        return points.get(index);
    }

    public Point getFirstPoint()
    {
        return points.get(0);
    }

    public Point getLastPoint()
    {
        return points.get(points.size() - 1);
    }

    public int size()
    {
        return points.size();
    }

    @Override
    public String toString()
    {
        return "PointList" + points.toString();
    }
}
