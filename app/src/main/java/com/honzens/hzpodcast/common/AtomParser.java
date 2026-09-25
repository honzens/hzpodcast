package com.honzens.hzpodcast.common;

import android.util.Xml;

import com.honzens.hzpodcast.classes.Episode;
import com.honzens.hzpodcast.classes.Programs;

import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;

public class AtomParser {
    public static Programs parsePodcast(InputStream inputStream) throws Exception {
        Programs podcast = new Programs();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");
        Episode episode = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                // channel
                if (episode == null) {
                    if ("title".equals(tag)) {
                        podcast.title = parser.nextText();
                    } else if ("description".equals(tag)) {
                        podcast.description = parser.nextText();
                    } else if ("author".equals(tag)
                            || "itunes:author".equals(tag)) {
                        podcast.author = parser.nextText();
                    } else if ("image".equals(tag)) {
                        String href = parser.getAttributeValue(
                                null, "href");
                        if (href != null) {
                            podcast.imageUrl = href;
                        }
                    }
                }
                // item
                if ("item".equals(tag)) {
                    episode = new Episode();
                } else if (episode != null) {
                    if ("title".equals(tag)) {
                        episode.title = parser.nextText();
                    } else if ("description".equals(tag)) {
                        episode.description = parser.nextText();
                    } else if ("pubDate".equals(tag)) {
                        episode.pubDate = parser.nextText();
                    } else if ("duration".equals(tag)) {
                        episode.duration = parser.nextText();
                    } else if ("image".equalsIgnoreCase(tag)) {
                        String href = parser.getAttributeValue(null, "href");
                        if (href != null) {
                            episode.imageUrl = href;
                        }
                    }
                    else if ("enclosure".equals(tag)) {
                        episode.audioUrl = parser.getAttributeValue(null, "url");
                        episode.audioType = parser.getAttributeValue(null, "type");
                        String length = parser.getAttributeValue(null, "length");
                        if (length != null) {
                            try {
                                episode.audioLength = Long.parseLong(length);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("item".equals(parser.getName()) && episode != null && episode.audioUrl!= null && !episode.audioUrl.isEmpty())
                {
                    podcast.episodes.add(episode);
                    episode = null;
                }
            }
            eventType = parser.next();
        }
        return podcast;
    }
}