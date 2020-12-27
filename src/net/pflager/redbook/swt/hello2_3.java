/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package net.pflager.redbook.swt;

import com.pflager.glu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class hello2_3 extends glu {

	static GLCanvas canvas;

	public void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		/* clear all pixels */
		glClear(GL_COLOR_BUFFER_BIT);

		glColor3f(1.0f, 1.0f, 1.0f);
		glBegin(GL_POLYGON);
		{
			glVertex2f(0.0f, 0.0f);
			glVertex2f(0.0f, 3.0f);
			glVertex2f(4.0f, 3.0f);
			glVertex2f(6.0f, 1.5f);
			glVertex2f(4.0f, 0.0f);
		}
		glEnd();
		glFlush();
	}

	public void init() {
		/* select clearing color */
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	void reshape(int w, int h) {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(-10.0f, 10.0f, -10.0f, 10.0f);
	}

	public void execute(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, new GLData());
		canvas.setCurrent();

		init();

		{ // sort of glutInitWindowSize()
			shell.setSize(shell.computeTrim(0, 0, 250, 250).width, shell.computeTrim(0, 0, 250, 250).height);
			reshape(canvas.getBounds().width, canvas.getBounds().height);
		}

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		{ // sort of glutCreateWindow()
			shell.setText("hello");
			shell.open();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void main(String[] args) {
		new hello2_3().execute(args);
	}
}
