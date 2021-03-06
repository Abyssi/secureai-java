package com.secureai.utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import static org.nd4j.linalg.indexing.NDArrayIndex.interval;

public class Nd4jUtils {
    public static INDArray hInsert(INDArray a, INDArray b, int index) {
        if (index == 0)
            return Nd4j.hstack(b, a.get(NDArrayIndex.all(), interval(index, 1, a.columns())));
        else if (index == a.columns())
            return Nd4j.hstack(a.get(NDArrayIndex.all(), interval(0, 1, index)), b);

        return Nd4j.hstack(a.get(NDArrayIndex.all(), interval(0, 1, index)), b, a.get(NDArrayIndex.all(), interval(index, 1, a.columns())));
    }

    public static INDArray hSwitch(INDArray a, INDArrayIndex from, INDArrayIndex to) {
        INDArray toTemp = a.get(to);
        a.put(new INDArrayIndex[]{to}, a.get(from));
        a.put(new INDArrayIndex[]{to}, toTemp);

        return a;
    }

    public static INDArray hDelete(INDArray a, INDArrayIndex indices) {
        return a.get(interval(0, 1, indices.offset()), interval(indices.end(), 1, a.columns()));
    }

    public static INDArray hMove(INDArray a, INDArrayIndex from, int index) {
        return Nd4jUtils.hInsert(Nd4jUtils.hDelete(a, from), a.get(from), index);
    }

    public static INDArray remap(INDArray a, INDArrayIndex[] fromIntervals, INDArrayIndex[] toIntervals) {
        INDArray res = Nd4j.zeros(a.shape());
        for (int i = 0; i < fromIntervals.length; i++)
            res.put(new INDArrayIndex[]{toIntervals[i]}, a.get(fromIntervals[i]));
        return res;
    }

    public static INDArrayIndex[] createRightCoveringShape(long[] shapeA, long[] shapeB) {
        INDArrayIndex[] ret = new INDArrayIndex[shapeA.length];

        for (int i = 0; i < ret.length; ++i) {
            long delta = shapeB[i] - shapeA[i];
            ret[i] = interval(delta, shapeA[i] + delta);
        }

        return ret;
    }

}
