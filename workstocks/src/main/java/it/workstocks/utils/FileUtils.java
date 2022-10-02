package it.workstocks.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class FileUtils {

	// true OK, false NOT OK
	public static boolean checkFileSizeInMB(byte[] file, long maxFileSize) {
		if (file != null) {
			long fileSizeInMB = (file.length / 1024) / 1024;
			return (fileSizeInMB <= maxFileSize);
		}
		
		return true;
	}

	public static byte[] getByteArrayFromBase64(String base64) {
		return Base64.getDecoder().decode(base64);
	}

	public static String getBase64FromByteArray(byte[] blob) {
		if (blob.length <= 0) return null;
		return Base64.getEncoder().encodeToString(blob);
	}
	
	public static StreamingResponseBody getStreamingOutput(byte[] file) {
		StreamingResponseBody res = new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write(file);	
			}
		};

		return res;
	}
}
