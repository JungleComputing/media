package ibis.imaging4j.io;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import ibis.imaging4j.UnsupportedFormatException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IO {

    public static void save(Image image, File file) throws Exception {

        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG")) {
            Image jpg;
            if (image.getFormat() == Format.JPG) {
                jpg = image;
            } else {
                jpg = Imaging4j.convert(image, Format.JPG);
            }

            FileOutputStream out = new FileOutputStream(file);

            out.write(jpg.getData().array());

            out.flush();
            out.close();
        } else if (file.getName().endsWith(".rgb")
                || file.getName().endsWith(".RGB")) {
            Image rgb;
            if (image.getFormat() == Format.RGB24) {
                rgb = image;
            } else {
                rgb = Imaging4j.convert(image, Format.RGB24);
            }

            FileOutputStream fileOut = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fileOut);

            out.writeInt(rgb.getWidth());
            out.writeInt(rgb.getHeight());
            out.write(rgb.getData().array());

            out.flush();
            fileOut.close();
        } else {
            throw new UnsupportedFormatException("cannot save to file: \""
                    + file + "\", unsupported file format");
        }

    }

    public static Image load(File file) throws IOException {
        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG")) {
            FileInputStream fileIn = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fileIn);

            int length = (int) file.length();

            Image result = new Image(Format.JPG, length);

            in.readFully(result.getData().array());

            in.close();
            fileIn.close();

            return result;
        } else if (file.getName().endsWith(".rgb")
                || file.getName().endsWith(".rgb")) {
            FileInputStream fileIn = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fileIn);

            int width = in.readInt();
            int height = in.readInt();
            
            Image result = new Image(Format.RGB24, width, height);

            in.readFully(result.getData().array());

            in.close();
            fileIn.close();

            return result;
        } else {
            throw new UnsupportedFormatException("cannot load file: \"" + file
                    + "\", unsupported file format");
        }
    }

}
