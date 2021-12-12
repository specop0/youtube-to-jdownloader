package restservices;

import java.util.List;

import models.Video;

public class RestCache extends RestCacheBase {

    public RestCache(int port, String authorization) {
        super(port, authorization);
    }

    public void SetVideos(String channelId, List<Video> videos) {
        this.SetItems(channelId, videos, x -> x.toJSON());
    }

    public List<Video> GetVideos(String channelId) {
        return this.GetItems(channelId, x -> Video.fromJSON(x));
    }
}
