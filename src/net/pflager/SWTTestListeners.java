package net.pflager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTTestListeners {
	
	private static void paint() {
		System.out.println("paint()");
	}
	
	private static void resize(int width, int height) {
		System.out.println("resize(" + width + ", " + height + ")");
	}
	
	public static void main(String[] args) {
		// Screen aspect ratio is assumed to be exactly proportionate to pixel size. i.e. pixels are exactly square.
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("SWTTestListeners");

		shell.addPaintListener(event -> paint());
		
		shell.addListener(SWT.Resize, event -> resize(shell.getBounds().width, shell.getBounds().height));
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
