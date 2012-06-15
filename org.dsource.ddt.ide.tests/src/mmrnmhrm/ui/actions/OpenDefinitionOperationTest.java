/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package mmrnmhrm.ui.actions;

import junit.framework.Assert;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.SampleNonDeeProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.ui.editor.DeeEditor;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

public class OpenDefinitionOperationTest extends BaseDeeUITest {
	
	private static final String TEST_SRCFILE = ITestResourcesConstants.TR_SAMPLE_SRC1 + "/testGoToDefOp.d";
	private static final String TEST_SRC_TARGETFILE = ITestResourcesConstants.TR_SAMPLE_SRC3 +"/pack/sample.d";
	private static final String TEST_OUTSRCFILE = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL + "/testGoToDefOp.d";
	
	protected IFile file; 
	protected IEditorPart editor;
	protected ITextEditor srcEditor;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		OperationsManager.get().unitTestMode = true;
	}
	
	@Before
	public void setUp() throws Exception {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, TEST_SRCFILE);
	}
	
	private void setupWithFile(IProject project, String path) throws PartInitException, CoreException {
		file = project.getFile(path);
		assertTrue(file.exists());
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		srcEditor = (ITextEditor) editor;
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test1() {
		// find target in same file
		doTest(123, IStatus.OK, file.getProject(), TEST_SRCFILE); 
	}
	
	@Test
	public void test2() {
		// not found
		doTest(135, IStatus.WARNING, file.getProject(), TEST_SRCFILE); 
	}
	
	@Test
	public void test3() {		
		// already a def
		doTest(54, IStatus.INFO, file.getProject(), TEST_SRCFILE); 
	}
	
	@Test
	public void test4() {
		// find target in other file
		doTest(157, IStatus.OK, file.getProject(), TEST_SRC_TARGETFILE); 
	}
	
	@Test
	public void testOutside() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(123, IStatus.OK, project, TEST_OUTSRCFILE);
	}
	
	@Test
	public void testOutside2() throws CoreException {
		IProject project = SampleMainProject.scriptProject.getProject();
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(157, IStatus.OK, project, TEST_SRC_TARGETFILE);
	}
	
	
	@Test
	public void testReallyOutside() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(123, IStatus.OK, project, TEST_OUTSRCFILE);
	}
	
	@Test
	public void testReallyOutside2() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(157, IStatus.WARNING, project, TEST_OUTSRCFILE);
	}
	
	private void doTest(int offset, int result, IProject project, String editorFile) {
		EditorUtil.setEditorSelection(srcEditor, offset, 0);
		GoToDefinitionHandler.executeChecked(srcEditor, true);
		assertTrue(OperationsManager.get().opResult == result, "Got result: " + result);
		assertCurrentEditorIsEditing(project.getFullPath(), editorFile);
	}
	
	private void assertCurrentEditorIsEditing(IPath prjpath, String targetpath) {
		DeeEditor deeEditor;
		deeEditor = (DeeEditor) WorkbenchUtils.getActivePage().getActiveEditor();
		IFile editorFile = ((FileEditorInput) deeEditor.getEditorInput()).getFile();
		IPath path = editorFile.getFullPath();
		Assert.assertEquals(path, prjpath.append(targetpath));
	}
	
	
}
