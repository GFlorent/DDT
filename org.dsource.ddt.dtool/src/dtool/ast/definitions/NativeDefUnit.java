package dtool.ast.definitions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.resolver.IDefUnitReference;
import dtool.resolver.INamedScope;
import dtool.resolver.IScope;
import dtool.resolver.api.IModuleResolver;

public abstract class NativeDefUnit extends SyntheticDefUnit implements INativeDefUnit, IScope {
	
	/** A module like class, contained all native defunits. */
	public static class NativesScope implements IScope, INamedScope {
		
		public NativesScope() {
		}
		
		@Override
		public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
			// TODO: put intrinsics here?
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		
		@Override
		public INamedScope getModuleScope() {
			return this;
		}
		
		@Override
		public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
			return null;
		}
		
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}
		
		@Override
		public String toString() {
			return "<natives>";
		}
		
		@Override
		public String toStringAsElement() {
			return toString();
		}
	}
	
	private static final class UndeterminedReference implements IDefUnitReference {
		@Override
		public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
			return null;
		}
		@Override
		public String toStringAsElement() {
			return "<unknown>";
		}
	}
	
	public static final NativesScope nativesScope = new NativesScope();
	//public static final DefUnit unknown = new NativesScope();
	public static final IDefUnitReference nullReference = new UndeterminedReference();
	
	public NativeDefUnit(String name) {
		super(name);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	
	@Override
	public abstract IScope getMembersScope(IModuleResolver moduleResolver);
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	//public abstract IScope getSuperScope();
	
	@Override
	public INamedScope getModuleScope() {
		return nativesScope;
	}
	
}