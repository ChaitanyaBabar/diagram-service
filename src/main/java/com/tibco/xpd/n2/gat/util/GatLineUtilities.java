/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

import com.tibco.xpd.n2.gat.stubs.Point;
import com.tibco.xpd.n2.gat.stubs.PointList;

/**
 * Extracted line calculation utilities from
 * {@code com.tibco.xpd.resources.ui.util.XPDLineUtilities}.
 *
 * <p>Used by {@code GatEventGenerator} for calculating boundary event positions
 * along connection paths. All methods are pure geometry calculations with no
 * external dependencies.</p>
 *
 * @see Section 5.4 of the design document
 */
public final class GatLineUtilities
{
    private GatLineUtilities()
    {
        // Static utility class
    }

    /**
     * Get a point along a polyline at a given proportional distance (0.0 to 1.0).
     *
     * @param points   the polyline points
     * @param portion  the proportional distance (0.0 = start, 1.0 = end)
     * @return the interpolated point
     */
    public static Point getLinePointFromPortion(PointList points, double portion)
    {
        if (points == null || points.size() == 0)
        {
            return new Point(0, 0);
        }
        if (points.size() == 1)
        {
            return points.getFirstPoint();
        }

        double totalLength = getLineLength(points);
        double targetLength = totalLength * portion;

        return getLinePointFromOffset(points, (int) Math.round(targetLength));
    }

    /**
     * Get a point along a polyline at a given pixel offset from the start.
     *
     * @param points the polyline points
     * @param offset the pixel offset from the start
     * @return the interpolated point
     */
    public static Point getLinePointFromOffset(PointList points, int offset)
    {
        if (points == null || points.size() == 0)
        {
            return new Point(0, 0);
        }
        if (points.size() == 1 || offset <= 0)
        {
            return points.getFirstPoint().getCopy();
        }

        double remaining = offset;
        for (int i = 0; i < points.size() - 1; i++)
        {
            Point p1 = points.getPoint(i);
            Point p2 = points.getPoint(i + 1);
            double segLength = getLineLength(p1, p2);

            if (remaining <= segLength)
            {
                // Interpolate within this segment
                double ratio = remaining / segLength;
                int x = (int) (p1.preciseX() + ratio * (p2.preciseX() - p1.preciseX()));
                int y = (int) (p1.preciseY() + ratio * (p2.preciseY() - p1.preciseY()));
                return new Point(x, y);
            }
            remaining -= segLength;
        }

        // Past the end, return last point
        return points.getLastPoint().getCopy();
    }

    /**
     * Calculate the length of a line segment between two points.
     *
     * @param p1 start point
     * @param p2 end point
     * @return the Euclidean distance
     */
    public static double getLineLength(Point p1, Point p2)
    {
        double dx = p2.preciseX() - p1.preciseX();
        double dy = p2.preciseY() - p1.preciseY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculate the total length of a polyline.
     *
     * @param points the polyline points
     * @return the total length
     */
    public static double getLineLength(PointList points)
    {
        if (points == null || points.size() < 2)
        {
            return 0.0;
        }
        double total = 0.0;
        for (int i = 0; i < points.size() - 1; i++)
        {
            total += getLineLength(points.getPoint(i), points.getPoint(i + 1));
        }
        return total;
    }
}
