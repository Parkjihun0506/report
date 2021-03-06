import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class HandlerTest4 implements Initializable {

	JavaFXTCPSnd javafxTcpSnd1;
	JavaFXTCPRcv javafxTcpRcv1;
//	HandlerTest1 handlerTest1;
	public static String tcpRcv;

	@FXML
	Button btnfnd1, btnfnd2, btnreg2;
	@FXML
	TextField tf14, tf15, tf16, tf17, tf18;
	@FXML
	Label lbl1;
	@FXML
	ImageView ib1;

	Stage newc;

	String path = System.getProperty("user.dir");

	public static String strEmpInfo;
	public static String strEmpID;
	public static String strName;
	public static String rcvEmpID;
	public static String rcvName;
	public static String rcvCall;
	public static String rcvDep;
	public static String rcvFinger;
	public static String rcvDate;
	public static String rcvTF;

	HandlerTest2 han2;

	String ftpIP = "192.168.34.38";
	String ftpID = "kb304-01";
	String ftpPW = "iot3*";

	@Override
	public void initialize(URL location, ResourceBundle resources) {

//		System.out.print(rcvEmpID);

		tf14.setText(rcvEmpID);
		tf15.setText(rcvName);
		tf16.setText(rcvCall);
		tf17.setText(rcvDep);

	}

	public void registHandle(ActionEvent event) throws Exception {

		FTPClient ftp = new FTPClient();
		FileInputStream fis1 = null;

		try {

			if (ib1.getImage() == null) {

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("[ERR]에러");
				alert.setHeaderText("");
				alert.setContentText("[ERR_IMG_NULL_ADD_IMG]수정할 이미지를 설정해주세요.");
				alert.showAndWait();

			} else {

				int pathGet = tf18.getText().lastIndexOf("\\");

				String cmd = "18";
				String textField14 = tf14.getText();
				String textField15 = String.format("%8s", tf15.getText());
				String textField16 = String.format("%14s", tf16.getText());
				String textField17 = String.format("%10s", tf17.getText());
				String textField18 = String.format("%100s", tf18.getText().substring(pathGet + 1));
				String textFinger = String.format("%100s", rcvFinger);
				String textDate = String.format("%10s", rcvDate);
				String sndName = String.format("%8s", strName);

				String sndMsg = cmd + textField14 + textField15 + textField16 + textField17 + textField18 + textFinger
						+ textDate + strEmpID + sndName;

//				System.out.print(rcvTF);

				// 원하시는 인코딩 타입
				ftp.setControlEncoding("EUC-KR");
				ftp.connect(ftpIP);
				ftp.login(ftpID, ftpPW);

				// 제대로 연결이 안됐을 경우 ftp접속을 끊습니다.
				if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
					ftp.disconnect();
				}
				// 파일을 넣을 디렉토리를 설정해줍니다.
				// makeDirectory는 directory 생성이 필요할 때만 해주시면 됩니다.
//				ftp.makeDirectory("/DataPic");
				ftp.changeWorkingDirectory("/");
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

				// System.out.print(tf3.getText());

//				C:\\Test1\\APTLogo.JPG

				String strPath1 = tf18.getText().replace("\\", "\\\\");

//				System.out.printf("[0]" + strPath1);

				int nNum1 = tf18.getText().lastIndexOf("\\");
				int nLen1 = tf18.getText().length();

				String strSub1 = tf18.getText().substring(nNum1 + 1, nLen1);

//				System.out.printf("[DBG]" + strSub1);
//				System.out.print(tf3.getText());

				fis1 = new FileInputStream(strPath1); // 보낼 파일명
				boolean isSuccess1 = ftp.storeFile(strSub1, fis1);// 내가정할 파일이름

				// storeFile Method는 파일 송신결과를 boolean값으로 리턴합니다
				if (isSuccess1) {

					System.out.println("등록 성공");

					if (sndMsg != null) {
						JavaFXTCPSnd.rcvMsg = sndMsg;
					}
				}

				Thread.sleep(2000);

				if (rcvTF != null) {
					if (rcvTF.equals("T")) {

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("수정 완료");
						alert.setHeaderText("");
						alert.setContentText("수정이 완료되었습니다.");
						Optional<ButtonType> result = alert.showAndWait();

						if (result.get() == ButtonType.OK) {
							HandlerTest2.popF.close();
						}
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("수정 실패");
						alert.setHeaderText("");
						alert.setContentText("수정을 실패했습니다.");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							HandlerTest2.popF.close();
						}

					}
					rcvTF = null;
				}
			}
		} catch (Exception e) {
			System.out.print(e);
		} finally {
			if (fis1 != null) {
				try {
					fis1.close();

				} catch (Exception e) {
					System.out.print(e);
				}
			}
		}

	}

	public void faceChooser(ActionEvent event) throws Exception {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"),
				new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
				new ExtensionFilter("Text Files", "*.txt"));
		fileChooser.setInitialDirectory(new File(path));

		File selectedFile = fileChooser.showOpenDialog(newc);

		// 파일 읽어오기
		FileInputStream fis = new FileInputStream(selectedFile);

		BufferedInputStream bis = new BufferedInputStream(fis);
		// 이미지 생성하기
		Image img = new Image(bis);
		// 이미지 띄우기
		ib1.setImage(img);

		if (selectedFile != null) {
//			System.out.print(selectedFile.getPath());

			tf18.setText(selectedFile.getPath());
		}
	}

	public void cancleHandle1(ActionEvent event) throws Exception {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("종료");
		alert.setHeaderText("");
		alert.setContentText("종료하시겠습니까?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			try {
				HandlerTest2.popF.close();
			} catch (Exception e) {
				System.out.print(e.getLocalizedMessage());
			}

		} else {
			alert.close();
		}

	}

}