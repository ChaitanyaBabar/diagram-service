/**
 * GAT generation classes - copied from S5x with modifications.
 *
 * <p>Source location in S5x:</p>
 * <pre>
 * C:\src\S5x\tbs-bpm\product\com.tibco.xpd.n2.feature\plugins\
 *   com.tibco.xpd.n2.pe\src\com\tibco\xpd\n2\pe\gat\generation\
 * </pre>
 *
 * <p>This package will contain the following files (27 total) when
 * populated by the implementer agent:</p>
 * <ul>
 *   <li>{@code BaseGatGenerator.java} - base class with common generation logic</li>
 *   <li>{@code GatProcessGenerator.java} - main orchestrator, generates full model</li>
 *   <li>{@code GatCollaborationGenerator.java} - collaboration/pool/lane generation</li>
 *   <li>{@code GatFlowNodeGeneratorFactory.java} - factory for activity-type-specific generators</li>
 *   <li>{@code GatFlowNodeGenerator.java} - base for all flow node generators</li>
 *   <li>{@code GatFlowElementGenerator.java} - base for flow element generators</li>
 *   <li>{@code GatActivityGenerator.java} - activity/task generation</li>
 *   <li>{@code GatEventGenerator.java} - event generation (start/intermediate/end/boundary)</li>
 *   <li>{@code GatStartEventGenerator.java} - start event specifics</li>
 *   <li>{@code GatEndEventGenerator.java} - end event specifics</li>
 *   <li>{@code GatIntermediateEventGenerator.java} - intermediate event specifics</li>
 *   <li>{@code GatGatewayGenerator.java} - gateway generation</li>
 *   <li>{@code GatSequenceFlowGenerator.java} - sequence flow/transition generation</li>
 *   <li>{@code GatAssociationGenerator.java} - association generation</li>
 *   <li>{@code GatSubProcessGenerator.java} - embedded subprocess generation</li>
 *   <li>{@code GatCallActivityGenerator.java} - call activity generation (Eclipse deps stubbed)</li>
 *   <li>{@code GatUserTaskGenerator.java} - user task specifics</li>
 *   <li>{@code GatEmptyTaskGenerator.java} - empty/abstract task</li>
 *   <li>{@code GatScriptTaskGenerator.java} - script task</li>
 *   <li>{@code GatServiceInvokeTaskGenerator.java} - service invoke task</li>
 *   <li>{@code GatSendTaskGenerator.java} - send task</li>
 *   <li>{@code GatReceiveTaskGenerator.java} - receive task</li>
 *   <li>{@code GatEmailTaskGenerator.java} - email task</li>
 *   <li>{@code GatDatabaseTaskGenerator.java} - database task</li>
 *   <li>{@code GatBusinessRuleTaskGenerator.java} - business rule task</li>
 *   <li>{@code GatCaseTaskGenerator.java} - case/global data task</li>
 *   <li>{@code GatArtifactGenerator.java} - artifact generation</li>
 *   <li>{@code GatTextAnnotationGenerator.java} - text annotation generation</li>
 *   <li>{@code GatDataObjectReferenceGenerator.java} - data object generation</li>
 *   <li>{@code GatGroupGenerator.java} - group generation</li>
 * </ul>
 *
 * <p>Modifications applied during copy (see design doc Section 11):</p>
 * <ol>
 *   <li>Package rename: {@code com.tibco.xpd.n2.pe.gat} -> {@code com.tibco.xpd.n2.gat}</li>
 *   <li>Import redirects: utility classes -> {@code com.tibco.xpd.n2.gat.util.*}</li>
 *   <li>Import redirects: adapters -> {@code com.tibco.xpd.n2.gat.adapters.*}</li>
 *   <li>Import redirects: Draw2D -> {@code com.tibco.xpd.n2.gat.stubs.*}</li>
 *   <li>Eclipse logger -> SLF4J</li>
 *   <li>Eclipse constants -> inlined values or GatConstants</li>
 *   <li>GatCallActivityGenerator Eclipse deps -> stubbed (return null)</li>
 * </ol>
 *
 * @see "bpme-xpdl-diagram-service-design.md, Sections 2.1 and 11"
 */
package com.tibco.xpd.n2.gat.generation;
