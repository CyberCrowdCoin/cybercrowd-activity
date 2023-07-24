package org.cybercrowd.activity.dto;

import java.io.Serializable;

public class TwitterOauthTokenResDto implements Serializable {


    /**
     * token_type : bearer
     * expires_in : 7200
     * access_token : eGJsNDMyOW9veWNOZFdZWVkxT3JkRjQtYlhqNVY0dlBDelBwN3Rpa1M3WEs1OjE2NjE4NzE1MTIyNDQ6MTowOmF0OjE
     * scope : users.read follows.read tweet.read offline.access
     * refresh_token : aW5BakZ4ZzJ2R0lMRi1SNmQ3MUNYMTlndG9rbV9JVG9SejladWhEY3h0XzRyOjE2NjE4NzE1MTIyNDQ6MToxOnJ0OjE
     */

    private String token_type;
    private int expires_in;
    private String access_token;
    private String scope;
    private String refresh_token;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
