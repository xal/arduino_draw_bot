package com.jff.arduino.drawbot.image.convertor.ui;

import com.jff.arduino.drawbot.image.convertor.controller.ConverterController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ImageConvertPanel extends Composite {
    private Button buttonConvert;

    private ConverterController controller;

    public ImageConvertPanel(Composite parent, ConverterController controller) {
        super(parent, SWT.NONE);
        this.controller = controller;
        init();
    }

    private void init() {


        Layout layout = new GridLayout(1, false);
        this.setLayout(layout);





        buttonConvert = new Button(this, SWT.NONE);
        buttonConvert.setText(">>");

        buttonConvert.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                controller.applyConvert();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }
}
