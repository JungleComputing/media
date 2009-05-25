package ibis.imaging4j.io;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IO {

    public static void save(Image image, File file) throws IOException {

        FileOutputStream out = new FileOutputStream(file);

        out.write(image.getData().array());

        out.flush();
        out.close();

    }

    public static Image load(File file) throws IOException {

        if (!file.getName().endsWith(".jpg")
                && !file.getName().endsWith(".JPG")) {
            throw new IOException("We only support loading JPG files for now");
        }

        FileInputStream fileIn = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fileIn);

        int length = (int) file.length();

        Image result = new Image(Format.JPG, length);
        
        in.readFully(result.getData().array());
        
        in.close();
        fileIn.close();
        
        return result;
    }

}
