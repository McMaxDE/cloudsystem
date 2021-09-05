package de.polocloud.internalwrapper.utils.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class ServiceProperties {

    private File file;
    private int port;
    private String[] properties;

    private boolean can;

    public ServiceProperties(File file, String child, int port) {
        can = new File(file.getPath(), child).exists();
        this.file = new File(file.getPath(), child);

        this.port = port;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public void writeFile() {
        if (!can) {
            try {
                FileWriter fileWriter = new FileWriter(file);

                for (String line : properties) {
                    fileWriter.write(line + "\n");
                }
                fileWriter.close();

            } catch (IOException ignored) { }
        }
    }

    public File getFile() {
        return file;
    }


    public int getPort() {
        return port;
    }
}
