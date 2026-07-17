package com.google.android.exoplayer2.audio;

import android.util.SparseArray;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class ChannelMixingAudioProcessor extends BaseAudioProcessor {
    private final SparseArray<ChannelMixingMatrix> matrixByInputChannelCount = new SparseArray<>();

    public void putChannelMixingMatrix(ChannelMixingMatrix channelMixingMatrix) {
        this.matrixByInputChannelCount.put(channelMixingMatrix.getInputChannelCount(), channelMixingMatrix);
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat audioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        if (audioFormat.encoding != 2) {
            throw new AudioProcessor.UnhandledAudioFormatException(audioFormat);
        }
        ChannelMixingMatrix channelMixingMatrix = this.matrixByInputChannelCount.get(audioFormat.channelCount);
        if (channelMixingMatrix == null) {
            throw new AudioProcessor.UnhandledAudioFormatException("No mixing matrix for input channel count", audioFormat);
        }
        if (channelMixingMatrix.isIdentity()) {
            return AudioProcessor.AudioFormat.NOT_SET;
        }
        return new AudioProcessor.AudioFormat(audioFormat.sampleRate, channelMixingMatrix.getOutputChannelCount(), 2);
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer byteBuffer) {
        ChannelMixingMatrix channelMixingMatrix = (ChannelMixingMatrix) Assertions.checkStateNotNull(this.matrixByInputChannelCount.get(this.inputAudioFormat.channelCount));
        ByteBuffer byteBufferReplaceOutputBuffer = replaceOutputBuffer((byteBuffer.remaining() / this.inputAudioFormat.bytesPerFrame) * this.outputAudioFormat.bytesPerFrame);
        int inputChannelCount = channelMixingMatrix.getInputChannelCount();
        int outputChannelCount = channelMixingMatrix.getOutputChannelCount();
        float[] fArr = new float[outputChannelCount];
        while (byteBuffer.hasRemaining()) {
            for (int i = 0; i < inputChannelCount; i++) {
                short s = byteBuffer.getShort();
                for (int i2 = 0; i2 < outputChannelCount; i2++) {
                    fArr[i2] = fArr[i2] + (channelMixingMatrix.getMixingCoefficient(i, i2) * s);
                }
            }
            for (int i3 = 0; i3 < outputChannelCount; i3++) {
                short sConstrainValue = (short) Util.constrainValue(fArr[i3], -32768.0f, 32767.0f);
                byteBufferReplaceOutputBuffer.put((byte) (sConstrainValue & 255));
                byteBufferReplaceOutputBuffer.put((byte) ((sConstrainValue >> 8) & 255));
                fArr[i3] = 0.0f;
            }
        }
        byteBufferReplaceOutputBuffer.flip();
    }
}
