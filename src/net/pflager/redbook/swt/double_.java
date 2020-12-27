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

public class double_ extends glu {

	static GLCanvas canvas;
	static float spin = 0.0f;
	static boolean spinning;

	void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glClear(GL_COLOR_BUFFER_BIT);
		glPushMatrix();
		glRotatef(spin, 0.0f, 0.0f, 1.0f);
		glColor3f(1.0f, 1.0f, 1.0f);
		glRectf(-25.0f, -25.0f, 25.0f, 25.0f);
		glPopMatrix();

		canvas.swapBuffers();
	}

	void spinDisplay() {
		spin = spin + 2.0f;
		if (spin > 360.0f)
			spin = spin - 360.0f;
		display();
	}

	void init() {
		/* select clearing color */
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glShadeModel(GL_FLAT);

		/* initialize viewing values */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-50.0, 50.0, -50.0, 50.0, -1.0, 1.0);
	}

	void reshape(int w, int h) {
		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-50.0, 50.0, -50.0, 50.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	/*
	 * GLUT API macro definitions -- mouse state definitions
	 */
	private final static int GLUT_LEFT_BUTTON =                0x0000;
	private final static int GLUT_MIDDLE_BUTTON =              0x0001;
	private final static int GLUT_RIGHT_BUTTON =               0x0002;
	private final static int GLUT_DOWN =                       0x0000;
	
	private static void mouse(int button, int state, int x, int y) 
	{
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
		new double_().execute();
	}

	private void execute() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, glData);
		canvas.setCurrent();

		init();

		{ // sort of glutInitWindowSize()
			shell.setSize(shell.computeTrim(0, 0, 250, 250).width, shell.computeTrim(0, 0, 250, 250).height);
			glViewport(0, 0, canvas.getBounds().width, canvas.getBounds().height);
		}

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.MouseDown, event -> mouse(event.button - 1, GLUT_DOWN, event.x, event.y));

		display.asyncExec(new Runnable() {
			public void run() {
				spinDisplay();
				display.asyncExec(this);
			}
		});

		{ // sort of glutCreateWindow()
			shell.setText("double");
			shell.open();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
