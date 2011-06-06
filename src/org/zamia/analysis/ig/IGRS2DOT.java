/* 
 * Copyright 2011 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jun 6, 2011
 */
package org.zamia.analysis.ig;

import java.io.PrintWriter;

/**
 * convert an IGRSResult into a dot-file for graphviz
 * 
 * @author Guenter Bartsch
 *
 */

public class IGRS2DOT {

	private final IGRSResult fResult;

	private PrintWriter fOut;

	private int fUID;

	public IGRS2DOT(IGRSResult aResult) {
		fResult = aResult;
	}

	public void convert(PrintWriter aOut) {

		fOut = aOut;
		fUID = 1;

		aOut.printf("digraph IGRS {\n");

		dumpNode(fResult.getRoot(), 1);

		aOut.printf("}\n");

	}

	private void dumpNode(IGRSNode aNode, int aIndent) {

		printI("subgraph cluster_" + fUID++ + " {\n", aIndent);

		printI("label = \"" + getLabel(aNode.getInstanceName()) + "\";\n", aIndent + 1);

		int n = aNode.getNumPorts();
		for (int i = 0; i < n; i++) {

			IGRSPort p = aNode.getPort(i);

			printI(getPortUID(p) + " [label=\"" + getLabel(p.getId()) + "\"];\n", aIndent + 1);
		}

		n = aNode.getNumSubs();
		for (int i = 0; i < n; i++) {
			IGRSNode sub = aNode.getSub(i);

			dumpNode(sub, aIndent + 1);

		}
		
		n = aNode.getNumSignals();
		for (int i = 0; i < n; i++) {
			IGRSSignal s = aNode.getSignal(i);

			IGRSPort src = s.getPort();
			
			int m = s.getNumConns();
			for (int j=0; j<m; j++) {
				
				IGRSPort conn = s.getConn(j);

				if (conn == src) {
					continue;
				}
				
				printI(getPortUID(src) + " -> "+getPortUID(conn)+";\n", aIndent + 1);
				
			}
			

		}
		

		printI("}\n", aIndent);

	}

	private String getPortUID(IGRSPort aPort) {
		long hash = aPort.hashCode();
		if (hash > 0)
			return "o" + hash;
		return "p" + (-hash);
	}

	private void printI(String aStr, int aIndent) {
		for (int i = 0; i < aIndent; i++) {
			fOut.print("  ");
		}

		fOut.print(aStr);
	}
	
	private String getLabel(String aId) {
		
		StringBuilder buf = new StringBuilder();
		
		int l = aId.length();
		if (l>10)
			l = 10;
		
		for (int i = 0; i<l; i++) {
			
			char c = aId.charAt(i);
			if (Character.isLetter(c)) {
				buf.append(c);
			} else if (Character.isDigit(c)) {
				buf.append(c);
			} else
				buf.append('.');
		}
		
		return buf.toString();
	}

}
