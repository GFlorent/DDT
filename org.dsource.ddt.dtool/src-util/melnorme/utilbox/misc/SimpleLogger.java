/*******************************************************************************
 * Copyright (c) 2007, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.misc;

/** 
 * Very simple and basic logging helper. 
 */
public class SimpleLogger {

	public static boolean masterLoggEnabled = true;

	protected boolean enabled = true;
	
	public SimpleLogger(boolean enabled) {
		this.enabled = enabled;
	}
	
	public SimpleLogger() {
		this(true);
	}
	
	public SimpleLogger(String propertyKey) {
		this();
		enabled = System.getProperty("SimpleLogger." + propertyKey) != null;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void println(Object... objs) {
		for(Object obj : objs)
			print(obj);
		println();
	}
	
	public void print(Object string) {
		if (masterLoggEnabled && enabled)
			System.out.print(string);
	}
	
	public void println() {
		if (masterLoggEnabled && enabled)
			System.out.println();
	}

}