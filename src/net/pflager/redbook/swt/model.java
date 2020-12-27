package net.pflager.redbook.swt;

import com.pflager.glut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class model extends glut {
	final Display display;
	final Shell shell;
	static GLCanvas canvas;

	model() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Rectangle trimRectangle = shell.computeTrim(0, 0, 740, 540); // DPP: 190307T114700: Why change the canvas dimensions?
		shell.setSize(trimRectangle.width, trimRectangle.height);

		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, new GLData());
		canvas.setCurrent();

		init();

		glViewport(0, 0, canvas.getBounds().width, canvas.getBounds().height);

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.KeyDown, event -> keyboard(event.character, event.x, event.y));

		shell.setText("Model3_2");
		shell.open();
	}

	void draw_triangle() { // DPP: 190307T114700: Why change the order of this function relative to other functions?  It's not alphabetical.
		glBegin(GL_LINE_LOOP);
		glVertex2f(0.0f, 25.0f);
		glVertex2f(25.0f, -25.0f);
		glVertex2f(-25.0f, -25.0f);
		glEnd();
	}

	void init() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glShadeModel(GL_SMOOTH);	// DPP: 190307T114700: Why change this from GL_FLAT to GL_SMOOTH?

	}

	void display() {
		System.out.println("<<Model3_2.display() Start>>>>");
		if (canvas.isDisposed()) {
			return;
		}

		glClear(GL_COLOR_BUFFER_BIT);
		glColor3f(1.0f, 1.0f, 1.0f);

		glLoadIdentity();
		glColor3f(1.0f, 1.0f, 1.0f);
		draw_triangle();

		glEnable(GL_LINE_STIPPLE);
		glLineStipple(1, (short) 0xF0F0);
		glLoadIdentity();
		glTranslatef(-20.0f, 0.0f, 0.0f);
		draw_triangle();

		glLineStipple(1, (short) 0xF00F);
		glLoadIdentity();
		glScalef(1.5f, 0.5f, 1.0f);
		draw_triangle();

		glLineStipple(1, (short) 0x8888);
		glLoadIdentity();
		glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
		draw_triangle();
		glDisable(GL_LINE_STIPPLE);

		glFlush();
		canvas.swapBuffers();
		System.out.println("<<<<Model3_2.display() <<End");
	}

	void keyboard(char key, int x, int y) {
		switch (key) { // (int)key
		case '\33':
			System.exit(0);
			break;
		}
	}

	void reshape(int w, int h) {
		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		if (w <= h)
			glOrtho(-50.0, 50.0, -50.0 * (float) h / (float) w, 50.0 * (float) h / (float) w, -1.0, 1.0);

		else
			glOrtho(-50.0 * (float) w / (float) h, 50.0 * (float) w / (float) h, -50.0, 50.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
	}

	void loop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void main(String[] args) {
		new model().loop();

	}
}
