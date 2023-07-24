package org.cybercrowd.activity.utils;

import org.apache.http.client.methods.*;

/**
 * @program: transferwallet-parent
 * @description: 内置http请求方式的枚举类型
 * @create: Created by 孟凡鼎 on 2019-07-18
 */
public class HttpRequestMethod {
    public enum HttpRequestMethodEnum {
        // HttpGet请求
        HttpGet {
            @Override
            public HttpRequestBase createRequest(String url) { return new HttpGet(url); }
        },
        // HttpPost 请求
        HttpPost {
            @Override
            public HttpRequestBase createRequest(String url) { return new HttpPost(url); }
        },
        // HttpPut 请求
        HttpPut {
            @Override
            public HttpRequestBase createRequest(String url) { return new HttpPut(url); }
        },
        // HttpDelete 请求
        HttpDelete {
            @Override
            public HttpRequestBase createRequest(String url) { return new HttpDelete(url); }
        };

        public HttpRequestBase createRequest(String url) { return null; }
    }
}
