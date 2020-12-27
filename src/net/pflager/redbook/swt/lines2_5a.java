/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package net.pflager.redbook.swt;

import com.pflager.glu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class lines2_5a extends glu {

	final Display display;
	final Shell shell;

	public lines2_5a() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Rectangle trimRectangle = shell.computeTrim(0, 0, 450, 150);
		shell.setSize(trimRectangle.width, trimRectangle.height);
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, glData);
		canvas.setCurrent();

		/* initialize viewing values */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);

		init();

		glViewport(0, 0, canvas.getBounds().width, canvas.getBounds().height);

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.KeyDown, event -> keyboard(event.character, event.x, event.y));

		shell.setText("lines2_5");
		shell.open();
	}

	void loop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	void drawOneLine(float x1, float y1, float x2, float y2) {
		glBegin(GL_LINES);
		glVertex2f(x1, y1);
		glVertex2f(x2, y2);
		glEnd();
	}

	void init() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glShadeModel(GL_FLAT);
	}

	void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glClear(GL_COLOR_BUFFER_BIT);

		/* select white for all lines */
		glColor3f(1.0f, 1.0f, 1.0f);

		/* in 1st row, 3 lines, each with a different stipple */
		glEnable(GL_LINE_STIPPLE);

		glLineStipple(1, (short) 0x0101); /* dotted */
		glBegin(GL_POLYGON);
		{
			glVertex2f(50.0f, 125.0f);
			glVertex2f(150.0f, 125.0f);
			glVertex2f(140.0f, 160.0f);
			glVertex2f(100.0f, 60.0f);
		}
		glEnd();
		
//		glLineStipple(1, (short) 0x00FF); /* dashed */
//		drawOneLine(150.0f, 125.0f, 250.0f, 125.0f);
//		glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
//		drawOneLine(250.0f, 125.0f, 350.0f, 125.0f);
//
//		/* in 2nd row, 3 wide lines, each with different stipple */
//		glLineWidth(5.0f);
//		glLineStipple(1, (short) 0x0101); /* dotted */
//		drawOneLine(50.0f, 100.0f, 150.0f, 100.0f);
//		glLineStipple(1, (short) 0x00FF); /* dashed */
//		drawOneLine(150.0f, 100.0f, 250.0f, 100.0f);
//		glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
//		drawOneLine(250.0f, 100.0f, 350.0f, 100.0f);
//		glLineWidth(1.0f);
//
//		/* in 3rd row, 6 lines, with dash/dot/dash stipple */
//		/* as part of a single connected line strip */
//		glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
//		glBegin(GL_LINE_STRIP);
//		for (i = 0; i < 7; i++)
//			glVertex2f(50.0f + i * 50.0f, 75.0f);
//		glEnd();
//
//		/* in 4th row, 6 independent lines with same stipple */
//		for (i = 0; i < 6; i++) {
//			drawOneLine(50.0f + i * 50.0f, 50.0f, 50.0f + (i + 1) * 50.f, 50.0f);
//		}
//
//		/* in 5th row, 1 line, with dash/dot/dash stipple */
//		/* and a stipple repeat factor of 5 */
//		glLineStipple(5, (short) 0x1C47); /* dash/dot/dash */
//		drawOneLine(50.0f, 25.0f, 350.0f, 25.0f);

		glDisable(GL_LINE_STIPPLE);

		canvas.swapBuffers();
	}

	void reshape(int w, int h) {
		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(0.0f, (float) w, 0.0f, (float) h);
	}

	void keyboard(char key, int x, int y) {
		switch (key) { // (int)key
		case '\33':
			System.exit(0);
			break;
		}
	}

	static GLCanvas canvas;

	public static void main(String[] args) {
		new lines2_5a().loop();
	}
}
