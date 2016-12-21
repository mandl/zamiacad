/* 
 * Copyright 2009-2011 by the authors indicated in the @author tags. 
 * All rights reserved. 
 * 
 * See the LICENSE file for details.
 * 
 * Created by Guenter Bartsch on Sep 8, 2009
 */
package org.zamia.cli.jython;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.zamia.ExceptionLogger;
import org.zamia.FSCache;
import org.zamia.ZamiaException;
import org.zamia.ZamiaLogger;
import org.zamia.ZamiaProject;

/**
 * Sets up a Jython interpreter for a given project, runs init + user tcl scripts
 * Will inject zamia-specific python commands into the interpreter
 * 
 * @author Guenter Bartsch
 *
 */

public class ZCJInterpreter {

	protected final static ZamiaLogger logger = ZamiaLogger.getInstance();

	protected final static ExceptionLogger el = ExceptionLogger.getInstance();

	private PythonInterpreter fInterp;

	private ZamiaProject fZPrj;

	public ZCJInterpreter(ZamiaProject aZPrj,String jythonPath) throws ZamiaException {

		fZPrj = aZPrj;

		// FIXME StdChannel.setOut(LoggerPrintStream.getInstance());
		
		if (jythonPath != null)
		{
		Properties props = new Properties();
		props.put("python.home",jythonPath);
		props.put("python.console.encoding", "UTF-8"); // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
		props.put("python.security.respectJavaAccessibility", "false"); //don't respect java accessibility, so that we can access protected members on subclasses
		props.put("python.import.site","false");

		Properties preprops = System.getProperties();
		PythonInterpreter.initialize(preprops, props, new String[0]);
		}
				
		fInterp = new PythonInterpreter();

		fInterp.set("project", fZPrj);
		

		// run boot.py to set up basic zamia-specific commands

		evalFile("builtin:/python/boot.py");

	}

	public synchronized void evalFile(String aPath) throws ZamiaException {

		//if (System.getenv("PYTHONPATH") == null) {
		//	logger.info("System variable PYTHONPATH is not specified, python will not work");
		//	return;
		//}

		if (aPath.startsWith("builtin:")) {
			String uri = aPath.substring(8);

			InputStream in = null;
			BufferedReader rin = null;

			try {
				in = FSCache.getInstance().getClass().getResourceAsStream(uri);

				if (in == null) {
					throw new ZamiaException("Jython interpreter: evalFile(): '" + uri + "' not found");
				}

				rin = new BufferedReader(new InputStreamReader(in));

				String line = null;
				StringBuilder buf = new StringBuilder();
				while ((line = rin.readLine()) != null) {
					buf.append(line + "\n");
				}

				fInterp.exec(buf.toString());

			} catch (IOException e) {
				el.logException(e);
			} catch (PyException e) {
				el.logException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						el.logException(e);
					}
				}
				if (rin != null) {
					try {
						rin.close();
					} catch (IOException e) {
						el.logException(e);
					}
				}
			}

		} else {
			fInterp.execfile(aPath);
		}
	}

	public synchronized void eval(String aScript) {
		fInterp.exec(aScript);
	}

	public synchronized boolean hasCommand(String aCmdName) {
		// FIXME return fInterp.getCommand(aCmdName) != null;
		return false;
	}

	public void setObject(String name, Object aObject) {
		fInterp.set(name, aObject);
	}

	public <T> T getObject(String name, Class<T> aClass) {
		PyObject pyObject = fInterp.get(name);
		if (pyObject == null) {
			return null;
		}
		return (T) pyObject.__tojava__(aClass);
	}

	public ZamiaProject getZprj() {
		return fZPrj;
	}
}
