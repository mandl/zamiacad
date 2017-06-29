/*
 * Copyright 2006-2010 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 */

package org.zamia.analysis;

import org.junit.After;
import org.junit.Test;
import org.zamia.ASTNode;
import org.zamia.DMManager;
import org.zamia.ERManager;
import org.zamia.IDesignModule;
import org.zamia.SourceFile;
import org.zamia.SourceLocation;
import org.zamia.ToplevelPath;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;
import org.zamia.ZamiaProjectBuilder;
import org.zamia.analysis.ast.ASTDeclarationSearch;
import org.zamia.analysis.ast.ASTReferencesSearch;
import org.zamia.analysis.ig.IGAssignmentsSearch;
import org.zamia.analysis.ig.IGAssignmentsSearch.RootResult;
import org.zamia.analysis.ig.IGReferencesSearch;
import org.zamia.instgraph.IGInstantiation;
import org.zamia.instgraph.IGItem;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGOperationLiteral;
import org.zamia.instgraph.IGOperationObject;
import org.zamia.util.HashSetArray;
import org.zamia.util.Pair;
import org.zamia.util.ZStack;
import org.zamia.vhdl.ast.DMUID;
import org.zamia.vhdl.ast.DMUID.LUType;
import org.zamia.vhdl.ast.DeclarativeItem;
import org.zamia.vhdl.ast.DesignUnit;
import org.zamia.vhdl.ast.Name;
import org.zamia.vhdl.ast.VHDLNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Static analysis code test cases.
 * <p/>
 * Static analysis provides solutions for:
 * <p/>
 * - AST based declaration search - AST based reference search - AST based
 * completion proposal generation - IG based declaration search - IG based
 * reference search
 *
 * @author Guenter Bartsch
 */
public class SATest {

	public final static ZamiaLogger logger = ZamiaLogger.getInstance();

	private ZamiaProject fZPrj;

	public void setupTest(String aBasePath, String aBuildPath) throws Exception {
		// ZamiaLogger.setup(Level.INFO);

		SourceFile sf = new SourceFile(new File(aBuildPath));

		fZPrj = new ZamiaProject("SA Test Tmp Project", aBasePath, sf);
		fZPrj.clean();
		ZamiaProjectBuilder builder = fZPrj.getBuilder();

		builder.build(true, true, null);

		ERManager erm = fZPrj.getERM();

		assertEquals(0, erm.getNumErrors());

	}

	private ReferenceSearchResult runASTReferenceSearch(SourceFile aSF, int aLine, int aCol, boolean aUpwards, boolean aDownwards) throws ZamiaException, IOException {

		SourceLocation location = new SourceLocation(aSF, aLine, aCol);

		ASTNode nearest = SourceLocation2AST.findNearestASTNode(location, true, fZPrj);

		assertNotNull("Failed to find nearest AST Object", nearest);

		DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, fZPrj);

		if (declaration == null) {
			return null;
		}

		ReferenceSearchResult result = ASTReferencesSearch.search(declaration, true, true, fZPrj);

		return result;

	}

	private void checkLocation(SourceLocation aLocation, int aLine, int aCol) {
		assertNotNull("SourceLocation is null", aLocation);

		int line = aLocation.fLine;
		int col = aLocation.fCol;

		assertEquals("SourceLocation: wrong line", aLine, line);
		assertEquals("SourceLocation: wrong column", aCol, col);
	}

	@Test
	public void testGCounterIGReferenceSearchIncr() throws Exception {
		setupTest("examples/gcounter", "examples/gcounter/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/gcounter/va.vhdl"));

		ReferenceSearchResult result = runIGReferenceSearch(sf, "WORK.COUNTER_TB:COUNTER0.ADDG.GEN1#0.VAI.HA1", 18, 33, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(6, result.countRefs());

		// now, do an incremental rebuild of the design and run the reference search again

		HashSetArray<SourceFile> changed = new HashSetArray<SourceFile>(2);
		changed.add(sf);

		ZamiaProjectBuilder builder = fZPrj.getBuilder();

		int n = builder.build(false, false, changed);
		assertEquals(2, n);

		result = runIGReferenceSearch(sf, "WORK.COUNTER_TB:COUNTER0.ADDG.GEN1#0.VAI.HA1", 18, 33, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(6, result.countRefs());
	}

	@Test
	public void testGCounterThroughAssignmentSearch() throws Exception {
		setupTest("examples/refsearch", "examples/refsearch/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/refsearch/if-then.vhdl"));
		runThroughAssignmentSearch(sf, "WORK.IF_THEN:", 9, 4, true, -1, 4);
		runThroughAssignmentSearch(sf, "WORK.IF_THEN:", 6, 6, false, -1, 3);

		sf = new SourceFile(new File("examples/refsearch/levels.vhd"));
		runThroughAssignmentSearch(sf, "WORK.LEVEL0:U1.U2.", 5, 3, true, -1, 3); // LEVEL22
	}

	@Test
	public void testGCounterIGReferenceSearch() throws Exception {
		setupTest("examples/gcounter", "examples/gcounter/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/gcounter/addg.vhdl"));

		ReferenceSearchResult result = runIGReferenceSearch(sf, "COUNTER_TB:COUNTER0.ADDG", 22, 35, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(21, result.countRefs());

		SourceFile sf2 = new SourceFile(new File("examples/gcounter/ha.vhdl"));
		result = runIGReferenceSearch(sf2, "COUNTER_TB:COUNTER0.ADDG.GEN1#3.VAI.HA2", 14, 3, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(4, result.countRefs());

		result = runIGReferenceSearch(sf, "WORK.COUNTER_TB:COUNTER0.ADDG.GEN1#0.VAI", 22, 64, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(27, result.countRefs());

		DMUID duuid = new DMUID(LUType.Architecture, "WORK", "COUNTER_TB", "RTL");
		stressTestIGReferenceSearch(duuid, "COUNTER_TB:");
	}

	@Test
	public void testPlasmaIGReferenceSearch() throws Exception {
		setupTest("examples/plasma", "examples/plasma/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/plasma/uart.vhd"));

		/*
		 * 3,7    M1.M2.P1  => S          M1.M2.S
		 * 5,9    M1.M2     => S          M1.M2.P1
		 * 7,14   M1.M2.P1  => S          M1.M2.
		 * 7,14   M1.M2     => S          M1.M2.
		*/

		runIGItemSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE.UARTOP", 80, 14, "WORK.PLASMA:U3_UART.UART_MODULE.RIN");
		runIGItemSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE", 298, 6, "WORK.PLASMA:U3_UART.UART_MODULE.UARTOP");
		runIGItemSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE.UARTOP", 316, 13, "WORK.PLASMA:U3_UART.UART_MODULE.");
		runIGItemSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE", 316, 13, "WORK.PLASMA:U3_UART.UART_MODULE.");
		runIGItemSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE.", 316, 13, "WORK.PLASMA:U3_UART.UART_MODULE.");

		ReferenceSearchResult result = runIGReferenceSearch(sf, "WORK.PLASMA:U3_UART.UART_MODULE.UARTOP.BRATE", 80, 14, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(3, result.countRefs());
	}

	private SourceLocation runASTDeclarationSearch(SourceFile aSF, int aLine, int aCol) throws Exception {
		SourceLocation location = new SourceLocation(aSF, aLine, aCol);

		ASTNode nearest = SourceLocation2AST.findNearestASTNode(location, true, fZPrj);

		assertNotNull("Failed to find nearest AST Object", nearest);

		DeclarativeItem declaration = ASTDeclarationSearch.search(nearest, fZPrj);

		if (declaration == null) {
			return null;
		}

		return declaration.getLocation();
	}

	private HashSetArray<String> runASTCompletion(SourceFile aSF, int aLine, int aCol) throws Exception {
		SourceLocation location = new SourceLocation(aSF, aLine, aCol);

		ASTNode nearest = SourceLocation2AST.findNearestASTNode(location, true, fZPrj);

		assertNotNull("Failed to find nearest AST Node", nearest);

		HashSetArray<String> identifiers = new HashSetArray<String>();

		// FIXME: implement in language-agnostic way, e.g. using an AST visitor
		if (nearest instanceof VHDLNode) {
			VHDLNode node = (VHDLNode) nearest;
			node.collectIdentifiers(identifiers, fZPrj);
		}

		return identifiers;
	}

	@Test
	public void testGCounterASTCompletion() throws Exception {
		setupTest("examples/gcounter", "examples/gcounter/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/gcounter/counter_tb.vhdl"));

		HashSetArray<String> ids = runASTCompletion(sf, 25, 4);
		assertNotNull("Failed to find completion proposals", ids);
		assertEquals("Wrong number of completion proposals", 6, ids.size());

		ids = runASTCompletion(sf, 15, 11);
		assertNotNull("Failed to find completion proposals", ids);
		assertEquals("Wrong number of completion proposals", 6, ids.size());

		sf = new SourceFile(new File("examples/gcounter/addg.vhdl"));

		ids = runASTCompletion(sf, 22, 9);
		assertNotNull("Failed to find completion proposals", ids);
		assertEquals("Wrong number of completion proposals", 10, ids.size());
	}

	@Test
	public void testPlasmaASTCompletion() throws Exception {
		setupTest("examples/plasma", "examples/plasma/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/plasma/uart.vhd"));

		HashSetArray<String> ids = runASTCompletion(sf, 235, 12);
		assertNotNull("Failed to find completion proposals", ids);
		assertEquals("Wrong number of completion proposals", 27, ids.size());

		ids = runASTCompletion(sf, 316, 5);
		assertNotNull("Failed to find completion proposals", ids);
		assertEquals("Wrong number of completion proposals", 21, ids.size());

	}

	@Test
	public void testGCounterASTDeclarationSearch() throws Exception {
		setupTest("examples/gcounter", "examples/gcounter/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/gcounter/counter_tb.vhdl"));

		SourceLocation location = runASTDeclarationSearch(sf, 13, 55);
		checkLocation(location, 6, 12);

		location = runASTDeclarationSearch(sf, 13, 25);
		checkLocation(location, 1, 1);

		location = runASTDeclarationSearch(sf, 10, 30);
		checkLocation(location, 6, 12);

		location = runASTDeclarationSearch(sf, 31, 7);
		checkLocation(location, 9, 10);

		location = runASTDeclarationSearch(sf, 6, 23);
		checkLocation(location, 29, 3);

		location = runASTDeclarationSearch(sf, 10, 23);
		checkLocation(location, 35, 3);

		sf = new SourceFile(new File("examples/gcounter/addg.vhdl"));

		location = runASTDeclarationSearch(sf, 24, 2);
		checkLocation(location, 10, 9);

		location = runASTDeclarationSearch(sf, 22, 46);
		checkLocation(location, 7, 12);
	}

	@Test
	public void testGCounterASTReferenceSearch() throws Exception {
		setupTest("examples/gcounter", "examples/gcounter/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/gcounter/addg.vhdl"));

		ReferenceSearchResult result = runASTReferenceSearch(sf, 22, 35, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(9, result.countRefs());

		SourceFile sf2 = new SourceFile(new File("examples/gcounter/ha.vhdl"));
		result = runASTReferenceSearch(sf2, 14, 3, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(8, result.countRefs());

		result = runASTReferenceSearch(sf, 22, 64, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(9, result.countRefs());

		// look for instantiation sites of entities and architectures

		result = runASTReferenceSearch(sf2, 5, 16, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(2, result.countRefs());

		result = runASTReferenceSearch(sf2, 10, 16, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(2, result.countRefs());

		DMUID duuid = new DMUID(LUType.Architecture, "WORK", "COUNTER_TB", "RTL");
		stressTestASTReferenceSearch(duuid);
	}

	@Test
	public void testPlasmaASTReferenceSearch() throws Exception {
		setupTest("examples/plasma", "examples/plasma/BuildPath.txt");

		SourceFile sf = new SourceFile(new File("examples/plasma/uart.vhd"));

		ReferenceSearchResult result = runASTReferenceSearch(sf, 316, 9, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(30, result.countRefs());

		result = runASTReferenceSearch(sf, 228, 13, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(82, result.countRefs());

		DMUID duuid = new DMUID(LUType.Architecture, "WORK", "MLITE_CPU", "LOGIC");
		stressTestASTReferenceSearch(duuid);
	}

	@Test
	public void testLeonIGReferenceSearch() throws Exception {
		setupTest("examples/leonExtern", "examples/leonExtern/BuildPath.txt");

		File f = new File("examples/leonSOC/lib/gaisler/greth/grethm.vhd").getCanonicalFile();

		SourceFile sf = new SourceFile(f);

		ReferenceSearchResult result = runIGReferenceSearch(sf, "WORK.LEON3MP(RTL):ETH0.E1.M100", 84, 22, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(999, result.countRefs());

		result = runIGReferenceSearch(sf, "WORK.LEON3MP(RTL):ETH0.E1.M100", 84, 17, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(102, result.countRefs());

		result = runIGReferenceSearch(sf, "WORK.LEON3MP(RTL):ETH0.E1.", 84, 55, true, true);
		assertNotNull(result);
		result.dump(0, System.out);
		assertEquals(83, result.countRefs());
	}

	private void runIGItemSearch(SourceFile aSF, String aTLPath, int aLine, int aCol, String aExpectedPath) throws ZamiaException, IOException {

		ToplevelPath tlp = new ToplevelPath(aTLPath);

		SourceLocation location = new SourceLocation(aSF, aLine, aCol);

		Pair<IGItem, ToplevelPath> res = SourceLocation2IG.findNearestItem(location, tlp, fZPrj);

		assertNotNull("Failed to find nearest IG Item", res);

		IGItem item = res.getFirst();
		assertNotNull("Failed to find nearest IG Item", item);

		ToplevelPath path = res.getSecond();

		assertEquals(aExpectedPath, path.toString());
	}

	private ReferenceSearchResult runIGReferenceSearch(SourceFile aSF, String aTLPath, int aLine, int aCol, boolean aUpwards, boolean aDownwards) throws ZamiaException,
			IOException {
		ToplevelPath tlp = new ToplevelPath(aTLPath);

		SourceLocation location = new SourceLocation(aSF, aLine, aCol);

		Pair<IGItem, ToplevelPath> res = SourceLocation2IG.findNearestItem(location, tlp, fZPrj);

		assertNotNull("Failed to find nearest IG Item", res);

		IGItem item = res.getFirst();
		assertNotNull("Failed to find nearest IG Item", item);
		ToplevelPath path = res.getSecond();

		logger.info("SATest: nearest item: %s, path: %s", item, path);

		if (item != null) {

			IGReferencesSearch rs = new IGReferencesSearch(fZPrj);

			IGObject object = null;

			if (item instanceof IGObject) {
				object = (IGObject) item;
			} else if (item instanceof IGOperationObject) {
				object = ((IGOperationObject) item).getObject();
			} else if ((item instanceof IGInstantiation) || (item instanceof IGOperationLiteral)) {
				// not supported, ok.
				return null;
			} else {
				fail("Unknown item class: " + item);
			}

			ReferenceSearchResult result = rs.search(object, path, aUpwards, aDownwards, false, false);

			return result;

		} else {
			fail("Item not found.");
		}

		return null;
	}

	private void runThroughAssignmentSearch(SourceFile aSF, String aTLPath, int aLine, int aCol, boolean backward, int depth, int expected) throws ZamiaException,
	IOException {
		ToplevelPath tlp = new ToplevelPath(aTLPath);
		SourceLocation location = new SourceLocation(aSF, aLine, aCol);
		Pair<IGItem, ToplevelPath> res = SourceLocation2IG.findNearestItem(location, tlp, fZPrj);
		assertNotNull("Failed to find nearest IG Item", res);
		IGItem item = res.getFirst();
		assertNotNull("Failed to find nearest IG Item", item);
		ToplevelPath path = res.getSecond();
		logger.info("SATest: nearest item: %s, path: %s", item, path);
		IGObject object = IGReferencesSearch.asObject(item);
		IGAssignmentsSearch rs = new IGAssignmentsSearch(fZPrj, depth);
		Map<Long, RootResult> searches = rs.assignmentThroughSearch(object, path, true, true, backward);
		int count = 0;
		for (Long key : searches.keySet()) {
			ReferenceSearchResult result = searches.get(key);
			count += result.countRefs();
		}; System.out.println("expected " + expected + ", got "+ count);
		assertEquals(count, expected);
	}

	private void stressTestIGReferenceSearch(DMUID aDUUID, String aPath) throws Exception {
		// do a real stress test - global search on all names

		DMManager dum = fZPrj.getDUM();

		IDesignModule du = dum.getDM(aDUUID);

		ZStack<VHDLNode> stack = new ZStack<VHDLNode>();

		stack.push((VHDLNode) du);
		while (!stack.isEmpty()) {
			VHDLNode obj = stack.pop();

			if (obj == null) {
				continue;
			}

			if (obj instanceof Name) {

				SourceLocation location = obj.getLocation();

				logger.info("SATest: Searching for %s at %s...", obj, location);

				ReferenceSearchResult result = runIGReferenceSearch(location.fSF, aPath, location.fLine, location.fCol, true, true);
				// no asserts - we're searching blindly for all identifiers
				// assertNotNull (result);
				if (result != null) {
					result.dump(0, System.out);
				}
			}

			int n = obj.getNumChildren();
			for (int i = 0; i < n; i++) {
				VHDLNode child = obj.getChild(i);

				stack.push(child);
			}
		}

	}


	private void stressTestASTReferenceSearch(DMUID aDUUID) throws Exception {
		// do a real stress test - global search on all names

		DMManager dum = fZPrj.getDUM();

		DesignUnit du = (DesignUnit) dum.getDM(aDUUID);

		ZStack<VHDLNode> stack = new ZStack<VHDLNode>();

		stack.push(du);
		while (!stack.isEmpty()) {
			VHDLNode obj = stack.pop();

			if (obj == null) {
				continue;
			}

			if (obj instanceof Name) {

				SourceLocation location = obj.getLocation();

				logger.info("SATest: Searching for %s at %s...", obj, location);

				ReferenceSearchResult result = runASTReferenceSearch(location.fSF, location.fLine, location.fCol, true, true);
				// no asserts - we're searching blindly for all identifiers
				// assertNotNull (result);
				if (result != null) {
					result.dump(0, System.out);
				}
			}

			int n = obj.getNumChildren();
			for (int i = 0; i < n; i++) {
				VHDLNode child = obj.getChild(i);

				stack.push(child);
			}
		}

	}

	@After
	public void tearDown() {
		if (fZPrj != null) {
			fZPrj.shutdown();
			fZPrj = null;
		}
	}

}
