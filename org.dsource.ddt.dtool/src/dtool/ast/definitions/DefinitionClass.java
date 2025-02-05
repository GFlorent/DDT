package dtool.ast.definitions;

import static melnorme.utilbox.misc.IteratorUtil.nonNullIterable;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.parser.Token;
import dtool.project.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.LanguageIntrinsics;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {
	
	public final ArrayView<Reference> baseClasses;
	public final boolean baseClassesAfterConstraint;
	
	public DefinitionClass(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, ArrayView<Reference> baseClasses, boolean baseClassesAfterConstraint, 
		IAggregateBody aggrBody) 
	{
		super(comments, defId, tplParams, tplConstraint, aggrBody);
		this.baseClasses = parentize(baseClasses);
		this.baseClassesAfterConstraint = baseClassesAfterConstraint;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CLASS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "class ");
	}
	
	public void classLikeToStringAsCode(ASTCodePrinter cp, String keyword) {
		cp.append(keyword);
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		if(baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendList(": ", baseClasses, ",", " ");
		if(!baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append(aggrBody, "\n");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		if(baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, baseClasses);
		if(!baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInHierarchyScope(search);
		LanguageIntrinsics.d_2_063_intrinsics.objectPropertiesScope.resolveSearchInScope(search);
	}
	
	public void resolveSearchInHierarchyScope(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInScope(search, getBodyScope());
		if(getBodyScope() == null) {
			// Even without a body scope, we can resolve in super scopes
			resolveSearchInSuperScopes(search);
		}
	}
	
	public void resolveSearchInSuperScopes(CommonDefUnitSearch search) {
		IModuleResolver mr = search.getModuleResolver();
		
		for(Reference baseclass : CoreUtil.nullToEmpty(baseClasses)) {
			INamedElement baseClassElem = baseclass.findTargetDefElement(mr);
			if(baseClassElem == null)
				continue;
			
			if(baseClassElem instanceof DefinitionClass) {
				DefinitionClass baseClassDef = (DefinitionClass) baseClassElem;
				baseClassDef.resolveSearchInHierarchyScope(search);
			}
		}
	}
	
	public INamedElement resolveSuperClass(IModuleResolver mr) {
		
		for (Reference baseClassRef : nonNullIterable(baseClasses)) {
			INamedElement baseClass = baseClassRef.findTargetDefElement(mr);
			
			if(baseClass.getArcheType() == EArcheType.Interface) {
				continue;
			}
			if(baseClass instanceof DefinitionClass) {
				return baseClass;
			}
		}
		// TODO test implicit object reference
		return LanguageIntrinsics.d_2_063_intrinsics.object_reference.findTargetDefElement(mr);
	}
	
}
