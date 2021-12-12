package main;

import com.google.api.services.youtube.YouTube;

import org.json.JSONObject;

import helper.File;
import restservices.RestCache;
import youtube.MyDataStoreFactory;
import youtube.YouTubeApiFactory;
import youtube.YouTubeApiWrapper;

public class App {
    public static void main(String[] args) {
        String configurationFilename = "configuration.json";
        if (args.length > 0) {
            configurationFilename = args[0];
        }

        try {
            JSONObject configuration = new JSONObject(File.ReadAllText(configurationFilename));
            JSONObject cacheConfiguration = configuration.getJSONObject("cache");
            JSONObject jdownloaderConfiguration = configuration.getJSONObject("jdownloader");
            String jdownloaderFolderWatch = jdownloaderConfiguration.getString("folderwatch");

            RestCache cache = new RestCache(cacheConfiguration.getInt("port"),
                    cacheConfiguration.getString("authorization"));
            MyDataStoreFactory.Initialize(cache);

            YouTube youTubeApi = new YouTubeApiFactory().getService();
            YouTubeApiWrapper youTubeApiWrapper = new YouTubeApiWrapper(youTubeApi);

            Controller controller = new Controller(youTubeApiWrapper, cache, jdownloaderFolderWatch);
            controller.NotifyUnwatchedVideosFromSubscriptionFeed();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
