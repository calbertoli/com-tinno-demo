package kotlin.time;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import kotlin.Metadata;

/* JADX INFO: compiled from: TimeSources.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002ø\u0001\u0000¢\u0006\u0004\b\t\u0010\nJ\u001b\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086\u0002ø\u0001\u0000¢\u0006\u0004\b\f\u0010\nJ\b\u0010\r\u001a\u00020\u0004H\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u000e"}, d2 = {"Lkotlin/time/TestTimeSource;", "Lkotlin/time/AbstractLongTimeSource;", "()V", "reading", "", "overflow", "", TypedValues.TransitionType.S_DURATION, "Lkotlin/time/Duration;", "overflow-LRDsOJo", "(J)V", "plusAssign", "plusAssign-LRDsOJo", "read", "kotlin-stdlib"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class TestTimeSource extends AbstractLongTimeSource {
    private long reading;

    @Override // kotlin.time.AbstractLongTimeSource
    /* JADX INFO: renamed from: read, reason: from getter */
    protected long getReading() {
        return this.reading;
    }

    public TestTimeSource() {
        super(DurationUnit.NANOSECONDS);
    }

    /* JADX INFO: renamed from: plusAssign-LRDsOJo, reason: not valid java name */
    public final void m1765plusAssignLRDsOJo(long duration) {
        long j;
        long jM1676toLongimpl = Duration.m1676toLongimpl(duration, getUnit());
        if (jM1676toLongimpl == Long.MIN_VALUE || jM1676toLongimpl == Long.MAX_VALUE) {
            double dM1673toDoubleimpl = this.reading + Duration.m1673toDoubleimpl(duration, getUnit());
            if (dM1673toDoubleimpl > 9.223372036854776E18d || dM1673toDoubleimpl < -9.223372036854776E18d) {
                m1764overflowLRDsOJo(duration);
            }
            j = (long) dM1673toDoubleimpl;
        } else {
            long j2 = this.reading;
            j = j2 + jM1676toLongimpl;
            if ((jM1676toLongimpl ^ j2) >= 0 && (j2 ^ j) < 0) {
                m1764overflowLRDsOJo(duration);
            }
        }
        this.reading = j;
    }

    /* JADX INFO: renamed from: overflow-LRDsOJo, reason: not valid java name */
    private final void m1764overflowLRDsOJo(long duration) {
        throw new IllegalStateException("TestTimeSource will overflow if its reading " + this.reading + DurationUnitKt.shortName(getUnit()) + " is advanced by " + ((Object) Duration.m1679toStringimpl(duration)) + '.');
    }
}
