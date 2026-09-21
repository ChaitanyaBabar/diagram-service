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
     * Returns a point on a sequence of lines that is {@code percentPortion}
     * distance along the line.
     *
     * @param points         the polyline points
     * @param percentPortion the percentage distance (0 to 100)
     * @return the interpolated point
     */
    public static Point getLinePointFromPortion(PointList points, double percentPortion)
    {
        if (percentPortion <= 0.0)
        {
            return points.getFirstPoint().getCopy();
        }
        else if (percentPortion >= 100.0)
        {
            return points.getLastPoint().getCopy();
        }

        /* Truncate to int to match S5x XPDLineUtilities behaviour */
        int totalLength = (int) getLineLength(points);

        int pixelsFromStart = (int) (totalLength * (percentPortion / 100));

        return getLinePointFromOffset(points, pixelsFromStart);
    }

    /**
     * Get a point along a polyline at a given pixel offset from the start.
     *
     * @param points          the polyline points
     * @param pixelsFromStart the pixel offset from the start
     * @return the interpolated point
     */
    public static Point getLinePointFromOffset(PointList points, int pixelsFromStart)
    {
        int totalLength = (int) getLineLength(points);

        if (pixelsFromStart > totalLength)
        {
            return points.getLastPoint().getCopy();
        }

        double[] lineLengths = new double[points.size() - 1];

        for (int p = 0; p < points.size() - 1; p++)
        {
            lineLengths[p] = getLineLength(points.getPoint(p), points.getPoint(p + 1));
        }

        /* Find out how far down which line segment the pixel position lies */
        int ll = 0;
        for (ll = 0; ll < lineLengths.length; ll++)
        {
            if (pixelsFromStart <= lineLengths[ll])
            {
                break;
            }
            pixelsFromStart -= lineLengths[ll];
        }

        Point startLine = points.getPoint(ll);
        Point endLine = points.getPoint(ll + 1);

        double portion = pixelsFromStart / getLineLength(startLine, endLine);

        double x = (endLine.preciseX() - startLine.preciseX()) * portion;
        double y = (endLine.preciseY() - startLine.preciseY()) * portion;

        return new Point((int) (startLine.preciseX() + Math.round(x)),
                (int) (startLine.preciseY() + Math.round(y)));
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
