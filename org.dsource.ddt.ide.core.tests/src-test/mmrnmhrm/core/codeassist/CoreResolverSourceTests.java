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
package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

import dtool.resolver.ResolverSourceTests;
import dtool.sourcegen.AnnotatedSource;

public abstract class CoreResolverSourceTests extends ResolverSourceTests {
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
	}
	
	public CoreResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	protected static HashMap<String, IScriptProject> fixtureProjects = new HashMap<>();
	
	protected ISourceModule sourceModule;
	protected IModuleSource moduleSource;
	
	
	@Override
	public void setupTestProject(String moduleName, String projectFolderName, AnnotatedSource testCase) {
		try {
			setupTestProject_do(moduleName, projectFolderName, testCase);
		} catch(CoreException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void setupTestProject_do(String explicitModuleName, String projectFolderName, AnnotatedSource testCase)
		throws CoreException, IOException {
		assertTrue(parseResult == null); // sourceModule is used instead of this.
		
		IScriptProject scriptProject = fixtureProjects.get(projectFolderName /*Can be null*/);
		
		if(scriptProject == null) {
			File projectDir = projectFolderName == null ? null : new File(file.getParent(), projectFolderName);
			scriptProject = TestsWorkspaceModuleResolver.createTestsWorkspaceProject(projectDir);
			fixtureProjects.put(projectFolderName, scriptProject);
		}
		
		String moduleName = nullToOther(explicitModuleName, DEFAULT_MODULE_NAME);
		mr = new TestsWorkspaceModuleResolver(scriptProject, moduleName, testCase.source);
		
		sourceModule = (ISourceModule) DLTKCore.create(getModuleResolver().customFile);
		assertTrue(sourceModule != null && sourceModule.exists());
		
		IModelElement modelElement = projectFolderName == null ? null : sourceModule;
		moduleSource = new ModuleSource(explicitModuleName, modelElement, testCase.source);
	}
	
	protected TestsWorkspaceModuleResolver getModuleResolver() {
		return (TestsWorkspaceModuleResolver) mr; 
	}
	
	protected IScriptProject getScriptProject() {
		return getModuleResolver().scriptProject;
	}
	
	@Override
	public void processResolverTestMetadata(AnnotatedSource testCase) {
		assertTrue(sourceModule != null && sourceModule.exists());
		super.processResolverTestMetadata(testCase);
	}
	
}