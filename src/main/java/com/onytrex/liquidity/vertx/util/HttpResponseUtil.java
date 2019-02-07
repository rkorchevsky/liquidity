package com.onytrex.liquidity.vertx.util;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

public class HttpResponseUtil {

    private static final CharSequence RESPONSE_TYPE_PLAIN = HttpHeaders.createOptimized("text/plain");
    private static final CharSequence RESPONSE_TYPE_JSON = HttpHeaders.createOptimized("application/json");
    private static final CharSequence SERVER = HttpHeaders.createOptimized("https://liquidity.onytrex.com");
    private static final CharSequence HEADER_SERVER = HttpHeaders.createOptimized("server");
    private static final CharSequence HEADER_CONTENT_TYPE = HttpHeaders.createOptimized("content-type");
    private static final CharSequence HEADER_CONTENT_LENGTH = HttpHeaders.createOptimized("content-length");

    private static final String BAD_REQUEST_PARAMETER = "Bad request parameter";

    public static void fillHeader(MultiMap headers, int contentLength) {
        cors(headers, RESPONSE_TYPE_JSON);

        if (contentLength == 0) return;

        headers.add(HEADER_CONTENT_LENGTH, HttpHeaders.createOptimized("" + contentLength));
    }

    public static void badRequestParameter(HttpServerRequest request) {
        request.response().setStatusCode(400);
        errorFor(request, BAD_REQUEST_PARAMETER);
    }

    public static void nofFound(HttpServerRequest request, String msg) {
        request.response().setStatusCode(404);
        errorFor(request, msg);
    }

    public static void methodNotAllowed(HttpServerRequest request) {
        request.response().setStatusCode(405);
        errorFor(request, request.method() + " method not allowed");
    }

    private static void errorFor(HttpServerRequest request, String message) {
        var response = request.response();
        var headers = response.headers();
        cors(headers, RESPONSE_TYPE_PLAIN);

        headers.add(HEADER_CONTENT_LENGTH, HttpHeaders.createOptimized("" + message.length()));
        response.end(Buffer.buffer(message));
    }

    private static void cors(MultiMap headers, CharSequence value) {
        headers.add(HEADER_CONTENT_TYPE, value)
                .add(HEADER_SERVER, SERVER)
                .add("Access-Control-Allow-Origin", "*")
                .add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS")
                .add("Access-Control-Allow-Header","Authentication")
                .add("Access-Control-Allow-Credentials", "true");
    }

    public static void ok(HttpServerRequest request, Buffer buffer) {
        final var response = request.response();
        fillHeader(response.headers(), buffer.length());
        response.end(buffer);
    }
}
