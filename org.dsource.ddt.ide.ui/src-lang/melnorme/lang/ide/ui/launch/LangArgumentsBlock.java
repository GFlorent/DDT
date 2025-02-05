/*******************************************************************************
 * Copyright (c) 2005, 2012 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     IBM Corporation
 *     Bruno Medeiros - lang modifications
 *******************************************************************************/
package melnorme.lang.ide.ui.launch;

import melnorme.lang.ide.ui.LangUIMessages;
import melnorme.util.swt.ControlAccessibleListener;
import melnorme.util.swt.components.WidgetFieldComponent;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LangArgumentsBlock extends WidgetFieldComponent<String> {
	
	protected Label fPrgmArgumentsLabel;
	protected Text fPrgmArgumentsText;
	protected Button fArgumentVariablesButton;
	
	@Override
	public Group createComponent(Composite parent) {
		Group topControl = new Group(parent, SWT.NONE);
		topControl.setText(LangUIMessages.LangArgumentsTab_Program_Arguments);
		topControl.setLayout(new GridLayout());
		
		createContents(topControl);
		return topControl;
	}
	
	@Override
	protected void createContents(Composite topControl) {
		fPrgmArgumentsText = createFieldText(topControl, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		fPrgmArgumentsText.getAccessible().addAccessibleListener(
			new AccessibleAdapter() {
				@Override
				public void getName(AccessibleEvent e) {
					e.result = LangUIMessages.LangArgumentsTab_Program_Arguments;
				}
			});
		
		fPrgmArgumentsText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(40, 100).create());
		fArgumentVariablesButton = AbstractLaunchConfigurationTabExt.
				createVariablesButton(topControl, LangUIMessages.LangArgumentsTab_Variables, fPrgmArgumentsText); 
		fArgumentVariablesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		// need to strip the mnemonic from buttons:
		ControlAccessibleListener.addControlAccessibleListener(fArgumentVariablesButton, 
				fArgumentVariablesButton.getText());
	}
	
	/**
	 * Returns the string in the text widget, or <code>null</code> if empty.
	 * 
	 * @return text or <code>null</code>
	 */
	protected static String getAttributeValueFrom(Text text) {
		String content = text.getText().trim();
		// Bug #131513 - eliminate Windows \r line delimiter
		content = content.replaceAll("\r\n", "\n");  //$NON-NLS-1$//$NON-NLS-2$
		if (content.length() > 0) {
			return content;
		}
		return null;
	}
	
	@Override
	public String getFieldValue() {
		return getAttributeValueFrom(fPrgmArgumentsText);
	}
	
	@Override
	public void setFieldValue(String value) {
		fPrgmArgumentsText.setText(value);
	}
	
}