package org.cybercrowd.activity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscordAccountDto implements Serializable {

    private String id;
    private String username;
    private String avatar;
    private Object avatar_decoration;
    private String discriminator;
    private int public_flags;
    private int flags;
    private Object banner;
    private Object banner_color;
    private Object accent_color;
    private String locale;
    private boolean mfa_enabled;
    private int premium_type;
    private String email;
    private boolean verified;
}
