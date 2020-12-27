/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package net.pflager.redbook.swt;

import com.pflager.glut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class hello extends glut {

    static GLCanvas canvas;

    public void display() {
        if (canvas.isDisposed()) {
            return;
        }
        canvas.setCurrent();

        /* clear all pixels */
        glClear(GL_COLOR_BUFFER_BIT);

        /*
         * draw white polygon (rectangle) with corners at (0.25, 0.25, 0.0) and (0.75,
         * 0.75, 0.0)
         */
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
        glFlush();
    }

    public void init() {
        /* select clearing color */
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        /* initialize viewing values */
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
    }

    public void execute(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        canvas = new GLCanvas(shell, SWT.NO_BACKGROUND, new GLData());
        canvas.setCurrent();

        init();

        { // sort of glutInitWindowSize()
            shell.setSize(shell.computeTrim(0, 0, 250, 250).width, shell.computeTrim(0, 0, 250, 250).height);
            glViewport(0, 0, canvas.getBounds().width, canvas.getBounds().height);
        }

        canvas.addPaintListener(event -> display());

        { // sort of glutCreateWindow()
            shell.setText("hello");
            shell.open();
        }

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    public static void main(String[] args) {
        new hello().execute(args);
    }
}
