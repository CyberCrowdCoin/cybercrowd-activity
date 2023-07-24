package org.cybercrowd.activity.configuration;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class RequestMessageConverter implements HttpMessageConverter {

    @Override
    public boolean canRead(Class clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean canWrite(Class clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
//        ArrayList<MediaType> list = Lists.newArrayList();
//        list.add(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE));
//        list.add(MediaType.parseMediaType(MediaType.MULTIPART_FORM_DATA_VALUE));
        /*这样就可以了*/
        return Lists.newArrayList();
    }

    @Override
    public Object read(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream body = inputMessage.getBody();
        String json = StreamUtils.copyToString(body, Charset.forName("UTF-8"));
        return JSONUtil.toBean(json, clazz);
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    }
}