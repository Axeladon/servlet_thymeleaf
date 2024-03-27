package com.example;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String getAndEncodeRequestParam(String parameter) throws UnsupportedEncodingException {
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
    }
}
