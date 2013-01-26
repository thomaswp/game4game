package com.eujeux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileUtils {
	
	public static boolean writeObject(HttpServletResponse resp, Serializable object) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
			oos.writeObject(object);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(HttpServletRequest req, Class<T> c) {
		try {
			ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
			return (T)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] readBytes(HttpServletRequest req) {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			InputStream is = req.getInputStream();
			int read;
			
			while ((read = is.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return baos.toByteArray();
	}
}
