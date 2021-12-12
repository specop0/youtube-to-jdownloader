package helper;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.util.IOUtils;

public class MemorySerializer {
    public static <T extends Serializable> String Serialize(T item) {
        String serializedItem = null;
        try {
            if (item != null) {
                serializedItem = new String(Base64.getEncoder().encode(IOUtils.serialize(item)),
                        StandardCharsets.UTF_8);
            }
        } catch (Exception exception) {
            Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, exception);
        }
        return serializedItem;
    }

    public static <T extends Serializable> T Deserialize(String serializedItem) {
        T item = null;
        try {
            if (serializedItem != null) {
                item = IOUtils.deserialize(Base64.getDecoder().decode(serializedItem.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (Exception exception) {
            Logger.getLogger(File.class.getName()).log(Level.SEVERE, null, exception);
        }
        return item;
    }
}
