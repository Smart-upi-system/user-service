package com.uws.user_service.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class QRCodeGenerator {

    public static final int QR_CODE_WIDTH = 300;
    public static final int QR_CODE_HEIGHT = 300;

/**
 * Generates a QR code PNG image as a byte array.
 *
 * @param text   The payload to encode (URL, deep link, token, etc.)
 * @param width  Image width in pixels
 * @param height Image height in pixels
 * @return PNG byte array
 */

    public static byte[] generateQRCodeUtil(String payload) throws WriterException, IOException {
        Map<EncodeHintType,Object> hints=Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN,1,
                EncodeHintType.CHARACTER_SET,"UTF-8"
        );
        BitMatrix matrix=new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE,QR_CODE_WIDTH,QR_CODE_HEIGHT,hints);

        MatrixToImageConfig config=new MatrixToImageConfig(0xFF000000,0xFFFFFFFF);
        try (ByteArrayOutputStream outputStream=new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix,"PNG",outputStream,config);
            return outputStream.toByteArray();
        }

    }



}
