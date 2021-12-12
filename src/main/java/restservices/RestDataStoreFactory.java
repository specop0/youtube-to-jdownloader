package restservices;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

public class RestDataStoreFactory implements DataStoreFactory {

    protected final RestCache Cache;
    protected ConcurrentHashMap<String, DataStore<?>> DataStores = new ConcurrentHashMap<>();

    public RestDataStoreFactory(RestCache cache) {
        this.Cache = cache;
    }

    public DataStore<StoredCredential> GetCredentialDataStore() {
        return this.<StoredCredential>getDataStore("oauth");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        if (this.DataStores.containsKey(id)) {
            return (DataStore<V>) this.DataStores.get(id);
        }

        DataStore<V> dataStore = new RestDataStore<V>(id, this, this.Cache);
        this.DataStores.put(id, dataStore);
        return dataStore;
    }
}
