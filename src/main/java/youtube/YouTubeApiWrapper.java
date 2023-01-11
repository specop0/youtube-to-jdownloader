package youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoLiveStreamingDetails;

import helper.Linq;
import models.Video;

public class YouTubeApiWrapper implements IYouTubeApi {
    private final YouTube api;

    public YouTubeApiWrapper(YouTube api) {
        this.api = api;
    }

    public List<String> GetSubscribedChannelIds() throws IOException {
        List<String> subscribedChannelIds = new ArrayList<>();

        YouTube.Subscriptions.List request = this.api.subscriptions().list(Arrays.asList("snippet", "contentDetails"));
        request.setMine(true).setMaxResults(50l);

        String pageToken = null;
        while (true) {
            if (pageToken != null) {
                request.setPageToken(pageToken);
            }
            SubscriptionListResponse response = request.execute();
            response.getItems().stream().filter(item -> "youtube#subscription".equals(item.getKind()))
                    .map(item -> item.getSnippet().getResourceId().getChannelId())
                    .forEach(item -> subscribedChannelIds.add(item));

            pageToken = response.getNextPageToken();
            if (pageToken == null) {
                break;
            }
        }

        return subscribedChannelIds;
    }

    public List<String> GetUploadPlaylistIds(List<String> channelIds) throws IOException {
        List<String> uploadPlaylistIds = new ArrayList<>();

        for (List<String> channelIdsBatch : Linq.Chunk(channelIds, 50)) {
            YouTube.Channels.List request = this.api.channels().list(Arrays.asList("contentDetails")).setMaxResults(50l)
                    .setId(channelIdsBatch);

            ChannelListResponse response = request.execute();

            response.getItems().stream().map(item -> item.getContentDetails().getRelatedPlaylists().getUploads())
                    .forEach(item -> uploadPlaylistIds.add(item));
        }

        return uploadPlaylistIds;
    }

    public List<String> GetPlaylistItemIds(String playlistId, long maxResults) throws IOException {
        YouTube.PlaylistItems.List request = this.api.playlistItems().list(Arrays.asList("contentDetails"))
                .setMaxResults(maxResults).setPlaylistId(playlistId);

        PlaylistItemListResponse response = request.execute();
        return response.getItems().stream().filter(item -> "youtube#playlistItem".equals(item.getKind()))
                .map(item -> item.getContentDetails().getVideoId()).collect(Collectors.toList());
    }

    public List<Video> GetVideos(List<String> playlistItemIds) throws IOException {
        List<Video> videos = new ArrayList<>();

        for (List<String> videoIdsBatch : Linq.Chunk(playlistItemIds, 50)) {
            YouTube.Videos.List request = this.api.videos()
                    .list(Arrays.asList("snippet", "liveStreamingDetails", "status"))
                    .setMaxResults(50l)
                    .setId(videoIdsBatch);

            VideoListResponse response = request.execute();
            response.getItems()
                    .stream()
                    .filter(item -> {
                        VideoLiveStreamingDetails liveStreamingDetails = item.getLiveStreamingDetails();
                        if (liveStreamingDetails == null) {
                            return true;
                        }

                        DateTime start = liveStreamingDetails.getActualStartTime();
                        DateTime end = liveStreamingDetails.getActualEndTime();
                        if (start == null || end == null) {
                            return false;
                        }

                        long durationInMs = end.getValue() - start.getValue();
                        long twoHoursInMs = 2 * 60 * 60 * 1000;

                        return durationInMs < twoHoursInMs;
                    })
                    .filter(item -> !"unlisted".equals(item.getStatus().getPrivacyStatus()))
                    .map(item -> new Video(item))
                    .forEach(item -> videos.add(item));
        }

        return videos;
    }
}
