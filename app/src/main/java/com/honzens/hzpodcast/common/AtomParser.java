package com.honzens.hzpodcast.common;

import android.util.Xml;
import com.honzens.hzpodcast.classes.FeedItem;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AtomParser {
    public static List<FeedItem> parse(InputStream input) throws Exception
    {
        List<FeedItem> result = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(input, "UTF-8");
        int event = parser.getEventType();
        boolean isAtom = false;
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                // 判斷 Atom
                if ("feed".equals(name)) {
                    isAtom = true;
                }
                // RSS item
                if (!isAtom && "item".equals(name)) {
                    FeedItem item = parseRSSItem(parser);
                    result.add(item);
                }
                // Atom entry
                if (isAtom && "entry".equals(name)) {
                    FeedItem item = parseAtomEntry(parser);
                    result.add(item);
                }
            }
            event = parser.next();
        }
        return result;
    }
    private static FeedItem parseRSSItem(XmlPullParser parser)
            throws Exception {
        FeedItem item = new FeedItem();
        int event;
        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                switch(name) {
                    case "title":
                        item.setTitle(parser.nextText());
                        break;
                    case "link":
                        item.setUrl(parser.nextText());
                        break;
                    case "description":
                        item.setSummary(parser.nextText());
                        break;
                    case "pubDate":
                        item.setPubDate(parser.nextText());
                        break;
                }
            }
            else if (event == XmlPullParser.END_TAG
                    && "item".equals(parser.getName())) {
                break;
            }
        }
        return item;
    }
    private static FeedItem parseAtomEntry(XmlPullParser parser)
            throws Exception {
        FeedItem item = new FeedItem();
        int event;
        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                switch(name) {
                    case "title":
                        item.setTitle(parser.nextText());
                        break;
                    case "link":
                        String href = parser.getAttributeValue(null,"href");
                        if (href != null) {
                            item.setUrl(href);
                        }
                        break;
                    case "summary":
                        item.setSummary(parser.nextText());
                        break;
                    case "content":
                        item.setContent(parser.nextText());
                        break;
                    case "published":
                        item.setPubDate(parser.nextText());
                        break;
                    case "updated":
                        if(item.pubDate != null) {
                            if (item.pubDate.isEmpty())
                                item.setPubDate(parser.nextText());
                        }
                        else {
                            item.setPubDate(parser.nextText());
                        }
                        break;
                }
            }
            else if(event == XmlPullParser.END_TAG
                    && "entry".equals(parser.getName())) {
                break;
            }
        }
        return item;
    }
}