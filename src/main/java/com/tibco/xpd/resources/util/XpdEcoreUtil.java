/*
 * Copyright (c) TIBCO Software Inc 2004, 2009. All rights reserved.
 *
 * Extracted from com.tibco.xpd.resources plugin (tbs-core) for standalone
 * use outside Eclipse Platform. This class is required by the xpdl2 EMF
 * model (UniqueIdElementImpl calls generateUUID() in its constructor).
 *
 * Original source: tbs-core/product/com.tibco.xpd.core.feature/plugins/
 *   com.tibco.xpd.resources/src/com/tibco/xpd/resources/util/XpdEcoreUtil.java
 */

package com.tibco.xpd.resources.util;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Utility class providing UUID generation for XPDL model elements.
 * <p>
 * The xpdl2 EMF model's {@code UniqueIdElementImpl} constructor calls
 * {@link #generateUUID()} to automatically assign a unique ID to every
 * new model element. This class delegates to EMF's {@link EcoreUtil}
 * which generates RFC 4122 compliant UUIDs with no Eclipse Platform
 * dependency.
 * </p>
 *
 * @author kupadhya
 */
public class XpdEcoreUtil {

    /**
     * Generate a universally unique ID suitable for use as an XPDL element ID.
     * <p>
     * Delegates to {@link EcoreUtil#generateUUID()} which produces a UUID
     * string prefixed with an underscore (e.g., {@code "_abc123..."}).
     * </p>
     *
     * @return a new unique ID string
     */
    public static String generateUUID() {
        return EcoreUtil.generateUUID();
    }

}
