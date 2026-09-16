/**
 * GAT model classes - copied verbatim from S5x (package rename only).
 *
 * <p>Source location in S5x:</p>
 * <pre>
 * C:\src\S5x\tbs-bpm\product\com.tibco.xpd.n2.feature\plugins\
 *   com.tibco.xpd.n2.pe\src\com\tibco\xpd\n2\pe\gat\model\
 * </pre>
 *
 * <p>All model classes extend {@code LinkedHashMap<String, Object>} and have
 * zero external dependencies. They are pure data structures serialized to JSON
 * by Gson. 52 files total (47 in this package + 5 in the {@code diagram} sub-package).</p>
 *
 * <p>The only modification needed is the package declaration rename from
 * {@code com.tibco.xpd.n2.pe.gat.model} to {@code com.tibco.xpd.n2.gat.model}.</p>
 *
 * <p>Inheritance hierarchy:</p>
 * <pre>
 * LinkedHashMap&lt;String, Object&gt;
 *   GatModelElement
 *     GatIdElement
 *       GatNamedElement
 *         GatFlowElement
 *           GatFlowNodeElement
 *             GatActivityElement
 *               GatTaskElement -> GatUserTaskElement, GatScriptTaskElement, etc.
 *             GatGatewayElement -> GatExclusiveGatewayElement, etc.
 *             GatEventElement -> GatStartEventElement, GatEndEventElement, etc.
 *             GatSubProcessElement
 *             GatCallActivityElement
 *           GatSequenceFlowElement
 *           GatAssociationElement
 *         GatProcessElement
 *         GatCollaborationElement
 *         GatParticipantElement
 *         GatLaneSetElement
 *         GatLaneElement
 *       GatDefinitionsElement
 *       GatBPShapeElement
 *       GatBPLabelElement
 *       GatEventDefinitionElement -> GatSignalEventDefElement, etc.
 * </pre>
 *
 * @see "bpme-xpdl-diagram-service-design.md, Section 2.1"
 */
package com.tibco.xpd.n2.gat.model;
