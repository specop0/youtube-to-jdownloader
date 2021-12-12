package main;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import helper.File;
import models.Video;
import restservices.RestCache;
import youtube.IYouTubeApi;

public class Controller {
    private final IYouTubeApi youTube;
    private final RestCache cache;
    private final String jdownloaderFolderWatch;
    public static int MAX_VIDEOS_FOR_SUBSCRIPTION = 7;

    public Controller(IYouTubeApi youTube, RestCache cache, String jdownloaderFolderWatch) {
        this.youTube = youTube;
        this.cache = cache;
        this.jdownloaderFolderWatch = jdownloaderFolderWatch;
    }

    public void NotifyUnwatchedVideosFromSubscriptionFeed() {
        List<Video> videosFromSubscriptions = this.GetSubscriptionFeed();

        List<Video> unwatchedVideos = this.GetUnwatchedVideos(videosFromSubscriptions);

        this.NotifyUnwatchedVideos(unwatchedVideos);
    }

    public List<Video> GetSubscriptionFeed() {
        try {
            List<String> subscribedChannelIds = this.youTube.GetSubscribedChannelIds();

            List<String> uploadPlaylistIds = this.youTube.GetUploadPlaylistIds(subscribedChannelIds);

            List<String> playlistItemIds = new ArrayList<>();
            for (String uploadPlayListId : uploadPlaylistIds) {
                playlistItemIds.addAll(this.youTube.GetPlaylistItemIds(uploadPlayListId, MAX_VIDEOS_FOR_SUBSCRIPTION));
            }

            List<Video> videos = this.youTube.GetVideos(playlistItemIds);

            List<Video> sortedVideos = videos.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

            return sortedVideos;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new ArrayList<>();
    }

    public List<Video> GetUnwatchedVideos(List<Video> videos) {
        Map<String, Map<String, Video>> watchedVideosByChannelId = new HashMap<>();
        List<Video> unwatchedVideos = new ArrayList<>();

        for (Video video : videos) {
            String channelId = video.ChannelId;

            if (!watchedVideosByChannelId.containsKey(channelId)) {
                watchedVideosByChannelId.put(channelId, this.GetWatchedVideos(channelId));
            }
            Map<String, Video> watchedVideosById = watchedVideosByChannelId.get(channelId);

            if (!watchedVideosById.containsKey(video.Id)) {
                watchedVideosById.put(video.Id, video);
                unwatchedVideos.add(video);
            }
        }

        for (String channelId : watchedVideosByChannelId.keySet()) {
            this.SetWatchedVideos(channelId, watchedVideosByChannelId.get(channelId).values());
        }

        return unwatchedVideos;
    }

    public Map<String, Video> GetWatchedVideos(String channelId) {
        Map<String, Video> videosById = new HashMap<>();
        for (Video video : this.cache.GetVideos(channelId)) {
            videosById.put(video.Id, video);
        }
        return videosById;
    }

    public void SetWatchedVideos(String channelId, Collection<Video> videos) {
        List<Video> latestWatchedVideos = videos.stream().sorted(Collections.reverseOrder())
                .limit(MAX_VIDEOS_FOR_SUBSCRIPTION * 2).collect(Collectors.toList());
        this.cache.SetVideos(channelId, latestWatchedVideos);
    }

    public void NotifyUnwatchedVideos(List<Video> videos) {
        if (videos.isEmpty()) {
            System.out.println("No New Video");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Video video : videos) {
            builder.append(String.format("text=https://www.youtube.com/watch?v=%s", video.Id));
            builder.append("\n");
        }
        String youTubeVideoLinks = builder.toString();
        System.out.println(youTubeVideoLinks);

        long timestamp = Clock.systemUTC().millis();
        String filename = new java.io.File(this.jdownloaderFolderWatch, timestamp + ".crawljob").getPath();
        File.WriteAllText(filename, youTubeVideoLinks);
    }
}
