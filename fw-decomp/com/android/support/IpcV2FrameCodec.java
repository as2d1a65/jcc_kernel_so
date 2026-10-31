package com.android.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import kotlin.UByte;

/* loaded from: classes.dex */
public final class IpcV2FrameCodec {

    public enum Error {
        END_OF_STREAM,
        TRUNCATED_FRAME,
        INVALID_LENGTH,
        FRAME_TOO_LARGE,
        INVALID_UTF8
    }

    public static final class FrameException extends IOException {
        private final Error error;

        FrameException(Error error, String str) {
            super(str);
            this.error = error;
        }

        public Error error() {
            return this.error;
        }
    }

    private IpcV2FrameCodec() {
    }

    public static String readUtf8(InputStream inputStream) throws IOException {
        return new Reader().readUtf8(inputStream);
    }

    public static final class Reader {
        private final byte[] header = new byte[4];
        private int headerOffset;
        private byte[] payload;
        private int payloadOffset;

        public String readUtf8(InputStream inputStream) throws IOException {
            byte[] bArr;
            if (inputStream == null) {
                throw new IllegalArgumentException("input is required");
            }
            while (true) {
                int i = this.headerOffset;
                bArr = this.header;
                if (i >= bArr.length) {
                    break;
                }
                this.headerOffset = i + readChunk(inputStream, bArr, i, true);
            }
            if (this.payload == null) {
                long j = (bArr[3] & UByte.MAX_VALUE) | ((bArr[0] & UByte.MAX_VALUE) << 24) | ((bArr[1] & UByte.MAX_VALUE) << 16) | ((bArr[2] & UByte.MAX_VALUE) << 8);
                if (j == 0) {
                    throw new FrameException(Error.INVALID_LENGTH, "frame payload is empty");
                }
                if (j > 1048576) {
                    throw new FrameException(Error.FRAME_TOO_LARGE, "frame exceeds protocol limit");
                }
                this.payload = new byte[(int) j];
            }
            while (true) {
                int i2 = this.payloadOffset;
                byte[] bArr2 = this.payload;
                if (i2 < bArr2.length) {
                    this.payloadOffset = i2 + readChunk(inputStream, bArr2, i2, false);
                } else {
                    try {
                        try {
                            return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(this.payload)).toString();
                        } catch (CharacterCodingException unused) {
                            throw new FrameException(Error.INVALID_UTF8, "frame is not valid UTF-8");
                        }
                    } finally {
                        this.headerOffset = 0;
                        this.payloadOffset = 0;
                        this.payload = null;
                    }
                }
            }
        }

        private int readChunk(InputStream inputStream, byte[] bArr, int i, boolean z) throws IOException {
            try {
                int i2 = inputStream.read(bArr, i, bArr.length - i);
                if (i2 >= 0) {
                    return i2;
                }
                if (z && i == 0) {
                    throw new FrameException(Error.END_OF_STREAM, "stream closed between frames");
                }
                throw new FrameException(Error.TRUNCATED_FRAME, "stream closed inside frame");
            } catch (InterruptedIOException e) {
                int i3 = e.bytesTransferred;
                if (i3 < 0 || i3 > bArr.length - i) {
                    throw new IOException("invalid interrupted read count", e);
                }
                if (z) {
                    this.headerOffset += i3;
                } else {
                    this.payloadOffset += i3;
                }
                throw e;
            }
        }
    }

    public static void writeUtf8(OutputStream outputStream, String str) throws IOException {
        if (outputStream == null || str == null) {
            throw new IllegalArgumentException("output and value are required");
        }
        try {
            ByteBuffer byteBufferEncode = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).encode(CharBuffer.wrap(str));
            int iRemaining = byteBufferEncode.remaining();
            if (iRemaining == 0) {
                throw new FrameException(Error.INVALID_LENGTH, "frame payload is empty");
            }
            if (iRemaining > 1048576) {
                throw new FrameException(Error.FRAME_TOO_LARGE, "frame exceeds protocol limit");
            }
            outputStream.write((iRemaining >>> 24) & 255);
            outputStream.write((iRemaining >>> 16) & 255);
            outputStream.write((iRemaining >>> 8) & 255);
            outputStream.write(iRemaining & 255);
            if (byteBufferEncode.hasArray()) {
                outputStream.write(byteBufferEncode.array(), byteBufferEncode.arrayOffset() + byteBufferEncode.position(), iRemaining);
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(iRemaining);
                int iMin = Math.min(8192, Math.max(1, iRemaining));
                byte[] bArr = new byte[iMin];
                while (byteBufferEncode.hasRemaining()) {
                    int iMin2 = Math.min(byteBufferEncode.remaining(), iMin);
                    byteBufferEncode.get(bArr, 0, iMin2);
                    byteArrayOutputStream.write(bArr, 0, iMin2);
                }
                byteArrayOutputStream.writeTo(outputStream);
            }
            outputStream.flush();
        } catch (CharacterCodingException unused) {
            throw new FrameException(Error.INVALID_UTF8, "value cannot be encoded as UTF-8");
        }
    }
}
