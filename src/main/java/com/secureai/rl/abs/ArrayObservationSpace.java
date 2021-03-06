package com.secureai.rl.abs;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

@Value()
@NonFinal // This is the fix for extending rl4j
public class ArrayObservationSpace<O> implements ObservationSpace<O> {

    String name;
    int[] shape;
    INDArray low;
    INDArray high;

    public ArrayObservationSpace(int[] shape) {
        name = "Custom";
        this.shape = shape;
        low = Nd4j.create(1);
        high = Nd4j.create(1);
    }

    public int size() {
        return this.getShape()[0];
    }

}
