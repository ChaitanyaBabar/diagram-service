# BPME XPDL Diagram Service — Analysis

This document captures the runtime issues encountered while standing up the
`bpme-xpdl-diagram-service` and the fixes applied.

---

# Part 1: XPDL2 Jar Runtime Dependency Analysis

## 1. The Failing Class Chain

```
ClassNotFoundException: com.tibco.xpd.resources.util.XpdEcoreUtil
  at com.tibco.xpd.xpdl2.impl.UniqueIdElementImpl.<init>
```

**Root cause:** `UniqueIdElementImpl` (line 78) calls `XpdEcoreUtil.generateUUID()` in its
constructor. Every XPDL model object (Activity, Process, Transition, Event, etc.) extends
`UniqueIdElementImpl` or `NamedElementImpl` (which extends `UniqueIdElementImpl`). There are
**34 impl classes** in the xpdl2+xpdExtension model that inherit from this base class, so
the very first EMF object instantiation during XPDL parsing triggers the failure.

---

## 2. XPDL2 Jar Declared Dependencies (MANIFEST.MF Require-Bundle)

| Bundle                        | Purpose in xpdl2                                                | Already Provided?    |
|-------------------------------|-----------------------------------------------------------------|----------------------|
| `com.tibco.xpd.resources`     | XpdEcoreUtil.generateUUID() used in UniqueIdElementImpl         | **MISSING** (the bug)|
| `org.eclipse.core.runtime`    | Platform logging in HelperExtensions                            | Not needed at runtime|
| `org.eclipse.emf.ecore`       | EMF Core (model infrastructure)                                 | YES (pom.xml)        |
| `org.eclipse.emf.ecore.xmi`   | EMF XMI serialization                                           | YES (pom.xml)        |
| `com.tibco.xpd.xpdl2`         | Self-reference (re-export)                                      | N/A                  |

---

## 3. Dependency Deep-Dive: `com.tibco.xpd.resources`

### 3.1 Location

```
C:\src\S5x\tbs-core\product\com.tibco.xpd.core.feature\plugins\com.tibco.xpd.resources
```

### 3.2 Plugin Nature

This is an **Eclipse OSGi plugin** with:
- `Bundle-Activator: com.tibco.xpd.resources.XpdResourcesPlugin` (requires OSGi lifecycle)
- 12+ Eclipse Platform bundle dependencies
- Exports 20+ packages (builders, indexers, project config, refactoring, GMF integration)

### 3.3 Transitive Dependencies (from its MANIFEST.MF Require-Bundle)

```
com.tibco.xpd.resources
  +-- org.eclipse.core.runtime              (Eclipse Platform core)
  +-- org.eclipse.core.resources            (Eclipse workspace/project model)
  +-- org.eclipse.emf.transaction           (EMF transactional editing domain)
  +-- org.eclipse.ltk.core.refactoring      (Eclipse refactoring framework)
  +-- com.tibco.xpd.tpcl.org.apache.derby   (embedded database)
  +-- org.eclipse.compare                   (Eclipse compare framework)
  +-- org.eclipse.core.expressions          (Eclipse expression language)
  +-- org.eclipse.emf.edit.ui              (EMF editor UI framework)
  +-- org.eclipse.gmf.runtime.diagram.core  (GMF diagramming)
  +-- org.eclipse.gmf.runtime.diagram.ui.resources.editor (GMF editor)
  +-- org.eclipse.ui                        (Eclipse Workbench UI)
  +-- org.eclipse.ui.ide                    (Eclipse IDE UI)
  +-- org.eclipse.help                      (Eclipse help system)
  +-- org.eclipse.pde.core                  (Plugin Development Environment)
```

**Verdict: CANNOT be included as-is.** Including this jar would require pulling in the
entire Eclipse Platform, GMF, PDE, and more. Its Bundle-Activator would fail immediately
without an OSGi runtime container.

### 3.4 The Only Class Actually Used

The **single class** from `com.tibco.xpd.resources` that xpdl2 uses at runtime:

```java
// File: com/tibco/xpd/resources/util/XpdEcoreUtil.java
package com.tibco.xpd.resources.util;

import org.eclipse.emf.ecore.util.EcoreUtil;

public class XpdEcoreUtil {
    public static String generateUUID() {
        String uuId = EcoreUtil.generateUUID();
        return uuId;
    }
}
```

**This method is a trivial pass-through to `EcoreUtil.generateUUID()`**, which is already
available in the `org.eclipse.emf.ecore` jar that the service includes. The class has no
other runtime dependencies.

---

## 4. All Eclipse-Dependent Classes Inside the xpdl2 Jar

The jar contains classes from four packages. Only the `extension` and `internal` packages
have Eclipse Platform dependencies:

### 4.1 Classes with Eclipse Platform Dependencies (in the jar)

| Class                                                  | Eclipse Dependencies                                     | Loaded at Runtime? |
|--------------------------------------------------------|----------------------------------------------------------|--------------------|
| `com.tibco.xpd.xpdl2.extension.HelperExtensions`      | `o.e.core.runtime.Platform`, `ILog`, `Status`, `o.osgi.framework.Bundle` | NO (service uses default XMLHelperImpl) |
| `com.tibco.xpd.xpdl2.extension.ResourceExtensions`    | `o.e.emf.transaction.*`, `o.e.emf.edit.domain.*`        | NO (service uses StandaloneXpdlResource) |
| `com.tibco.xpd.internal.Messages`                     | `o.e.osgi.util.NLS`                                     | NO (only triggered by PublicationStatusType.getUIText()) |

### 4.2 Classes WITHOUT Eclipse Dependencies (safe at runtime)

| Package                                | Count | Dependencies              |
|----------------------------------------|-------|---------------------------|
| `com.tibco.xpd.xpdl2` (interfaces/enums) | ~180 | EMF only                  |
| `com.tibco.xpd.xpdl2.impl`            | ~180  | EMF + **XpdEcoreUtil**    |
| `com.tibco.xpd.xpdl2.util`            | ~10   | EMF only                  |
| `com.tibco.xpd.xpdExtension`          | ~120  | EMF only                  |
| `com.tibco.xpd.xpdExtension.impl`     | ~120  | EMF only                  |
| `com.tibco.xpd.xpdExtension.util`     | ~10   | EMF only                  |
| `com.tibco.xpd.xpdl2.extension.LoadExtensions` | 1 | EMF only               |
| `com.tibco.xpd.xpdl2.extension.SaveExtensions` | 1 | EMF only               |
| `com.tibco.xpd.xpdl2.extension.SAXParserExtensions` | 1 | EMF only          |
| `com.tibco.xpd.xpdl2.extension.EMFSearchUtil` | 1 | EMF only               |
| `com.tibco.xpd.xpdl2.extension.ExtensionsConstants` | 1 | None              |

---

## 5. Complete Dependency Tree

```
com.tibco.xpd.xpdl2.jar (910 class files)
|
+-- org.eclipse.emf.common           [INCLUDED in pom.xml]
+-- org.eclipse.emf.ecore            [INCLUDED in pom.xml]
+-- org.eclipse.emf.ecore.xmi        [INCLUDED in pom.xml]
|
+-- com.tibco.xpd.resources          [MISSING - causes ClassNotFoundException]
|   |
|   +-- Only class used: XpdEcoreUtil.generateUUID()
|   |   Delegates to: EcoreUtil.generateUUID() [already in emf.ecore]
|   |
|   +-- Full plugin CANNOT be included (requires Eclipse Platform + 12 bundles)
|
+-- org.eclipse.core.runtime          [NOT NEEDED - only used by HelperExtensions]
+-- org.eclipse.osgi                  [NOT NEEDED - only used by Messages]
+-- org.eclipse.emf.transaction       [NOT NEEDED - only used by ResourceExtensions]
```

---

## 6. Resolution Options

### Option A: Provide a Shim Jar (RECOMMENDED)

Create `lib/com.tibco.xpd.resources-shim.jar` containing exactly ONE class:

```java
package com.tibco.xpd.resources.util;

import org.eclipse.emf.ecore.util.EcoreUtil;

public class XpdEcoreUtil {
    public static String generateUUID() {
        return EcoreUtil.generateUUID();
    }
}
```

This is NOT a "stub" -- it is **the identical implementation** as the real
`XpdEcoreUtil.generateUUID()`. The real class does nothing more than delegate to
`EcoreUtil.generateUUID()`. The shim simply provides this class without pulling in the
entire `com.tibco.xpd.resources` Eclipse plugin and its 12+ transitive dependencies.

**Pros:**
- Zero additional dependencies
- Jar is ~1KB
- Identical behavior to the real code
- No risk of loading other Eclipse-dependent classes from the resources plugin

**Cons:**
- If future xpdl2 versions add more references to `com.tibco.xpd.resources`, the shim
  would need updating (but this has been stable for 10+ years)

### Option B: Rebuild xpdl2 Jar Without the Dependency

Modify `UniqueIdElementImpl.java` in the S5x source to call `EcoreUtil.generateUUID()`
directly instead of `XpdEcoreUtil.generateUUID()`, then rebuild the xpdl2 jar.

**Pros:**
- Eliminates the `com.tibco.xpd.resources` dependency entirely
- Clean solution

**Cons:**
- Requires modifying the S5x codebase (xpdl2 is a shared EMF model used by all Studio products)
- Requires rebuilding via Tycho (Eclipse PDE build system)
- Change affects all consumers of the xpdl2 model

### Option C: Include the Full Resources Jar (NOT FEASIBLE)

Include the real `com.tibco.xpd.resources` jar from S5x.

**Why this does not work:**
1. The jar has a `Bundle-Activator` that requires an OSGi container
2. Transitive dependencies include: Eclipse Core Runtime, Eclipse Core Resources,
   EMF Transaction, Eclipse LTK Refactoring, Apache Derby (TIBCO-wrapped), Eclipse
   Compare, Eclipse Core Expressions, EMF Edit UI, GMF Runtime (2 jars), Eclipse UI,
   Eclipse UI IDE, Eclipse Help, Eclipse PDE Core
3. Each of those has its own transitive dependencies (hundreds of jars)
4. Many require a running Eclipse Workbench

**Verdict: Infeasible for a standalone Spring Boot service.**

### Option D: Provide Shim Jar with Safety Net (RECOMMENDED if paranoid)

Same as Option A, but also include a stub `Messages` class for the latent
`PublicationStatusType.getUIText()` dependency:

```java
package com.tibco.xpd.internal;

public class Messages {
    public static String PublicationStatusType_Under_Revision_UI_Text = "Under Revision";
    public static String PublicationStatusType_Released_UI_Text = "Released";
    public static String PublicationStatusType_Under_Test_UI_Text = "Under Test";
}
```

And a no-op `NLS` class:

```java
package org.eclipse.osgi.util;

public class NLS {
    public static void initializeMessages(String bundleName, Class<?> clazz) {
        // no-op in standalone mode
    }
}
```

This prevents any `ClassNotFoundException` if code accidentally calls
`PublicationStatusType.getUIText()` or if another codepath triggers `Messages` loading.

---

## 7. Latent Risks (classes in the jar with Eclipse dependencies)

These classes are in the xpdl2 jar but are NOT loaded during normal XPDL parsing in the
standalone service. They would only cause issues if the service's code explicitly references
them:

| Class | Trigger Condition | Risk |
|-------|-------------------|------|
| `HelperExtensions` | Someone overrides `createXMLHelper()` to use it | LOW - service has its own StandaloneXpdlResource |
| `ResourceExtensions` | Someone creates `ResourceExtensions` directly | LOW - service uses StandaloneXpdlResource |
| `Messages` | Code calls `PublicationStatusType.getUIText(int)` | LOW - UI-only method |

---

## 8. Recommended Additions to lib/ and pom.xml

### Jars to Add

| Jar                                 | Source                                    | Size  | Purpose                       |
|--------------------------------------|-------------------------------------------|-------|-------------------------------|
| `com.tibco.xpd.resources-shim.jar`  | Create new (see Option A/D above)         | ~1KB  | XpdEcoreUtil.generateUUID()   |

### pom.xml Entry

```xml
<!-- Shim for com.tibco.xpd.resources.util.XpdEcoreUtil -->
<dependency>
    <groupId>com.tibco.xpd</groupId>
    <artifactId>resources-shim</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/com.tibco.xpd.resources-shim.jar</systemPath>
</dependency>
```

### NO Additional Jars Needed

The following are NOT needed because their dependent classes are never loaded at runtime:
- `org.eclipse.core.runtime` (HelperExtensions - not used)
- `org.eclipse.osgi` / `org.eclipse.equinox.common` (Messages/NLS - not used)
- `org.eclipse.emf.transaction` (ResourceExtensions - not used)
- `org.eclipse.emf.edit.ui` (ResourceExtensions - not used)
- Any other Eclipse Platform jars

---

## 9. Summary

| Question | Answer |
|----------|--------|
| How many classes from `com.tibco.xpd.resources` are used? | **1** (XpdEcoreUtil) |
| What does that class do? | Calls `EcoreUtil.generateUUID()` |
| Can we include the real resources jar? | **NO** (12+ Eclipse dependencies, needs OSGi) |
| Can we include just the needed class? | **YES** (shim jar, identical behavior) |
| Are there other missing dependencies? | **NO** (all other deps are EMF, already included) |
| How many impl classes are affected? | **34** (all extend UniqueIdElementImpl) |

**Resolution applied:** Created `XpdEcoreUtil.java` at
`src/main/java/com/tibco/xpd/resources/util/XpdEcoreUtil.java` with the identical
`generateUUID()` implementation (delegates to `EcoreUtil.generateUUID()`). No shim jar
needed — the class is compiled directly as part of the service project.

---
---

# Part 2: XPDL Parsing — Feature 'Participants' Not Found

## 1. The Error

After fixing the `XpdEcoreUtil` dependency, the next runtime error was:

```
org.eclipse.emf.ecore.xmi.FeatureNotFoundException:
  Feature 'Participants' not found. (file:///...input.xpdl, 14, 23)
```

EMF could now instantiate model objects, but the XML parser could not map XML element
names to EMF model features during XPDL loading.

---

## 2. Root Cause: Custom `wrap` Annotation Pattern

The XPDL `.ecore` model uses a **non-standard `wrap` annotation** that is not part of
EMF's built-in `ExtendedMetaData` processing. Example from `xpdl2.ecore`:

```xml
<!-- Feature: participants on ParticipantsContainer -->
<eAnnotations source="http:///org/eclipse/emf/ecore/util/ExtendedMetaData">
    <details key="kind" value="element"/>
    <details key="name" value="Participant"/>       <!-- individual element -->
    <details key="wrap" value="Participants"/>       <!-- wrapper element -->
</eAnnotations>
```

This means the XPDL XML has a **wrapper element** that groups the individual elements:

```xml
<Participants>              <!-- wrapper — no model feature, just grouping -->
  <Participant Id="p1"/>    <!-- actual model element -->
  <Participant Id="p2"/>
</Participants>
```

Standard EMF's `SAXXMLHandler` sees `<Participants>`, looks for a feature literally named
`Participants` on the current context object — and throws `FeatureNotFoundException`
because the feature is actually named `participants` with XML name `Participant` (singular).

---

## 3. Scale of the Problem

| Ecore Model | `wrap` Annotations | `subclass-wrap` Annotations |
|---|---|---|
| `xpdl2.ecore` | **38** | **3** (`Implementation`, `Event`, `DataType`) |
| `XpdExtension.ecore` | **15** | **0** |
| **Total** | **53** | **3** |

Every one of these wrapper elements would cause the same `FeatureNotFoundException` if
encountered during parsing. This is not a one-off — it's a fundamental pattern in the
XPDL model.

---

## 4. How S5x Handles It

In the S5x Eclipse environment, three custom classes handle wrapper elements:

| S5x Class | Package | What It Does |
|---|---|---|
| `SAXParserExtensions` | `com.tibco.xpd.xpdl2.extension` | Overrides `startElement()`/`endElement()` to detect and skip wrapper elements by scanning features for `wrap`/`subclass-wrap` annotations |
| `HelperExtensions` | `com.tibco.xpd.xpdl2.extension` | Provides namespace prefix tracking, lenient feature resolution, fallback for namespace mismatches |
| `LoadExtensions` | `com.tibco.xpd.xpdl2.extension` | Creates the custom SAX handler via `makeDefaultHandler()` and wires it into EMF's load pipeline |

These classes exist inside the `com.tibco.xpd.xpdl2.jar`, but they have **Eclipse Platform
dependencies** (`org.eclipse.core.runtime.Platform`, `ILog`, `Status`, `Bundle`,
`InternalTransactionalEditingDomain`) and cannot be used directly in a standalone service.

---

## 5. The Fix — Standalone Ports of S5x Extension Classes

Four new files created, one modified — all in
`src/main/java/com/tibco/xpd/n2/gat/xpdl/`:

### 5.1 `StandaloneExtensionsConstants.java` (new)

Constants for the annotation keys:
- `WRAP_KEY = "wrap"` — marks a wrapper element name
- `SUBCLASS_WRAP_KEY = "subclass-wrap"` — marks a wrapper that also indicates subclass type

### 5.2 `StandaloneHelperExtensions.java` (new)

Port of S5x `HelperExtensions`. Replaces Eclipse Platform logging with SLF4J. Provides:
- Namespace prefix tracking (needed for wrapper detection)
- Lenient `createFromString()` — catches `NumberFormatException` etc. instead of crashing
- Fallback feature resolution — retries with null namespace for attributes, scans
  `subclass-wrap` annotations for elements

### 5.3 `StandaloneSAXParserExtensions.java` (new)

Port of S5x `SAXParserExtensions`. **The critical class.** Overrides `startElement()` and
`endElement()`:

```
startElement("Participants"):
  1. Get current context EObject
  2. For each EStructuralFeature on the object:
     - Get ExtendedMetaData annotation
     - Check if annotation has "wrap" = "Participants"
  3. If found → push to skip stack, return (element ignored)
  4. If not found → delegate to standard EMF handler
```

Also handles `subclass-wrap` for elements like `<Implementation>`, `<Event>`, `<DataType>`
where the wrapper also carries type information.

### 5.4 `StandaloneLoadExtensions.java` (new)

Port of S5x `LoadExtensions`. Overrides `makeDefaultHandler()` to create the custom SAX
handler (`StandaloneSAXParserExtensions`) instead of EMF's default.

### 5.5 `StandaloneXpdlResource.java` (modified)

Now overrides two methods to wire everything together:
- `createXMLHelper()` → returns `StandaloneHelperExtensions`
- `createXMLLoad()` → returns `StandaloneLoadExtensions`

---

## 6. Wrapper Element Examples

| XML Wrapper Element | Wraps | Feature Name | Model Type |
|---|---|---|---|
| `<Participants>` | `<Participant>` | `participants` | `ParticipantsContainer` |
| `<Activities>` | `<Activity>` | `activities` | `FlowContainer` |
| `<Transitions>` | `<Transition>` | `transitions` | `FlowContainer` |
| `<DataFields>` | `<DataField>` | `dataFields` | `DataFieldsContainer` |
| `<Pools>` | `<Pool>` | `pools` | `Package` |
| `<Lanes>` | `<Lane>` | `lanes` | `Pool` |
| `<Artifacts>` | `<Artifact>` | `artifacts` | `FlowContainer` |
| `<Associations>` | `<Association>` | `associations` | `Package` |
| `<Implementation>` | varies | `implementation` | `Activity` (subclass-wrap) |
| `<Event>` | varies | `event` | `Activity` (subclass-wrap) |
| ... | ... | ... | (53 total `wrap` + 3 `subclass-wrap`) |

---

## 7. Summary

| Question | Answer |
|----------|--------|
| What caused the error? | XPDL `.ecore` uses custom `wrap` annotations; standard EMF doesn't process them |
| How many wrapper elements exist? | **56** (53 `wrap` + 3 `subclass-wrap`) across both ecore models |
| How does S5x handle it? | Custom `SAXParserExtensions` intercepts and skips wrapper elements |
| Why couldn't we use S5x's code? | Eclipse Platform dependencies (`Platform.getLog()`, `ILog`, `Bundle`) |
| What did we do? | Ported 3 S5x classes to standalone versions with SLF4J logging |
| Files created | 4 new (`StandaloneExtensionsConstants`, `StandaloneHelperExtensions`, `StandaloneSAXParserExtensions`, `StandaloneLoadExtensions`) + 1 modified (`StandaloneXpdlResource`) |

---
---

# Part 3: GAT Generation Correctness — Boundary Event Coordinates and AdHoc Task Type

Two correctness issues were found by comparing the service's `.json` output against Studio's
reference `.gat` output for the `GroupProcess` example.

---

## Issue 1: Boundary Event Coordinates Wrong

### Symptom

The boundary event `_v0inwK29EfGYJLLbLtGxiw` (a timer attached to the "Manage Case" user
task) was positioned at the **top-right corner** of the host task instead of its correct
position on the **bottom edge**.

| Source | x | y | width | height |
|--------|-------|-------|-------|--------|
| Studio .gat (correct) | 248.5 | 211.5 | 27.0 | 27.0 |
| Service .json (wrong) | 259.5 | 137.5 | 27.0 | 27.0 |

Delta: X +11, Y -74. The event center in the service output (273, 151) sits exactly at the
top-right corner of the host task rectangle, not at 23.75% around the border.

### Root Cause

**`GatLineUtilities.getLinePointFromPortion()` interpreted the `portion` parameter as a
0.0-1.0 ratio instead of a 0-100 percentage.**

The XPDL stores boundary event position as a percentage (0-100) around the host task border,
starting from the top-right corner and going clockwise:

```xml
<xpdl2:NodeGraphicsInfo ToolId="XPD.BorderEventPosition">
    <xpdl2:Coordinates XCoordinate="23.75" YCoordinate="0.0"/>
</xpdl2:NodeGraphicsInfo>
```

S5x's `XPDLineUtilities.getLinePointFromPortion()` correctly divides by 100:

```java
int pixelsFromStart = (int) (totalLength * (percentPortion / 100));
```

The service's original implementation multiplied directly:

```java
// BUG: totalLength * 23.75 = 8550 pixels (way past perimeter of ~360)
double targetLength = totalLength * portion;
```

With a task perimeter of ~360 pixels, `360 * 23.75 = 8550` — far exceeding the perimeter.
`getLinePointFromOffset` then returned the **last point** in the border polyline, which is
the top-right corner (the start/close point).

### The Fix

Changed `GatLineUtilities.getLinePointFromPortion()` to match S5x:

```java
// File: src/main/java/com/tibco/xpd/n2/gat/util/GatLineUtilities.java

// Before (broken):
double targetLength = totalLength * portion;

// After (fixed):
int totalLength = (int) getLineLength(points);
int pixelsFromStart = (int) (totalLength * (percentPortion / 100));
```

Also added boundary checks matching S5x: `percentPortion <= 0.0` returns first point,
`percentPortion >= 100.0` returns last point.

### Verification

With the fix, 23.75% of 360-pixel perimeter = 85 pixels from top-right:

1. Segment 1 (top-right to bottom-right): 74 pixels. 85 > 74, remaining = 11.
2. Segment 2 (bottom-right to bottom-left): 106 pixels. 11 <= 106 -- on this segment.
3. Interpolation: x = 213 - 11 = 202, y = 225 (bottom edge).
4. Subtract half event size: (202 - 13.5, 225 - 13.5) = (188.5, 211.5).
5. After `adjustToGatCoordSystem`: (248.5, 211.5) -- matches Studio .gat.

### File Modified

- `src/main/java/com/tibco/xpd/n2/gat/util/GatLineUtilities.java`

---

## Issue 2: AdHocUserTask Instead of UserTask

### Symptom

All four user tasks in the output had type `bp:AdHocUserTask` instead of `bp:UserTask`:

| Activity | XPDL Has AdHocTaskConfiguration? | Studio .gat Type | Service .json Type |
|----------|----------------------------------|------------------|--------------------|
| Manage Case | No | `bp:UserTask` | `bp:AdHocUserTask` |
| Review Reminder | No | `bp:UserTask` | `bp:AdHocUserTask` |
| Close Case | No | `bp:UserTask` | `bp:AdHocUserTask` |
| Refer Case | No | `bp:UserTask` | `bp:AdHocUserTask` |

### Root Cause

**`GatModelUtil.getOtherElement()` used `FeatureMap.get(feature, false)` instead of
`FeatureMap.list(feature)`, returning a non-null empty EList for absent features.**

`GatUserTaskGenerator.generateActivity()` checks for an AdHoc configuration:

```java
Object adHocConfiguration = GatModelUtil.getOtherElement(xpdlActivity,
    XpdExtensionPackage.eINSTANCE.getDocumentRoot_AdHocTaskConfiguration());

if (adHocConfiguration != null) {
    return new GatAdHocUserTaskElement(...);  // wrong!
} else {
    return new GatUserTaskElement(...);       // correct
}
```

The EMF `FeatureMap.get(EStructuralFeature, boolean)` method checks
`FeatureMapUtil.isMany(owner, feature)`. Document root features generated from XSD global
elements typically have `upperBound = -2` (unbounded in the document root context), making
them multi-valued. For multi-valued features, `get()` returns `list()` -- which is a live
`EList` view that is **never null**, even when empty.

So `getOtherElement()` was returning an empty `EList` (truthy, non-null) for every user task,
regardless of whether `<xpdExt:AdHocTaskConfiguration>` was actually present.

S5x's `Xpdl2ModelUtil.getOtherElement()` avoids this with an explicit emptiness check:

```java
// S5x (correct):
EList eList = owner.getOtherElements().list(elementFeature);
if (!eList.isEmpty()) {
    item = eList.get(0);
}
```

### The Fix

Changed `GatModelUtil.getOtherElement()` to use `featureMap.list(feature)` with an
emptiness check, matching S5x:

```java
// File: src/main/java/com/tibco/xpd/n2/gat/util/GatModelUtil.java

// Before (broken):
return featureMap.get(feature, false);

// After (fixed):
EList<?> eList = featureMap.list(feature);
if (eList != null && !eList.isEmpty()) {
    return eList.get(0);
}
return null;
```

### File Modified

- `src/main/java/com/tibco/xpd/n2/gat/util/GatModelUtil.java`

---

## Summary

| Issue | Root Cause | Fix Location | Impact |
|-------|-----------|-------------|--------|
| Boundary event at wrong position | `getLinePointFromPortion()` treated 0-100 percentage as 0-1 ratio | `GatLineUtilities.java` | All boundary events positioned incorrectly |
| All user tasks typed as AdHoc | `getOtherElement()` returned non-null empty EList for absent features | `GatModelUtil.java` | Every user task rendered with AdHoc marker |

Both issues stem from subtle API contract differences between S5x's Eclipse-based utilities
and the service's standalone reimplementations. The service code was functionally correct in
isolation but did not match the exact semantics of the S5x originals.

---
---

# Part 4: Multi-Process XPDL Support

## 1. The Requirement

A single XPDL file can contain **multiple processes** (via `Package.getWorkflowProcesses()`).
The original `/api/diagram/transform` endpoint only returned a single GAT JSON object for one
process (the first, or one selected by an optional `processId` parameter). Clients consuming
this endpoint had no way to access other processes in the same XPDL file without making
separate requests and knowing each process ID in advance.

---

## 2. Response Format Change

### Before (single object)

```
POST /api/diagram/transform
Content-Type: multipart/form-data
```

Response:
```json
{
  "$type": "bp:Definitions",
  "id": "_processId_gat",
  "rootElements": [...],
  "diagrams": [...],
  "collaboration": {...}
}
```

### After (array of objects)

```
POST /api/diagram/transform
Content-Type: multipart/form-data
```

Response:
```json
[
  {
    "$type": "bp:Definitions",
    "id": "_process1Id_gat",
    "processId": "_process1Id",
    "processName": "My First Process",
    "rootElements": [...],
    "diagrams": [...],
    "collaboration": {...}
  },
  {
    "$type": "bp:Definitions",
    "id": "_process2Id_gat",
    "processId": "_process2Id",
    "processName": "My Second Process",
    "rootElements": [...],
    "diagrams": [...],
    "collaboration": {...}
  }
]
```

Each array element is a complete GAT Definitions model that can be passed directly to the
diagram renderer. Two additional properties are added for identification:

| Property | Source | Purpose |
|----------|--------|---------|
| `processId` | `Process.getId()` | XPDL process ID for programmatic lookup |
| `processName` | `Process.getName()` (falls back to ID if null) | Human-readable label |

These extra properties are ignored by the diagram renderer — `GatDefinitionsElement` extends
`LinkedHashMap<String, Object>`, so arbitrary keys can be added without affecting the model
contract.

---

## 3. Server-Side Changes

### GatController.java

The `/transform` endpoint was changed to call `transformAllProcesses()` (the same method
already used by `/transform-all`) instead of `transformXpdlToGat()`. The `processId` request
parameter was removed since the endpoint now always returns all processes.

Both `/transform` and `/transform-all` now produce identical output. The `/transform-all`
endpoint is retained for backward compatibility.

### GatTransformService.java

`transformAllProcesses()` was updated to inject `processId` and `processName` into each
`GatDefinitionsElement` before serialization, so clients can identify which process each
array element represents.

The single-process method `transformXpdlToGat(InputStream, String)` is retained but is no
longer called by any controller endpoint. It may be useful for future internal use or testing.

---

## 4. Client-Side Changes (index.html)

The demo `index.html` in `bpm-process-diagram-js` was updated to:

1. **URL**: Points to `http://localhost:8099/api/diagram/transform`
2. **Response handling**: Expects an array response, validates it is non-empty, and loads the
   **first element** (`gatJsonArray[0]`) into the diagram via `loadDiagram()`
3. **Logging**: Logs the total number of processes found and a note when additional processes
   are available but not displayed

---

## 5. Backward Compatibility

| Aspect | Impact |
|--------|--------|
| `/transform` callers expecting a single object | **BREAKING** — response is now an array; callers must use `response[0]` |
| `/transform-all` callers | **Non-breaking** — response format unchanged (was already an array), now includes `processId`/`processName` |
| Single-process XPDL files | Array contains one element — functionally equivalent after `[0]` access |
| `processId` query parameter on `/transform` | **Removed** — callers that relied on server-side process selection must now filter client-side |

---

## 6. Summary

| Question | Answer |
|----------|--------|
| What changed? | `/transform` returns a JSON array instead of a single object |
| Why? | An XPDL file can contain multiple processes; single-object response could only represent one |
| How are processes identified? | Each array element includes `processId` and `processName` |
| What does the client do? | Loads `response[0]` (the first process) |
| Is `/transform-all` still needed? | Retained for backward compat; identical behavior to `/transform` |

---
---

# Part 5: Duplicate Artifacts and Associations in Multi-Process XPDL

## 1. The Problem

When a single XPDL file contains **multiple processes**, all package-level artifacts
(Groups, TextAnnotations, DataObjects) and Associations are **duplicated into every
process's GAT JSON output**. Each process should only contain the artifacts and associations
that belong to it.

### Example: `group.xpdl` (3 processes)

| Process | Studio .gat Artifacts | Service .json Artifacts |
|---|---|---|
| `grpProcess` | 1 Group + 1 Association | **5 Groups + 1 Association** (all package artifacts) |
| `grp1` | 2 Groups | **5 Groups + 1 Association** (all package artifacts) |
| `GroupProcess` | 2 Groups | **5 Groups + 1 Association** (all package artifacts) |

The service produces 15 Group elements across the three processes instead of the correct 5.
Associations are similarly duplicated.

---

## 2. Root Cause

Two methods in `GatModelUtil.java` return **all** package-level artifacts/associations
without filtering by process ownership:

### `getAllArtifactsInProcess()` (line 579)

```java
public static List<Artifact> getAllArtifactsInProcess(Process process)
{
    // ...
    EList<Artifact> artifacts = process.getPackage().getArtifacts();
    if (artifacts != null)
    {
        result.addAll(artifacts);  // BUG: adds ALL package artifacts, not just this process's
    }
    return result;
}
```

### `getAllAssociationsInProc()` (line 600)

```java
public static List<Association> getAllAssociationsInProc(Process process)
{
    // ...
    EList<Association> associations = process.getPackage().getAssociations();
    if (associations != null)
    {
        result.addAll(associations);  // BUG: adds ALL package associations
    }
    return result;
}
```

**Contrast with `getProcessPools()` (line 232)**, which DOES correctly filter:

```java
public static Collection<Pool> getProcessPools(Process process)
{
    // ...
    for (Pool pool : allPools)
    {
        if (processId != null && processId.equals(pool.getProcessId()))  // filtered!
        {
            processPools.add(pool);
        }
    }
    return processPools;
}
```

The artifact and association methods need the same kind of process-scoped filtering.

---

## 3. XPDL Model Structure — Package vs. Process Ownership

In XPDL 2.1, `<xpdl2:Artifacts>` and `<xpdl2:Associations>` are declared at the
**Package level**, not per-Process. They are siblings of `<xpdl2:Pools>` and
`<xpdl2:WorkflowProcesses>`:

```xml
<xpdl2:Package>
    <xpdl2:Pools>...</xpdl2:Pools>
    <xpdl2:WorkflowProcesses>
        <xpdl2:WorkflowProcess Id="_proc1">...</xpdl2:WorkflowProcess>
        <xpdl2:WorkflowProcess Id="_proc2">...</xpdl2:WorkflowProcess>
    </xpdl2:WorkflowProcesses>
    <xpdl2:Artifacts>
        <xpdl2:Artifact Id="_art1" ArtifactType="Group">
            <xpdl2:NodeGraphicsInfo LaneId="_proc1" .../>   <!-- belongs to proc1 -->
        </xpdl2:Artifact>
        <xpdl2:Artifact Id="_art2" ArtifactType="Group">
            <xpdl2:NodeGraphicsInfo LaneId="_lane3" .../>   <!-- belongs to proc2 via lane -->
        </xpdl2:Artifact>
    </xpdl2:Artifacts>
    <xpdl2:Associations>
        <xpdl2:Association Source="_act1" Target="_art1" .../>
    </xpdl2:Associations>
</xpdl2:Package>
```

Process ownership is determined by the artifact's `NodeGraphicsInfo.LaneId` attribute,
which can reference one of three things:

| LaneId Value | Meaning |
|---|---|
| A **Process ID** | Artifact sits directly on the process canvas (no lanes) |
| A **Lane ID** within a pool | Artifact sits inside a lane; the lane belongs to a pool, which belongs to a process |
| An **ActivitySet ID** | Artifact sits inside an embedded subprocess (handled separately by `isArtifactInSubProcess()`) |

### Concrete Example from `group.xpdl`

| Artifact ID | LaneId | LaneId Resolves To | Belongs To Process |
|---|---|---|---|
| `_mm9I8IRfEfG0a4r1_WaoBg` | `_mm9I4oRfEfG0a4r1_WaoBg` | processId of `grpProcess` | `grpProcess` |
| `_43ZgUIThEfGZEJ5GWndp5w` | `_jpbtwIThEfGZEJ5GWndp5w` | processId of `grp1` | `grp1` |
| `_IoI1MYTiEfGZEJ5GWndp5w` | `_jpbtwIThEfGZEJ5GWndp5w` | processId of `grp1` | `grp1` |
| `_E3PEnoTkEfGZEJ5GWndp5w` | `_E3PEoYTkEfGZEJ5GWndp5w` | processId of `GroupProcess` | `GroupProcess` |
| `_E3PEoITkEfGZEJ5GWndp5w` | `_E3PEoYTkEfGZEJ5GWndp5w` | processId of `GroupProcess` | `GroupProcess` |

---

## 4. Filtering Logic

### 4.1 Artifact Filtering

An artifact belongs to a process if its `NodeGraphicsInfo.getLaneId()` matches any of the
following IDs within the process scope:

```
Valid LaneId values for a process:
  1. The process ID itself              (process.getId())
  2. Any lane ID in any pool            (pool.getLanes() → lane.getId(), recursively)
  3. Any ActivitySet ID in the process   (process.getActivitySets() → actSet.getId())
```

Note: case (3) is already handled downstream — `isArtifactInSubProcess()` in
`GatProcessGenerator` skips artifacts whose LaneId matches an ActivitySet ID (those are
generated separately by the embedded subprocess generator). So the filtering in
`getAllArtifactsInProcess()` should include cases (1) and (2), and case (3) will be filtered
at the caller level.

**Algorithm:**

```java
public static List<Artifact> getAllArtifactsInProcess(Process process)
{
    // Build the set of valid LaneId values for this process
    Set<String> validLaneIds = new HashSet<>();
    validLaneIds.add(process.getId());

    for (Pool pool : getProcessPools(process))
    {
        collectLaneIds(pool.getLanes(), validLaneIds);  // recursive for nested lanes
    }

    for (ActivitySet actSet : process.getActivitySets())
    {
        validLaneIds.add(actSet.getId());
    }

    // Filter package artifacts by LaneId membership
    List<Artifact> result = new ArrayList<>();
    for (Artifact artifact : process.getPackage().getArtifacts())
    {
        NodeGraphicsInfo ngi = getNodeGraphicsInfo(artifact);
        if (ngi != null && validLaneIds.contains(ngi.getLaneId()))
        {
            result.add(artifact);
        }
    }
    return result;
}

private static void collectLaneIds(EList<Lane> lanes, Set<String> ids)
{
    if (lanes != null)
    {
        for (Lane lane : lanes)
        {
            ids.add(lane.getId());
            collectLaneIds(lane.getChildLanes(), ids);  // nested lanes
        }
    }
}
```

### 4.2 Association Filtering

An association belongs to a process if **both** its `Source` and `Target` reference
elements (activities or artifacts) that belong to the current process. The Source/Target
values are string IDs that can reference either an Activity or an Artifact.

**Algorithm:**

```java
public static List<Association> getAllAssociationsInProc(Process process)
{
    // Build a set of all element IDs belonging to this process
    Set<String> processElementIds = new HashSet<>();

    // Activity IDs (from process-level activities)
    for (Activity act : process.getActivities())
    {
        processElementIds.add(act.getId());
    }

    // Activity IDs from ActivitySets (embedded subprocesses)
    for (ActivitySet actSet : process.getActivitySets())
    {
        for (Activity act : actSet.getActivities())
        {
            processElementIds.add(act.getId());
        }
    }

    // Artifact IDs belonging to this process (reuse filtered list)
    for (Artifact art : getAllArtifactsInProcess(process))
    {
        processElementIds.add(art.getId());
    }

    // Filter associations by Source/Target membership
    List<Association> result = new ArrayList<>();
    for (Association assoc : process.getPackage().getAssociations())
    {
        if (processElementIds.contains(assoc.getSource())
            && processElementIds.contains(assoc.getTarget()))
        {
            result.add(assoc);
        }
    }
    return result;
}
```

---

## 5. The Fix

Modify the two methods in `GatModelUtil.java`:

### File: `src/main/java/com/tibco/xpd/n2/gat/util/GatModelUtil.java`

| Method | Current Behavior | Fixed Behavior |
|---|---|---|
| `getAllArtifactsInProcess(Process)` | Returns `process.getPackage().getArtifacts()` (all) | Filters by `NodeGraphicsInfo.getLaneId()` membership in the process's valid ID set |
| `getAllAssociationsInProc(Process)` | Returns `process.getPackage().getAssociations()` (all) | Filters by Source/Target referencing elements within the process |

Add one private helper:

| Method | Purpose |
|---|---|
| `collectLaneIds(EList<Lane>, Set<String>)` | Recursively collects lane IDs (including nested lanes) into a set |

No changes needed in `GatProcessGenerator.java` — the existing `isArtifactInSubProcess()`
and `isAssociationInSubProcess()` methods will continue to work correctly on the
already-filtered lists.

---

## 6. Verification

Compare the service's JSON output against Studio's reference `.gat` files for the
`group.xpdl` test case (3 processes):

| Process | Expected Artifacts | Expected Associations |
|---|---|---|
| `grpProcess` | 1 Group (`_mm9I8IRfEfG0a4r1_WaoBg`) | 1 Association |
| `grp1` | 2 Groups (`_43ZgUIThEfGZEJ5GWndp5w`, `_IoI1MYTiEfGZEJ5GWndp5w`) | 0 Associations |
| `GroupProcess` | 2 Groups (`_E3PEnoTkEfGZEJ5GWndp5w`, `_E3PEoITkEfGZEJ5GWndp5w`) | 0 Associations |
| **Total** | **5 Groups** (not 15) | **1 Association** (not 3) |

---

## 7. Summary

| Question | Answer |
|----------|--------|
| What is duplicated? | All package-level artifacts (Groups, TextAnnotations, DataObjects) and Associations appear in every process |
| When does it happen? | Only in multi-process XPDL files (single-process files are unaffected) |
| Root cause? | `getAllArtifactsInProcess()` and `getAllAssociationsInProc()` return unfiltered package-level collections |
| How does XPDL encode ownership? | `NodeGraphicsInfo.LaneId` on artifacts; Source/Target activity/artifact IDs on associations |
| Does `getProcessPools()` have this bug? | **No** — it already filters by `pool.getProcessId()` |
| Fix location | `GatModelUtil.java` — two methods + one helper |
| Impact if unfixed | Visual duplication of groups/annotations/data objects across all process diagrams; phantom associations connecting to elements in other processes |
