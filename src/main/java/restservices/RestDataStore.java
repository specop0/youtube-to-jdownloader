package restservices;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

public class RestDataStore<V extends Serializable> extends RestBase implements DataStore<V> {

    protected final String Id;
    protected final DataStoreFactory Factory;
    protected final RestCache Cache;

    public RestDataStore(String id, DataStoreFactory factory, RestCache cache) {
        this.Id = id;
        this.Factory = factory;
        this.Cache = cache;
    }

    protected Map<String, V> GetDictionary(){
        return this.Cache.<V>GetDictionary(this.Id);
    }

    protected void SetDictionary(Map<String, V>  items){
        this.Cache.<V>SetDictionary(this.Id, items);
    }

    @Override
    public DataStoreFactory getDataStoreFactory() {
        return this.Factory;
    }

    @Override
    public String getId() {
        return this.Id;
    }

    @Override
    public int size() throws IOException {
        return this.GetDictionary().size();
    }

    @Override
    public boolean isEmpty() throws IOException {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(String key) throws IOException {
        return this.GetDictionary().containsKey(key);
    }

    @Override
    public boolean containsValue(V value) throws IOException {
        return this.GetDictionary().values().contains(value);
    }

    @Override
    public Set<String> keySet() throws IOException {
        return this.GetDictionary().keySet();
    }

    @Override
    public Collection<V> values() throws IOException {
        return this.GetDictionary().values();
    }

    @Override
    public V get(String key) throws IOException {
        return this.GetDictionary().get(key);
    }

    @Override
    public DataStore<V> set(String key, V value) throws IOException {
        Map<String, V> items = this.GetDictionary();
        items.put(key, value);
        this.SetDictionary(items);
        return this;
    }

    @Override
    public DataStore<V> clear() throws IOException {
        this.SetDictionary(new HashMap<>());
        return this;
    }

    @Override
    public DataStore<V> delete(String key) throws IOException {
        Map<String, V> items = this.GetDictionary();
        items.remove(key);
        this.SetDictionary(items);
        return this;
    }

}