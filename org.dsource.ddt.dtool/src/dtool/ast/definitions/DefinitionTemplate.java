package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ChainedIterator;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.IScope;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * Definition of a template.
 * http://dlang.org/template.html#TemplateDeclaration
 * 
 * (Technically not allowed as statement, but parse so anyways.)
 */
public class DefinitionTemplate extends CommonDefinition implements IScope, IDeclaration, IStatement {
	
	public final boolean isMixin;
	public final ArrayView<TemplateParameter> tplParams;
	public final Expression tplConstraint;
	public final DeclBlock decls;
	
	public final boolean wrapper;
	
	public DefinitionTemplate(Token[] comments, boolean isMixin, ProtoDefSymbol defId, 
		ArrayView<TemplateParameter> tplParams, Expression tplConstraint, DeclBlock decls) {
		super(comments, defId);
		this.isMixin = isMixin;
		this.tplParams = parentize(tplParams);
		this.tplConstraint = parentize(tplConstraint);
		this.decls = parentize(decls);
		
		this.wrapper = false; // TODO: determine this
		if(wrapper) {
			assertTrue(this.decls.nodes.size() == 1);
			assertTrue(decls.nodes.get(0) instanceof DefUnit);
		}
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_TEMPLATE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, decls);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isMixin, "mixin ");
		cp.append("template ");
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append(decls);
	}
	
	public static void tplConstraintToStringAsCode(ASTCodePrinter cp, Expression tplConstraint) {
		if(tplConstraint instanceof MissingParenthesesExpression) {
			cp.append("if", tplConstraint);
		} else {
			cp.append("if(", tplConstraint, ")");
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}
	
	@Override
	public IScope getMembersScope(IModuleResolver moduleResolver) {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO: template super scope
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public Iterator<? extends IASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		// BUG in accessing decls
		// TODO: check if in a template invocation
		// TODO: test this more, redo
		if(wrapper) {
			// Go straight to members of the inner decl
			IScope scope = ((DefUnit)decls.nodes.get(0)).getMembersScope(moduleResolver);
			Iterator<? extends IASTNode> tplIter = tplParams.iterator();
			return ChainedIterator.create(tplIter, scope.getMembersIterator(moduleResolver));
		}
		return ChainedIterator.create(tplParams.iterator(), decls.nodes.iterator());
	}
	
}