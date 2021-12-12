package restservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

import helper.MemorySerializer;

public abstract class RestCacheBase extends RestBase {

    public static final String KEY_SINGLE_ENTRY = "$data";

    protected RestCacheBase(int port, String authorization) {
        this(String.format("http://localhost:%d/data/%s", port, authorization));
    }

    private RestCacheBase(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    protected String BaseUrl;

    protected String GetUrl(String key) {
        return String.format("%s/%s", this.BaseUrl, key);
    }

    protected <T> void SetItems(String key, List<T> items, Function<T, JSONObject> jsonFunc) {
        JSONObject obj = new JSONObject();
        List<JSONObject> serializedItems = items.stream().map(jsonFunc).collect(Collectors.toList());
        obj.put(KEY_SINGLE_ENTRY, serializedItems);

        String url = this.GetUrl(key);
        this.Do(url, "PUT", obj);
    }

    protected <T> List<T> GetItems(String key, Function<JSONObject, T> jsonFunc) {
        List<T> items = new ArrayList<>();

        String url = this.GetUrl(key);
        JSONObject jsonResult = this.Do(url, "GET");

        if (jsonResult.keySet().contains(KEY_SINGLE_ENTRY)) {
            jsonResult.getJSONArray(KEY_SINGLE_ENTRY).forEach(x -> {
                JSONObject jsonObject = (JSONObject) x;
                items.add(jsonFunc.apply(jsonObject));
            });
        }

        return items;
    }

    public <T extends Serializable> Map<String, T> GetDictionary(String key) {
        Map<String, T> items = new HashMap<>();

        String url = this.GetUrl(key);
        JSONObject jsonResult = this.Do(url, "GET");

        for (String itemKey : jsonResult.keySet()) {
            String serializedItem = jsonResult.getString(itemKey);
            T item = MemorySerializer.Deserialize(serializedItem);
            items.put(itemKey, item);
        }

        return items;
    }

    public <T extends Serializable> void SetDictionary(String key, Map<String, T> items) {
        JSONObject obj = new JSONObject();

        for (String itemKey : items.keySet()) {
            T item = items.get(itemKey);
            String serializedItem = MemorySerializer.Serialize(item);
            obj.put(itemKey, serializedItem);
        }

        String url = this.GetUrl(key);
        this.Do(url, "PUT", obj);
    }
}
