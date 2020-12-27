package dpp;

import com.pflager.glut;

public class Lesson022 extends glut {

	void display() {
		/* clear all pixels */
		glClear(GL_COLOR_BUFFER_BIT);
		
		drawCoordinateSystem(10.0, 1, 1, 5);

		drawLine(3, 4, 1024, 0);

		/*
		 * don't wait! start processing buffered OpenGL routines
		 */
		glFlush();
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

	enum AxisEndCap {
		arrowhead,
	};

	// 
	void drawCoordinateSystem(double majorDivisionValue, int majorDivisions, int minorDivisions, int minorSubdivisions) {
		// Draw xAxis
		drawXAxis(-0.90, -0.70, 1.7, 2, 0x000000, majorDivisionValue, majorDivisions, minorDivisions, minorSubdivisions, AxisEndCap.arrowhead, 1.0, "x");

		// Draw yAxis
		drawYAxis(-0.70, -0.90, 1.7, 2, 0x000000, majorDivisionValue, majorDivisions, minorDivisions, minorSubdivisions, AxisEndCap.arrowhead, 1.0, "y");
	}
	
	void drawLine(double m, double b, int increments, int color) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glBegin(GL_POINTS);
		for (int i = 0; i < increments; i++) {
			
			double x = -11.0 / 3.0 + 14.0 * (double)i / (double)increments / 3.0;
			System.out.println("(" + x + ", " + f(x) + ")");
			
			glVertex3d(x / 7.0, f(x) / 7.0, 0);
		}
		glEnd();
	}

	void drawXAxis(double xStart, double yStart, double length, double lineWidth, int color, double majorDivisionValue, int majorDivisions, int minorDivisions, int minorSubdivisions, AxisEndCap axisEndCap, double axisEndCapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(1 + lineWidth * (int)Math.max(width, height) / 1000);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart + length - 0.03, yStart, 0.0);
		glEnd();

		glBegin(GL_POLYGON);
		glVertex3d(xStart + length, yStart, 0.0);
		glVertex3d(xStart + length - 0.07, yStart - 0.02, 0.0);
		glVertex3d(xStart + length - 0.07, yStart + 0.02, 0.0);
		glEnd();

		glRasterPos2d(xStart + length + 9.0 / width, yStart - 7.0 / height);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_9_BY_15, c);
		}
		
		glBegin(GL_LINES);
		glVertex3d(xStart + length - 0.1, yStart - 0.07, 0);
		glVertex3d(xStart + length - 0.1, yStart + 0.07, 0);
		glEnd();
		
		glLineWidth(1.0);
		
		glBegin(GL_LINES);
		glVertex3d(-0.7 + (length - 0.3)/ 2, yStart - 0.05, 0);
		glVertex3d(-0.7 + (length - 0.3)/ 2, yStart + 0.05, 0);
		
		for (int i = 0; i < 10; i++) {
			glVertex3d(xStart + 0.2 + i * (length - 0.3)/ 10.0, yStart - 0.03, 0);
			glVertex3d(xStart + 0.2 + i * (length - 0.3)/ 10.0, yStart + 0.03, 0);
		}		
		glEnd();
		
		glRasterPos2d(xStart + length - 0.15 - 9.0 / width, yStart - 3.5 * 13.0 / height);
		for (char c : Double.toString(majorDivisionValue).toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}
		
	}

	void drawYAxis(double xStart, double yStart, double length, double lineWidth, int color, double majorDivisionValue, int majorDivisions, int minorDivisions, int minorSubdivisions, AxisEndCap axisEndCap, double axisEndCapScale, String label) {
		glColor3i(color >> 16, (color >> 8) & 0xFF, (color & 0xFF));
		glLineWidth(1 + lineWidth * (int)Math.max(width, height) / 1000);
		glBegin(GL_LINES);
		glVertex3d(xStart, yStart, 0.0);
		glVertex3d(xStart, yStart + length - 0.03, 0.0);
		glEnd();

		glBegin(GL_POLYGON);
		glVertex3d(xStart, yStart + length, 0.0);
		glVertex3d(xStart - 0.02, yStart + length - 0.07, 0.0);
		glVertex3d(xStart + 0.02, yStart + length - 0.07, 0.0);
		glEnd();

		glRasterPos2d(xStart - 9.0 / width, yStart + length + 15.0 / height);
		for (char c : label.toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_9_BY_15, c);
		}
		
		glBegin(GL_LINES);
		glVertex3d(xStart - 0.07, yStart + length - 0.1, 0);
		glVertex3d(xStart + 0.07, yStart + length - 0.1, 0);
		glEnd();
		
		glLineWidth(1.0);
		
		glBegin(GL_LINES);
		glVertex3d(-0.7 - 0.05, -0.7 + (length - 0.3)/ 2, 0);
		glVertex3d(-0.7 + 0.05, -0.7 + (length - 0.3)/ 2, 0);
		
		for (int i = 0; i < 10; i++) {
			glVertex3d(xStart - 0.03, yStart + 0.2 + i * (length - 0.3)/ 10.0, 0);
			glVertex3d(xStart + 0.03, yStart + 0.2 + i * (length - 0.3)/ 10.0, 0);
		}
		glEnd();		

		glRasterPos2d(xStart - 0.275 - 9.5 / width, yStart + length - 0.07 - 15.0 / height);
		for (char c : Double.toString(majorDivisionValue).toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}
	}

	double f(double x) {
		return 3.0 * x + 4.0;
	}

	void init() {
		/* select clearing color */
		glClearColor(1.0, 1.0, 1.0, 1.0);

		/* initialize viewing values */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);
	}
	
	int height;
	int width;

	void reshape(int width, int height) {
		this.width = width;
		this.height = height;
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
		new Lesson022().main(args.length, args);
	}
}
