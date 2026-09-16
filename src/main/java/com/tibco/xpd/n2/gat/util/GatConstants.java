/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

/**
 * Constants inlined from various S5x classes to eliminate Eclipse dependencies.
 *
 * <p>These values were extracted from:</p>
 * <ul>
 *   <li>{@code ProcessWidgetConstants} - diagram dimension constants</li>
 *   <li>{@code ProcessEditPart} - pool margin and diagram extent constants</li>
 *   <li>{@code TaskImplementationTypeDefinitions} - task implementation type string IDs</li>
 * </ul>
 *
 * @see Appendix B of the design document
 */
public final class GatConstants
{
    private GatConstants()
    {
        // Constants class
    }

    // ---- From ProcessWidgetConstants ----

    /** Size of intermediate event shape in pixels */
    public static final int INTERMEDIATE_EVENT_SIZE = 27;

    /** Content margin for embedded subprocess shapes */
    public static final int EMB_SUBPROC_CONTENT_MARGIN = 20;

    // ---- From ProcessEditPart ----

    /** Minimum width of the diagram extent */
    public static final int MINIMUM_DIAGRAM_EXTENT_WIDTH = 700;

    /** Horizontal margin for pool shapes */
    public static final int POOL_MARGIN_CX = 5;

    /** Vertical margin for pool shapes */
    public static final int POOL_MARGIN_CY = 5;

    // ---- From TaskImplementationTypeDefinitions ----

    /** Implementation type ID for email service tasks */
    public static final String EMAIL_SERVICE = "EmailService";

    /** Implementation type ID for REST service tasks */
    public static final String REST_SERVICE = "RestService";

    /** Implementation type ID for global data (case) tasks */
    public static final String GLOBAL_DATA = "GlobalData";

    /** Implementation type ID for database service tasks */
    public static final String DATABASE_SERVICE = "DatabaseService";

    /** Implementation type ID for business rules tasks */
    public static final String BUSINESS_RULES = "BusinessRules";
}
