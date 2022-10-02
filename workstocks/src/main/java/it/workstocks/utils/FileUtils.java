package it.workstocks.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import it.workstocks.exception.WorkstocksBusinessException;

public class FileUtils {
	public static byte[] getBinaryData(Part filePart) throws WorkstocksBusinessException {
		
		try {
			if (filePart.getSize() != 0) {
				InputStream is;
				is = filePart.getInputStream();
				return is.readAllBytes();
			}
			
			return null;
		} catch (IOException e) {
			throw new WorkstocksBusinessException("Failed to convert binary data", e);
		}	
	}
	
	// true OK, false NOT OK
	public static boolean checkFileSizeInMB(Part filePart, long maxFileSize) {
		if (filePart == null) return true;
		long fileSizeInMB = (filePart.getSize() / 1024) / 1024;
		return (fileSizeInMB <= maxFileSize);
	}
}
