package youtube;

import java.io.IOException;
import java.util.List;

import models.Video;

public interface IYouTubeApi {
    public List<String> GetSubscribedChannelIds() throws IOException;

    public List<String> GetUploadPlaylistIds(List<String> channelIds) throws IOException;

    public List<String> GetPlaylistItemIds(String playlistId, long maxResults) throws IOException;

    public List<Video> GetVideos(List<String> playlistItemIds) throws IOException;
}
