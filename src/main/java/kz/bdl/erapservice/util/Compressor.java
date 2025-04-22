package kz.bdl.erapservice.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Compressor {
    public static byte[] compressString(String input) {
        if (input == null || input.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            gzipStream.write(inputBytes);
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
