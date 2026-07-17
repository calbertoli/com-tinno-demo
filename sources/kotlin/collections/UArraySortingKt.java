package kotlin.collections;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import kotlin.Metadata;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UArraySorting.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0010\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0010\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\u0014\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0019\u0010\u001a\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001e\u0010\u0014\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001f\u0010\u0016\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b \u0010\u0018\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b!\u0010\u001a\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", TtmlNode.LEFT, TtmlNode.RIGHT, "partition-4UcCI2c", "([BII)I", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "quickSort-oBK06Vg", "([III)V", "quickSort--nroSd4", "([JII)V", "quickSort-Aa5vz7o", "([SII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-oBK06Vg", "sortArray--nroSd4", "sortArray-Aa5vz7o", "kotlin-stdlib"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class UArraySortingKt {
    /* JADX INFO: renamed from: partition-4UcCI2c, reason: not valid java name */
    private static final int m760partition4UcCI2c(byte[] bArr, int i, int i2) {
        int i3;
        byte bM380getw2LRezQ = UByteArray.m380getw2LRezQ(bArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                i3 = bM380getw2LRezQ & 255;
                if (Intrinsics.compare(UByteArray.m380getw2LRezQ(bArr, i) & 255, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UByteArray.m380getw2LRezQ(bArr, i2) & 255, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                byte bM380getw2LRezQ2 = UByteArray.m380getw2LRezQ(bArr, i);
                UByteArray.m385setVurrAj0(bArr, i, UByteArray.m380getw2LRezQ(bArr, i2));
                UByteArray.m385setVurrAj0(bArr, i2, bM380getw2LRezQ2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-4UcCI2c, reason: not valid java name */
    private static final void m764quickSort4UcCI2c(byte[] bArr, int i, int i2) {
        int iM760partition4UcCI2c = m760partition4UcCI2c(bArr, i, i2);
        int i3 = iM760partition4UcCI2c - 1;
        if (i < i3) {
            m764quickSort4UcCI2c(bArr, i, i3);
        }
        if (iM760partition4UcCI2c < i2) {
            m764quickSort4UcCI2c(bArr, iM760partition4UcCI2c, i2);
        }
    }

    /* JADX INFO: renamed from: partition-Aa5vz7o, reason: not valid java name */
    private static final int m761partitionAa5vz7o(short[] sArr, int i, int i2) {
        int i3;
        short sM643getMh2AYeg = UShortArray.m643getMh2AYeg(sArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int iM643getMh2AYeg = UShortArray.m643getMh2AYeg(sArr, i) & UShort.MAX_VALUE;
                i3 = sM643getMh2AYeg & UShort.MAX_VALUE;
                if (Intrinsics.compare(iM643getMh2AYeg, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UShortArray.m643getMh2AYeg(sArr, i2) & UShort.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                short sM643getMh2AYeg2 = UShortArray.m643getMh2AYeg(sArr, i);
                UShortArray.m648set01HTLdE(sArr, i, UShortArray.m643getMh2AYeg(sArr, i2));
                UShortArray.m648set01HTLdE(sArr, i2, sM643getMh2AYeg2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-Aa5vz7o, reason: not valid java name */
    private static final void m765quickSortAa5vz7o(short[] sArr, int i, int i2) {
        int iM761partitionAa5vz7o = m761partitionAa5vz7o(sArr, i, i2);
        int i3 = iM761partitionAa5vz7o - 1;
        if (i < i3) {
            m765quickSortAa5vz7o(sArr, i, i3);
        }
        if (iM761partitionAa5vz7o < i2) {
            m765quickSortAa5vz7o(sArr, iM761partitionAa5vz7o, i2);
        }
    }

    /* JADX INFO: renamed from: partition-oBK06Vg, reason: not valid java name */
    private static final int m762partitionoBK06Vg(int[] iArr, int i, int i2) {
        int iM459getpVg5ArA = UIntArray.m459getpVg5ArA(iArr, (i + i2) / 2);
        while (i <= i2) {
            while (Integer.compareUnsigned(UIntArray.m459getpVg5ArA(iArr, i), iM459getpVg5ArA) < 0) {
                i++;
            }
            while (Integer.compareUnsigned(UIntArray.m459getpVg5ArA(iArr, i2), iM459getpVg5ArA) > 0) {
                i2--;
            }
            if (i <= i2) {
                int iM459getpVg5ArA2 = UIntArray.m459getpVg5ArA(iArr, i);
                UIntArray.m464setVXSXFK8(iArr, i, UIntArray.m459getpVg5ArA(iArr, i2));
                UIntArray.m464setVXSXFK8(iArr, i2, iM459getpVg5ArA2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-oBK06Vg, reason: not valid java name */
    private static final void m766quickSortoBK06Vg(int[] iArr, int i, int i2) {
        int iM762partitionoBK06Vg = m762partitionoBK06Vg(iArr, i, i2);
        int i3 = iM762partitionoBK06Vg - 1;
        if (i < i3) {
            m766quickSortoBK06Vg(iArr, i, i3);
        }
        if (iM762partitionoBK06Vg < i2) {
            m766quickSortoBK06Vg(iArr, iM762partitionoBK06Vg, i2);
        }
    }

    /* JADX INFO: renamed from: partition--nroSd4, reason: not valid java name */
    private static final int m759partitionnroSd4(long[] jArr, int i, int i2) {
        long jM538getsVKNKU = ULongArray.m538getsVKNKU(jArr, (i + i2) / 2);
        while (i <= i2) {
            while (Long.compareUnsigned(ULongArray.m538getsVKNKU(jArr, i), jM538getsVKNKU) < 0) {
                i++;
            }
            while (Long.compareUnsigned(ULongArray.m538getsVKNKU(jArr, i2), jM538getsVKNKU) > 0) {
                i2--;
            }
            if (i <= i2) {
                long jM538getsVKNKU2 = ULongArray.m538getsVKNKU(jArr, i);
                ULongArray.m543setk8EXiF4(jArr, i, ULongArray.m538getsVKNKU(jArr, i2));
                ULongArray.m543setk8EXiF4(jArr, i2, jM538getsVKNKU2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort--nroSd4, reason: not valid java name */
    private static final void m763quickSortnroSd4(long[] jArr, int i, int i2) {
        int iM759partitionnroSd4 = m759partitionnroSd4(jArr, i, i2);
        int i3 = iM759partitionnroSd4 - 1;
        if (i < i3) {
            m763quickSortnroSd4(jArr, i, i3);
        }
        if (iM759partitionnroSd4 < i2) {
            m763quickSortnroSd4(jArr, iM759partitionnroSd4, i2);
        }
    }

    /* JADX INFO: renamed from: sortArray-4UcCI2c, reason: not valid java name */
    public static final void m768sortArray4UcCI2c(byte[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m764quickSort4UcCI2c(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-Aa5vz7o, reason: not valid java name */
    public static final void m769sortArrayAa5vz7o(short[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m765quickSortAa5vz7o(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-oBK06Vg, reason: not valid java name */
    public static final void m770sortArrayoBK06Vg(int[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m766quickSortoBK06Vg(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray--nroSd4, reason: not valid java name */
    public static final void m767sortArraynroSd4(long[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m763quickSortnroSd4(array, i, i2 - 1);
    }
}
