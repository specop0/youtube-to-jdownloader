package models;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.VideoSnippet;

import org.json.JSONObject;

public class Video implements Comparable<Video> {

    public final String Title;
    public final String Id;
    public final String ChannelId;
    public final DateTime PublishedAt;

    public Video(com.google.api.services.youtube.model.Video video) {
        this.Id = video.getId();
        VideoSnippet snippet = video.getSnippet();
        this.PublishedAt = snippet.getPublishedAt();
        this.ChannelId = snippet.getChannelId();
        this.Title = snippet.getTitle();
    }

    public Video(String id, String title, String channelId, long publishedAt) {
        this.Id = id;
        this.Title = title;
        this.ChannelId = channelId;
        this.PublishedAt = new DateTime(publishedAt);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.Id);
        json.put("title", this.Title);
        json.put("channelId", this.ChannelId);
        json.put("publishedAt", this.PublishedAt.getValue());
        return json;
    }

    public static Video fromJSON(JSONObject json) {
        return new Video(json.getString("id"), json.getString("title"), json.getString("channelId"),
                json.getLong("publishedAt"));
    }

    @Override
    public int compareTo(Video right) {
        Video left = this;
        long leftValue = left.PublishedAt.getValue();
        long rightValue = right.PublishedAt.getValue();

        if (leftValue == rightValue) {
            return 0;
        }

        return leftValue > rightValue ? 1 : -1;
    }
}
