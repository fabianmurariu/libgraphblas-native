package com.github.fabianmurariu.libgraphblas;

import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import graphblas.GraphBLASLibrary;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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
            assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_BOOL, 5, 5));
            assertEquals(0, g.GrB_Matrix_setElement_BOOL(grBM.getValue(), true, 4, 4));
            IntByReference fakeBool2 = new IntByReference();
            assertEquals(0, g.GrB_Matrix_extractElement_BOOL(fakeBool2, grBM.getValue(), 4, 4));
            assertEquals(255, fakeBool2.getValue());
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
            assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 5, 5));
            assertEquals(0, g.GrB_Matrix_setElement_INT8(grBM.getValue(), (byte) 8, 4, 4));
            IntByReference fakeBool2 = new IntByReference();
            assertEquals(0, g.GrB_Matrix_extractElement_INT8(fakeBool2, grBM.getValue(), 4, 4));
            assertEquals(8, fakeBool2.getValue());
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
            assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 5, 5));
            LongBuffer intByReference0 = LongBuffer.allocate(8);
            assertEquals(0, g.GrB_Matrix_nvals(intByReference0, grBM.getValue()));
            assertEquals(0, intByReference0.get());
            assertEquals(0, g.GrB_Matrix_setElement_INT8(grBM.getValue(), (byte) 8, 4, 4));
            LongBuffer intByReference = LongBuffer.allocate(8);
            assertEquals(0, g.GrB_Matrix_nvals(intByReference, grBM.getValue()));
            assertEquals(1, intByReference.get());
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }

    @Test
//    @Ignore
    public void testBuildMatrixINT8() {
        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        GraphBLASLibrary.GrB_Matrix_ByReference grBM = null;
        try {
            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            grBM = new GraphBLASLibrary.GrB_Matrix_ByReference();
            assertEquals(0, g.GrB_Matrix_new(grBM, g.GrB_INT8, 2, 2));
            long i[] = {0};
            long j[] = {0};
            byte xs[] = {2};
            assertEquals(0, g.GrB_Matrix_build_INT8(grBM.getValue(), i, j, xs, 1L, g.GrB_SECOND_INT8));
        } finally {
            if (grBM != null) {
                assertEquals(0, g.GrB_Matrix_free(grBM));
            }
            g.GrB_wait();
            g.GrB_finalize();
        }

    }

    @Test
    public void testVector() {

        GraphBLASLibrary g = GraphBLASLibrary.INSTANCE;
        try {
            long n = 7L;

            g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING);
            GraphBLASLibrary.GrB_Vector_ByReference v = new GraphBLASLibrary.GrB_Vector_ByReference();
            assertEquals(0, g.GrB_Vector_new(v, g.GrB_INT32, n));

            GraphBLASLibrary.GrB_Vector_ByReference q = new GraphBLASLibrary.GrB_Vector_ByReference();
            assertEquals(0, g.GrB_Vector_new(q, g.GrB_BOOL, n));
            assertEquals(0, g.GrB_Vector_setElement_BOOL(q.getValue(), true, 1L));

            GraphBLASLibrary.GrB_Matrix_ByReference A = new GraphBLASLibrary.GrB_Matrix_ByReference();
            assertEquals(0, g.GrB_Matrix_new(A, g.GrB_BOOL, n, n));

            long i[] = {0, 0, 1, 1, 2, 3, 3, 4, 5, 6, 6, 6};
            long j[] = {1, 3, 4, 6, 5, 0, 2, 5, 2, 2, 3, 4};
            boolean xs[] = {true, true, true, true, true, true, true, true, true, true, true, true};
            assertEquals(0, g.GrB_Matrix_build_BOOL(A.getValue(), i, j, xs, 12L, g.GrB_SECOND_BOOL));


            GraphBLASLibrary.GrB_Monoid_ByReference lor = new GraphBLASLibrary.GrB_Monoid_ByReference();
            assertEquals(0, g.GrB_Monoid_new_BOOL(lor, g.GrB_LOR, false));

            GraphBLASLibrary.GrB_Semiring_ByReference booleanSR = new GraphBLASLibrary.GrB_Semiring_ByReference();
            assertEquals(0, g.GrB_Semiring_new(booleanSR, lor.getValue(), g.GrB_LAND));

            GraphBLASLibrary.GrB_Descriptor_ByReference desc = new GraphBLASLibrary.GrB_Descriptor_ByReference();
            assertEquals(0, g.GrB_Descriptor_new(desc));

            assertEquals(0, g.GrB_Descriptor_set(desc.getValue(), GraphBLASLibrary.GrB_Desc_Field.GrB_MASK, GraphBLASLibrary.GrB_Desc_Value.GrB_SCMP));
            assertEquals(0, g.GrB_Descriptor_set(desc.getValue(), GraphBLASLibrary.GrB_Desc_Field.GrB_OUTP, GraphBLASLibrary.GrB_Desc_Value.GrB_REPLACE));

            // 1 loop BFS
            ByteByReference successor = new ByteByReference();
            successor.setValue((byte)1);
            assertEquals(0, g.GrB_Vector_assign_INT32(v.getValue(), q.getValue(), null, 1, g.GrB_ALL , n, null));
            assertEquals(0, g.GrB_vxm(q.getValue(), v.getValue(), null, booleanSR.getValue(), q.getValue(), A.getValue(), desc.getValue()));
            assertEquals(0, g.GrB_Vector_reduce_BOOL(successor, null, lor.getValue(), q.getValue(), desc.getValue()));
            assertEquals(1, successor.getValue());
        } finally {
            g.GrB_wait();
            g.GrB_finalize();
        }
    }
}
