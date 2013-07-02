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
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.SourceRange;


/**
 * An extension of the simple {@link Token} with additional info.
 * Stores information about some preceding sub-channel tokens.
 * Can be a {@link MissingLexElement}, a synthetic token that represents a missing expected token.
 */
public abstract class BaseLexElement implements IToken {
	
	public final DeeTokens type;
	public final int startPos;
	public final String source;
	/** This array stores some (but not all) preceding subchannel tokens.  */
	protected final Token[] relevantPrecedingSubChannelTokens;
	
	public BaseLexElement(DeeTokens type, String source, int startPos, Token[] ignoredPrecedingTokens) {
		this.type = assertNotNull(type);
		if(type.hasSourceValue()) {
			assertEquals(type.getSourceValue(), source);
		}
		this.source = assertNotNull(source);
		this.startPos = startPos;
		this.relevantPrecedingSubChannelTokens = ignoredPrecedingTokens;
		assertTrue(ignoredPrecedingTokens == null || ignoredPrecedingTokens.length > 0);
	}
	
	@Override
	public final String getSourceValue() {
		return source;
	}
	
	@Override
	public final int getStartPos() {
		return startPos;
	}
	
	public final int getLength() {
		return source.length();
	}
	
	@Override
	public final int getEndPos() {
		return startPos + source.length();
	}
	
	@Override
	public final SourceRange getSourceRange() {
		return new SourceRange(getStartPos(), getLength());
	}
	
	public final boolean isEOF() {
		return type == DeeTokens.EOF;
	}
	
	
	public final int getFullRangeStartPos() {
		if(relevantPrecedingSubChannelTokens != null && relevantPrecedingSubChannelTokens.length > 0) {
			return relevantPrecedingSubChannelTokens[0].getStartPos();
		}
		return getStartPos();
	}
	
	public static final Token[] EMPTY_ARRAY = new Token[0];
	
	public Token[] getRelevantPrecedingSubChannelTokens() {
		return relevantPrecedingSubChannelTokens == null ? EMPTY_ARRAY : relevantPrecedingSubChannelTokens;
	}
	
	public abstract boolean isMissingElement();
	
	public abstract ParserError getMissingError();
	
	
	public String toStringRangePrefix() {
		return getFullRangeStartPos() != getStartPos() ? "【"+getFullRangeStartPos()+"】" : "";
	}
	
	@Override
	public String toString() {
		return toStringRangePrefix() + type +"►"+ source;
	}
}