package restservices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public abstract class RestBase {
    protected JSONObject Do(String url, String httpMethod) {
        return this.Do(url, httpMethod, null);
    }

    protected JSONObject Do(String url, String httpMethod, Object input) {
        try {
            URL actualUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) actualUrl.openConnection();
            connection.setRequestMethod(httpMethod);
            connection.setDoOutput(true);
            if (input != null) {
                connection.getOutputStream().write(input.toString().getBytes());
            }

            connection.connect();

            String body;
            try {
                body = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                body = null;
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK && body != null && !"".equals(body)) {
                return new JSONObject(body);
            }
        } catch (IOException ex) {
        }
        return new JSONObject();
    }
}
