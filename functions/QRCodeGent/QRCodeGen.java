import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalyst.advanced.CatalystAdvancedIOHandler;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGen implements CatalystAdvancedIOHandler {
	private static final Logger LOGGER = Logger.getLogger(QRCodeGen.class.getName());
	

	
	private static void createQRImage(String qrCodeText, int size, String fileType, HttpServletResponse res)
			throws WriterException, IOException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ImageIO.write(image, fileType, res.getOutputStream());	
		res.setContentType("image/png");//setting the content type  
	}

	@Override
    public void runner(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int size = 125;
		String fileType = "png";
		try {
			
			//Code128
			String input = req.getParameter("message");
			if(input == null || input.trim().isEmpty()) {
				PrintWriter pw = res.getWriter();
				pw.write("{'error':'need input in the parameter \'message\''}");
				res.setContentType("application/json");//setting the content type
				return;
			}
			
			createQRImage(input, size, fileType, res);
		} catch (WriterException | IOException e) {
			e.printStackTrace();

			PrintWriter pw = res.getWriter();
			pw.write("{'error':'unknown error'}");
			res.setContentType("application/json");//setting the content type
			return;
		
		}
		System.out.println("DONE");
	

	}
	
}