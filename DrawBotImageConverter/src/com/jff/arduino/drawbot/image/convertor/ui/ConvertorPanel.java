package com.jff.arduino.drawbot.image.convertor.ui;

import com.jff.arduino.drawbot.image.convertor.controller.ConverterController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class ConvertorPanel extends Composite {

    private ConverterController controller;

    public ConvertorPanel(Composite parent) {
        super(parent, SWT.NONE);
        init();
    }

    private void init() {

        controller = new ConverterController();

        Layout layout = new GridLayout(3, false);
        this.setLayout(layout);

        ImageInputPanel imageInputPanel = new ImageInputPanel(this, controller);
        ImageConvertPanel imageConvertPanel = new ImageConvertPanel(this, controller);
        ImageOutputPanel imageOutputPanel = new ImageOutputPanel(this, controller);

        controller.setImageConvertPanel(imageConvertPanel);
        controller.setImageInputPanel(imageInputPanel);
        controller.setImageOutputPanel(imageOutputPanel);

    }
}
