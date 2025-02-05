/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.iteration;

import java.util.Iterator;

public abstract class AbstractIterator<T> implements Iterator<T>{
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}