package info.iconmaster.shuv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author iconmaster
 */
public class ShuvConsts {
	public static void setKey(String key) {
		API_KEY = key;
		API_URL = API_URL_BASE + API_KEY;
	}
	
	public static String API_KEY;
	public static final String API_URL_BASE = "https://www.googleapis.com/urlshortener/v1/url?key=";
	public static String API_URL;
	
	public static final int MAX_CHUNK = 2000;
	
	static {
		File keyFile = new File("data/api_key");
		String key = null;
		try {
			Scanner scan = (new Scanner(keyFile)).useDelimiter("\\A");
			key = scan.hasNext() ? scan.next() : null;
		} catch (FileNotFoundException ex) {}
		
		if (key != null) {
			setKey(key);
		}
	}
}
