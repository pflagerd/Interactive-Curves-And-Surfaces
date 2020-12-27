package dpp;

import com.pflager.glut;

public class Lesson001 extends glut {

	// (1-x)^3+4(1-x)^2+(1-x)\ 
	double y(double x) {
		return Math.pow(0.5-1.5*x, 3) + 4 * Math.pow(0.5-1.5*x, 2)+(0.5-1.5*x);
	}

	void drawSomeCurve(int color) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(3.0);
		glBegin(GL_LINE_STRIP);
		
		for (int i = 0; i < 122; i++) {
			glVertex3d(0.01 * i - 0.8, 0.1 * y(0.03 * i - 0.7) - 0.7, 0);
			
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

	void drawXAxis(double xStart, double yStart, double length, double lineWidth, int color, drawAxisEndCap endcap, double endcapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(lineWidth);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart + length, yStart, 0.0);

		glVertex3d(xStart + length, yStart, 0.0);
		glVertex3d(xStart + length - 0.05, yStart - 0.03, 0.0);

		glVertex3d(xStart + length, yStart, 0.0);
		glVertex3d(xStart + length - 0.05, yStart + 0.03, 0.0);

		glEnd();

		glRasterPos2d(xStart + length + 0.02, yStart - 0.02);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}
	}

	void drawYAxis(double xStart, double yStart, double length, double lineWidth, int color, drawAxisEndCap endcap, double endcapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(lineWidth);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart, yStart + length, 0.0);

		glVertex3d(xStart, yStart + length, 0.0);
		glVertex3d(xStart - 0.02, yStart + length - 0.07, 0.0);

		glVertex3d(xStart, yStart + length, 0.0);
		glVertex3d(xStart + 0.02, yStart + length - 0.07, 0.0);

		glEnd();

		glRasterPos2d(xStart - 0.1, yStart + length + 0.03);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}

	}

	void display() {
		/* clear all pixels */
		glClear(GL_COLOR_BUFFER_BIT);

		// Draw xAxis
		drawXAxis(-0.90, -0.70, 1.7, 2, 0x000000, drawAxisEndCap.arrowhead, 1.0, "x");

		// Draw yAxis
		drawYAxis(-0.70, -0.90, 1.7, 2, 0x000000, drawAxisEndCap.arrowhead, 1.0, "y=f(x)");

		drawSomeCurve(0);

		drawAnnotation(0.0, 0.20, "Graph of the set\nof points (x, y)");

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
	}

	final double widthPx = 400;
	final double heightPx = 280;
	final double aspect = widthPx / heightPx;

	/*
	 * Declare initial window size, position, and display mode (single buffer and RGBA). Open window with "hello" in its title bar. Call initialization routines. Register callback function to display graphics. Enter main loop and process events.
	 */
	int main(int argc, String[] argv) {
		glutInit(argc, argv);
		glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
		glutInitWindowSize((int) widthPx, (int) heightPx);
		glutInitWindowPosition(100, 100);
		glutCreateWindow("Lesson 001");
		init();
		glutDisplayFunc(this::display);
		glutMainLoop();
		return 0; /* ANSI C requires main to return int. */
	}

	public static void main(String[] args) {
		new Lesson001().main(args.length, args);
	}
}
