package youtube;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

import restservices.RestCache;
import restservices.RestDataStore;

public class MyDataStoreFactory implements DataStoreFactory {

    private static MyDataStoreFactory INSTANCE;
    protected RestCache Cache;
    protected ConcurrentHashMap<String, DataStore<?>> DataStores = new ConcurrentHashMap<>();

    private MyDataStoreFactory() {
    }

    public static MyDataStoreFactory getDefaultInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyDataStoreFactory();
        }

        return INSTANCE;
    }

    public static void Initialize(RestCache cache) {
        getDefaultInstance().Cache = cache;
    }

    public static DataStore<StoredCredential> GetCredentialDataStore() {
        return getDefaultInstance().<StoredCredential>getDataStore("oauth");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        if (this.DataStores.containsKey(id)) {
            return (DataStore<V>) this.DataStores.get(id);
        }

        DataStore<V> dataStore = new RestDataStore<V>(id, getDefaultInstance(), this.Cache);
        this.DataStores.put(id, dataStore);
        return dataStore;
    }
}
