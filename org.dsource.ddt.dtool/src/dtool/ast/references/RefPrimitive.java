package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.parser.IToken;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

public class RefPrimitive extends NamedReference {
	
	public final IToken primitive;
	
	public RefPrimitive(IToken primitiveToken) {
		this.primitive = assertNotNull(primitiveToken);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_PRIMITIVE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(primitive);
	}
	
	@Override
	public String getCoreReferenceName() {
		return primitive.getSourceValue();
	}
	
	@Override
	public boolean isMissingCoreReference() {
		return false;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(getCoreReferenceName(), this, this.getStartPos(), 
			findOneOnly, moduleResolver);
		/*BUG here*/
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInFullLexicalScope(this, search);
	}
	
}