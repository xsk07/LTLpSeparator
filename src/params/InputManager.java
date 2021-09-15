package params;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputManager {

    public static InputStream readFile(String filename) throws IOException {
        File file = new File(filename);
        InputStream fileStream = new FileInputStream(file);
        return fileStream;
    }

}
