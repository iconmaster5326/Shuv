package info.iconmaster.shuv;

import info.iconmaster.shuv.ShuvDecoder.ShuvData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iconmaster
 */
public class Shuv {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		if (args.length <= 1 || args.length > 3) {
			usageMessage();
			return;
		}
		
		if ("encode".equals(args[0])) {
			checkForKey();
			
			String code = ShuvEncoder.encode(new File(args[1]));
			if (code == null) {
				System.err.println("An error occured. Operation not successfull.");
				return;
			}
			System.out.print(code);
		} else if ("decode".equals(args[0])) {
			checkForKey();
			
			ShuvData data = ShuvDecoder.decodeToFilesName(new File("."), args[1]);
			if (data == null) {
				System.err.println("An error occured. Operation not successfull.");
				return;
			}
			System.out.print(data.name);
		} else {
			usageMessage();
		}
	}
	
	public static void usageMessage() {
		System.out.println("Usage: shuv encode <file>");
		System.out.println("       shuv decode <code>");
	}
	
	public static void checkForKey() {
		if (ShuvConsts.API_KEY == null) {
			System.out.print("API key not set! Please provide a new key: ");
			Scanner scan = new Scanner(System.in);
			String key = scan.nextLine();
			ShuvConsts.setKey(key);
			
			try {
				Files.createDirectory((new File("data")).toPath());
			} catch (IOException ex) {
				Logger.getLogger(Shuv.class.getName()).log(Level.SEVERE, null, ex);
			}
			
			try {
				Files.write((new File("data/api_key")).toPath(), key.getBytes());
			} catch (IOException ex) {
				Logger.getLogger(Shuv.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
