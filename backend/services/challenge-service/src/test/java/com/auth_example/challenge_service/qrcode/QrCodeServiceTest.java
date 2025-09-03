package com.auth_example.challenge_service.qrcode;

import com.auth_example.challenge_service.exceptions.ErrorGeneratingQRException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QrCodeServiceTest {
    private final String mockAuthUrl = "otpauth://totp/test@example.com?secret=ABC123&issuer=TestApp";

    @Test
    @DisplayName("[QrCodeService :: generateForBase64] - should return a base64 string")
    public void shouldReturnBase64String() throws Exception {
        // arrange
        QrCodeService qrCodeService = new QrCodeService();
        // act
        String functionCall = qrCodeService.generateForBase64(mockAuthUrl);
        // assert
        assertNotNull(functionCall);
        assertFalse(functionCall.isEmpty());

        // decode and verify it produces an actual image
        byte[] decodedBytes = Base64.getDecoder().decode(functionCall);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));

        assertNotNull(image);
        assertEquals(200, image.getWidth());
        assertEquals(200, image.getHeight());
    }

    @Test
    @DisplayName("[QrCodeService :: generateForBase64] - should throw [ErrorGeneratingQRException] when error is caught during QR image generation")
    public void should() throws Exception {
        // arrange
        MultiFormatWriter mockWriter = Mockito.mock(MultiFormatWriter.class);
        QrCodeService qrCodeService = new QrCodeService(mockWriter);
        when(mockWriter.encode(anyString(), any(), anyInt(), anyInt())).thenThrow(new WriterException("simulate failure"));
        // assert
        assertThrows(ErrorGeneratingQRException.class, () -> qrCodeService.generateForBase64(mockAuthUrl));
    }
}
