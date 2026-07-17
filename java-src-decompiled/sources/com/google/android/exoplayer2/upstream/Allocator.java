package com.google.android.exoplayer2.upstream;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface Allocator {

    public interface AllocationNode {
        Allocation getAllocation();

        AllocationNode next();
    }

    Allocation allocate();

    int getIndividualAllocationLength();

    int getTotalBytesAllocated();

    void release(Allocation allocation);

    void release(AllocationNode allocationNode);

    void trim();
}
