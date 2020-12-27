package dpp;

import com.pflager.glut;

public class Lesson000 extends glut {
	
	double f(double x) {
		return 3.0 * x + 4.0;
	}

	void drawLine(double m, double b, int increments, int color) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glBegin(GL_POINTS);
		for (int i = 0; i < increments; i++) {
			
			double x = -11.0 / 3 + 14.0 * (double)i / (double)increments / 3.0;
			System.out.println("(" + x + ", " + f(x) + ")");
			
			glVertex3d(x / 7.0, f(x) / 7.0, 0);
		}
		glEnd();
	}

	void display() {
		/* clear all pixels */
		glClear(GL_COLOR_BUFFER_BIT);

		drawLine(3, 4, 1024, 0);

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

	void reshape(int width, int height) {
		glViewport((width - Math.min(width, height)) / 2, (height - Math.min(width, height)) / 2, Math.min(width, height), Math.min(width, height));
	}

	/*
	 * Declare initial window size, position, and display mode (single buffer and RGBA). Open window with "hello" in its title bar. Call initialization routines. Register callback function to display graphics. Enter main loop and process events.
	 */
	int main(int argc, String[] argv) {
		glutInit(argc, argv);
		glutInitDisplayMode(GLUT_SINGLE | GLUT_RGBA);
		glutInitWindowSize(400, 280);
		glutInitWindowPosition(100, 100);
		glutCreateWindow("Lesson 001");
		init();
		glutDisplayFunc(this::display);
		glutReshapeFunc(this::reshape);
		glutMainLoop();
		return 0; /* ANSI C requires main to return int. */
	}

	public static void main(String[] args) {
		new Lesson000().main(args.length, args);
	}
}
