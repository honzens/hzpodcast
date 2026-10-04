package com.honzens.hzpodcast.ui.radio;

import androidx.media3.common.MimeTypes;

import java.util.ArrayList;
import java.util.List;

public class RadioStationData {

    public static List<RadioStation> getStations() {
        List<RadioStation> stations = new ArrayList<>();
        stations.add(new RadioStation(
                "中央廣播電臺 RTI",
                "Taiwan",
                "中文",
                "https://streamak0138.akamaized.net/live0138lh-mbm9/_definst_/rti3/playlist.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "ICRT",
                "Taiwan",
                "English",
                "https://stream.rcs.revma.com/nkdfurztxp3vv",
                MimeTypes.AUDIO_MP4
        ));
        stations.add(new RadioStation(
                "警察廣播電臺",
                "Taiwan",
                "中文",
                "https://stream.pbs.gov.tw/live/mp3:PBS/playlist.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "飛碟聯播網",
                "Taiwan",
                "中文",
                "https://stream.rcs.revma.com/em90w4aeewzuv",
                null
        ));
        stations.add(new RadioStation(
                "九八新聞台 News98",
                "Taiwan",
                "中文",
                "https://stream.rcs.revma.com/pntx1639ntzuv.m4a",
                MimeTypes.AUDIO_MP4
        ));
        //stations.add(new RadioStation(
        //        "中廣新聞網",
        //        "Taiwan",
        //        "中文",
        //        "https://stream.rcs.revma.com/78fm9wyy2tzuv",
        //        null
        //));
        //stations.add(new RadioStation(
        //        "中廣音樂網 i Radio",
        //        "Taiwan",
        //        "中文",
        //        "https://stream.rcs.revma.com/ndk05tyy2tzuv",
        //        null
        //));
        //stations.add(new RadioStation(
        //        "中廣流行網 I Like Radio",
        //        "Taiwan",
        //        "中文",
        //        "https://stream.rcs.revma.com/aw9uqyxy2tzuv",
        //        null
        //));
        //stations.add(new RadioStation(
        //        "高雄廣播電臺 FM94.3",
        //        "Taiwan",
        //        "中文",
        //        "https://stream.rcs.revma.com/pet2dvug1bkvv/",
        //        null
        //));
        stations.add(new RadioStation(
                "臺北廣播電臺",
                "Taiwan",
                "中文",
                "https://stream.ginnet.cloud/live0130lo-yfyo/_definst_/fm/playlist.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        //
        stations.add(new RadioStation(
                "ERA",
                "Malaysia",
                "Bahasa Melayu",
                "https://n17a-eu.rcs.revma.com/crec9cmbv4uvv",
                null
        ));
        //stations.add(new RadioStation(
        //        "SINAR",
        //        "Malaysia",
        //        "Bahasa Melayu",
        //        "https://n08a-eu.rcs.revma.com/azatk0tbv4uvv/playlist.m3u8",
        //        MimeTypes.APPLICATION_M3U8
        //));
        stations.add(new RadioStation(
                "HITZ",
                "Malaysia",
                "English",
                "https://stream.rcs.revma.com/488kt4sbv4uvv",
                null
        ));
        stations.add(new RadioStation(
                "Hot FM",
                "Malaysia",
                "Bahasa Melayu",
                "https://stream.rcs.revma.com/drakdf8mtd3vv/hls.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "Raaga",
                "Malaysia",
                "Tamil",
                "https://n06.rcs.revma.com/1ut6qwtbv4uvv",
                null
        ));
        stations.add(new RadioStation(
                "LITE FM",
                "Malaysia",
                "English",
                "https://stream.rcs.revma.com/bn4ex8sbv4uvv",
                null
        ));
        stations.add(new RadioStation(
                "8FM",
                "Malaysia",
                "中文",
                "https://stream.rcs.revma.com/qp0xrd9mtd3vv",
                null
        ));
        //stations.add(new RadioStation(
        //        "ZAYAN",
        //        "Malaysia",
        //        "Bahasa Melayu",
        //        "https://n04.rcs.revma.com/7ww2a4tbv4uvv/4_vp",
        //        null
        //));
        stations.add(new RadioStation(
                "988 FM",
                "Malaysia",
                "中文",
                "https://28103.live.streamtheworld.com/988_FMAAC.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "TraXX FM",
                "Malaysia",
                "English",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/TRAXX_FMAAC_SC",
                null
        ));
        //
        stations.add(new RadioStation(
                "RTHK Radio 1",
                "Hong Kong",
                "粵語",
                "https://rthkradio1-live.akamaized.net/hls/live/2035313/radio1/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK Radio 2",
                "Hong Kong",
                "粵語",
                "https://rthkradio2-live.akamaized.net/hls/live/2040078/radio2/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK Radio 3",
                "Hong Kong",
                "English",
                "https://rthkradio3-live.akamaized.net/hls/live/2040079/radio3/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK Radio 4",
                "Hong Kong",
                "中文/English",
                "https://rthkradio4-live.akamaized.net/hls/live/2040080/radio4/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK Radio 5",
                "Hong Kong",
                "粵語",
                "https://rthkradio5-live.akamaized.net/hls/live/2040081/radio5/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK 普通話台",
                "Hong Kong",
                "普通話",
                "https://rthkradiopth-live.akamaized.net/hls/live/2040082/radiopth/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK 香港之聲",
                "Hong Kong",
                "普通話",
                "https://rthkradiocnrhk-live.akamaized.net/hls/live/2046111/radiocnrhk/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        stations.add(new RadioStation(
                "RTHK 粵港澳大灣區之聲",
                "Hong Kong",
                "普通話",
                "https://rthkradiocmgrgb-live.akamaized.net/hls/live/2046112/radiocmgrgb/master.m3u8",
                MimeTypes.APPLICATION_M3U8
        ));
        //stations.add(new RadioStation(
        //        "新城知訊台 997",
        //        "Hong Kong",
        //        "粵語",
        //        "https://metroradio-lh.akamaihd.net/i/997_h@349799/index_48_a-b.m3u8",
        //        MimeTypes.APPLICATION_M3U8
        //));
        //stations.add(new RadioStation(
        //        "新城財經台 104",
        //        "Hong Kong",
        //        "粵語",
        //        "https://metroradio-lh.akamaihd.net/i/104_h@349798/index_48_a-p.m3u8",
        //        MimeTypes.APPLICATION_M3U8
        //));
        //
        stations.add(new RadioStation(
                "YES 933",
                "Singapore",
                "中文",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/YES933AAC_SC",
                MimeTypes.AUDIO_AAC

        ));
        stations.add(new RadioStation(
                "LOVE 972",
                "Singapore",
                "中文",
                "https://28333.live.streamtheworld.com/LOVE972FM_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "CAPITAL 958",
                "Singapore",
                "中文",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/CAPITAL958FM_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "CLASS 95",
                "Singapore",
                "English",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/CLASS95_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "GOLD 905",
                "Singapore",
                "English",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/GOLD905_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "987",
                "Singapore",
                "English",
                "https://22393.live.streamtheworld.com/987FM_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "CNA938",
                "Singapore",
                "English",
                "https://22893.live.streamtheworld.com/MP3_938NOW.mp3",
                null
        ));
        //stations.add(new RadioStation(
        //        "WARNA 942",
        //        "Singapore",
        //        "Bahasa Melayu",
        //        "https://playerservices.streamtheworld.com/api/livestream-redirect/WARNA942_PREM.aac",
        //        MimeTypes.AUDIO_AAC
        //));
        stations.add(new RadioStation(
                "OLI 968",
                "Singapore",
                "Tamil",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/OLI968FM_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        stations.add(new RadioStation(
                "SYMPHONY 924",
                "Singapore",
                "English",
                "https://playerservices.streamtheworld.com/api/livestream-redirect/SYMPHONY924_PREM.aac",
                MimeTypes.AUDIO_AAC
        ));
        return stations;
    }
}