package com.honzens.hzpodcast.ui.radio;

import androidx.media3.common.MimeTypes;

import java.util.ArrayList;
import java.util.List;

public class RadioStationData {

    public static List<RadioStation> getStations() {
        List<RadioStation> stations = new ArrayList<>();
        stations.add(new RadioStation(
                "臺北廣播電臺",
                "FM 93.1",
                "https://stream.ginnet.cloud/live0130lo-yfyo/_definst_/fm/playlist.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        //stations.add(new RadioStation(
        //        "臺北廣播電臺",
        //        "AM 1134",
        //        "https://stream.ginnet.cloud/live0130lo-yfyo/_definst_/am/playlist.m3u8"
        //));
        stations.add(new RadioStation(
                "高雄廣播電臺",
                "FM 94.3",
                "https://stream.rcs.revma.com/pet2dvug1bkvv.mp3",
                MimeTypes.AUDIO_MPEG
        ));
        stations.add(new RadioStation(
                "ICRT",
                "FM 100.7",
                "https://stream.rcs.revma.com/nkdfurztxp3vv",
                MimeTypes.AUDIO_MPEG
        ));
        stations.add(new RadioStation(
                "警察廣播電臺",
                "全國治安交通網",
                "https://stream.pbs.gov.tw/live/PBS/playlist.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        //stations.add(new RadioStation(
        //        "國立教育廣播電臺",
        //        "臺北總臺",
        //        "https://cast.ner.gov.tw/1",
        //        null
        //));
        //stations.add(new RadioStation(
        //        "國立教育廣播電臺",
        //        "高雄分臺",
        //        "https://cast.ner.gov.tw/5",
        //        null
        //));
        stations.add(new RadioStation(
                "News98",
                "FM 98.1",
                "https://stream.rcs.revma.com/pntx1639ntzuv.m4a",
                MimeTypes.AUDIO_MP4
        ));
        return stations;
    }
}