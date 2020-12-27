package net.pflager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DemoStackLayout2ButtonsTopTransparent {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		final Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		final StackLayout layout = new StackLayout();
		parent.setLayout(layout);

		Button topButton = new Button(parent, SWT.PUSH);
		topButton.setBackground(display.getSystemColor(SWT.COLOR_TRANSPARENT));
		topButton.setText("topButton");
		layout.topControl = topButton;
		
		Button bottomButton = new Button(parent, SWT.PUSH);		
		bottomButton.setText("bottomButton");

		Button b = new Button(shell, SWT.PUSH);
		b.setText("Show Next Button");
		
		b.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
//				parent.layout();
			}
		});

		shell.open();
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
