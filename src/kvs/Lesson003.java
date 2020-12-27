package kvs;

import com.pflager.glut;

public class Lesson003 extends glut {

	private int WindowWidth = 800, WindowHeight = 800;
	private double gluOrthoWidth = WindowWidth / 2, gluOrthoHeight = WindowHeight / 2;
	private int XAxisGridSize = (int) (WindowWidth / 10), YAxisGridSize = (int) (WindowHeight / 10);
	private double LastZoomFactorWidth = WindowWidth / 2;
	private double pan_x = 0.0;
	private double pan_y = 0.0;
	private double panstep = 10;

	void DrawCurve(Double PointSize) {
		glPointSize(PointSize);
		glBegin(GL_POINTS);
		for (double i = 0; i <= 360; i++) {
			double[] XY = EvaluateCurveEquationForY(i,80);
			glVertex3d(XY[0], XY[1], 0);
		}
		glEnd();
	}

	// x = R * sin(theta) , y = R * cos(theta)
	private double[] EvaluateCurveEquationForY(double thetaIncrement, double circleRadious) {
		return (new double[] {(circleRadious * Math.sin(thetaIncrement)) ,(circleRadious * Math.cos(thetaIncrement))} );
	}

	public void init() {
		glClearColor(255, 255, 255, 0);
		glMatrixMode(GL_PROJECTION);/*
									 * This indicates that the current matrix specifies the projection
									 * transformation; the following transformation calls then affect the projection
									 * matrix. other matrix are GL_Modelview etc
									 */
		glLoadIdentity(); // clear the matrix, viewing transformation
		gluOrtho2D(-(gluOrthoWidth), (gluOrthoWidth), -(gluOrthoHeight), (gluOrthoHeight));
		glTranslatef(-pan_x, -pan_y, 0);
	}

	private int NearestTenth(int Value) {
		int res = Value;
		if (Integer.toString(res).toCharArray().length == 2) {
			res = (int) (Math.round((res) / 10.0) * 10.0);
		}
		return res;
	}

	private void DrawXaxis(Double lineWidth, boolean DrawXGrid, int GridSize) {
		glColor3d(0.3, 0.3, 0);
		glLineWidth(lineWidth);

		double arrowWidth = gluOrthoWidth * 0.03, arrowHeight = gluOrthoHeight * 0.03;

		glBegin(GL_LINE_STRIP); // Axis Line with right arrow Head
		glVertex2d(-(gluOrthoWidth - pan_x), 0);
		glVertex2d(gluOrthoWidth + pan_x, 0);

		glVertex3d((gluOrthoWidth + pan_x) - arrowWidth, arrowHeight, 0);

		glVertex3d(gluOrthoWidth + pan_x, 0, 0);
		glVertex3d((gluOrthoWidth + pan_x) - arrowWidth, -arrowHeight, 0);

		glEnd();

		glBegin(GL_LINE_STRIP); // left arrow head
		glVertex3d(-(gluOrthoWidth - pan_x), 0, 0);
		glVertex3d(-(gluOrthoWidth - pan_x) + arrowWidth, arrowHeight, 0);

		glVertex3d(-(gluOrthoWidth - pan_x), 0, 0);
		glVertex3d(-(gluOrthoWidth - pan_x) + arrowWidth, -arrowHeight, 0);
		glEnd();

		glLineWidth(5);
		glRasterPos2d(-(gluOrthoWidth - pan_x), 1.5 * arrowWidth);
		for (char c : "X Axis".toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}

		// This creates X-axis gird
		if (DrawXGrid == true) {
			glEnable(GL_LINE_STIPPLE);
			glLineWidth(0.5);
			glLineStipple(1, 0xAAAA);
			int GrindLineCount = (int) (gluOrthoHeight / GridSize);
			for (int i = 1; i <= (GrindLineCount); i++) {
				glBegin(GL_LINE_STRIP); // Right side gird line
				glVertex2d((gluOrthoWidth + pan_x), i * GridSize);
				glVertex2d(-(gluOrthoWidth - pan_x), i * GridSize);
				glEnd();

				glBegin(GL_LINE_STRIP); // Right side gird line
				glVertex2d((gluOrthoWidth + pan_x), -(i * GridSize));
				glVertex2d(-(gluOrthoWidth - pan_x), -(i * GridSize));
				glEnd();
			}
			glDisable(GL_LINE_STIPPLE);

			for (int i = 1; i <= (GrindLineCount); i++) {
				glBegin(GL_LINE_STRIP); // Right side gird line
				glVertex2d(GridSize / 6, i * GridSize);
				glVertex2d(-GridSize / 6, i * GridSize);
				glEnd();

				glRasterPos2d(GridSize / 3, i * GridSize);
				for (char c : Integer.toString(i * GridSize).toCharArray()) {
					glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
				}

				glBegin(GL_LINE_STRIP); // Left side gird line
				glVertex2d(GridSize / 6, -(i * GridSize));
				glVertex2d(-GridSize / 6, -(i * GridSize));
				glEnd();
				glRasterPos2d(GridSize / 3, -(i * GridSize));
				for (char c : Integer.toString(-i * GridSize).toCharArray()) {
					glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
				}
			}

		}

	}

	private void DrawYaxis(Double lineWidth, boolean DrawYGrid, int GridSize) {
		glColor3d(0.3, 0.3, 0);
		glLineWidth(lineWidth);

		double arrowWidth = gluOrthoWidth * 0.03, arrowHeight = gluOrthoHeight * 0.03;

		glBegin(GL_LINE_STRIP); // Axis Line with top arrow Head
		glVertex2d(0, -(gluOrthoHeight - pan_y));
		glVertex2d(0, (gluOrthoHeight + pan_y));

		glVertex2d(arrowWidth, (gluOrthoHeight + pan_y) - arrowHeight);
		glVertex2d(0, (gluOrthoHeight + pan_y));
		glVertex2d(-arrowWidth, (gluOrthoHeight + pan_y) - arrowHeight);
		glEnd();

		glLineWidth(5);
		glRasterPos2d(1.5 * arrowWidth, (gluOrthoHeight + pan_y));
		for (char c : "Y Axis".toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}

		glLineWidth(lineWidth);
		glBegin(GL_LINE_STRIP); // bottom arrow head
		glVertex2d(0, -(gluOrthoHeight - pan_y));

		glVertex2d(arrowWidth, -(gluOrthoHeight - pan_y) + arrowHeight);
		glVertex2d(0, -(gluOrthoHeight - pan_y));
		glVertex2d(-arrowWidth, -(gluOrthoHeight - pan_y) + arrowHeight);
		glEnd();

		glLineWidth(5);
		glRasterPos2d(1.5 * arrowWidth, -(gluOrthoHeight - pan_y));
		for (char c : "Y Axis".toCharArray()) {
			glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
		}

		// X-axis Grid creation
		if (DrawYGrid == true) {
			glEnable(GL_LINE_STIPPLE);
			glLineWidth(0.5);
			glLineStipple(1, 0xAAAA);
			int GrindLineCount = (int) (gluOrthoWidth / GridSize);
			for (int i = 1; i <= (GrindLineCount); i++) {
				glBegin(GL_LINE_STRIP); // upper grid line
				glVertex2d(i * GridSize, (gluOrthoHeight + pan_y));
				glVertex2d(i * GridSize, -(gluOrthoHeight - pan_y));
				glEnd();

				glBegin(GL_LINE_STRIP); // lower grid line
				glVertex2d(-(i * GridSize), (gluOrthoHeight + pan_y));
				glVertex2d(-(i * GridSize), -(gluOrthoHeight - pan_y));
				glEnd();
			}
			glDisable(GL_LINE_STIPPLE);

			for (int i = 1; i <= (GrindLineCount); i++) {
				glBegin(GL_LINE_STRIP); // upper grid line
				glVertex2d(i * GridSize, GridSize / 6);
				glVertex2d(i * GridSize, -GridSize / 6);
				glEnd();

				glRasterPos2d(i * GridSize, -GridSize / 3);
				for (char c : Integer.toString(i * GridSize).toCharArray()) {
					glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
				}

				glBegin(GL_LINE_STRIP); // lower grid line
				glVertex2d(-(i * GridSize), GridSize / 6);
				glVertex2d(-(i * GridSize), -GridSize / 6);
				glEnd();

				glRasterPos2d(-(i * GridSize), -GridSize / 3);
				for (char c : Integer.toString(i * GridSize).toCharArray()) {
					glutBitmapCharacter(GLUT_BITMAP_8_BY_13, c);
				}
			}
		}

	}

	void pan(double dx, double dy) {
		pan_x += dx;
		pan_y += dy;
	}

	void resetPan() {
		pan_x = 0.0;
		pan_y = 0.0;
	}

	void special(int k, int x, int y) {
		if (k == GLUT_KEY_HOME)
			resetPan();
		else if (k == GLUT_KEY_LEFT)
			pan(-panstep, 0);
		else if (k == GLUT_KEY_RIGHT)
			pan(panstep, 0);
		else if (k == GLUT_KEY_DOWN)
			pan(0, -panstep);
		else if (k == GLUT_KEY_UP)
			pan(0, panstep);
		glutPostRedisplay();
	}

	void mouse(int button, int state, int x, int y) {
		double ZoomFactor = 0.01;
		// Wheel reports as button 3(scroll up) and button 4(scroll down)
		if ((button == 3) || (button == 4)) // It's a wheel event
		{
			// Each wheel event reports like a button click, GLUT_DOWN then GLUT_UP
			if (state == GLUT_UP)
				return; // Disregard redundant GLUT_UP events
			if (button == 3) {
				gluOrthoWidth = gluOrthoWidth + glutGet(GLUT_WINDOW_WIDTH) * ZoomFactor;
				gluOrthoHeight = gluOrthoHeight + glutGet(GLUT_WINDOW_HEIGHT) * ZoomFactor;
				if ((gluOrthoWidth - LastZoomFactorWidth) >= 80) {
					XAxisGridSize = NearestTenth((int) (gluOrthoWidth / (5)));
					YAxisGridSize = NearestTenth((int) (gluOrthoHeight / (5)));
					LastZoomFactorWidth = gluOrthoWidth;
				}
				init();
				display();
			} else {
				System.out.println(" Scroll Down is at X = " + x + ": Y = " + y);
				if (((gluOrthoWidth - glutGet(GLUT_WINDOW_WIDTH) * ZoomFactor) > 0)
						& ((gluOrthoHeight - glutGet(GLUT_WINDOW_HEIGHT) * ZoomFactor) > 0)) {
					if ((gluOrthoWidth / 2) >= 16.0) {
						gluOrthoWidth = gluOrthoWidth - glutGet(GLUT_WINDOW_WIDTH) * ZoomFactor;
						gluOrthoHeight = gluOrthoHeight - glutGet(GLUT_WINDOW_HEIGHT) * ZoomFactor;
					}

					if ((gluOrthoWidth / 2) <= 16.0) {
						XAxisGridSize = 10;
						YAxisGridSize = 10;
						LastZoomFactorWidth = gluOrthoWidth;
					} else {
						if (Math.abs((gluOrthoWidth - LastZoomFactorWidth)) >= 80) {
							XAxisGridSize = NearestTenth((int) (gluOrthoWidth / (5)));
							YAxisGridSize = NearestTenth((int) (gluOrthoHeight / (5)));
							LastZoomFactorWidth = gluOrthoWidth;
						}
					}
					init();
					display();
				}
			}
		}
	}

	void keyboard(char key, int x, int y) {
		if (key == '0')
			resetPan();
		else if (key == '4')
			pan(-panstep, 0);
		else if (key == '6')
			pan(panstep, 0);
		else if (key == '2')
			pan(0, -panstep);
		else if (key == '8')
			pan(0, panstep);
		else
			return;
		init();
		display();
	}

	void display() {
		glClear(GL_COLOR_BUFFER_BIT);// clear all pixels
		DrawXaxis(2.0, true, XAxisGridSize);
		DrawYaxis(2.0, true, YAxisGridSize);
		glColor3d(0.5, 0.5, 1);
		DrawCurve(1.5);
		glFlush();// flushes buffer
	}

	int main(int argc, String[] argv) {
		glutInit(argc, argv);
		glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
		glutInitWindowSize(WindowWidth, WindowHeight);
		glutInitWindowPosition(100, 100);
		glutCreateWindow("Lesson003");
		init();
		glutDisplayFunc(this::display);
		glutKeyboardFunc(this::keyboard);
		glutMouseFunc(this::mouse);
		glutMainLoop();
		return 0;
	}

	public static void main(String[] args) {

		new Lesson003().main(args.length, args);
	}

}
