/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
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
 * <p>The XPDL Ecore model uses a custom "wrap" annotation pattern on
 * structural features. For example, the {@code participants} feature has
 * annotation {@code wrap=Participants}, meaning the XML serialization wraps
 * {@code <Participant>} elements inside a {@code <Participants>} container.
 * Standard EMF XML parsing doesn't understand this custom annotation and
 * would throw {@code FeatureNotFoundException} when encountering wrapper
 * elements like {@code <Participants>}.</p>
 *
 * <p>To handle this, this resource overrides the XML helper and XML load
 * to use standalone ports of the S5x extension classes
 * ({@link StandaloneHelperExtensions}, {@link StandaloneLoadExtensions},
 * {@link StandaloneSAXParserExtensions}) which implement wrapper element
 * skipping and lenient feature resolution.</p>
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

    /**
     * Creates a custom XML helper that handles XPDL-specific parsing concerns:
     * <ul>
     *   <li>Namespace prefix tracking for wrapper element detection</li>
     *   <li>Lenient feature resolution with fallback for namespace mismatches</li>
     *   <li>Subclass-wrap annotation support for abstract type hierarchies</li>
     * </ul>
     *
     * <p>Ported from S5x {@code HelperExtensions} with Eclipse logging
     * replaced by SLF4J.</p>
     */
    @Override
    protected XMLHelper createXMLHelper()
    {
        return new StandaloneHelperExtensions(this);
    }

    /**
     * Creates a custom XML load handler that wraps the SAX parser in a
     * {@link StandaloneSAXParserExtensions} — this handler intercepts
     * {@code startElement}/{@code endElement} calls to skip XPDL wrapper
     * elements (like {@code <Participants>}, {@code <Activities>},
     * {@code <Transitions>}, etc.) that have no direct model counterpart.
     *
     * <p>Ported from S5x {@code LoadExtensions}.</p>
     */
    @Override
    protected XMLLoad createXMLLoad()
    {
        return new StandaloneLoadExtensions(createXMLHelper());
    }
}
