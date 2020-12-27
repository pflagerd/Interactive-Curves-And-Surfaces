package dpp;

import com.pflager.glut;

public class Lesson005 extends glut {

	double rotAngle = 0.0;

	void drawCircle(double radius, int increments, int color, double lineWidth) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(lineWidth);
		glBegin(GL_LINE_LOOP);
		for (int i = 0; i < increments; i++) {
			glVertex3d(radius * Math.cos(2 * Math.PI * i / increments), radius * Math.sin(2 * Math.PI * i / increments), 0);
		}
		glEnd();
	}

	void drawAnnotation(double xStart, double yStart, String annotation) {
		glRasterPos2d(xStart, yStart);
		int i = 0;
		for (char c : annotation.toCharArray()) {
			if (c == '\n') {
				i++;
				glRasterPos2d(xStart, yStart - 0.12 * i);
				continue;
			}
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}
	}

	enum drawAxisEndCap {
		arrowhead,
	};

	void drawXAxis(double xStart, double yStart, double length, double lineWidth, int color, drawAxisEndCap startEndcap, drawAxisEndCap endEndcap, double endcapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(lineWidth);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart + length, yStart, 0.0);

		if (startEndcap == drawAxisEndCap.arrowhead) {
			glVertex3d(xStart, yStart, 0.0);
			glVertex3d(xStart + 0.07, yStart - 0.02, 0.0);

			glVertex3d(xStart, yStart, 0.0);
			glVertex3d(xStart + 0.07, yStart + 0.02, 0.0);
		}

		if (endEndcap == drawAxisEndCap.arrowhead) {
			glVertex3d(xStart + length, yStart, 0.0);
			glVertex3d(xStart + length - 0.07, yStart - 0.02, 0.0);

			glVertex3d(xStart + length, yStart, 0.0);
			glVertex3d(xStart + length - 0.07, yStart + 0.02, 0.0);
		}

		glEnd();

		glRasterPos2d(xStart + length + 0.02, yStart - 0.015);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}
	}

	void drawYAxis(double xStart, double yStart, double length, double lineWidth, int color, drawAxisEndCap startEndcap, drawAxisEndCap endEndcap, double endcapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(lineWidth);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart, yStart + length, 0.0);

		if (startEndcap == drawAxisEndCap.arrowhead) {
			glVertex3d(xStart, yStart, 0.0);
			glVertex3d(xStart - 0.02, yStart + 0.07, 0.0);

			glVertex3d(xStart, yStart, 0.0);
			glVertex3d(xStart + 0.02, yStart + 0.07, 0.0);
		}

		if (endEndcap == drawAxisEndCap.arrowhead) {
			glVertex3d(xStart, yStart + length, 0.0);
			glVertex3d(xStart - 0.02, yStart + length - 0.07, 0.0);

			glVertex3d(xStart, yStart + length, 0.0);
			glVertex3d(xStart + 0.02, yStart + length - 0.07, 0.0);
		}

		glEnd();

		glRasterPos2d(xStart - 0.02, yStart + length + 0.05);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}

	}

	void display() {
		/* clear all pixels */
		glClear(GL_COLOR_BUFFER_BIT);

		// Draw xAxis
		drawXAxis(-0.80, 0.00, 1.6, 2.5, 0x000000, drawAxisEndCap.arrowhead, drawAxisEndCap.arrowhead, 1.0, "x");

		// Draw yAxis8
		drawYAxis(0.00, -0.8, 1.6, 2.5, 0x000000, drawAxisEndCap.arrowhead, drawAxisEndCap.arrowhead, 1.0, "y");

		drawCircle(0.5, 60, 0, 3);

		// drawAnnotation(0.0, 0.20, "Graph of the set\nof points (x, y)");
		glColor3f(0.0, 0.0, 0.0);
		glPushMatrix();
		glRotatef(-rotAngle, 0.0, 0.0, 0.1);
		glBegin(GL_LINES);
		glVertex2f(-0.5, 0.5);
		glVertex2f(0.5, -0.5);
		glEnd();
		glPopMatrix();

		glColor3f(0.0, 0.0, 0.0);
		glPushMatrix();
		glRotatef(rotAngle, 0.0, 0.0, 0.1);
		glBegin(GL_LINES);
		glVertex2f(0.5, 0.5);
		glVertex2f(-0.5, -0.5);
		glEnd();
		glPopMatrix();
		/*
		 * don't wait! start processing buffered OpenGL routines
		 */
		glFlush();
	}

	void init() {
		/* select clearing color */
		glClearColor(1.0, 1.0, 1.0, 1.0);

		/* initialize viewing values */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);

		/* Turn on Line Smoothing */
		glEnable(GL_LINE_SMOOTH);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	}

	private boolean lineSmoothing = true;

	void keyboard(char key, int x, int y) {
		switch (key) {
			case ' ':
				if (lineSmoothing) {
					glDisable(GL_LINE_SMOOTH);
					glDisable(GL_BLEND);
					lineSmoothing = false;
				} else {
					glEnable(GL_LINE_SMOOTH);
					glEnable(GL_BLEND);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
					lineSmoothing = true;
				}
				glutPostRedisplay();
				break;
			case '+':
				rotAngle++;
				glutPostRedisplay();
				break;
			case '-':
				rotAngle--;
				glutPostRedisplay();
				break;
			case 27: /* Escape Key */
				System.exit(0);
				break;
			default:
				break;
		}
	}

	void reshape(int width, int height) {
		glViewport((width - Math.min(width, height)) / 2, (height - Math.min(width, height)) / 2, Math.min(width, height), Math.min(width, height));
	}

	final double widthPx = 400;
	final double heightPx = 280;
	final double aspect = widthPx / heightPx;

	/*
	 * Declare initial window size, position, and display mode (single buffer and RGBA). Open window with "hello" in its title bar. Call initialization routines. Register callback function to display graphics. Enter main loop and process events.
	 */
	int main(int argc, String[] argv) {
		glutInit(argc, argv);
		glutInitDisplayMode(GLUT_SINGLE | GLUT_RGBA);
		glutInitWindowSize((int) widthPx, (int) heightPx);
		glutInitWindowPosition(100, 100);
		glutCreateWindow("Lesson 005");
		init();
		glutDisplayFunc(this::display);
		glutReshapeFunc(this::reshape);
		glutKeyboardFunc(this::keyboard);
		glutMainLoop();
		return 0; /* ANSI C requires main to return int. */
	}

	public static void main(String[] args) {
		new Lesson005().main(args.length, args);
	}
}
