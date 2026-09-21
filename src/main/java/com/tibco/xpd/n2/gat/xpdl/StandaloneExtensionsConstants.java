/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

/**
 * Constants for the XPDL model extension annotations.
 *
 * <p>Ported from {@code com.tibco.xpd.xpdl2.extension.ExtensionsConstants} in S5x.
 * These constants identify custom EAnnotation details that control XML
 * serialization behavior for the XPDL Ecore model.</p>
 *
 * <ul>
 *   <li>{@code wrap} — indicates a feature's XML elements should be wrapped in an
 *       additional container element (e.g., {@code <Participant>} items wrapped in
 *       {@code <Participants>})</li>
 *   <li>{@code subclass-wrap} — indicates an abstract class acts as a wrapper for
 *       its concrete subclass elements (e.g., {@code Implementation} wrapping
 *       {@code Task}, {@code SubFlow}, etc.)</li>
 * </ul>
 */
public final class StandaloneExtensionsConstants
{
    /**
     * Name of an annotation that indicates a feature should be wrapped with
     * an additional XML element during serialization.
     *
     * <p>For example, the {@code participants} feature on {@code ParticipantsContainer}
     * has annotation {@code wrap=Participants}, meaning the XML output is:</p>
     * <pre>{@code
     * <Participants>
     *   <Participant Id="p1" .../>
     *   <Participant Id="p2" .../>
     * </Participants>
     * }</pre>
     * <p>During parsing, the wrapper element must be ignored (skipped) so that
     * the contained elements are parsed directly into the feature's list.</p>
     */
    public static final String WRAP_ANNOTATION = "wrap"; //$NON-NLS-1$

    /**
     * Name of an annotation that specifies this abstract class should be
     * treated as a wrapper for its child subclass element.
     *
     * <p>For example, the {@code implementation} feature on {@code Activity}
     * has annotation {@code subclass-wrap=Implementation}, meaning the XML
     * contains {@code <Implementation>} wrapping a concrete subclass element
     * like {@code <Task>} or {@code <SubFlow>}.</p>
     */
    public static final String SUBCLASS_WRAP_ANNOTATION = "subclass-wrap"; //$NON-NLS-1$

    private StandaloneExtensionsConstants()
    {
        // utility class
    }
}
