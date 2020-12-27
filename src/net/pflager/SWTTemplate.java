package net.pflager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class SWTTemplate {
	
	private static void paint(PaintEvent paintEvent) {
		GC gc = paintEvent.gc;
		
		// create a formula
		TeXFormula formula = new TeXFormula("\\beta_{0}(1-t)^3 + \\beta_{1}3t(1-t)^2 + \\beta_{2}3t^2(1-t) + \\beta_{3}t^3");

//		// render the formula to an icon of the same size as the formula.
//		TeXIcon icon = formula
//				.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
//		
//		// insert a border 
//		icon.setInsets(new Insets(5, 5, 5, 5));
//
//		// now create an actual image of the rendered equation
//		BufferedImage image = new BufferedImage(icon.getIconWidth(),
//				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g2 = image.createGraphics();
//		g2.setColor(Color.white);
//		g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
//		JLabel jl = new JLabel();
//		jl.setForeground(new Color(0, 0, 0));
//		icon.paintIcon(jl, g2, 0, 0);
//		// at this point the image is created, you could also save it with ImageIO
		
		// now draw it to the screen
//		gc.drawImage(image,0,0);
	}
	
	private static void resize(int width, int height) {
		System.out.println("resize(" + width + ", " + height + ")");
	}
	
	public static void main(String[] args) {
		// Screen aspect ratio is assumed to be exactly proportionate to pixel size. i.e. pixels are exactly square.
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("SWTTemplate");

		shell.addPaintListener(event -> paint(event));
		
		shell.addListener(SWT.Resize, event -> resize(shell.getBounds().width, shell.getBounds().height));
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
