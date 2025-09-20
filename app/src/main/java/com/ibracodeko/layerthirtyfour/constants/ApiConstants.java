package com.ibracodeko.layerthirtyfour.constants;

import java.util.Arrays;
import java.util.List;

public class ApiConstants {
    
    // API Configuration
    public static final String API_KEY = "qwertyuiop";
    
    // Available Methods from API Server
    public static final String METHOD_MOONX_KILL = "MoonXKill";
    public static final String METHOD_MOONX_PPS = "MoonXPps";
    public static final String METHOD_MOONX_UDP = "MoonXUdp";
    public static final String METHOD_MOONX_TLS = "MoonXTls";
    public static final String METHOD_MOONX_FAST = "MoonXFast";
    public static final String METHOD_MOONX_SLOW = "MoonXSlow";
    public static final String METHOD_MOONX_MIX = "MoonXMix";
    public static final String METHOD_MOONX_RAW = "MoonXRaw";
    public static final String METHOD_MOONX_CF = "MoonXCf";
    public static final String METHOD_MOONX_PIDORAS = "MoonXPidoras";
    public static final String METHOD_MOONX_TCP = "MoonXTcp";
    public static final String METHOD_MOONX_OVH = "MoonXOvh.js";
    public static final String METHOD_PROXY = "proxy";
    
    // Method Categories
    public static final List<String> WEB_METHODS = Arrays.asList(
        METHOD_MOONX_KILL,
        METHOD_MOONX_PPS,
        METHOD_MOONX_TLS,
        METHOD_MOONX_FAST,
        METHOD_MOONX_SLOW,
        METHOD_MOONX_CF,
        METHOD_MOONX_RAW
    );
    
    public static final List<String> NETWORK_METHODS = Arrays.asList(
        METHOD_MOONX_UDP,
        METHOD_MOONX_TCP,
        METHOD_MOONX_OVH,
        METHOD_MOONX_MIX,
        METHOD_MOONX_PIDORAS
    );
    
    public static final List<String> PROXY_METHODS = Arrays.asList(
        METHOD_PROXY
    );
    
    // Get all available methods
    public static final List<String> ALL_METHODS = Arrays.asList(
        METHOD_MOONX_KILL,
        METHOD_MOONX_PPS,
        METHOD_MOONX_UDP,
        METHOD_MOONX_TLS,
        METHOD_MOONX_FAST,
        METHOD_MOONX_SLOW,
        METHOD_MOONX_MIX,
        METHOD_MOONX_RAW,
        METHOD_MOONX_CF,
        METHOD_MOONX_PIDORAS,
        METHOD_MOONX_TCP,
        METHOD_MOONX_OVH,
        METHOD_PROXY
    );
    
    // Method Descriptions
    public static String getMethodDescription(String method) {
        switch (method) {
            case METHOD_MOONX_KILL:
                return "High-intensity network stress testing";
            case METHOD_MOONX_PPS:
                return "Packets per second testing";
            case METHOD_MOONX_UDP:
                return "UDP protocol testing with proxy support";
            case METHOD_MOONX_TLS:
                return "TLS/SSL connection testing";
            case METHOD_MOONX_FAST:
                return "Fast connection testing";
            case METHOD_MOONX_SLOW:
                return "Slow connection testing";
            case METHOD_MOONX_MIX:
                return "Mixed protocol testing with proxy";
            case METHOD_MOONX_RAW:
                return "Raw socket testing";
            case METHOD_MOONX_CF:
                return "Cloudflare bypass testing";
            case METHOD_MOONX_PIDORAS:
                return "Advanced network testing with proxy";
            case METHOD_MOONX_TCP:
                return "TCP protocol testing with proxy";
            case METHOD_MOONX_OVH:
                return "OVH server testing";
            case METHOD_PROXY:
                return "Proxy scraping and testing";
            default:
                return "Network testing method";
        }
    }
    
    // Method display names
    public static String getMethodDisplayName(String method) {
        switch (method) {
            case METHOD_MOONX_KILL:
                return "MoonX Kill";
            case METHOD_MOONX_PPS:
                return "MoonX PPS";
            case METHOD_MOONX_UDP:
                return "MoonX UDP";
            case METHOD_MOONX_TLS:
                return "MoonX TLS";
            case METHOD_MOONX_FAST:
                return "MoonX Fast";
            case METHOD_MOONX_SLOW:
                return "MoonX Slow";
            case METHOD_MOONX_MIX:
                return "MoonX Mix";
            case METHOD_MOONX_RAW:
                return "MoonX Raw";
            case METHOD_MOONX_CF:
                return "MoonX CF";
            case METHOD_MOONX_PIDORAS:
                return "MoonX Pidoras";
            case METHOD_MOONX_TCP:
                return "MoonX TCP";
            case METHOD_MOONX_OVH:
                return "MoonX OVH";
            case METHOD_PROXY:
                return "Proxy Scraper";
            default:
                return method;
        }
    }
}