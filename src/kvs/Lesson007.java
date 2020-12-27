package kvs;

import com.pflager.glut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class Lesson007 extends glut {

	static GLCanvas canvas;
	private int WindowWidth = 800, WindowHeight = 800;
	private double gluOrthoWidth = WindowWidth / 2, gluOrthoHeight = WindowHeight / 2;
	private int XAxisGridSize = (int) (WindowWidth / 10), YAxisGridSize = (int) (WindowHeight / 10);
	private double pan_x = 0.0;
	private double pan_y = 0.0;
	private Scale scaleA , scaleB,scaleC,scaleD;
	private Label LabelAVal , LabelBVal,LabelCVal,LabelDVal;

	void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glClear(GL_COLOR_BUFFER_BIT);// clear all pixels
		DrawXaxis(2.0, true, XAxisGridSize);
		DrawYaxis(2.0, true, YAxisGridSize);
		glColor3d(1, 0, 0);
		DrawDefaultCurve(5.0);
		glColor3d(0.5, 0.5, 1);
		DrawCurve(1.5);
		glFlush();// flushes buffer

		canvas.swapBuffers();
	}

	void DrawDefaultCurve(Double lineWidth) {
		glEnable(GL_LINE_STIPPLE);
		glLineStipple(1, 0xAAAA);
		glLineWidth(lineWidth);
		glBegin(GL_LINE_STRIP);
		for (int i = -20; i < 200; i++) {
			glVertex3d(i, EvaluateDefaultCurveEquationForY(i), 0);
		}
		
		glEnd();
		glDisable(GL_LINE_STIPPLE);
	}

		private double EvaluateCurveEquationForY(double x) {
			LabelAVal.setText(Integer.toString(scaleA.getSelection()));
			LabelBVal.setText(Integer.toString(scaleB.getSelection()));
			LabelCVal.setText(Integer.toString(scaleC.getSelection()));
			LabelDVal.setText(Integer.toString(scaleD.getSelection()));
			return (scaleA.getSelection() * (Math.pow(x , 3) + scaleB.getSelection() * Math.pow(x , 2))) + scaleC.getSelection() * x  + scaleD.getSelection();
		}

		
		void DrawCurve(Double lineWidth) {
			glLineWidth(lineWidth);
			glBegin(GL_LINE_STRIP);
			for (int i = -20; i < 200; i++) {
				glVertex3d(i, EvaluateCurveEquationForY(i), 0);
			}
			glEnd();
		}

		// f:y=-300 (((x)/(85)-1.2)^(3)+ ((x)/(85)-1.2)^(2))+45
			private double EvaluateDefaultCurveEquationForY(double x) {
				return (-300 * (Math.pow(((x / 85) - 1.1), 3) + Math.pow(((x / 85) - 1.1), 2))) + 45;
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

	}

	void init() {
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

	void reshape(int w, int h) {
		display();
	}

	private void mouse(int button, int state, int x, int y) {

	}

	private void execute() {
		glutInit();
		final Display display = new Display();
		final Shell shell = new Shell(display);
		//shell.setBackground(SWTResourceManager.getColor(169, 169, 169));
		CreateGUIElements(display, shell);
		
		init();
		{ // sort of glutInitWindowSize()
			shell.setSize(1070, 850);
			glViewport(0, 0, 800, 800);
		}

		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));

		canvas.addListener(SWT.MouseDown, event -> mouse(event.button - 1, GLUT_DOWN, event.x, event.y));

		display.asyncExec(new Runnable() {
			public void run() {
				display();
				display.asyncExec(this);
			}
		});

		{ // sort of glutCreateWindow()
			shell.setText("Lesson007");
			shell.open();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void CreateGUIElements(Display display, Shell shell) {
			

		Label lblEqua = new Label(shell, SWT.LEFT);
		lblEqua.setBounds(35, 40, 200, 40);
		lblEqua.setText("f(x)=ax^3+b x^2+cx+d");
		FontData[] fD = lblEqua.getFont().getFontData();
		fD[0].setHeight(10);
		lblEqua.setFont( new Font(display,fD[0]));
		
		 scaleA = new Scale(shell, SWT.VERTICAL);
		scaleA.setBounds(20, 80, 50, 200);
		scaleA.setMinimum(-500);
		scaleA.setIncrement(1);
		scaleA.setMaximum(500);

		 scaleB = new Scale(shell, SWT.VERTICAL );
		scaleB.setBounds(80 , 80, 50, 200);
		scaleB.setMinimum(-500);
		scaleB.setIncrement(1);
		scaleB.setMaximum(500);

		
		 scaleC = new Scale(shell, SWT.VERTICAL );
		scaleC.setBounds(140 , 80, 50, 200);
		scaleC.setMinimum(-500);
		scaleC.setIncrement(1);
		scaleC.setMaximum(500);

		
		 scaleD = new Scale(shell, SWT.VERTICAL );
		scaleD.setBounds(190, 80, 50, 200);
		scaleD.setMinimum(-500);
		scaleD.setIncrement(1);
		scaleD.setMaximum(500);

		
		Label a = new Label(shell, SWT.CENTER);
		a.setBounds(20, 300, 50, 50);
		a.setText("a");
		
		Label b = new Label(shell, SWT.CENTER);
		b.setBounds(80 , 300, 50, 50);
		b.setText("b");
		
		Label c = new Label(shell, SWT.CENTER);
		c.setBounds(140 , 300, 50, 50);
		c.setText("c");
		
		Label d = new Label(shell, SWT.CENTER);
		d.setBounds(190, 300, 50, 50);
		d.setText("d");
		
		
		LabelAVal = new Label(shell, SWT.CENTER);
		LabelAVal.setBounds(20, 350, 50, 50);
		LabelAVal.setText(" ");
		
		LabelBVal = new Label(shell, SWT.CENTER);
		LabelBVal.setBounds(80 , 350, 50, 50);
		LabelBVal.setText(" ");
		
		LabelCVal = new Label(shell, SWT.CENTER);
		LabelCVal.setBounds(140 , 350, 50, 50);
		LabelCVal.setText(" ");
		
		LabelDVal = new Label(shell, SWT.CENTER);
		LabelDVal.setBounds(190, 350, 50, 50);
		LabelDVal.setText(" ");
		
		

		GLData glData = new GLData();
		glData.doubleBuffer = true;
		canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, glData);
		canvas.setBounds(250,0,800,800);
		canvas.setCurrent();
		
		
	}

	public static void main(String[] args) {

		new Lesson007().execute();
	}

}
