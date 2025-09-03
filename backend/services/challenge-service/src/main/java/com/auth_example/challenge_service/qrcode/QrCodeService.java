package com.auth_example.challenge_service.qrcode;

import com.auth_example.challenge_service.exceptions.ErrorGeneratingQRException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class QrCodeService {

    private final MultiFormatWriter multiFormatWriter;

    public QrCodeService() {
        this.multiFormatWriter = new MultiFormatWriter();
    }

    public String generateForBase64(String authUrl) {
        try {
            // generate a 200x200 qr code matrix from uri
            BitMatrix bitMatrix = multiFormatWriter.encode(authUrl, BarcodeFormat.QR_CODE, 200, 200);
            // convert bit matrix to buffered image
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            // convert buffered image to byte array output stream (png format)
            ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baoStream);

            return Base64.getEncoder().encodeToString(baoStream.toByteArray());
        } catch (WriterException | IOException exp) {
            throw new ErrorGeneratingQRException("error during qr code generation");
        }
    }
}
