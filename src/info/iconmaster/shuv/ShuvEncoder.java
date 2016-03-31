package info.iconmaster.shuv;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 *
 * @author iconmaster
 */
public class ShuvEncoder {
	public static String encode(File file) {
		
		try {
			return encode(file.getName(), Files.readAllBytes(file.toPath()));
		} catch (Exception ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	public static String encode(String name, byte[] data) {
		data = Base64.getEncoder().encode(data);
		
		ArrayList<byte[]> a = new ArrayList<>();
		
		for (int i = 0; i <= (data.length - 1) / ShuvConsts.MAX_CHUNK; i++) {
			a.add(Arrays.copyOfRange(data, i*ShuvConsts.MAX_CHUNK, Math.min(data.length,(i+1)*ShuvConsts.MAX_CHUNK)));
		}
		
		StringBuilder sb = new StringBuilder(name);
		sb.append('/');
		
		for (byte[] b : a) {
			String url = writeChunk(new String(b));
			
			sb.append(url).append('/');
		}
		
		return writeChunk(sb.toString());
	}
	
	public static String writeChunk(String chunk) {
		try {
			URLConnection conn = new URL(ShuvConsts.API_URL).openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			
			JSONObject json = new JSONObject();
			json.put("longUrl", "http://shuv/" + chunk);
			
			OutputStream output = conn.getOutputStream();
			output.write(json.toString().getBytes());
			
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
				
				json = new JSONObject(s);
				String url = json.getString("id");
				
				Pattern p = Pattern.compile("^http\\:\\/\\/goo\\.gl\\/(.*)$");
				Matcher m = p.matcher(url);
				m.find();
				return m.group(1);
			}
			
			
		} catch (Exception ex) {
			Logger.getLogger(ShuvEncoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
}
