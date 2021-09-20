package ru.granby.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageUtils {

    // compressionQuality ∈ [0,1]
    public static void saveCompressedJPEG(BufferedImage bufferedImage, String imageOutputPath, float compressionQuality) throws IOException {
        Iterator imageWritersIterator = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter imageWriter = (ImageWriter)imageWritersIterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(compressionQuality);

        File file = new File(imageOutputPath);
        FileImageOutputStream output = new FileImageOutputStream(file);
        imageWriter.setOutput(output);
        IIOImage image = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, image, imageWriteParam);
        imageWriter.dispose();
    }
}
