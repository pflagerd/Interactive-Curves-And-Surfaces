package net.pflager;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Curve2_3 {
	
	static double [][] pointArray = null;
	static int [] intArray = null;
	static double absMaxX = 0;
	static double absMaxY = 0;
	static double maxX = Double.NEGATIVE_INFINITY;
	static double maxY = Double.NEGATIVE_INFINITY;
	static double minX = Double.POSITIVE_INFINITY;
	static double minY = Double.POSITIVE_INFINITY;
	static int borderPixels;
	static int xOriginPixel;
	static int yOriginPixel;
	static int width;
	static int height;

	
	/**
	 * Draw a sequence of lines by connecting an array of given points.
	 * 
	 * @param pointArray an array of (x, y) values
	 */
	static void drawPolyLine(double[][] pointArray) {
		intArray = new int[pointArray.length * 2];
		
		int i = 0;
		for (double[] point : pointArray) {
			intArray[i] = (int)(point[0] * width / absMaxX) + xOriginPixel;
			intArray[i + 1] = (int)(yOriginPixel - point[1] * height / absMaxY);
			i += 2;
		}
	}
	
	public static void main(String[] args) {
		// Screen aspect ratio is assumed to be exactly proportionate to pixel size. i.e. pixels are exactly square.
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Curve 2-3");
		System.out.println("shell.getLocation() -> " + shell.getLocation());
//		Rectangle clientAreaRectangle = shell.getClientArea();
		height = shell.getClientArea().height / 2;
		width = shell.getClientArea().width / 2;
		System.out.println("shell.getClientArea() == " + shell.getClientArea());
		System.out.println("shell.getForeground() == " + shell.getForeground());
		double borderPercent = 2;
		borderPixels = (int)(borderPercent * (shell.getClientArea().height < shell.getClientArea().width ? shell.getClientArea().height : shell.getClientArea().width) / 100.0);
		xOriginPixel = shell.getClientArea().width / 2;
		yOriginPixel = shell.getClientArea().height / 2;
		
		double increment = 0.1;
		double start = -1.0;
		double stop = 1.0;
		int points = (int)((stop - start) / increment);
		
		pointArray = new double[(int)((stop - start) / increment)][2];
		
		// x(t) = 4t^3 - 9t^2 + 6t 
		// y(t) = 4t^3 - 3t^2
		for (int i = 0; i < points; i++) {
			double t = start + i * increment;
			
			double x = 4.0 * t * t * t - 9.0 * t * t + 6.0 * t;
			if (maxX == Double.NEGATIVE_INFINITY) {
				maxX = x;
			} else {
				maxX = x > maxX ? x : maxX;
			}
			
			if (minX == Double.POSITIVE_INFINITY) {
				minX = x;
			} else {
				minX = x < minX ? x : minX;
			}
			
			double y = 4.0 * t * t * t - 3.0 * t * t;
			if (maxY == Double.NEGATIVE_INFINITY) {
				maxY = y;
			} else {
				maxY = y > maxY ? y : maxY;
			}
			
			if (minY == Double.POSITIVE_INFINITY) {
				minY = y;
			} else {
				minY = y < minY ? y : minY;
			}
			System.out.println("(" + x + ", " + y + ")");

			pointArray[i] = new double[] {x, y};
		}
		absMaxX = Math.abs(minX) < maxX ? maxX : Math.abs(minX);
		absMaxY = Math.abs(minY) < maxY ? maxY : Math.abs(minY);
		System.out.println("absMaxX == " + absMaxX + ", absMaxY == " + absMaxY);
		drawPolyLine(pointArray);

		shell.addPaintListener(event -> {
			GC gc = event.gc;
			Color foreground = gc.getForeground();
			System.out.println("foreground == " + foreground);
//			gc.drawRectangle(10, 10, 110, 110);
//			gc.drawRectangle(rect.x + 10, rect.y + 10, rect.width - 20, rect.height - 20);
//			gc.drawString("Hello_world", rect.x + 20, rect.y + 20);
			
			// Draw Y-axis line
			Color white = new Color(display, 255, 255, 255, 255);
			gc.setForeground(white);
			gc.setLineWidth(3);
			gc.drawLine(xOriginPixel, borderPixels, xOriginPixel, 2 * height - borderPixels);
			gc.drawLine(borderPixels, yOriginPixel, width * 2 - borderPixels, yOriginPixel);
			
			// Draw X-axis line
			
			Color black = new Color(display, 0, 0, 0, 255);
			gc.setForeground(black);
			gc.setLineWidth(1);
			gc.drawPolyline(intArray);			
			
			black.dispose();
			white.dispose();
		});
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
