package info.iconmaster.shuv.gui;

import info.iconmaster.shuv.ShuvDecoder;
import info.iconmaster.shuv.ShuvDecoder.ShuvData;
import info.iconmaster.shuv.ShuvEncoder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author iconmaster
 */
public class ShuvApplication extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Label encodeLabel = new Label("File:");
		encodeLabel.setPadding(new Insets(5));
		
		TextField encodeText = new TextField();
		encodeText.setPadding(new Insets(5));
		
		Button encodeBrowse = new Button("Browse...");
		encodeBrowse.setPadding(new Insets(5));
		encodeBrowse.setOnAction((ev)->{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("."));
			fileChooser.setTitle("Open File");
			File file = fileChooser.showOpenDialog(stage);
			if (file != null) {
				encodeText.setText(file.getPath());
			}
		});
		
		Button encodeSubmit = new Button("Upload!");
		encodeSubmit.setPadding(new Insets(5));
		encodeSubmit.setOnAction((ev)->{
			String code = ShuvEncoder.encode(new File(encodeText.getText()));
			if (code == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "An error occured in uploading.");
				alert.show();
			} else {
				Clipboard clip = Clipboard.getSystemClipboard();
				ClipboardContent cc = new ClipboardContent();
				cc.putString(code);
				clip.setContent(cc);
				
				TextInputDialog tid = new TextInputDialog(code);
				tid.setContentText("Upload successful! The code has been copied to your clipboard."); //uNyRmd
				tid.show();
			}
		});
		
		HBox encodeHbox = new HBox(encodeLabel, encodeText, encodeBrowse);
		encodeHbox.setPadding(new Insets(10));
		
		VBox encodeVbox = new VBox(encodeHbox, encodeSubmit);
		encodeVbox.setPadding(new Insets(10));
		
		Tab encodeTab = new Tab("Upload File", encodeVbox);
		
		Label decodeLabel = new Label("Code:");
		decodeLabel.setPadding(new Insets(5));
		
		TextField decodeCode = new TextField();
		decodeCode.setPadding(new Insets(5));
		
		Button decodeSubmit = new Button("Download!");
		decodeSubmit.setPadding(new Insets(5));
		decodeSubmit.setOnAction((ev)->{
			ShuvData data = ShuvDecoder.decode(decodeCode.getText());
			
			if (data == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "An error occured in downloading.");
				alert.show();
			} else {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(new File("."));
				fileChooser.setInitialFileName(data.name);
				fileChooser.setTitle("Save File");
				File file = fileChooser.showSaveDialog(stage);
				
				if (file != null) {
					try {
						Files.write(file.toPath(), data.data);
						
						Alert alert = new Alert(Alert.AlertType.INFORMATION, "File downloaded successfully.");
						alert.show();
					} catch (IOException ex) {
						Logger.getLogger(ShuvApplication.class.getName()).log(Level.SEVERE, null, ex);
						Alert alert = new Alert(Alert.AlertType.ERROR, "An error occured in downloading.");
						alert.show();
					}
				}
			}
		});
		
		HBox decodeHbox = new HBox(decodeLabel, decodeCode);
		decodeHbox.setPadding(new Insets(10));
		
		VBox decodeVbox = new VBox(decodeHbox, decodeSubmit);
		decodeVbox.setPadding(new Insets(10));
		
		Tab decodeTab = new Tab("Download File", decodeVbox);
		
		TabPane root = new TabPane(encodeTab, decodeTab);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
