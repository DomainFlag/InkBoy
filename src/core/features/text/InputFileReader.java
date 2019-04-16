package core.features.text;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputFileReader {

    public static ByteBuffer readFileResource(String resource) {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if(Files.isReadable(path)) {
            try {
                SeekableByteChannel seekableByteChannel = Files.newByteChannel(path);
                buffer = BufferUtils.createByteBuffer((int) seekableByteChannel.size() + 1);

                int read = 0;
                while(read != -1) {
                    read = seekableByteChannel.read(buffer);
                }
            } catch(IOException e) {
                throw new IllegalArgumentException("Given resource can't be read: " + resource);
            }
        } else {
            throw new IllegalArgumentException("Given resource can't be read: " + resource);
        }

        buffer.flip();

        return buffer.slice();
    }
}
