package com.jff.arduino.drawbot.image.convertor.main;

import com.jff.arduino.drawbot.image.convertor.ui.ConvertorPanel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

    public static void main(String args[]) {

        final Display display = new Display();
        final Shell shell = new Shell(display);

        ConvertorPanel convertorPanel = new ConvertorPanel(shell);

        shell.setLayout(new FillLayout());

        shell.setSize(800,600);
        shell.open ();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();

    }
}
