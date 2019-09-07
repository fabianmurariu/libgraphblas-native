package com.github.fabianmurariu.libgraphblas;

import com.sun.jna.ptr.IntByReference;
import graphblas.GraphBLASLibrary;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class SanityTest {

    @Test
    public void testInitBlockingAndFinalize() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
        } finally {
            g.GrB_finalize();
        }
    }

    @Test
    public void testInitNonBlockingWaitAndFinalize() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_NONBLOCKING);
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }

    @Test
    public void testCreateMatrix() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            GraphBLASLibrary.GrB_Matrix_ByReference grBM = new GraphBLASLibrary.GrB_Matrix_ByReference();
            Assert.assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_BOOL, 5, 5));
            Assert.assertEquals(0, g.GrB_Matrix_setElement_BOOL(grBM.getValue(), true, 4, 4));
            IntByReference fakeBool2 = new IntByReference();
            Assert.assertEquals(0, g.GrB_Matrix_extractElement_BOOL(fakeBool2, grBM.getValue(), 4, 4));
            Assert.assertEquals(255, fakeBool2.getValue());
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }

    @Test
    public void testCreateMatrixINT8() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            GraphBLASLibrary.GrB_Matrix_ByReference grBM = new GraphBLASLibrary.GrB_Matrix_ByReference();
            Assert.assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 5, 5));
            Assert.assertEquals(0, g.GrB_Matrix_setElement_INT8(grBM.getValue(), (byte)8, 4, 4));
            IntByReference fakeBool2 = new IntByReference();
            Assert.assertEquals(0, g.GrB_Matrix_extractElement_INT8(fakeBool2, grBM.getValue(), 4, 4));
            Assert.assertEquals(8, fakeBool2.getValue());
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }

    @Test
    public void testCreateMatrixINT8NVals() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            GraphBLASLibrary.GrB_Matrix_ByReference grBM = new GraphBLASLibrary.GrB_Matrix_ByReference();
            Assert.assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 5, 5));
            IntByReference intByReference0 = new IntByReference();
            Assert.assertEquals(0, g.GrB_Matrix_nvals(intByReference0,grBM.getValue()));
            Assert.assertEquals(0, intByReference0.getValue());
            Assert.assertEquals(0, g.GrB_Matrix_setElement_INT8(grBM.getValue(), (byte)8, 4, 4));
            IntByReference intByReference = new IntByReference();
            Assert.assertEquals(0, g.GrB_Matrix_nvals(intByReference,grBM.getValue()));
            Assert.assertEquals(1, intByReference.getValue());
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }

    @Test
//    @Ignore
    public void testBuildMatrixINT8() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;

        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            GraphBLASLibrary.GrB_Matrix_ByReference grBM = new GraphBLASLibrary.GrB_Matrix_ByReference();
            Assert.assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 2, 2));
            long i[] = {0};
            long j[] = {0};
            byte xs[] = {2};
            Assert.assertEquals(0, g.GrB_Matrix_build_INT8(grBM.getValue(), i ,j,  xs, 1L, g.GrB_SECOND_INT8));
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }

    }
}
