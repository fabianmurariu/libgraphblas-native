package com.github.fabianmurariu.libgraphblas;

import com.sun.jna.ptr.IntByReference;
import graphblas.GraphBLASLibrary;
import org.junit.Assert;
import org.junit.Test;

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
}
