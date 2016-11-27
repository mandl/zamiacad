/*
 * Copyright 2009,2010 by the authors indicated in the @author tags.
 * All rights reserved.
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Jul 24, 2009
 */
package org.zamia.instgraph.interpreter;

import static org.zamia.instgraph.IGObject.OIDir.APPEND;
import static org.zamia.instgraph.IGObject.OIDir.IN;
import static org.zamia.instgraph.IGObject.OIDir.NONE;
import static org.zamia.instgraph.IGObject.OIDir.OUT;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.zamia.ErrorReport;
import org.zamia.SourceLocation;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.instgraph.IGContainer;
import org.zamia.instgraph.IGObject;
import org.zamia.instgraph.IGStaticValue;
import org.zamia.instgraph.IGStaticValueBuilder;
import org.zamia.instgraph.IGSubProgram;
import org.zamia.instgraph.IGSubProgram.IGBuiltin;
import org.zamia.instgraph.IGType;
import org.zamia.instgraph.IGTypeStatic;
import org.zamia.instgraph.interpreter.IGStmt.ReturnStatus;
import org.zamia.instgraph.sim.ref.IGFileDriver;
import org.zamia.instgraph.sim.ref.IGSimProcess;
import org.zamia.vhdl.ast.VHDLNode.ASTErrorMode;

/**
 * Collection of builtin operation implementations
 * 
 * @author Guenter Bartsch
 * 
 */

public class IGBuiltinOperations {

	public static ReturnStatus execBuiltin(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGBuiltin builtin = aSub.getBuiltin();

		switch (builtin) {
		case INT_ABS:
		case INT_NEG:
		case INT_POS:
			return execIntUnary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case INT_ADD:
		case INT_DIV:
		case INT_MINUS:
		case INT_MOD:
		case INT_MUL:
		case INT_POWER:
		case INT_REM:
			return execIntBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case REAL_ABS:
		case REAL_NEG:
		case REAL_POS:
			return execRealUnary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case REAL_ADD:
		case REAL_MINUS:
		case REAL_DIV:
		case REAL_MUL:
		case REAL_POWER:
			return execRealBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case SCALAR_EQUALS:
		case SCALAR_GREATER:
		case SCALAR_GREATEREQ:
		case SCALAR_LESS:
		case SCALAR_LESSEQ:
		case SCALAR_NEQUALS:
			return execScalarCompare(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case BOOL_AND:
		case BOOL_NAND:
		case BOOL_NOR:
		case BOOL_OR:
		case BOOL_XNOR:
		case BOOL_XOR:
			return execBoolBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case BOOL_NOT:
			return execBoolNot(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case BIT_NOT:
			return execBitNot(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case BIT_AND:
		case BIT_NAND:
		case BIT_NOR:
		case BIT_OR:
		case BIT_XNOR:
		case BIT_XOR:
			return execBitBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case ARRAY_NOT:
			return execArrayNot(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case ARRAY_EQUALS:
		case ARRAY_NEQUALS:
			return execArrayCompare(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case ARRAY_GREATER:
		case ARRAY_GREATEREQ:
		case ARRAY_LESS:
		case ARRAY_LESSEQ:
			return execArrayCompareRelative(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case ARRAY_AND:
		case ARRAY_NAND:
		case ARRAY_NOR:
		case ARRAY_OR:
		case ARRAY_XNOR:
		case ARRAY_XOR:
			return execArrayBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case BITVECTOR_ROR:
		case BITVECTOR_ROL:
		case BITVECTOR_SLL:
		case BITVECTOR_SRL:
		case BITVECTOR_SLA:
		case BITVECTOR_SRA:
			return execBitvectorBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);
			
		case ARRAY_CONCATAA:
		case ARRAY_CONCATAE:
		case ARRAY_CONCATEA:
		case ARRAY_CONCATEE:
			return execArrayConcat(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case READ:
			return execRead(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case WRITE:
			return execWrite(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case READLINE:
			return execReadline(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case WRITELINE:
			return execWriteline(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case ENDFILE:
			return execEndfile(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case FILE_OPEN:
			return execFileOpen(aSub, aRuntime, aLocation, aErrorMode, aReport);

		case FILE_CLOSE:
			return execFileClose(aSub, aRuntime, aLocation, aErrorMode, aReport);
			
		case FLUSH:
			ZamiaLogger.getInstance().info("Flush is not needed. Zamia keeps files closed. ");
			return ReturnStatus.CONTINUE;

		case NOW:
			return execNow(aSub, aRuntime, aLocation, aErrorMode, aReport);
		
		default:
			throw new ZamiaException("Sorry, unimplemented builtin: " + builtin, aLocation);
		}

	}
	
	private static ReturnStatus execNow(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport) throws ZamiaException {
		
		if (aRuntime instanceof IGSimProcess) {
			
			BigInteger now = ((IGSimProcess) aRuntime).getCurrentTime(aLocation);
		
			IGTypeStatic timeType = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		
			IGStaticValue nowTime = new IGStaticValue.INT(timeType, "NOW", aLocation, now);
		
			aRuntime.push(nowTime);
		}
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execIntUnary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");

		IGStaticValue v = aRuntime.getObjectValue(intfA);

		BigInteger num = v.getNum();
		BigInteger res = null;

		switch (aSub.getBuiltin()) {
		case INT_ABS:
			res = num.abs();
			break;
		case INT_NEG:
			res = num.negate();
			break;
		case INT_POS:
			res = num;
			break;
		default:
			throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGStaticValue resValue = new IGStaticValue.INT(rt, null, aLocation, res);

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execIntBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue vA = aRuntime.getObjectValue(intfA);
		if (vA == null)	{
			return error(vA, "execIntBinary(): vA", aErrorMode, aLocation);
		}

		BigInteger numA = vA.getNum();

		IGObject intfB = container.resolveObject("b");
		IGStaticValue vB = aRuntime.getObjectValue(intfB);
		if (vB == null)	{
			return error(vB, "execIntBinary(): vB", aErrorMode, aLocation);
		}
		
		BigInteger numB = vB.getNum();

		if (numB == null) {
			numB = vB.getReal().toBigInteger();
		}

		BigInteger res = null;

		switch (aSub.getBuiltin()) {
		case INT_ADD:
			res = numA.add(numB);
			break;
		case INT_MINUS:
			res = numA.subtract(numB);
			break;
		case INT_DIV:
			res = numA.divide(numB);
			break;
		case INT_MUL:
			res = numA.multiply(numB);
			break;
		case INT_POWER:
			res = numA.pow(numB.intValue());
			break;
		case INT_MOD:
			res = numA.mod(numB);
			if (numB.signum() < 0) {
				res = res.negate();
			}
			break;
		case INT_REM:
			res = numA.remainder(numB);
			break;
		default:
			throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGStaticValue resValue = new IGStaticValue.INT(rt, null, aLocation, res);

		aRuntime.push(resValue);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execRealUnary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");

		IGStaticValue v = aRuntime.getObjectValue(intfA);

		BigDecimal num = v.getReal();
		BigDecimal res = null;

		switch (aSub.getBuiltin()) {
		case REAL_ABS:
			res = num.abs();
			break;
		case REAL_NEG:
			res = num.negate();
			break;
		case REAL_POS:
			res = num;
			break;
		default:
			throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGStaticValue resValue = new IGStaticValueBuilder(rt, null, aLocation).setReal(res).buildConstant();

		aRuntime.push(resValue);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execRealBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue vA = aRuntime.getObjectValue(intfA);
		if (vA == null)	{
			return error(vA, "execRealBinary(): vA", aErrorMode, aLocation);
		}
		
		BigDecimal numA = vA.getReal();

		IGObject intfB = container.resolveObject("b");
		IGStaticValue vB = aRuntime.getObjectValue(intfB);
		if (vB == null)	{
			return error(vB, "execRealBinary(): vB", aErrorMode, aLocation);
		}

		BigDecimal numB = vB.getReal();

		BigDecimal res = null;

		switch (aSub.getBuiltin()) {
		case REAL_ADD:
			res = numA.add(numB);
			break;
		case REAL_MINUS:
			res = numA.subtract(numB);
			break;
		case REAL_DIV:
			res = numA.divide(numB, 7, BigDecimal.ROUND_HALF_EVEN);
			break;
		case REAL_MUL:
			res = numA.multiply(numB);
			break;
		case REAL_POWER:
			res = numA.pow(numB.intValue());
			break;
		default:
			throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGStaticValue resValue = new IGStaticValueBuilder(rt, null, aLocation).setReal(res).buildConstant();

		aRuntime.push(resValue);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execScalarCompare(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue vA = aRuntime.getObjectValue(intfA);
		if (vA == null)	{
			return error(vA, "execScalarCompare(): vA", aErrorMode, aLocation);
		}
		long ordA = vA.getOrd();
		
		IGObject intfB = container.resolveObject("b");
		IGStaticValue vB = aRuntime.getObjectValue(intfB);
		if (vB == null)	{
			return error(vB, "execScalarCompare(): vB", aErrorMode, aLocation);
		}
		long ordB = vB.getOrd();

		boolean res;

		switch (aSub.getBuiltin()) {
			case SCALAR_EQUALS:
				res = ordA == ordB;
				break;
			case SCALAR_GREATER:
				res = ordA > ordB;
				break;
			case SCALAR_GREATEREQ:
				res = ordA >= ordB;
				break;
			case SCALAR_LESS:
				res = ordA < ordB;
				break;
			case SCALAR_LESSEQ:
				res = ordA <= ordB;
				break;
			case SCALAR_NEQUALS:
				res = ordA != ordB;
				break;
			default:
				throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}
		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(res ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execBoolNot(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");

		IGStaticValue v = aRuntime.getObjectValue(intfA);

		boolean b = v.getOrd() == 1;
		boolean res = !b;

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(res ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execBoolBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue vA = aRuntime.getObjectValue(intfA);
		if (vA == null)	{
			return error(vA, "execIntBinary(): vB", aErrorMode, aLocation);
		}
		
		boolean bA = vA.getOrd() == 1;

		IGObject intfB = container.resolveObject("b");
		IGStaticValue vB = aRuntime.getObjectValue(intfB);
		boolean bB = vB.getOrd() == 1;

		boolean res = false;

		switch (aSub.getBuiltin()) {
		case BOOL_AND:
			res = bA & bB;
			break;

		case BOOL_NAND:
			res = !(bA & bB);
			break;

		case BOOL_NOR:
			res = !(bA | bB);
			break;

		case BOOL_OR:
			res = bA | bB;
			break;

		case BOOL_XNOR:
			res = !(bA ^ bB);
			break;

		case BOOL_XOR:
			res = bA ^ bB;

			break;
		default:
			throw new ZamiaException("Sorry. Internal error. Unsupported operation: " + aSub, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(res ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execBitNot(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");

		IGStaticValue v = aRuntime.getObjectValue(intfA);

		boolean b = v.getOrd() == 1;
		boolean res = !b;

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(res ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execBitBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGBuiltin originalBuiltin = aSub.getBuiltin();
		IGBuiltin boolBuiltin;
		switch (originalBuiltin) {
			case BIT_AND:
				boolBuiltin = IGBuiltin.BOOL_AND; break;
			case BIT_NAND:
				boolBuiltin = IGBuiltin.BOOL_NAND; break;
			case BIT_NOR:
				boolBuiltin = IGBuiltin.BOOL_NOR; break;
			case BIT_OR:
				boolBuiltin = IGBuiltin.BOOL_OR; break;
			case BIT_XNOR:
				boolBuiltin = IGBuiltin.BOOL_XNOR; break;
			case BIT_XOR:
				boolBuiltin = IGBuiltin.BOOL_XOR; break;
			default:
				throw new ZamiaException("Sorry, unimplemented builtin: " + originalBuiltin, aLocation);
		}
		aSub.setBuiltin(boolBuiltin);
		ReturnStatus returnStatus = execBoolBinary(aSub, aRuntime, aLocation, aErrorMode, aReport);
		aSub.setBuiltin(originalBuiltin);

		return returnStatus;
	}

	private static ReturnStatus execArrayNot(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");

		IGStaticValue value = aRuntime.getObjectValue(intfA);

		int offset = value.getArrayOffset();
		int n = value.getNumArrayEntries(aLocation);
		IGType t = aSub.getReturnType();
		IGTypeStatic rt = t.computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		
		IGType et = rt.getElementType();

		rt = ensureConstrainedArray(rt, value, aLocation);

		IGStaticValueBuilder builder = new IGStaticValueBuilder(rt, null, aLocation);

		for (int i = 0; i < n; i++) {

			IGStaticValue v = value.getValue(i + offset, aLocation);
			boolean b = v.getOrd() == 1;

			v = et.getEnumLiteral(!b ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

			builder.set(i + offset, v, aLocation);
		}

		IGStaticValue resValue = builder.buildConstant();

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus error(IGStaticValue value, String varName, ASTErrorMode aErrorMode, SourceLocation aLocation) throws ZamiaException {
		if (aErrorMode == ASTErrorMode.RETURN_NULL) {
			return ReturnStatus.ERROR;
		}
		throw new ZamiaException ("IGBuiltinOperations: " + varName + "==null", aLocation);
	}
	private static ReturnStatus execArrayCompare(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue valueA = aRuntime.getObjectValue(intfA);
		if (valueA == null)	{
			return error(valueA, "execArrayCompare(): vA", aErrorMode, aLocation);
		}

		IGObject intfB = container.resolveObject("b");
		IGStaticValue valueB = aRuntime.getObjectValue(intfB);
		if (valueB == null) {
			return error(valueB, "execArrayCompare(): vB", aErrorMode, aLocation);
		}

		int offsetA = valueA.getArrayOffset();
		int offsetB = valueB.getArrayOffset();
		int nA = valueA.getNumArrayEntries(aLocation);
		int nB = valueB.getNumArrayEntries(aLocation);

		boolean doEquals = true;
		switch (aSub.getBuiltin()) {
		case ARRAY_EQUALS:
			break;
		case ARRAY_NEQUALS:
			doEquals = false;
			break;
		default:
			throw new ZamiaException("Internal interpreter error: execArrayCompare() called on non-compare op " + aSub.getBuiltin(), aLocation);
		}

		boolean bRes = false;

		if (nA != nB) {
			bRes = !doEquals;
		} else {

			// compute equals, invert it if necessary

			bRes = true;
			for (int i = 0; i < nA; i++) {

				IGStaticValue vA = valueA.getValue(i + offsetA, aLocation);
				IGStaticValue vB = valueB.getValue(i + offsetB, aLocation);

				if (!vA.equalsValue(vB)) {
					bRes = false;
					break;
				}
			}

			if (!doEquals) {
				bRes = !bRes;
			}
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(bRes ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execArrayCompareRelative(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue valueA = aRuntime.getObjectValue(intfA);
		if (valueA == null)	{
			return error(valueA, "execArrayCompareRelative(): vA", aErrorMode, aLocation);
		}

		IGObject intfB = container.resolveObject("b");
		IGStaticValue valueB = aRuntime.getObjectValue(intfB);
		if (valueB == null)	{
			return error(valueB, "execArrayCompareRelative(): vB", aErrorMode, aLocation);
		}

		int offsetA = valueA.getArrayOffset();
		int offsetB = valueB.getArrayOffset();
		int nA = valueA.getNumArrayEntries(aLocation);
		int nB = valueB.getNumArrayEntries(aLocation);

		switch (aSub.getBuiltin()) {
		case ARRAY_GREATER:
		case ARRAY_GREATEREQ:

			// swap everything
			IGStaticValue tempV = valueA;
			valueA = valueB;
			valueB = tempV;
			int tempO = offsetA;
			offsetA = offsetB;
			offsetB = tempO;
			int tempN = nA;
			nA = nB;
			nB = tempN;
			break;

		case ARRAY_LESS:
		case ARRAY_LESSEQ:
			break;
		default:
			throw new ZamiaException("Internal interpreter error: execArrayCompareRelative() called on non-compare op " + aSub.getBuiltin(), aLocation);
		}

		boolean bRes; // true when with EQ, false otherwise. see std_logic_arith.vhd:1355 (unsigned_is_less_or_equal())
		switch (aSub.getBuiltin()) {
			case ARRAY_GREATEREQ:
			case ARRAY_LESSEQ:
				bRes = true;
				break;
			default:
				bRes = false;
		}
		boolean bAis0, bBis1;
		int nM = nA > nB ? nA : nB;
		IGStaticValue zero = valueA.getStaticType().getStaticElementType(aLocation).findEnumLiteral('0');
		IGStaticValue one = valueA.getStaticType().getStaticElementType(aLocation).findEnumLiteral('1');
		for (int i = 0; i < nM; i++) {

			boolean beyondA = i + offsetA >= nA;
			boolean beyondB = i + offsetB >= nB;
			IGStaticValue vA = beyondA ? zero : valueA.getValue(i + offsetA, aLocation);
			IGStaticValue vB = beyondB ? zero : valueB.getValue(i + offsetB, aLocation);

			bAis0 = vA.equalsValue(zero);
			bBis1 = vB.equalsValue(one);

			bRes = (bAis0 && bBis1) || (bAis0 && bRes) || (bBis1 && bRes); // see std_logic_arith.vhd:1332 (unsigned_is_less())
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		IGStaticValue resValue = rt.getEnumLiteral(bRes ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static IGTypeStatic ensureConstrainedArray(IGTypeStatic aRt, IGStaticValue aAlternative, SourceLocation aLocation) throws ZamiaException {
		if (aRt.isUnconstrained()) {
			// compute constraints at runtime
			aRt = aAlternative.getStaticType();
			if (aRt.isUnconstrained()) {
				throw new ZamiaException("Interpreter error: cannot determine resulting array boundaries, all types involved are unconstrained :-/", aLocation);
			}
		}
		return aRt;
	}
		
	private static ReturnStatus execBitvectorBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, final SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		final IGStaticValue valueA = aRuntime.getObjectValue(intfA);
		if (valueA == null)	{
			return error(valueA, "execBitvectorBinary(): vA", aErrorMode, aLocation);
		}

		IGObject intfB = container.resolveObject("b");
		final IGStaticValue valueB = aRuntime.getObjectValue(intfB);
		if (valueB == null)	{
			return error(valueB, "execBitvectorBinary(): vB", aErrorMode, aLocation);
		}

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}
		
		rt = valueA.getStaticType();
		if (rt.isUnconstrained())
			throw new ZamiaException("Interpreter error: cannot determine resulting array boundaries, all types involved are unconstrained :-/", aLocation);

		final IGStaticValueBuilder builder = new IGStaticValueBuilder(rt, null, aLocation);

		IGSubProgram.IGBuiltin func = aSub.getBuiltin();

		final int offset = valueA.getArrayOffset();
		final int nA = valueA.getNumArrayEntries(aLocation);
		final int limit = offset + nA;
		final int valueb = valueB.getInt();
		
		class ROR {
			int src;
			/**Use right = 1 for right and -1 for left*/
			ROR(int right) {
				src = right * valueb;
			}
			void run() throws ZamiaException {
				for (int dst = offset ; dst != limit ; src++, dst++) {
					IGStaticValue el = get();
					builder.set(dst, el, aLocation);  // put into dest
				}
			}
			IGStaticValue get() throws ZamiaException {
				int wrappedSrc = //(src % nA) // this sux in Java
						(src % nA + nA) % nA // we must do this to support shift distance ouside the vector range
						+ offset;
				return valueA.getValue(wrappedSrc, aLocation); // take bit
			}
		}
		
		class SLL extends ROR {
			final IGStaticValue filler;
			SLL(int right) throws ZamiaException {
				super(right);
				src += offset;
				filler = getFiller();
			}
			IGStaticValue getFiller() throws ZamiaException {
				// fill with 0, by default
				IGTypeStatic et = builder.getType().getStaticElementType(aLocation);
				return et.getEnumLiteral(0, aLocation, ASTErrorMode.EXCEPTION, null);
			};
			boolean inRange() {return !(src < offset);}
			IGStaticValue get() throws ZamiaException {
				return (inRange()) ? valueA.getValue(src, aLocation) : filler;
			}
		}
		
		class SRL extends SLL {
			SRL(int right) throws ZamiaException {super(right);}
			int max = nA + offset;
			boolean inRange() {return src < max;}
		}
		
		class SLA extends SLL {
			SLA(int right) throws ZamiaException { super(right); }
			IGStaticValue getFiller() throws ZamiaException {
				return valueA.getValue(offset, aLocation); // a single value L'Right
			};
		}
		class SRA extends SRL {
			SRA(int right) throws ZamiaException {super(right);}

			IGStaticValue getFiller() throws ZamiaException {
				return valueA.getValue(/*max*/nA + offset-1, aLocation); // L'Left
			};
			
		}
		
		
		switch (func) {
		case BITVECTOR_ROR: new ROR( 1).run(); break;
		case BITVECTOR_ROL: new ROR(-1).run(); break;// just into opposite direction
		case BITVECTOR_SRL: ((valueb < 0) ? new SLL( 1) : new SRL( 1)).run(); break;
		case BITVECTOR_SLL: ((valueb < 0) ? new SRL(-1) : new SLL(-1)).run(); break;
		case BITVECTOR_SRA: ((valueb < 0) ? new SLA( 1) : new SRA( 1)).run(); break;
		case BITVECTOR_SLA: ((valueb < 0) ? new SRA(-1) : new SLA(-1)).run(); break;
		default:
			throw new ZamiaException("Internal interpreter error: execArrayBinary() called on non-implemented op " + aSub.getBuiltin(), aLocation);
		}

		IGStaticValue resValue = builder.buildConstant();

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}
	private static ReturnStatus execArrayBinary(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue valueA = aRuntime.getObjectValue(intfA);
		if (valueA == null)	{
			return error(valueA, "execArrayBinary(): vA", aErrorMode, aLocation);
		}

		IGObject intfB = container.resolveObject("b");
		IGStaticValue valueB = aRuntime.getObjectValue(intfB);
		if (valueB == null)	{
			return error(valueB, "execArrayBinary(): vB", aErrorMode, aLocation);
		}
		

		int offsetA = valueA.getArrayOffset();
		int offsetB = valueB.getArrayOffset();
		int nA = valueA.getNumArrayEntries(aLocation);
		int nB = valueB.getNumArrayEntries(aLocation);

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGTypeStatic et = rt.getStaticElementType(aLocation);

		if (rt.isUnconstrained()) {
			rt = ensureConstrainedArray(valueA.getStaticType(), valueB, aLocation);
		}

		IGStaticValueBuilder builder = new IGStaticValueBuilder(rt, null, aLocation);

		int offsetR = builder.getArrayOffset();

		if (nA != nB) {
			throw new ZamiaException("Interpreter error: execArrayBinary() called on non-equal-lengths operands: " + valueA + ", " + valueB, aLocation);
		} else {

			for (int i = 0; i < nA; i++) {

				IGStaticValue vA = valueA.getValue(i + offsetA, aLocation);
				IGStaticValue vB = valueB.getValue(i + offsetB, aLocation);

				boolean bA = vA.getOrd() == 1;
				boolean bB = vB.getOrd() == 1;
				boolean res = false;

				switch (aSub.getBuiltin()) {
				case ARRAY_AND:
					res = bA & bB;
					break;

				case ARRAY_NAND:
					res = !(bA & bB);
					break;

				case ARRAY_NOR:
					res = !(bA | bB);
					break;

				case ARRAY_OR:
					res = bA | bB;
					break;

				case ARRAY_XNOR:
					res = !(bA ^ bB);
					break;

				case ARRAY_XOR:
					res = bA ^ bB;

					break;

				default:
					throw new ZamiaException("Internal interpreter error: execArrayBinary() called on non-implemented op " + aSub.getBuiltin(), aLocation);
				}

				IGStaticValue resValue = et.getEnumLiteral(res ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

				builder.set(i + offsetR, resValue, aLocation);
			}
		}

		IGStaticValue resValue = builder.buildConstant();

		aRuntime.push(resValue);
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execArrayConcat(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfA = container.resolveObject("a");
		IGStaticValue valueA = aRuntime.getObjectValue(intfA);

		IGObject intfB = container.resolveObject("b");
		IGStaticValue valueB = aRuntime.getObjectValue(intfB);

		IGTypeStatic rt = aSub.getReturnType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (rt == null) {
			return ReturnStatus.ERROR;
		}

		IGTypeStatic tA = valueA.getStaticType();
		IGTypeStatic tB = valueB.getStaticType();

		if (rt.isUnconstrained()) {

			// compute constraints at runtime

			int l = 0, r = 0;
			boolean a = true;

			IGTypeStatic idxType = rt.getStaticIndexType(aLocation);

			switch (aSub.getBuiltin()) {
			case ARRAY_CONCATAA:

				IGStaticValue aRange = idxType.getStaticRange();
				l = (int) aRange.getLeft().getOrd();
				a = aRange.getAscending().isTrue();

				int cardA = (int) tA.getStaticIndexType(aLocation).computeCardinality(aLocation);
				int cardB = (int) tB.getStaticIndexType(aLocation).computeCardinality(aLocation);

				r = a ? l + cardA + cardB - 1 : l - cardA - cardB - 1;

				//				IGStaticValue aRange = tA.getStaticIndexType(aLocation).getStaticRange();
				//				l = (int) aRange.getLeft().getOrd();
				//				r = (int) aRange.getRight().getOrd();
				//				a = aRange.getAscending().isTrue();
				//				int card = (int) tB.getStaticIndexType(aLocation).computeCardinality(aLocation);
				//				r = a ? r + card : r - card;
				break;

			case ARRAY_CONCATAE:

				aRange = tA.getStaticIndexType(aLocation).getStaticRange();
				l = (int) aRange.getLeft().getOrd();
				r = (int) aRange.getRight().getOrd();
				a = aRange.getAscending().isTrue();

				r = a ? r + 1 : r - 1;
				break;

			case ARRAY_CONCATEA:

				IGStaticValue bRange = tB.getStaticIndexType(aLocation).getStaticRange();
				l = (int) bRange.getLeft().getOrd();
				r = (int) bRange.getRight().getOrd();
				a = bRange.getAscending().isTrue();

				l = a ? l - 1 : l + 1;
				break;

			case ARRAY_CONCATEE:

				l = idxType.getStaticLow(aLocation).getInt();
				a = idxType.isAscending();
				r = l + 1;

				break;

			default:
				throw new ZamiaException("Internal interpreter error: execArrayConcat() called on non-implemented op " + aSub.getBuiltin(), aLocation);
			}

			IGStaticValue left = new IGStaticValueBuilder(idxType, null, aLocation).setOrd(l).buildConstant();
			IGStaticValue right = new IGStaticValueBuilder(idxType, null, aLocation).setOrd(r).buildConstant();

			IGType boolT = container.findBoolType();

			IGStaticValue ascending = boolT.getEnumLiteral(a ? 1 : 0, aLocation, ASTErrorMode.EXCEPTION, null);

			IGTypeStatic rType = idxType.getStaticRange().getStaticType();
			IGStaticValue range = new IGStaticValue.RANGE(rType, left, right, ascending);

			rt = rt.createSubtype(range, aLocation);
		}

		IGStaticValueBuilder resBuilder = new IGStaticValueBuilder(rt, null, aLocation);

		int off = resBuilder.getArrayOffset();

		if (aSub.getBuiltin() == IGBuiltin.ARRAY_CONCATAA || aSub.getBuiltin() == IGBuiltin.ARRAY_CONCATEA) {
			int n = (int) tB.getStaticIndexType(aLocation).computeCardinality(aLocation);
			int offB = valueB.getArrayOffset();
			for (int i = 0; i < n; i++) {
				IGStaticValue op = valueB.getValue(i + offB, aLocation);
				resBuilder.set(i + off, op, aLocation);
			}
			off += n;
		} else {
			resBuilder.set(off, valueB, aLocation);
			off += 1;
		}

		if (aSub.getBuiltin() == IGBuiltin.ARRAY_CONCATAA || aSub.getBuiltin() == IGBuiltin.ARRAY_CONCATAE) {
			int n = (int) tA.getStaticIndexType(aLocation).computeCardinality(aLocation);
			int offA = valueA.getArrayOffset();
			for (int i = 0; i < n; i++) {
				IGStaticValue op = valueA.getValue(i + offA, aLocation);
				resBuilder.set(i + off, op, aLocation);
			}
			off += n;
		} else {
			resBuilder.set(off, valueA, aLocation);
			off += 1;
		}

		aRuntime.push(resBuilder.buildConstant());
		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execRead(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfL = container.resolveObject("L");
		IGStaticValue valueL = aRuntime.getObjectValue(intfL);

		IGObject intfV = container.resolveObject("VALUE");
		IGObject intfG = container.resolveObject("GOOD");

		IGTypeStatic lT = valueL.getStaticType();
		IGTypeStatic vCT = aRuntime.getDriver(intfV).getCurrentType();
		IGTypeStatic vT = intfV.getType().computeStaticType(aRuntime, aErrorMode, aReport);
		if (valueL.toString().isEmpty()) {
			if (intfG != null)
				aRuntime.setObjectValue(intfG, container.findFalseValue(), aLocation);
			else {
				String msg = "TEXTIO procedure READ(" + vT + ") : Parameter L designates an empty string.";
				throwReport(aErrorMode, msg, aLocation, aReport);
				return ReturnStatus.ERROR;
			}
			return ReturnStatus.CONTINUE;
		}

		IGTypeStatic idxType = lT.getStaticIndexType(aLocation);
		IGStaticValue range = idxType.getStaticRange();
		int l = (int) range.getLeft().getOrd();
		int r = (int) range.getRight().getOrd();
		int vL = -1, vR, vN = 1, offset = 0;
		boolean asc = true;
		IGTypeStatic vET = vCT;
		if (vCT.isArray()) {
			vET = vCT.getStaticElementType(aLocation);
			IGTypeStatic vIdxType = vCT.getStaticIndexType(aLocation);
			IGStaticValue vRange = vIdxType.getStaticRange();
			asc = vRange.getAscending().isTrue();
			vR = (int) vRange.getRight().getOrd();
			vL = (int) vRange.getLeft().getOrd();
			vN = asc ? vR - vL + 1 : vL - vR + 1;
			offset = asc ? vL : vR;
		}

		int i = r;
		int total = 0;
		String typeId = vT.getId();

//		The READ procedures defined for a given type other than CHARACTER and STRING begin by skipping
//		leading whitespace characters. A whitespace character is defined as a space, a nonbreaking space, or a
//		horizontal tabulation character (SP, NBSP, or HT)
		if (!(typeId.equals("CHARACTER") || typeId.equals("STRING"))) {
			while (i >= l) {
				IGStaticValue charV = valueL.getValue(i, aLocation);
				int ord = charV.getEnumOrd();
				if (ord == 32 || ord == 9 || ord == 160 /*160=nbsp*/) {
					i--;
				} else {
					break;
				}
			}
		}

		IGStaticValueBuilder retValueBuilder = new IGStaticValueBuilder(vCT, null, aLocation);
		String intMsg = null;

		if (typeId.equals("BOOLEAN")) {

			if (i - l + 1 > 3) {
				IGStaticValue literal = null;
				StringBuilder literalB = new StringBuilder();
				IGStaticValue c1 = valueL.getValue(i, aLocation);
				IGStaticValue c2 = valueL.getValue(i - 1, aLocation);
				IGStaticValue c3 = valueL.getValue(i - 2, aLocation);
				IGStaticValue c4 = valueL.getValue(i - 3, aLocation);
				literalB.append(c1).append(c2).append(c3).append(c4);
				if (literalB.toString().equals("TRUE")) {

					literal = vET.findEnumLiteral("TRUE");
					i = i - 3 - 1;

				} else {
					IGStaticValue c5 = valueL.getValue(i - 4, aLocation);
					literalB.append(c5);
					if (literalB.toString().equals("FALSE")) {

						literal = vET.findEnumLiteral("FALSE");
						i = i - 4 - 1;

					}
				}
				if (literal != null) {
					retValueBuilder.setConstant(literal);
					total = 1;
				}
			}

		} else if (vET.isInteger()) {

			StringBuilder intAsString = new StringBuilder();
			for (; i >= l; i--) {

				IGStaticValue charV = valueL.getValue(i, aLocation);
				char charLiteral = charV.getCharLiteral();

				if (Character.isDigit(charLiteral) || (charLiteral == '-' && intAsString.length() == 0)) {

					intAsString.append(charLiteral);
					total++;

				} else {
					break;
				}
			}

			try {
				long num = new BigInteger(intAsString.toString()).longValue();
				if (num >= -2147483648 && num <= 2147483647) {

					retValueBuilder.setNum(num);

				} else {
					intMsg = "Integer value exceeds INTEGER'high.";
					total = 0;
				}
			} catch (NumberFormatException e) {
				intMsg = "No digits found in abstract literal.";
				total = 0;
			}


		} else if (vET.isCharEnum()) {

			for (int j = vL; i >= l; i--) {

				if (total >= vN) {
					break;
				}

				IGStaticValue charV = valueL.getValue(i, aLocation);
				IGStaticValue enumLiteral = vET.findEnumLiteral(charV.getId());
				if (enumLiteral == null) {
					if (typeId.equals("BIT_VECTOR")) {
						if (charV.getId().equals("_")) {
							if (total > 0 && !valueL.getValue(i + 1, aLocation).getId().equals("_")) {
								continue;
							}
						}
					}
					break;
				}

				append(IGStaticValue.adjustIdx(j, asc, vN, offset), enumLiteral, retValueBuilder, aLocation);
				total++;

				if (asc) {
					j++;
				} else {
					j--;
				}
			}

		} else {
			//todo: REAL
			//todo: TIME
			throw new ZamiaException("Internal interpreter error: don't know how to read " + typeId + " from file.", aLocation);
		}

		boolean isSmthRead = total > 0;
		if (isSmthRead) {
			try {
				IGStaticValue value = retValueBuilder.buildConstant();
				aRuntime.setObjectValue(intfV, value, aLocation);
			} catch (ZamiaException e) {
				isSmthRead = false;
			}
		}
		IGStaticValue nValueL = valueL;
		if (isSmthRead) {
			IGStaticValue nR = new IGStaticValueBuilder(range.getRight().getStaticType(), null, aLocation).setNum(i).buildConstant();
			IGStaticValue nRange = new IGStaticValue.RANGE.Builder(range).setRight(nR).buildConstant();
			
			IGTypeStatic nType = lT.createSubtype(nRange, aLocation);

			IGStaticValueBuilder nValueLB = new IGStaticValueBuilder(nType, null, aLocation);
			for (; i >= l; i--) {
				nValueLB.set(i, valueL.getValue(i, aLocation), aLocation);
			}
			nValueL = nValueLB.buildConstant();
		}

		if (intfG != null) {
			aRuntime.setObjectValue(intfG, isSmthRead ? container.findTrueValue() : container.findFalseValue(), aLocation);
		}

		if (!isSmthRead && intfG == null) {
			String msg;
			if (typeId.equals("INTEGER")) {
				msg = "TEXTIO procedure READ(INTEGER) : Cannot read value from " + valueL + ": " + intMsg;
			} else {
				msg = "TEXTIO procedure READ(" + vT + ") : Wrong " + vT + " length. Expected " + vN + ", found " + total + ".";
			}
			throwReport(aErrorMode, msg, aLocation, aReport);
		}

		aRuntime.setObjectValue(intfL, nValueL, aLocation);

		return ReturnStatus.CONTINUE;
	}

	private static void throwReport(ASTErrorMode aErrorMode, String msg, SourceLocation aLocation, ErrorReport aReport) throws ZamiaException {
		if (aErrorMode == ASTErrorMode.EXCEPTION) {
			throw new ZamiaException(msg, aLocation);
		} else {
			if (aReport != null) {
				aReport.append(msg, aLocation);
			}
		}
	}
	private static void append(int idx, IGStaticValue aEnumLiteral, IGStaticValueBuilder aRetValueBuilder, SourceLocation aLocation) throws ZamiaException {

		IGTypeStatic type = aRetValueBuilder.getType();

		if (type.isBit() || type.isCharEnum()) {

			aRetValueBuilder.setConstant(aEnumLiteral);

		} else if (type.isArray()) {

			aRetValueBuilder.set(idx, aEnumLiteral, aLocation);

		} else {
			throw new ZamiaException("Internal interpreter error: execRead(): char appending is not implemented for type " + type, aLocation);
		}
	}

	private static ReturnStatus execWrite(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {
		
		//todo: also process JUSTIFIED/FIELD options
		IGContainer container = aSub.getContainer();
		IGObject intfL = container.resolveObject("L");

		IGObject intfV = container.resolveObject("VALUE");
		IGStaticValue valueV = aRuntime.getObjectValue(intfV);

		IGStaticValue valueL = aRuntime.getObjectValue(intfL);

		// vairables in procedures (as opposed to vars in processes) are not initialized
		// at sim start, so we can get line eq null at process start.
//		if (valueL == null) // Once implicit default initialization was fixed, this is not necessary
//			valueL = IGFileDriver.newLineBuilder(0, container.findStringType(), aRuntime, aLocation, aErrorMode, aReport).buildConstant();

		IGTypeStatic lT = valueL.getStaticType();
		String strV = valueV.toString();
		int wasL, wasR, len = strV.length();
		
		if (lT.isAccess()) { // null or not assigned pointer -> refer new line
			wasL = 1; wasR = 0;
		} else { // append to old
			IGTypeStatic idxType = lT.getStaticIndexType(aLocation);
			IGStaticValue range = idxType.getStaticRange();
			wasL = (int) range.getLeft().getOrd();
			wasR = (int) range.getRight().getOrd();
		}
		
		// allocate a string len chars longer than the original
		IGStaticValueBuilder nValueLB = IGFileDriver.newLineBuilder(
				wasR + len, container.findStringType(), aRuntime, aLocation, aErrorMode, aReport);
		
		// shift original symbols to the right. Can we avoid copying?
		for (int i = wasL; i < wasR+1; i++) {
			IGStaticValue cv = valueL.getValue(i, aLocation);
			nValueLB.set(i+len, cv, aLocation);
		}

		// Insert the value into provided slot.
		// Interestinly, we could just insert a single valueV.
		//  - - - - - - - -
		// nValueLB.set(1, valueV, aLocation);
		// ZamiaLogger.getInstance().info(valueV.getStaticType().toString());
		//  - - - - - - - -
		// It would result in a wired, non-char string but such line can be successfully saved into the file
		// , e.g. write(line, int); writeline(file, line). However, read, write(line, int); read(line, int),
		// fails. So, we have to convert our valueV into separate chars of line len.
		
		IGType t1 = lT.getElementType(), t2 = t1.getElementType();	// The line type can be either access or string. In
		IGTypeStatic charType = (IGTypeStatic) (t2 == null ? t1 : t2); // the first case, we need to take the type twice
		
		for (int i = len ; i != 0 ; i--) {
			char c = strV.charAt(len-i);
			IGStaticValue staticChar = charType.findEnumLiteral(c);
			nValueLB.set(i, staticChar, aLocation);
		}

		//Here is an error, however. We return a string rather than line as required.
		valueL = nValueLB.buildConstant();

		aRuntime.setObjectValue(intfL, valueL, aLocation);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execReadline(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfF = container.resolveObject("F");
		IGFileDriver driverF = (IGFileDriver) aRuntime.getDriver(intfF).getTargetDriver();

		IGObject intfL = container.resolveObject("L");

		IGStaticValue ret = driverF.readLine(aSub, aRuntime, aLocation, aErrorMode, aReport);

		aRuntime.setObjectValue(intfL, ret, aLocation);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execWriteline(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfF = container.resolveObject("F");
		IGObjectDriver driverF = aRuntime.getDriver(intfF).getTargetDriver();

		IGObject intfL = container.resolveObject("L");
		IGStaticValue valueL = aRuntime.getObjectValue(intfL);

		if (driverF.getValue(aLocation).getId().equals("STD_OUTPUT")) {
			ZamiaLogger.getInstance().info(valueL.toString());
		} else {
			((IGFileDriver) driverF).writeLine(valueL, aLocation);
		}

		//return empty line
		valueL = IGFileDriver.newLineBuilder(
				0, container.findStringType(), aRuntime, aLocation, aErrorMode, aReport).buildConstant();
		aRuntime.setObjectValue(intfL, valueL, aLocation);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execEndfile(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport)
			throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfF = container.resolveObject("F");
		IGFileDriver driverF = (IGFileDriver) aRuntime.getDriver(intfF).getTargetDriver();

		boolean isEOF = driverF.isEOF(aSub, aLocation);

		IGStaticValue ret = isEOF ? container.findTrueValue() : container.findFalseValue();

		aRuntime.push(ret);

		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execFileOpen(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport) throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfF = container.resolveObject("F");
		IGFileDriver driverF = (IGFileDriver) aRuntime.getDriver(intfF).getTargetDriver();

		IGObject intfN = container.resolveObject("EXTERNAL_NAME");
		IGStaticValue valueN = aRuntime.getObjectValue(intfN);

		IGObject intfK = container.resolveObject("FILE_OPEN_KIND");
		IGStaticValue valueK = aRuntime.getObjectValue(intfK);

		IGObject intfS = container.resolveObject("STATUS");
		IGType fost = intfS != null ? intfS.getType() : container.findFileOpenStatusType();

		String openMode = valueK.getId();
		IGObject.OIDir mode = NONE;
		if (openMode.equals("READ_MODE")) {
			mode = IN;
		} else if (openMode.equals("WRITE_MODE")) {
			mode = OUT;
		} else if (openMode.equals("APPEND_MODE")) {
			mode = APPEND;
		}

		IGStaticValue status = driverF.openFile(valueN, mode, fost, aLocation);

		if (intfS == null) {

			IGStaticValue openOk = fost.findEnumLiteral("OPEN_OK");
			IGStaticValue errorName = fost.findEnumLiteral("NAME_ERROR");
			IGStaticValue errorStatus = fost.findEnumLiteral("STATUS_ERROR");

			if (status.equalsValue(openOk)) {
				/* OK, do nothing */
			} else if (status.equalsValue(errorName)) {
				throw new ZamiaException("Failed to open VHDL file \"" + valueN + "\" in " + openMode + "; no such file or directory.", aLocation);
			} else if (status.equalsValue(errorStatus)) {
				throw new ZamiaException("Cannot open file \"" + valueN + "\"; it is already open.", aLocation);
			} else {
				throw new ZamiaException("Cannot open file \"" + valueN + "\" in " + openMode + ".", aLocation);
			}
		} else {
			aRuntime.setObjectValue(intfS, status, aLocation);
		}


		return ReturnStatus.CONTINUE;
	}

	private static ReturnStatus execFileClose(IGSubProgram aSub, IGInterpreterRuntimeEnv aRuntime, SourceLocation aLocation, ASTErrorMode aErrorMode, ErrorReport aReport) throws ZamiaException {

		IGContainer container = aSub.getContainer();
		IGObject intfF = container.resolveObject("F");
		IGFileDriver driverF = (IGFileDriver) aRuntime.getDriver(intfF).getTargetDriver();

		driverF.close();

		return ReturnStatus.CONTINUE;
	}

}
