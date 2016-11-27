/*
 * Copyright 2004-2009 by the authors indicated in the @author tags.
 * All rights reserved.
 *
 * See the LICENSE file for details.
 *
 */

package org.zamia.vhdl.ast;

import java.io.PrintStream;
import java.util.ArrayList;

import org.zamia.DMManager;
import org.zamia.ZamiaException;
import org.zamia.ZamiaProject;
import org.zamia.analysis.ReferenceSearchResult;
import org.zamia.analysis.ReferenceSite.RefType;
import org.zamia.analysis.ast.SearchJob;
import org.zamia.analysis.ast.ASTReferencesSearch.ObjectCat;
import org.zamia.instgraph.IGContainer;
import org.zamia.instgraph.IGDUUID;
import org.zamia.instgraph.IGElaborationEnv;
import org.zamia.instgraph.IGInstantiation;
import org.zamia.instgraph.IGStructure;


/**
 * 
 * @author Guenter Bartsch
 * 
 */

@SuppressWarnings("serial")
public class ComponentInstantiation extends InstantiatedUnit {

	public final static boolean dump = false;

	public ComponentInstantiation(String aLabel, Name aName, VHDLNode aParent, long aLocation) {
		super(aLabel, aName, aParent, aLocation);
	}

	@Override
	public IGInstantiation computeIGInstantiation(DMUID aDUUID, IGContainer aContainer, IGStructure aStructure, IGElaborationEnv aEE) {

		try {

			// find configured binding
			Name boundName = fName;
			String archId = null;

			int n = aContainer.getNumConfSpecs();
			AssociationList gma = null, pma = null;
			if (n > 0) {
				gma = new AssociationList(null, 0);
				if (fGMS != null) {
					for (int i = 0; i<fGMS.getNumAssociations(); i++) {
						gma.add(fGMS.getAssociation(i));
					}
				}

				pma = new AssociationList(null, 0);
				if (fPMS != null) {
					for (int i = 0; i<fPMS.getNumAssociations(); i++) {
						pma.add(fPMS.getAssociation(i));
					}
				}
			}
			for (int i = 0; i < n; i++) {

				ConfigurationSpecification cs = aContainer.getConfSpec(i);

				String csId = cs.getId();

				if (!csId.equals("ALL") && !csId.equals(getLabel())) {
					continue;
				}

				Name csName = cs.getComponentName();

				if (!csName.isSimpleName()) {
					// FIXME
					throw new ZamiaException("Sorry. Complex names in configuration specifications are not handled yet.", csName);
				}

				if (!csName.getId().equals(boundName.getId())) {
					continue;
				}

				BindingIndication bi = cs.getBindingIndication();

				AssociationList pm = bi.getPortMapAspect();
				if (pm != null) {
					for (int j=0; j<pm.getNumAssociations(); j++) {
						pma.add(pm.getAssociation(j));
					}
				}

				AssociationList gm = bi.getGenericMapAspect();
				if (gm != null) {
					for (int j=0; j<gm.getNumAssociations(); j++) {
						gma.add(gm.getAssociation(j));
					}
				}

				EntityAspect ea = bi.getEntityAspect();

				switch (ea.getKind()) {
				case Configuration:
					// FIXME
					throw new ZamiaException("Sorry, configurations are not supported yet.", ea);
				case Open:
					// fine.
					break;
				case Entity:
					boundName = ea.getName();
					break;
				}
				break;
			}

			IGDUUID zduuid = boundName.computeIGAsDesignUnit(aContainer, aEE, ASTErrorMode.EXCEPTION, null);

			DMUID duuid = null;

			Architecture arch = null;
			DMManager duManager = aEE.getZamiaProject().getDUM();

			if (zduuid != null) {
				duuid = zduuid.getDUUID();

				if (archId == null) {
					archId = duuid.getArchId();
				}

				arch = duManager.getArchitecture(duuid.getLibId(), duuid.getId(), archId);
				// FIXME
				//			} else {
				//
				//				if (!(compObj instanceof ZILComponent)) {
				//					name.reportError("ComponentInstantiation: Component name expected here, found %s instead.", compObj);
				//					return;
				//				}
				//
				//				ZILComponent zilc = (ZILComponent) compObj;
				//
				//				String id = zilc.getId();
				//
				//				arch = duManager.getArchitecture(getLibrary().getId(), id, null);
			}
			if (arch == null) {
				reportError("ComponentInstantiation: Could't find '%s'", fName);
				return null;
			}

			return instantiateIGModule(arch, aDUUID, aContainer, aStructure, aEE, gma, pma);
		} catch (ZamiaException e) {
			reportError(e);
		}
		return null;
	}

	@Override
	public void dumpVHDL(int aIndent, PrintStream aOut) throws ZamiaException {

		printlnIndented(fLabel + ": " + fName.toVHDL(), aIndent, aOut);

		if (fGMS != null) {
			printlnIndented("GENERIC MAP ", aIndent + 2, aOut);
			fGMS.dumpVHDL(aIndent + 4, aOut);
		}
		if (fPMS != null) {
			printlnIndented("PORT MAP ", aIndent + 2, aOut);
			fPMS.dumpVHDL(aIndent + 4, aOut);
		}
		printlnIndented(";", aIndent + 2, aOut);
	}

	@Override
	public String toString() {
		return fLabel + ": " + fName.getId();
	}

	@Override
	public void findReferences(String aId, ObjectCat aCat, RefType aRefType, int aDepth, ZamiaProject aZPrj, IGContainer aContainer, IGElaborationEnv aCache, ReferenceSearchResult aResult,
			ArrayList<SearchJob> aTODO) throws ZamiaException {

		DMUID duuid = getChildDUUID(aContainer, aCache);

		if (duuid == null) {
			logger.error("ComponentInstantiation: findReferences: %s: component not found", this);
			return;
		}

		Architecture arch = null;
		DMManager duManager = aCache.getZamiaProject().getDUM();

		arch = duManager.getArchitecture(duuid.getLibId(), duuid.getId(), duuid.getArchId());

		if (arch == null) {
			String msg = "Warning: " + fName + " not found.";
			logger.warn(msg);
			return;
		}

		findReferences(arch, aId, aDepth, aZPrj, aContainer, aCache, aResult, aTODO);
	}

}