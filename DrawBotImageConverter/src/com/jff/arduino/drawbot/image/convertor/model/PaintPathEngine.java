package com.jff.arduino.drawbot.image.convertor.model;

import java.util.ArrayList;
import java.util.List;

public class PaintPathEngine {


    public List<EngineState> states = new ArrayList<EngineState>();


    public boolean add(EngineState engineState) {
        return states.add(engineState);
    }

    public boolean remove(Object o) {
        return states.remove(o);
    }

    public void print() {


        for(EngineState engineState : states) {

            System.out.println(engineState);
        }

        System.out.println(states.size());
    }
}
