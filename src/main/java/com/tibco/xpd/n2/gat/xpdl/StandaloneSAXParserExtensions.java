/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMLHandler;

/**
 * Standalone port of {@code com.tibco.xpd.xpdl2.extension.SAXParserExtensions}.
 *
 * <p>Extends the EMF {@link SAXXMLHandler} to handle XPDL's custom wrapper
 * element pattern. The XPDL Ecore model uses {@code wrap} and {@code subclass-wrap}
 * annotations on structural features to indicate that the XML serialization
 * includes container/wrapper elements that have no direct model counterpart.</p>
 *
 * <p>Examples of wrapper elements in XPDL:</p>
 * <ul>
 *   <li>{@code <Participants>} wraps {@code <Participant>} elements</li>
 *   <li>{@code <Activities>} wraps {@code <Activity>} elements</li>
 *   <li>{@code <Transitions>} wraps {@code <Transition>} elements</li>
 *   <li>{@code <Implementation>} wraps concrete implementation elements</li>
 *   <li>{@code <Event>} wraps concrete event elements</li>
 * </ul>
 *
 * <p>During parsing, when a wrapper element is encountered, it is skipped
 * (ignored) so that its children are parsed directly into the containing
 * model object's feature list.</p>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StandaloneSAXParserExtensions extends SAXXMLHandler
{
    private final List<String> elementNames = new LinkedList<>();

    /**
     * Construct a SAX handler for standalone XPDL parsing.
     *
     * @param resource the XML resource being loaded
     * @param helper   the XML helper (must be a {@link StandaloneHelperExtensions})
     * @param options  the load options map
     */
    public StandaloneSAXParserExtensions(XMLResource resource, XMLHelper helper,
            Map<?, ?> options)
    {
        super(resource, helper, options);
    }

    /**
     * Construct a SAX handler for parsing XML content into an existing parent object.
     * Used by the "setAnyType" flow for inline content parsing.
     *
     * @param resource the XML resource
     * @param helper   the XML helper
     * @param options  the load options
     * @param parent   the parent EObject to parse content into
     */
    public StandaloneSAXParserExtensions(XMLResource resource, XMLHelper helper,
            Map<?, ?> options, EObject parent)
    {
        super(resource, helper, options);
        objects.add(parent);
    }

    /**
     * Intercepts startElement to skip wrapper elements.
     *
     * <p>If the element matches a {@code wrap} or {@code subclass-wrap}
     * annotation value on a structural feature of the current context object,
     * the element is recorded but NOT passed to the parent handler — effectively
     * making it invisible to the EMF model construction.</p>
     */
    @Override
    public void startElement(String uri, String localName, String name)
    {
        elementNames.add(name);
        if (!shouldIgnore(uri, localName, name))
        {
            super.startElement(uri, localName, name);
        }
    }

    /**
     * Intercepts endElement to skip wrapper element closings.
     */
    @Override
    public void endElement(String uri, String localName, String name)
    {
        Object removed = elementNames.remove(elementNames.size() - 1);
        if (!removed.equals(name))
        {
            System.err.println("Invalid XML: expected end of '" + removed
                    + "' but got end of '" + name + "'");
        }
        if (!shouldIgnore(uri, localName, name))
        {
            super.endElement(uri, localName, name);
        }
    }

    /**
     * Determines whether the given XML element is a wrapper that should be
     * ignored during parsing.
     *
     * <p>Scans the structural features of the current context object's EClass
     * (and all its supertypes) for EAnnotations containing a {@code wrap} or
     * {@code subclass-wrap} detail. If the annotation value matches the
     * element name (possibly with a namespace prefix), the element is a wrapper
     * and should be skipped.</p>
     *
     * @param uri       the namespace URI
     * @param localName the local name
     * @param name      the qualified name (may include prefix)
     * @return true if the element is a wrapper and should be ignored
     */
    private boolean shouldIgnore(String uri, String localName, String name)
    {
        EObject peekObject = (EObject) objects.peek();
        if (peekObject != null)
        {
            EClass cls = peekObject.eClass();

            // Collect all namespace prefixes for this class and its supertypes
            Set<String> prefixSet = new HashSet<>();
            StandaloneHelperExtensions helperExt = (StandaloneHelperExtensions) helper;

            prefixSet.addAll(helperExt.getRecordedPrefixes(cls.getEPackage().getNsURI()));
            EList superTypes = cls.getEAllSuperTypes();
            for (Iterator iterator = superTypes.iterator(); iterator.hasNext(); )
            {
                EClass superCl = (EClass) iterator.next();
                prefixSet.addAll(
                        helperExt.getRecordedPrefixes(superCl.getEPackage().getNsURI()));
            }

            // Check all structural features for wrap/subclass-wrap annotations
            EList features = cls.getEAllStructuralFeatures();
            for (Iterator iter = features.iterator(); iter.hasNext(); )
            {
                EStructuralFeature feature = (EStructuralFeature) iter.next();
                EList annotations = feature.getEAnnotations();
                for (Iterator annIter = annotations.iterator(); annIter.hasNext(); )
                {
                    EAnnotation ann = (EAnnotation) annIter.next();
                    Object val = ann.getDetails()
                            .get(StandaloneExtensionsConstants.WRAP_ANNOTATION);
                    if (val == null)
                    {
                        val = ann.getDetails()
                                .get(StandaloneExtensionsConstants.SUBCLASS_WRAP_ANNOTATION);
                    }
                    if (val != null)
                    {
                        // Check against all known prefixes for this namespace
                        for (Iterator pi = prefixSet.iterator(); pi.hasNext(); )
                        {
                            String prefix = (String) pi.next();
                            String qualifiedName;
                            if (prefix.length() > 0)
                            {
                                qualifiedName = prefix + ":" + val;
                            }
                            else
                            {
                                qualifiedName = String.valueOf(val);
                            }
                            if (name.equals(qualifiedName))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Process XML attributes for a newly created object.
     *
     * <p>Ported from S5x: works around an EMF bug where ExtendedPackage's
     * href element is incorrectly treated as a reference.</p>
     */
    @Override
    protected void handleObjectAttribs(EObject obj)
    {
        if (attribs != null)
        {
            InternalEObject internalEObject = (InternalEObject) obj;
            for (int i = 0, size = attribs.getLength(); i < size; ++i)
            {
                String attrName = attribs.getQName(i);
                if (attrName.equals(idAttribute))
                {
                    xmlResource.setID(internalEObject, attribs.getValue(i));
                }
                else if (!attrName.startsWith(XMLResource.XML_NS)
                        && !notFeatures.contains(attrName))
                {
                    setAttribValue(obj, attrName, attribs.getValue(i));
                }
            }
        }
    }

    /**
     * Handle proxy URI resolution — ensures relative URIs starting with "//"
     * are resolved against the resource URI.
     */
    @Override
    protected void handleProxy(InternalEObject proxy, String uriLiteral)
    {
        if (uriLiteral.startsWith("//"))
        {
            super.handleProxy(proxy,
                    resourceURI.appendFragment(uriLiteral).toString());
        }
        else
        {
            super.handleProxy(proxy, uriLiteral);
        }
    }

    /**
     * Create an object from a feature type, with special handling for
     * subclass-wrap patterns where the XML element name differs from the
     * feature's type name.
     */
    @Override
    protected EObject createObjectFromFeatureType(EObject peekObject,
            EStructuralFeature feature)
    {
        String currentName = elementNames.get(elementNames.size() - 1);
        int colonIdx = currentName.indexOf(':');
        String localPart;
        if (colonIdx >= 0)
        {
            localPart = currentName.substring(colonIdx + 1);
        }
        else
        {
            localPart = currentName;
        }

        if (!helper.getName(feature).equals(localPart))
        {
            return createObjectFromTypeName(peekObject,
                    currentName + "_._type", feature);
        }
        return super.createObjectFromFeatureType(peekObject, feature);
    }
}
