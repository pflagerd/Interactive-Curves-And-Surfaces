/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package net.pflager.redbook.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.pflager.glut;

public class varray extends glut {
	final int POINTER = 1;
	final int INTERLEAVED = 2;

	final int DRAWARRAY = 1;
	final int ARRAYELEMENT = 2;
	final int DRAWELEMENTS = 3;

	final int GLUT_LEFT_BUTTON = 0x0000;
	final int GLUT_MIDDLE_BUTTON = 0x0001;
	final int GLUT_RIGHT_BUTTON = 0x0002;
	final int GLUT_DOWN = 0x0000;

	static GLCanvas canvas;
	int derefMethod = DRAWARRAY;
	final Display display;
	int setupMethod = POINTER;
	final Shell shell;

	public varray() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Rectangle ShellTrimRectangle = shell.computeTrim(0, 0, 350, 350);
		shell.setSize(ShellTrimRectangle.width, ShellTrimRectangle.height);
		shell.setText("varray");
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, new GLData());
		canvas.setCurrent();

		init();

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.KeyDown, event -> keyboard(event.character, event.x, event.y));
		
		canvas.addListener(SWT.MouseDown, event -> mouse(event.button - 1, GLUT_DOWN, event.x, event.y));

		shell.open();
	}

	void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glClear(GL_COLOR_BUFFER_BIT);

		if (derefMethod == DRAWARRAY)
			glDrawArrays(GL_TRIANGLES, 0, 6);
		else if (derefMethod == ARRAYELEMENT) {
			glBegin(GL_TRIANGLES);
			glArrayElement(2);
			glArrayElement(3);
			glArrayElement(5);
			glEnd();
		} else if (derefMethod == DRAWELEMENTS) {
			int[] indices = { 0, 1, 3, 4 };
			glDrawElements(GL_POLYGON, indices.length, GL_UNSIGNED_INT, indices);
		}
		glFlush();
	}

	void init() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glShadeModel(GL_SMOOTH);
		setupPointers();
	}

	void keyboard(char key, int x, int y) {
		switch (key) {
		case 27:
			System.exit(0);
			break;
		}
	}

	void loop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	void mouse(int button, int state, int x, int y) {
		switch (button) {
		case GLUT_LEFT_BUTTON:
			if (state == GLUT_DOWN) {
				if (setupMethod == POINTER) {
					setupMethod = INTERLEAVED;
					setupInterleave();
				} else if (setupMethod == INTERLEAVED) {
					setupMethod = POINTER;
					setupPointers();
				}
				canvas.redraw();
			}
			break;
		case GLUT_MIDDLE_BUTTON:
		case GLUT_RIGHT_BUTTON:
			if (state == GLUT_DOWN) {
				if (derefMethod == DRAWARRAY)
					derefMethod = ARRAYELEMENT;
				else if (derefMethod == ARRAYELEMENT)
					derefMethod = DRAWELEMENTS;
				else if (derefMethod == DRAWELEMENTS)
					derefMethod = DRAWARRAY;
				canvas.redraw();
			}
			break;
		default:
			break;
		}
	}

	void reshape(int w, int h) {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(0.0f, (float)w, 0.0f, (float)h);
	}

	void setupPointers() {

		int[] vertices = { 25, 25, 100, 325, 175, 25, 175, 325, 250, 25, 325, 325 };
		float[] colors = { 1.0f, 0.2f, 0.2f, 0.2f, 0.2f, 1.0f, 0.8f, 1.0f, 0.2f, 0.75f, 0.75f, 0.75f, 0.35f, 0.35f, 0.35f, 0.5f, 0.5f, 0.5f };

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glVertexPointer(2, GL_INT, 0, vertices);

		glColorPointer(3, GL_FLOAT, 0, colors);
	}

	void setupInterleave() {
		float[] intertwined = { 1.0f, 0.2f, 1.0f, 100.0f, 100.0f, 0.0f, 1.0f, 0.2f, 0.2f, 0.0f, 200.0f, 0.0f, 1.0f, 1.0f, 0.2f, 100.0f, 300.0f, 0.0f, 0.2f, 1.0f, 0.2f, 200.0f, 300.0f, 0.0f, 0.2f, 1.0f, 1.0f, 300.0f, 200.0f, 0.0f, 0.2f, 0.2f, 1.0f, 200.0f, 100.0f, 0.0f };
		
		glInterleavedArrays(GL_C3F_V3F, 0, intertwined);
	}

	public static void main(String[] args) {
		new varray().loop();
	}
}
