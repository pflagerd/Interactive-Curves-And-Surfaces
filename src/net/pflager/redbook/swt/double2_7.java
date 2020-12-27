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

public class double2_7 extends glu {

	static GLCanvas canvas;
	static float spin = 0.0f;
	static boolean spinning;
	final Display display;
	final Shell shell;

	double2_7() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, glData);
		canvas.setCurrent();

		{ // sort of glutInitWindowSize()
			shell.setSize(shell.computeTrim(0, 0, 250, 250).width, shell.computeTrim(0, 0, 250, 250).height);
		}
		
		init();

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.MouseDown, event -> mouse(event.button - 1, GLUT_DOWN, event.x, event.y));

//		display.asyncExec(new Runnable() {
//			public void run() {
//				spinDisplay();
//				display.asyncExec(this);
//			}
//		});

		{ // sort of glutCreateWindow()
			shell.setText("double");
			shell.open();
		}
	}

	float[] V0 = { 1.0f, 1.0f, 0.0f };
	float[] V1 = { 3.0f, 1.5f, 0.0f };
	float[] V2 = { 2.0f, 2.0f, 0.0f };

	void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glBegin(GL_POLYGON);
		{
			glEdgeFlag(true /* GL_TRUE */);
			glVertex3fv(V0);
			glEdgeFlag(false /* GL_FALSE */);
			glVertex3fv(V1);
			glEdgeFlag(true /* GL_TRUE */);
			glVertex3fv(V2);
		}
		glEnd();

		canvas.swapBuffers();
	}

	void init() {
		/* select clearing color */
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glShadeModel(GL_FLAT);
		reshape(canvas.getBounds().width, canvas.getBounds().height);
	}

	void loop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	void reshape(int w, int h) {
		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glOrtho(0.0, 5.0, 0.0, 5.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
	}

	/*
	 * GLUT API macro definitions -- mouse state definitions
	 */
	private final static int GLUT_LEFT_BUTTON = 0x0000;
	private final static int GLUT_MIDDLE_BUTTON = 0x0001;
	private final static int GLUT_RIGHT_BUTTON = 0x0002;
	private final static int GLUT_DOWN = 0x0000;

	void mouse(int button, int state, int x, int y) {
		switch (button) {
		case GLUT_LEFT_BUTTON:
			if (state == GLUT_DOWN) {
				spinning = true;
			}
			break;
		case GLUT_MIDDLE_BUTTON:
		case GLUT_RIGHT_BUTTON:
			if (state == GLUT_DOWN)
				spinning = false;
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		new double2_7().loop();
	}
}
