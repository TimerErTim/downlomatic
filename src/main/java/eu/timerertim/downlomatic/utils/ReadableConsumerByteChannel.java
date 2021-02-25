package eu.timerertim.downlomatic.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.function.IntConsumer;

public class ReadableConsumerByteChannel implements ReadableByteChannel {
    private final ReadableByteChannel byteChannel;
    private final IntConsumer onRead;

    private int totalByteRead;

    public ReadableConsumerByteChannel(ReadableByteChannel byteChannel, IntConsumer onRead) {
        this.byteChannel = byteChannel;
        this.onRead = onRead;
        totalByteRead = 0;
    }

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException {
        int nRead = byteChannel.read(byteBuffer);
        notifyBytesRead(nRead);
        return nRead;
    }

    protected void notifyBytesRead(int nRead) {
        if (nRead <= 0) {
            return;
        }
        totalByteRead += nRead;
        onRead.accept(totalByteRead);
    }

    public int getTotalByteRead() {
        return totalByteRead;
    }

    @Override
    public boolean isOpen() {
        return byteChannel.isOpen();
    }

    @Override
    public void close() throws IOException {
        byteChannel.close();
    }
}
