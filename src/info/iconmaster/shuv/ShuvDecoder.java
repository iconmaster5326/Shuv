package info.iconmaster.shuv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author iconmaster
 */
public class ShuvDecoder {
	public static class ShuvData {
		String name;
		byte[] data;

		public ShuvData() {}

		public ShuvData(String name, byte[] data) {
			this.name = name;
			this.data = data;
		}
	}
	
	public static ShuvData decodeToFilesName(File path, String code) {
		ShuvData data = decode(code);
		
		try {
			OutputStream out = new FileOutputStream(new File(path, data.name));
			out.write(data.data);
		} catch (Exception ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return data;
	}
	
	public static ShuvData decode(File dest, String code) {
		ShuvData data = decode(code);
		
		try {
			OutputStream out = new FileOutputStream(dest);
			out.write(data.data);
		} catch (Exception ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return data;
	}
	
	public static ShuvData decode(String code) {
		String header = readChunk(code);
		String[] entries = header.split("\\/");
		StringBuilder sb = new StringBuilder();
		
		for (int i = 1; i < entries.length; i++) {
			sb.append(readChunk(entries[i]));
		}
		
		try {
			byte[] data = Base64.getDecoder().decode(sb.toString());
			return new ShuvData(entries[0], data);
		} catch (Exception ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	public static String readChunk(String url) {
		try {
			URLConnection conn = new URL(info.iconmaster.shuv.ShuvConsts.API_URL + "&shortUrl=http://goo.gl/" + url).openConnection();
			
			int status = ((HttpURLConnection)conn).getResponseCode();
			
			if (status >= 400) {
				InputStream response = ((HttpURLConnection) conn).getErrorStream();
				
				System.out.println("ERROR:");
				Scanner scan = new Scanner(response).useDelimiter("\\A");
				System.out.println(scan.hasNext() ? scan.next() : "");
				
				return null;
			} else {
				InputStream response = conn.getInputStream();
				
				Scanner scan = new Scanner(response).useDelimiter("\\A");
				String s = scan.hasNext() ? scan.next() : "{}";
				
				JSONObject json = new JSONObject(s);
				String longurl = json.getString("longUrl");
				
				Pattern p = Pattern.compile("^http\\:\\/\\/shuv\\/(.*)$");
				Matcher m = p.matcher(longurl);
				m.find();
				return m.group(1);
			}
		} catch (IOException | JSONException ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
}
