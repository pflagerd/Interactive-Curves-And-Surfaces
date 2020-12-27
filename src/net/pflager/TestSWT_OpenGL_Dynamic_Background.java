package net.pflager;

import com.pflager.glu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

public class TestSWT_OpenGL_Dynamic_Background extends glu {

	static GLCanvas canvas;
	private Text redText;
	private Text greenText;
	private Text blueText;
	
	Scale redScale;
	Scale greenScale;
	Scale blueScale;
	
	public void display() {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glClear(GL_COLOR_BUFFER_BIT);

		glColor3f(1.0f, 1.0f, 1.0f);
		glBegin(GL_POLYGON);
		{
			glVertex3f(0.25f, 0.25f, 0.0f);
			glVertex3f(0.75f, 0.25f, 0.0f);
			glVertex3f(0.75f, 0.75f, 0.0f);
			glVertex3f(0.25f, 0.75f, 0.0f);
		}
		glEnd();

		/*
		 * don't wait! start processing buffered OpenGL routines
		 */
		canvas.swapBuffers();
	}

	
	public void init() {
		canvas.setCurrent();

		/* select clearing color */
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		/* initialize viewing values */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	void execute() {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(970, 564);
		//shell.setSize(shell.computeTrim(0, 0, 250, 250).width, shell.computeTrim(0, 0, 250, 250).height);
		shell.setText("SWT Application");
		shell.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.EAST);
		
		redScale = new Scale(composite, SWT.NONE);
		redScale.setPageIncrement(16);
		redScale.setMaximum(127);
		redScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				redText.setText(new Integer(redScale.getSelection()).toString());
				updateBackgroundFromScales();
			}
		});
		redScale.setBounds(0, 41, 122, 42);
		
		greenScale = new Scale(composite, SWT.NONE);
		greenScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				greenText.setText(new Integer(greenScale.getSelection()).toString());
				updateBackgroundFromScales();
			}
		});
		greenScale.setPageIncrement(16);
		greenScale.setMaximum(127);
		greenScale.setBounds(0, 173, 122, 42);
		
		blueScale = new Scale(composite, SWT.NONE);
		blueScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				blueText.setText(new Integer(blueScale.getSelection()).toString());
				updateBackgroundFromScales();
			}
		});
		blueScale.setPageIncrement(16);
		blueScale.setMaximum(127);
		blueScale.setBounds(0, 304, 122, 42);
		
		Label lblRed = new Label(composite, SWT.NONE);
		lblRed.setAlignment(SWT.CENTER);
		lblRed.setBounds(31, 20, 55, 15);
		lblRed.setText("Red");
		
		Label lblGreen = new Label(composite, SWT.NONE);
		lblGreen.setText("Green");
		lblGreen.setAlignment(SWT.CENTER);
		lblGreen.setBounds(31, 152, 55, 15);
		
		Label lblBlue = new Label(composite, SWT.NONE);
		lblBlue.setText("Blue");
		lblBlue.setAlignment(SWT.CENTER);
		lblBlue.setBounds(31, 281, 55, 15);
		
		redText = new Text(composite, SWT.BORDER);
		redText.setText("0");
		redText.setBounds(23, 89, 76, 21);
		
		greenText = new Text(composite, SWT.BORDER);
		greenText.setText("0");
		greenText.setBounds(23, 221, 76, 21);
		
		blueText = new Text(composite, SWT.BORDER);
		blueText.setText("0");
		blueText.setBounds(23, 352, 76, 21);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.NORTH);
		
		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayoutData(BorderLayout.WEST);
		
		GLData glData = new GLData();
		glData.doubleBuffer = true;
		
		canvas = new GLCanvas(shell, SWT.NONE, glData);
		canvas.setLayoutData(BorderLayout.CENTER);
		canvas.setCurrent();

		shell.open();
		shell.layout();
		
		init();
		reshape(canvas.getBounds().width, canvas.getBounds().height);
		canvas.redraw();
		
		canvas.addPaintListener(event -> display());

		canvas.addListener(SWT.Resize, event -> reshape(canvas.getBounds().width, canvas.getBounds().height));
		
//        display.asyncExec(new Runnable() {
//            public void run() {            	
//            	// Want to wait here until backgroundChanged becomes "true"
//
//            	if (backgroundChanged) {
//            		backgroundChanged = false;
//            	}
//            	
//            	System.out.println("Got Here.");
//            	display();
//            	display.asyncExec(this);
//            }
//        });
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	long l = 0;
	
	void updateBackgroundFromScales() {
		System.out.println("Got here " + l++);
		glClearColor((float)redScale.getSelection()/127.0f, (float)greenScale.getSelection()/127.0f, (float)blueScale.getSelection()/127.0f, 1.0f);
		canvas.redraw();
	}
	
	
	void reshape(int w, int h) {
		if (canvas.isDisposed()) {
			return;
		}
		canvas.setCurrent();

		glViewport(0, 0, w, h);
		glMatrixMode(GL_PROJECTION); 
		glLoadIdentity();
		glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new TestSWT_OpenGL_Dynamic_Background().execute();
	}
}
