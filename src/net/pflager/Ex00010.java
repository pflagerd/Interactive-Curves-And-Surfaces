package net.pflager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Ex00010 {

	static double[][] pointArray = null;
	static int[] intArray = null;
	static double absMaxX = 0;
	static double absMaxY = 0;
	static double maxX = Double.NEGATIVE_INFINITY;
	static double maxY = Double.NEGATIVE_INFINITY;
	static double minX = Double.POSITIVE_INFINITY;
	static double minY = Double.POSITIVE_INFINITY;
	static int borderPixelWidth;
	static int xOriginPixel;
	static int yOriginPixel;
	static int width;
	static int height;

	static double[] originPercentOffset = new double[] { 17.5, 20 };
	static int[] originPixelOffset;
	static double[] originPercentOverlap = new double[] { 10, 10 };
	static int[] originPixelOverlap;
	static double[] axisPercentLength = new double[] { 87, 85 };
	static int[] axisPixelLength;

	/**
	 * 
	 * @param polynomialPoints includes an extra point containing (minY, maxY)
	 * 
	 * NOTE: minX is polynomialPoints[0], and maxX is polynomialPoints[polynomialPoints.length - 1][0].
	 * 
	 * @return
	 */
	public static int[][] computePolynomialPixelPoints(double[][] polynomialPoints) {
		int[][] polynomialPixelPoints = new int[polynomialPoints.length - 1][2];
		
		double [] scalingFactor = new double [] { 
				(double)(width - originPixelOffset[0]) / polynomialPoints[polynomialPoints.length - 2][0],
				(double)(height - originPixelOffset[1]) / polynomialPoints[polynomialPoints.length - 1][1]
		};
		
		for (int i = 0; i < polynomialPoints.length - 1; i++) {
			polynomialPixelPoints[i][0] = (int)(polynomialPoints[i][0] * scalingFactor[0]) + originPixelOffset[0];
			polynomialPixelPoints[i][1] = (height - originPixelOffset[1] - (int)(polynomialPoints[i][1] * scalingFactor[0]));
			System.out.println("pixelPoints[" + i + "] == ( " + polynomialPixelPoints[i][0] + ", " + polynomialPixelPoints[i][1] + " )");
		}
		
		return polynomialPixelPoints;
	}

	public static double[][] computePolynomialPoints(double[] coefficients, double minX, double maxX, int nPoints) {
		double[][] polynomialPoints = new double[nPoints + 1][2]; // The + 1 is to create a place to put minY and maxY
		double xDomainWidth = maxX - minX;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double xStrideLength = xDomainWidth / (nPoints - 1);
		for (int j = 0; j < nPoints; j++) {
			double x = j * xStrideLength + minX;
			double y = 0;
			for (int i = 0; i < coefficients.length; i++) {
				y += coefficients[i] * Math.pow(x, coefficients.length - i - 1);
			}

			polynomialPoints[j][0] = x;
			polynomialPoints[j][1] = y;

			if (y < minY) {
				minY = y;
			}

			if (y > maxY) {
				maxY = y;
			}

			System.out.println("polynomialPoints[" + j + "] == ( " + x + ", " + y + " )");
		}
		polynomialPoints[nPoints][0] = minY;
		polynomialPoints[nPoints][1] = maxY;

		System.out.println("polynomialPoints[" + nPoints + "] == ( " + minY + ", " + maxY + " )");

		return polynomialPoints;
	}

	public static int[] computeArrowHeadPixelPoints(double x, double y, double size, int direction) {
		int[][] pixelPoints = new int[3][2];
		int[] pixelPoints1D = new int[6];
		if (direction == SWT.UP) {
			pixelPoints[0] /* top */ = new int[] { (int) (x * width / 100), height - (int) (y * height / 100 + (size / 2) * width / 100) };
			pixelPoints[1] /* left */ = new int[] { (int) ((x - size / 2) * width / 100), height - (int) (y * height / 100 - (size / 2) * width / 100) };
			pixelPoints[2] /* right */ = new int[] { (int) ((x + size / 2) * width / 100), height - (int) (y * height / 100 - (size / 2) * width / 100) };
		} else if (direction == SWT.RIGHT) {
			pixelPoints[0] /* right */ = new int[] { (int)((x + size / 2) * width / 100), height - (int) (y * height / 100) };
			pixelPoints[1] /* top */ = new int[] { (int) ((x - size / 2) * width / 100), height - (int) (y * height / 100 - (size / 2) * width / 100) };
			pixelPoints[2] /* bottom */ = new int[] { (int) ((x - size / 2) * width / 100), height - (int) (y * height / 100 + (size / 2) * width / 100) };
		}
		
		pixelPoints1D[0] = pixelPoints[0][0];
		pixelPoints1D[1] = pixelPoints[0][1];
		pixelPoints1D[2] = pixelPoints[1][0];
		pixelPoints1D[3] = pixelPoints[1][1];
		pixelPoints1D[4] = pixelPoints[2][0];
		pixelPoints1D[5] = pixelPoints[2][1];
		return pixelPoints1D;
	}
	
	public static int [] convertPolynomialPixelPointsTo1D(int [][] polynomialPoints2D) {
		int [] polynomialPixelPoints1D = new int[polynomialPoints2D.length * 2];
		for (int i = 0; i < polynomialPoints2D.length; i++) {
			polynomialPixelPoints1D[i * 2] = polynomialPoints2D[i][0];
			polynomialPixelPoints1D[i * 2 + 1] = polynomialPoints2D[i][1];
		}
		return polynomialPixelPoints1D;
	}

	public static void main(String[] args) {
		// Screen aspect ratio is assumed to be exactly proportionate to pixel size.
		// i.e. pixels are exactly square.
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBounds(0, 0, 386, 324); // 371, 259?
		shell.setText("Ex00010");
		System.out.println("shell.getLocation() -> " + shell.getLocation());
		height = shell.getClientArea().height;
		width = shell.getClientArea().width;
		System.out.println("shell.getClientArea() == " + shell.getClientArea());
		System.out.println("shell.getForeground() == " + shell.getForeground());
		System.out.println("shell.getFont() == " + shell.getFont().getFontData()[0]);
		double minimumDimension = Math.min(width, height);
		double borderPercent = 2;
		borderPixelWidth = (int) (borderPercent * (shell.getClientArea().height < shell.getClientArea().width ? shell.getClientArea().height : shell.getClientArea().width) / 100.0);

		originPixelOffset = new int[] { (int) (originPercentOffset[0] * width / 100), (int) (originPercentOffset[1] * height / 100) };
		originPixelOverlap = new int[] { (int) (originPercentOverlap[0] * minimumDimension / 100), (int) (originPercentOverlap[1] * minimumDimension / 100) };
		axisPixelLength = new int[] { (int) (axisPercentLength[0] * width / 100), (int) (axisPercentLength[1] * height / 100) };

		double[] polynomialCoefficients = new double[] { -0.001, 0.08, -1.0, 4 }; // descending powers
		//double[] polynomialCoefficients = new double[] { 0.01, 0.011, 0.012, 0.013 }; // descending powers
		double[][] polynomialPoints = computePolynomialPoints(polynomialCoefficients, -originPercentOverlap[0], 100.0, 128);
		int[][] polynomialPixelPoints = computePolynomialPixelPoints(polynomialPoints);
		int[] polynomialPixelPoints1D = convertPolynomialPixelPointsTo1D(polynomialPixelPoints);

		shell.addPaintListener(event -> {
			GC gc = event.gc;

			Color blue = new Color(display, 0x00, 0x33, 0xA1, 255);
			gc.setForeground(blue);
			gc.setLineWidth(2);
			gc.drawLine(originPixelOffset[0] - originPixelOverlap[0], height - originPixelOffset[1], axisPixelLength[0], height - originPixelOffset[1]); // x-axis
			gc.drawLine(originPixelOffset[0], height - originPixelOffset[1] + originPixelOverlap[1], originPixelOffset[0], height - axisPixelLength[1]); // y-axis

			Color black = new Color(display, 0, 0, 0, 255);
			gc.setForeground(black);
			FontData fd = shell.getFont().getFontData()[0];
			final Font font = new Font(display, fd.getName(), 12, SWT.NORMAL);
			gc.setFont(font);
			Point textExtent = gc.textExtent("y = f(x)");
			gc.drawText("y = f(x)", originPixelOffset[0] - textExtent.x / 2 + 2, height - axisPixelLength[1] - 30);
			gc.drawText("x", originPixelOffset[0] - originPixelOverlap[0] + axisPixelLength[0] - textExtent.x / 2 + 2, height - originPixelOffset[1] - textExtent.y / 2 - 2);

			gc.drawText("Graph of the set", (int) (57.0 * width / 100), (int) (height - 75.0 * height / 100));
			gc.drawText("of points (x, y)", (int) (57.0 * width / 100), (int) (height - 75.0 * height / 100 + gc.textExtent("M").y));

			// Arrowheads are going to be complicated.
			// Start with a filled triangle. Then add the curvy bottom.
			// centerX, centerY, SWT.UP | SWT.DOWN
			gc.setBackground(blue);
			gc.fillPolygon(computeArrowHeadPixelPoints(originPercentOffset[0], 85, 3.7, SWT.UP));
			gc.fillPolygon(computeArrowHeadPixelPoints(85, originPercentOffset[1], 3.7, SWT.RIGHT));

			gc.setLineWidth(2);
			gc.drawPolyline(polynomialPixelPoints1D);

			font.dispose();
			black.dispose();
			blue.dispose();
		});
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
