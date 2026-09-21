/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.SAXWrapper;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Standalone port of {@code com.tibco.xpd.xpdl2.extension.LoadExtensions}.
 *
 * <p>Extends {@link XMLLoadImpl} to create a {@link StandaloneSAXParserExtensions}
 * handler that understands XPDL's custom wrapper element pattern.</p>
 *
 * <p>The S5x version ({@code LoadExtensions}) also includes a {@code setAnyType}
 * method for parsing inline XML content. That method is not ported here as it is
 * not needed for the initial XPDL-to-diagram loading use case. It can be added
 * later if required.</p>
 */
public class StandaloneLoadExtensions extends XMLLoadImpl
{
    /**
     * Construct a load handler using the given XML helper.
     *
     * @param helper the XML helper (should be a {@link StandaloneHelperExtensions})
     */
    public StandaloneLoadExtensions(XMLHelper helper)
    {
        super(helper);
    }

    /**
     * Creates the SAX default handler that performs the actual XML parsing.
     *
     * <p>Returns a {@link SAXWrapper} around our custom
     * {@link StandaloneSAXParserExtensions}, which handles wrapper element
     * skipping during XPDL parsing.</p>
     */
    @Override
    protected DefaultHandler makeDefaultHandler()
    {
        return new SAXWrapper(
                new StandaloneSAXParserExtensions(resource, helper, options));
    }
}
