/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 * Standalone XPDL resource that extends {@link XMLResourceImpl} directly,
 * bypassing the Eclipse-dependent {@code ResourceExtensions} chain.
 *
 * <p>In the S5x codebase, the XPDL loading chain is:
 * {@code Xpdl2ResourceImpl -> ResourceExtensions -> XMLResourceImpl}.
 * {@code ResourceExtensions.doLoad()} wraps the XML loading in an Eclipse
 * {@code InternalTransactionalEditingDomain}, which is not available outside Eclipse.</p>
 *
 * <p>This class extends {@code XMLResourceImpl} directly and relies on its default
 * {@code doLoad()} implementation, which performs standard EMF SAX-based XML parsing
 * without any transactional wrapper.</p>
 *
 * <p>The custom XML helper ({@code HelperExtensions}), XML load handler
 * ({@code LoadExtensions}), and SAX parser ({@code SAXParserExtensions}) from the
 * S5x codebase are NOT used here. If XPDL files require their special handling
 * (e.g., wrapper-element ignoring, lenient namespace resolution), these can be
 * added as overrides later by overriding {@code createXMLHelper()},
 * {@code createXMLLoad()}, etc.</p>
 *
 * <p>For the initial POC, the default XMLResourceImpl behavior should be
 * sufficient for well-formed XPDL files produced by TIBCO BPM Studio.</p>
 */
public class StandaloneXpdlResource extends XMLResourceImpl
{
    /**
     * Construct a new standalone XPDL resource.
     *
     * @param uri the resource URI (typically a synthetic URI like "input.xpdl")
     */
    public StandaloneXpdlResource(URI uri)
    {
        super(uri);
    }

    /*
     * NOTE: If default XMLResourceImpl parsing fails for certain XPDL files,
     * the following overrides may be needed:
     *
     * 1. createXMLHelper() - return a custom helper that handles:
     *    - xpdExtension namespace prefix tracking
     *    - Lenient parsing of unknown elements (log warning instead of error)
     *
     * 2. createXMLLoad() - return a custom load handler that handles:
     *    - Wrapper elements that should be ignored during deserialization
     *    - Custom SAX parser configuration via SAXParserExtensions
     *
     * These can be ported from HelperExtensions and LoadExtensions in the
     * S5x codebase with Eclipse logging calls replaced by SLF4J.
     */
}
