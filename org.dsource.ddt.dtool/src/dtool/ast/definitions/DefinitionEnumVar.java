/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;


import static dtool.util.NewUtils.assertCast;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.IInitializer;
import dtool.ast.statements.IStatement;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.INonScopedContainer;
import dtool.util.ArrayView;

/**
 * A definition of an enum variable (aka manifest constant):
 * This is different from normal variables as some additional syntaxes are allwod, 
 * such as template parameters
 */
public class DefinitionEnumVar extends ASTNode implements IDeclaration, IStatement, INonScopedContainer {
	
	public final ArrayView<DefinitionEnumVarFragment> defFragments;

	public DefinitionEnumVar(ArrayView<DefinitionEnumVarFragment> defFragments) {
		this.defFragments = parentize(defFragments);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ENUM_VAR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defFragments);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.appendList(defFragments, ", ", false);
		cp.append(";");
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return IteratorUtil.nonNullIterator(defFragments);
	}
	
	public static class DefinitionEnumVarFragment extends DefUnit {
		
		public final ArrayView<TemplateParameter> tplParams; // Since 2.064
		public final IInitializer initializer;
		
		public DefinitionEnumVarFragment(ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams, 
				IInitializer initializer) {
			super(defId);
			this.tplParams = parentize(tplParams);
			this.initializer = parentize(initializer);
		}
		
		@Override
		public DefinitionEnumVar getParent_Concrete() {
			return assertCast(getParent(), DefinitionEnumVar.class);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_ENUM_VAR_FRAGMENT;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
			acceptVisitor(visitor, tplParams);
			acceptVisitor(visitor, initializer);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
			cp.appendList("(", tplParams, ",", ") ");
			cp.append(" = ", initializer);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			DefinitionVariable.resolveSearchInMembersScopeForVariable(search, null, initializer);
		}
		
	}
	
}