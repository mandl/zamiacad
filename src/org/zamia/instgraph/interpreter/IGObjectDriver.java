/* 
 * Copyright 2010 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Nov 13, 2010
 */
package org.zamia.instgraph.interpreter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.zamia.SourceLocation;
import org.zamia.ZamiaException;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGObject.IGObjectCat;
import org.zamia.instgraph.IGObject.OIDir;
import org.zamia.instgraph.IGRecordField;
import org.zamia.instgraph.IGStaticValue;
import org.zamia.instgraph.IGStaticValueBuilder;
import org.zamia.instgraph.IGType;
import org.zamia.instgraph.IGTypeStatic;

/**
 * IGObjectDrivers form a tree of drivers for composite type objects
 * 
 * They can be pushed on the stack an interacted with to e.g. update the object's
 * current value completely or partially.
 * 
 * This class implements the simplest object semantics suitable for constants
 * and variables and is meant to be sub-classed to implement advanced
 * semantics e.g. for signals and signal parameters.
 *
 * This class exhibits chained behaviour for the next 3 cases:
 * - range,
 * - alias,
 * - direct mapping (e.g. port mapping or Multiple Sources Proof drivers).
 *
 * Range/alias/mapped drivers are all lightweight. They do not store any actual
 * data (apart from alias and range types), but instead delegate all calls to the
 * target driver they are mapped to. Only this target driver stores the actual
 * object values (i.e. is heavyweight).
 *
 * todo: think about introducing lightweight IGMappedDriver to represent chained drivers => should save some memory.
 * todo: NB! drivers can switch from lightweight mode to heavyweight at runtime (see usages of
 * todo: {@link #makeHeavyweight(org.zamia.SourceLocation)}).
 * todo: hence introduction of IGMappedDriver class hierarchy will require ability to replace
 * todo: drivers at runtime. too bothersome?
 *
 * @author Guenter Bartsch
 *
 */

@SuppressWarnings("serial")
public class IGObjectDriver implements Serializable {

	private final IGObjectDriver fParent;

	private ArrayList<IGObjectDriver> fArrayElementDrivers;

	private HashMap<String, IGObjectDriver> fRecordFieldDrivers;

	private IGStaticValue fValue = null;

	private final IGTypeStatic fDeclaredType;

	private IGTypeStatic fCurrentType;

	private final IGObjectCat fCat;

	private OIDir fDir;

	private IGObjectDriver fMappedTo = null;

	private IGTypeStatic fAliasedType = null;

	private IGTypeStatic fRangeType = null;

	private IGStaticValue fRange = null;

	private int fIdxOffset = 0;

	private boolean fWasActive;

	// for logging/debugging purposes only:
	protected final static boolean debug = false;

	private final String fId;

	private static int counter = 0;

	private final int fCnt = counter++;

	private static int cleaned = 0;

	public IGObjectDriver(String aId, OIDir aDir, IGObjectCat aCat, IGTypeStatic aType, SourceLocation aLocation) throws ZamiaException {
		this(aId, aDir, aCat, null, aType, aLocation, false);
	}

	protected IGObjectDriver(String aId, OIDir aDir, IGObjectCat aCat, IGObjectDriver aParent, IGTypeStatic aType, SourceLocation aLocation, boolean aIsLightweight) throws ZamiaException {
		fId = aId;
		fDir = aDir;
		fCat = aCat;
		fDeclaredType = aType;
		fCurrentType = aType;
		fParent = aParent;

		if (aIsLightweight) {
			return;
		}
		makeHeavyweight(aLocation);
	}

	void makeHeavyweight(SourceLocation aLocation) throws ZamiaException {
		if (fDeclaredType.isArray()) {

			if (fArrayElementDrivers != null) {
				throw new ZamiaException("IGObjectDriver.makeHeavyweight(): internal error: driver is already heavyweight");
			}
			fArrayElementDrivers = new ArrayList<IGObjectDriver>();

			adaptArraySize(aLocation);

		} else if (fDeclaredType.isRecord()) {

			if (fRecordFieldDrivers != null) {
				throw new ZamiaException("IGObjectDriver.makeHeavyweight(): internal error: driver is already heavyweight");
			}

			int n = fDeclaredType.getNumRecordFields(aLocation);

			fRecordFieldDrivers = new HashMap<String, IGObjectDriver>(n);

			for (int i = 0; i < n; i++) {

				IGRecordField rf = fDeclaredType.getRecordField(i, aLocation);
				IGTypeStatic rft = fDeclaredType.getStaticRecordFieldType(i);
				String rfid = rf.getId();

				fRecordFieldDrivers.put(rfid, createChildDriver(debug ? fId + "." + rfid : fId, fDir, fCat, this, rft, aLocation, false));

			}
		}
	}

	protected IGObjectDriver createChildDriver(String aId, OIDir aDir, IGObjectCat aCat, IGObjectDriver aParent, IGTypeStatic aType, SourceLocation aLocation, boolean aIsLightweight) throws ZamiaException {
		return new IGObjectDriver(aId, aDir, aCat, aParent, aType, aLocation, aIsLightweight);
	}

	public void setValue(IGStaticValue aValue, SourceLocation aLocation) throws ZamiaException {
		if (fMappedTo != null) {

			if (fRangeType != null) {

				int rangeL = (int) fRange.getLeft().getOrd();
				int rangeR = (int) fRange.getRight().getOrd();
				boolean rangeA = fRange.getAscending().isTrue();
				int rangeMin = rangeA ? rangeL : rangeR;
				int rangeMax = rangeA ? rangeR : rangeL;

				IGTypeStatic srcType = aValue.getStaticType();

				IGTypeStatic srcIdxType = srcType.getStaticIndexType(aLocation);
				int srcL = (int) srcIdxType.getStaticLeft(aLocation).getOrd();
				int srcR = (int) srcIdxType.getStaticRight(aLocation).getOrd();
				boolean srcA = srcIdxType.getStaticAscending().isTrue();
				int srcOff = srcA ? srcL : srcR;

				for (int i = rangeMin; i <= rangeMax; i++) {

					IGStaticValue v = aValue.getValue(i - rangeMin + srcOff, aLocation);

					IGObjectDriver elementDriver = fMappedTo.getArrayElementDriver(i, aLocation);

					elementDriver.setValue(v, aLocation);

				}

			} else {

				fMappedTo.setValue(aValue, aLocation);

			}

			return;
		}

		setValueInternal(aValue, aLocation);

		// update parent

		if (fParent != null) {
			fParent.updateValue(aLocation);
		}
	}

	private void adaptArraySize(SourceLocation aLocation) throws ZamiaException {

		IGTypeStatic idxT = fCurrentType.getStaticIndexType(aLocation);
		if (!fCurrentType.isUnconstrained()) {
			int card = (int) idxT.computeCardinality(aLocation);
			int l = (int) idxT.getStaticLeft(aLocation).getOrd();
			int r = (int) idxT.getStaticRight(aLocation).getOrd();
			boolean a = idxT.getStaticAscending().isTrue();
			fIdxOffset = a ? l : r;

			IGTypeStatic elementType = fCurrentType.getStaticElementType(aLocation);

			int n = fArrayElementDrivers.size();
			while (n < card) {
				int idx = fIdxOffset + n;

				fArrayElementDrivers.add(createChildDriver(debug ? fId + "(" + idx + ")" : fId, fDir, fCat, this, elementType, aLocation, false));
				n++;
			}
		}

	}

	private IGStaticValue shiftIndex(IGTypeStatic aDestType, IGStaticValue aValue, SourceLocation aLocation) throws ZamiaException {

		IGStaticValue value = aValue;

		IGTypeStatic srcType = value.getStaticType();

		IGTypeStatic srcIdxType = srcType.getStaticIndexType(null);
		int srcL = (int) srcIdxType.getStaticLeft(aLocation).getOrd();
		int srcR = (int) srcIdxType.getStaticRight(aLocation).getOrd();
		boolean srcA = srcIdxType.getStaticAscending().isTrue();
		int srcOff = srcA ? srcL : srcR;
		int srcCard = (int) srcIdxType.computeCardinality(null);

		IGTypeStatic destIdxType = aDestType.getStaticIndexType(null);
		int destL = (int) destIdxType.getStaticLeft(aLocation).getOrd();
		int destR = (int) destIdxType.getStaticRight(aLocation).getOrd();
		boolean destA = destIdxType.getStaticAscending().isTrue();
		int destOff = destA ? destL : destR;
		int destCard = (int) destIdxType.computeCardinality(null);

		if (destCard != srcCard) {
			throw new ZamiaException("Interpreter: error: tried to assign object from incompatible array value " + aValue + ": " + aDestType + " vs " + srcType, aLocation);
		}

		if (srcL != destL || srcR != destR || srcA != destA) {
			// yes, time to shift the index

			IGStaticValueBuilder builder = new IGStaticValueBuilder(aDestType, null, aLocation);

			for (int i = 0; i < srcCard; i++) {
				IGStaticValue v = aValue.getValue(i + srcOff, aLocation);
				builder.set(i + destOff, v, aLocation);
			}

			value = builder.buildConstant();
		}

		return value;
	}

	protected void setValueInternal(IGStaticValue aValue, SourceLocation aLocation) throws ZamiaException {
		fValue = aValue;
		fWasActive = true;

		// update children

		if (fDeclaredType.isArray()) {

			if (fArrayElementDrivers == null) {
				return;
			}
			IGTypeStatic t = aValue.getStaticType();
			if (!t.isArray()) {
				throw new ZamiaException("IGObjectDriver: Internal error: tried to assign non-array value to an array.", aLocation);
			}

			if (t.isUnconstrained()) {
				throw new ZamiaException("IGObjectDriver: Internal error: tried to assign unconstrained array value to an array.", aLocation);
			}

			if (fDeclaredType.isUnconstrained()) {
				fCurrentType = t;
			}
			adaptArraySize(aLocation);

			// so we have to do an index shift?
			if (!fDeclaredType.isUnconstrained() && fDeclaredType.getCat() != IGType.TypeCat.FILE && fDeclaredType.getCat() != IGType.TypeCat.ACCESS) {
				fValue = shiftIndex(fDeclaredType, fValue, aLocation);
			}

			// update children

			t = fValue.getStaticType();
			IGTypeStatic idxT = t.getStaticIndexType(null);
			int l = (int) idxT.getStaticLeft(aLocation).getOrd();
			int r = (int) idxT.getStaticRight(aLocation).getOrd();
			boolean a = idxT.getStaticAscending().isTrue();
			int off = a ? l : r;
			int c = (int) idxT.computeCardinality(null);

			for (int i = 0; i < c; i++) {
				IGStaticValue v = fValue.getValue(i + off, aLocation);
				fArrayElementDrivers.get(i).setValueInternal(v, aLocation);
			}

		} else if (fDeclaredType.isRecord()) {

			if (fRecordFieldDrivers == null) {
				return;
			}

			int n = fDeclaredType.getNumRecordFields(null);

			for (int i = 0; i < n; i++) {

				IGRecordField rf = fDeclaredType.getRecordField(i, null);
				String rfid = rf.getId();

				IGStaticValue v = fValue.getRecordFieldValue(rfid, aLocation);

				fRecordFieldDrivers.get(rfid).setValueInternal(v, aLocation);

			}

		}
	}

	protected void resetEventP() throws ZamiaException {

		if (fMappedTo != null) {

			if (fRangeType != null) {

				int rangeL = (int) fRange.getLeft().getOrd();
				int rangeR = (int) fRange.getRight().getOrd();
				boolean rangeA = fRange.getAscending().isTrue();
				int rangeMin = rangeA ? rangeL : rangeR;
				int rangeMax = rangeA ? rangeR : rangeL;

				for (int i = rangeMin; i <= rangeMax; i++) {

					IGObjectDriver elementDriver = fMappedTo.getArrayElementDriver(i, null);

					elementDriver.resetEventP();

				}

			} else {

				fMappedTo.resetEventP();

			}

			return;
		}


		resetEventInternal();

		if (fParent != null) {
			fParent.clearReset();
		}

	}

	/**
	 * to be overridden in signal driver
	 */
	protected void clearReset() {
		/* do nothing */
	}

	protected void resetEventInternal() throws ZamiaException {

		// update children

		if (fDeclaredType.isArray()) {

			if (fArrayElementDrivers != null) {
				for (IGObjectDriver arrayElement : fArrayElementDrivers) {
					arrayElement.resetEventInternal();
				}
			}

		} else if (fDeclaredType.isRecord()) {

			for (IGObjectDriver recordField : fRecordFieldDrivers.values()) {
				recordField.resetEventInternal();
			}
		}

	}

	public IGStaticValue getValue(SourceLocation aLocation) throws ZamiaException {
		if (fMappedTo != null) {
			IGStaticValue value = fMappedTo.getValue(aLocation);

			if (fAliasedType != null) {
				value = shiftIndex(fAliasedType, value, aLocation);
			}

			if (fRangeType != null) {

				int rangeL = (int) fRange.getLeft().getOrd();
				int rangeR = (int) fRange.getRight().getOrd();
				boolean rangeA = fRange.getAscending().isTrue();
				int rangeMin = rangeA ? rangeL : rangeR;
				int rangeMax = rangeA ? rangeR : rangeL;

				IGTypeStatic destIdxType = fRangeType.getStaticIndexType(aLocation);
				int destL = (int) destIdxType.getStaticLeft(aLocation).getOrd();
				int destR = (int) destIdxType.getStaticRight(aLocation).getOrd();
				boolean destA = destIdxType.getStaticAscending().isTrue();
				int destOff = destA ? destL : destR;

				IGStaticValueBuilder builder = new IGStaticValueBuilder(fRangeType, null, aLocation);

				for (int i = rangeMin; i <= rangeMax; i++) {

					IGStaticValue v = value.getValue(i, aLocation);

					builder.set(i - rangeMin + destOff, v, aLocation);
				}

				value = builder.buildConstant();

			}

			return value;
		}
		return fValue;
	}

	protected void updateValue(SourceLocation aLocation) throws ZamiaException {

		// children's value have changed, so we have to update fValue accordingly

		if (fDeclaredType.isArray()) {

			IGTypeStatic t = fCurrentType;
			if (!t.isArray()) {
				throw new ZamiaException("IGObjectDriver: Internal error.", aLocation);
			}

			if (t.isUnconstrained()) {
				throw new ZamiaException("IGObjectDriver: Internal error.", aLocation);
			}

			IGTypeStatic idxT = t.getStaticIndexType(null);
			int l = (int) idxT.getStaticLeft(aLocation).getOrd();
			int r = (int) idxT.getStaticRight(aLocation).getOrd();
			boolean a = idxT.getStaticAscending().isTrue();
			int off = a ? l : r;
			int c = (int) idxT.computeCardinality(null);

			IGStaticValueBuilder builder = new IGStaticValueBuilder(t, fId, aLocation);

			for (int i = 0; i < c; i++) {
				IGStaticValue v = fArrayElementDrivers.get(i).getValue(aLocation);

				builder.set(i + off, v, aLocation);
			}

			fValue = builder.buildConstant();

		} else if (fDeclaredType.isRecord()) {

			int n = fDeclaredType.getNumRecordFields(null);

			IGStaticValueBuilder builder = new IGStaticValueBuilder(fDeclaredType, fId, aLocation);

			for (int i = 0; i < n; i++) {

				IGRecordField rf = fDeclaredType.getRecordField(i, aLocation);
				String rfid = rf.getId();

				IGStaticValue v = fRecordFieldDrivers.get(rfid).getValue(aLocation);

				builder.set(rf, v, aLocation);
			}

			fValue = builder.buildConstant();

		}
	}

	@Override
	public String toString() {
		if (fMappedTo != null) {
			return "DRIVER@" + fCnt + "[" + fId + "]=>" + fMappedTo;
		}
		if (fValue != null) {
			return "DRIVER@" + fCnt + "[" + fId + "]=" + fValue;
		}
		return "DRIVER@" + fCnt + "[" + fId + "]";
	}

	public boolean isEvent() {
		if (fMappedTo != null) {
			return fMappedTo.isEvent();
		}

		//		// get signal from objectItem
		//		IGObject signal;
		//		IGItem objectItem = (IGItem) getZDB().load(fObjectItemDBID);
		//		if (objectItem instanceof IGOperationObject) {
		//			signal = ((IGOperationObject) objectItem).getObject();
		//		} else if (objectItem instanceof IGObject) {
		//			signal = (IGObject) objectItem;
		//		} else
		//			throw new ZamiaException(getClass().getSimpleName() + ": cannot extract signal (" + IGObject.class.getSimpleName() + ") from " + objectItem);
		//
		//		// check signal event
		//		boolean isChanged = aRuntime.isChanged(signal, computeSourceLocation());

		return isEventInternal();
	}

	/**
	 * to be overridden in signal driver
	 */
	protected boolean isEventInternal() {
		return false;
	}

	/**
	 * to be overridden in signal driver
	 */
	public boolean isActive() {
		return false;
	}

	protected IGObjectDriver getTargetDriver() {

		IGObjectDriver targetDriver = this;

		IGObjectDriver mappedTo;

		while ((mappedTo = targetDriver.fMappedTo) != null) {
			targetDriver = mappedTo;
		}

		return targetDriver;
	}

	public Collection<IGObjectDriver> getArrayElementDrivers() {
		return fArrayElementDrivers != null ? fArrayElementDrivers : Collections.<IGObjectDriver>emptyList();
	}

	public IGObjectDriver getArrayElementDriver(int aIdx, SourceLocation aLocation) throws ZamiaException {
		if (fMappedTo != null) {
			if (fAliasedType != null) {
				return fMappedTo.getArrayElementDriver(aIdx - fIdxOffset, aLocation);
			} else {
				return fMappedTo.getArrayElementDriver(aIdx, aLocation);
			}
		}

		return fArrayElementDrivers.get(aIdx - fIdxOffset);
	}

	public Collection<IGObjectDriver> getRecordFieldDrivers() {
		return fRecordFieldDrivers != null ? fRecordFieldDrivers.values() : Collections.<IGObjectDriver>emptyList();
	}

	public IGObjectDriver getRecordFieldDriver(String aId, SourceLocation aLocation) throws ZamiaException {
		if (fMappedTo != null) {
			return fMappedTo.getRecordFieldDriver(aId, aLocation);
		}

		return fRecordFieldDrivers.get(aId);
	}

	public void schedule(boolean aInertial, IGStaticValue aDelay, IGStaticValue aReject, IGStaticValue aValue, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation)
			throws ZamiaException {

		if (fCat == IGObjectCat.SIGNAL) {

			aRuntime.scheduleSignalChange(aInertial, aDelay, aReject, aValue, this, aLocation);

		} else {

			setValue(aValue, aLocation);

		}
	}

	public IGObjectCat getCat() {
		if (fMappedTo != null) {
			return fMappedTo.getCat();
		}

		return fCat;
	}

	public String getId() {
		return fId;
	}

	protected String getIdInternal() {
		return fId + "@" + fCnt;
	}

	public void map(IGObjectDriver aActual, SourceLocation aLocation) throws ZamiaException {
		fMappedTo = aActual;

		// let's make it lightweight to save some memory s(all calls will be delegated to fMappedTo anyway)
		makeLightweight();
	}

	private void makeLightweight() {

		if (fDeclaredType.isArray()) {

			if (fArrayElementDrivers == null) {
				return;
			}
			for (IGObjectDriver driver : fArrayElementDrivers) {
				driver.makeLightweight();
			}

			cleaned += fArrayElementDrivers.size();
			fArrayElementDrivers = null;

		} else if (fDeclaredType.isRecord()) {

			if (fRecordFieldDrivers == null) {
				return;
			}
			for (IGObjectDriver driver : fRecordFieldDrivers.values()) {
				driver.makeLightweight();
			}

			cleaned += fRecordFieldDrivers.size();
			fRecordFieldDrivers = null;
		}
	}

	public IGTypeStatic getCurrentType() {
		if (fMappedTo != null) {
			if (fAliasedType != null)
				return fAliasedType;
			if (fRangeType != null)
				return fRangeType;
			return fMappedTo.getCurrentType();
		}
		return fCurrentType;
	}

	private void setRange(IGObjectDriver aDriver, IGStaticValue aRange, IGTypeStatic aRangeType, int aIdxOffset, SourceLocation aLocation) throws ZamiaException {
		fMappedTo = aDriver;
		fRangeType = aRangeType;
		fRange = aRange;
		fIdxOffset = aIdxOffset;
	}

	public IGObjectDriver createRangeDriver(IGStaticValue aRange, IGTypeStatic aRangeType, SourceLocation aLocation) throws ZamiaException {
		if (aRangeType == null || !aRangeType.isArray() || aRangeType.isUnconstrained()) {
			return this;
		}

		String id = debug ? fId + "(" + aRange + ")" : fId;

		IGObjectDriver rangeDriver = createChildDriver(id, fDir, fCat, null, aRangeType, aLocation, true);

		int rangeOffset = (int) aRangeType.getStaticIndexType(aLocation).getStaticLow(aLocation).getOrd();
		IGTypeStatic currentType = getCurrentType();

		int currentOffset = (int) currentType.getStaticIndexType(aLocation).getStaticLow(aLocation).getOrd();

		rangeDriver.setRange(this, aRange, aRangeType, rangeOffset - currentOffset, aLocation);

		return rangeDriver;
	}

	private void setAlias(IGObjectDriver aDriver, IGTypeStatic aAliasedType, int aIdxOffset, SourceLocation aLocation) throws ZamiaException {
		fMappedTo = aDriver;
		fAliasedType = aAliasedType;
		fIdxOffset = aIdxOffset;
	}

	public IGObjectDriver createAliasDriver(IGTypeStatic aAliasType, SourceLocation aLocation) throws ZamiaException {

		if (aAliasType == null || !aAliasType.isArray() || aAliasType.isUnconstrained()) {
			return this;
		}

		String id = debug ? "&[" + toString() + "]{" + aAliasType + "}" : fId;

		IGObjectDriver aliasedDriver = createChildDriver(id, fDir, fCat, null, aAliasType, aLocation, true);

		int aliasOffset = (int) aAliasType.getStaticIndexType(aLocation).getStaticLow(aLocation).getOrd();
		IGTypeStatic currentType = getCurrentType();

		int currentOffset = (int) currentType.getStaticIndexType(aLocation).getStaticLow(aLocation).getOrd();

		aliasedDriver.setAlias(this, aAliasType, aliasOffset - currentOffset, aLocation);

		return aliasedDriver;
	}

	public OIDir getDir() {
		return fDir;
	}

	public void setDir(OIDir aDir) {
		fDir = aDir;
	}

	public boolean isInputPort() {
		return IGObject.isInputPort(fCat, fDir);
	}

	protected HashMap<IGObjectDriver, IGStaticValue> getActiveElements() throws ZamiaException {

		if (getTargetDriver() != this) {
			throw new ZamiaException("IGSimRef: IGObjectDriver: getActiveElements(): not a target driver");
		}

		HashMap<IGObjectDriver, IGStaticValue> ret = new HashMap<IGObjectDriver, IGStaticValue>();

		if (fDeclaredType.isArray()) {

			for (IGObjectDriver driver : fArrayElementDrivers) {
				addActive(driver, ret);
			}

		} else if (fDeclaredType.isRecord()) {

			for (IGObjectDriver driver : fRecordFieldDrivers.values()) {
				addActive(driver, ret);
			}

		} else {

			addActive(this, ret);
		}

		return ret;
	}

	private void addActive(IGObjectDriver driver, HashMap<IGObjectDriver, IGStaticValue> actives) {
		if (driver.fWasActive) {
			actives.put(driver, driver.fValue);
			driver.fWasActive = false;
		}
	}

	protected void clearWasActive() {

		if (fDeclaredType.isArray() && fArrayElementDrivers != null) {

			for (IGObjectDriver driver : fArrayElementDrivers) {
				driver.fWasActive = false;
			}

		} else if (fDeclaredType.isRecord() && fRecordFieldDrivers != null) {

			for (IGObjectDriver driver : fRecordFieldDrivers.values()) {
				driver.fWasActive = false;
			}

		}

		fWasActive = false;
	}

	public static int geNumDrivers() {
		return counter;
	}

	public static int geNumCleanedDrivers() {
		return cleaned;
	}

	protected <D extends IGObjectDriver, R extends IGInterpreterRuntimeEnv>
	void collectUniqueListeners(Collection<D> aVisitedDrivers, Collection<R> aUniqueListeners) throws ZamiaException {

		if (fMappedTo != null) {
			fMappedTo.collectUniqueListeners(aVisitedDrivers, aUniqueListeners);
		}
	}
}
