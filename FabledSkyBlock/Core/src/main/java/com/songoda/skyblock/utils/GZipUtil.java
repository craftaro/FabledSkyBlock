package com.songoda.skyblock.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GZipUtil {

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(data);
        gzip.flush();
        gzip.close();

        return obj.toByteArray();
    }

    public static byte[] decompress(final byte[] compressedData) throws IOException {
        if (isCompressed(compressedData)) {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedData));
            return IOUtils.toByteArray(gis);
        }

        return new byte[512];
    }

    public static boolean isCompressed(final byte[] compressedData) {
        return (compressedData[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressedData[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}
