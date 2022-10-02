package it.workstocks.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Base64;

import org.springframework.http.MediaType;
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
		return Base64.getEncoder().encodeToString(blob);
	}
	
	public static String getFileExtension(byte[] file) throws IOException {
		InputStream is = new ByteArrayInputStream(file);
		String mimeType = URLConnection.guessContentTypeFromStream(is);
		String fileExtension = mimeType.split("/")[1];
		is.close();
		return fileExtension;
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
	
	public static MediaType getMediaTypeFromImage(String fileExtension) {
		switch (fileExtension) {
		case "jpeg":
			return MediaType.IMAGE_JPEG;
		case "png":
			return MediaType.IMAGE_PNG;
		default:
			return null;
		}
	}
}
