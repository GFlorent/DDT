package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ExpStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementExp extends Statement {
	
	public Resolvable exp;

	public StatementExp(ExpStatement element, ASTConversionContext convContext) {
		if(element.hasNoSourceRangeInfo() && element.exp != null)
			setSourceRange(element.exp);
		else
			convertNode(element);
		
		this.exp = ExpressionConverter.convert(element.exp, convContext);
	}
	
	public StatementExp(Resolvable exp) {
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
