package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.util.swt.SWTTestUtils;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.junit.After;
import org.junit.Test;

import dtool.resolver.CompareDefUnits;
import dtool.tests.ref.cc.CodeCompletion_LookupTest;

public class ContentAssist_InteractionTest extends ContentAssistUI_CommonTest {
	
	public ContentAssist_InteractionTest() {
		super(SampleMainProject.getFile("src-ca/testCodeCompletion.d"));
	}
	
	@After
	public void editorCleanup() {
		editor.close(false);
	}
	
	protected void simulateCursorLeft() {
		simulateCharacterPress((char)0, SWT.ARROW_LEFT);
	}
	
	protected void simulateCharacterPress(char character, int keycode) {
		// TODO: we should use SWTbot instead
		Event event = new Event();
		event.character = character;
		event.keyCode = keycode;
		event.type = SWT.KeyDown;
		editor.getViewer().getTextWidget().notifyListeners(SWT.KeyDown, event);
		event.type = SWT.KeyUp;
		editor.getViewer().getTextWidget().notifyListeners(SWT.KeyUp, event);
	}
	
	@Test
	public void testMoveCursorBeforeStartOffset() throws Exception { testMoveCursorBeforeStartOffset$(); }
	public void testMoveCursorBeforeStartOffset$() throws Exception {
		if(TRUE()) 
			return; // This test is disable because this functionality was removed, not even JDT has it
		
		ISourceViewer viewer = editor.getViewer();
		
		int ccOffset = getMarkerStartPos("/+@CC.I+/");
		viewer.setSelectedRange(ccOffset, 0);
		assertTrue(viewer.getSelectedRange().x == ccOffset);
		
		CompletionWatcher caWatcher = setupAndActivateContentAssist(viewer, ccOffset);
		
		simulateCursorLeft(); // at start of defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset - 1);
		assertTrue(caWatcher.active == true);
		
		
		simulateCursorLeft(); // before defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset - 2);
		
		assertTrue(caWatcher.active == false); // Assert content Assist closed
	}
	
	protected CompletionWatcher setupAndActivateContentAssist(ISourceViewer viewer, int ccOffset) {
		ContentAssistant ca = getContentAssistant(editor);
		CompletionWatcher caWatcher = new CompletionWatcher();
		ca.addCompletionListener(caWatcher);
		
		viewer.revealRange(ccOffset, 10);
		invokeContentAssist(editor, ccOffset);
		assertTrue(caWatcher.active == true);
		return caWatcher;
	}
	
	protected class CompletionWatcher implements ICompletionListener {
		protected boolean active = false;
		protected ContentAssistEvent lastEvent;
		@Override
		public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		}
		
		@Override
		public void assistSessionStarted(ContentAssistEvent event) {
			active = true;
			lastEvent = event;
		}
		
		@Override
		public void assistSessionEnded(ContentAssistEvent event) {
			active = false;
		}
	}
	
	@Test
	public void testFilteringProposals() throws Exception { testFilteringProposals$(); }
	public void testFilteringProposals$() throws Exception {
		ISourceViewer viewer = editor.getViewer();
		
		int ccOffset = getMarkerStartPos("/+@CC.I+/");
		viewer.setSelectedRange(ccOffset, 0);
		
		ContentAssistant ca = getContentAssistant(editor);
		CompletionWatcher caWatcher = setupAndActivateContentAssist(viewer, ccOffset);
		
		ICompletionProposal[] proposals;
		proposals = getProposals(ca);
		CompareDefUnits.checkResults(/*1,*/ 
			proposalsToDefUnitResults(proposals),
			CodeCompletion_LookupTest.EXPECTED_IN_TEST_f);
		
		simulateCharacterPress('o', 'o'); // at start of defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset + 1);
		assertTrue(caWatcher.active == true && isProposalPopupActive(ca) == true);
		
		proposals = getProposals(ca);
		CompareDefUnits.checkResults(/*2,*/ 
			proposalsToDefUnitResults(proposals),
			CodeCompletion_LookupTest.EXPECTED_IN_TEST_fo);
		
		simulateCharacterPress('z', 'z'); // before defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset + 2);
		
		proposals = getProposals(ca);
		assertTrue(proposals == null);
		
		assertTrue(caWatcher.active == false && isProposalPopupActive(ca) == false);
	}
	
}