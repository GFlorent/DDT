package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ThrowStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementThrow extends Statement {

	public Resolvable exp;

	public StatementThrow(ThrowStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(elem.exp, convContext);
	}
	
	public StatementThrow(Resolvable exp) {
		this.exp = exp;
		
		if (this.exp != null)
			this.exp.setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
