package org.cybercrowd.activity.service;

import org.cybercrowd.activity.request.ActivityNewYear2022DiscrodLoginReq;
import org.cybercrowd.activity.request.ActivityNewYear2022Req;
import org.cybercrowd.activity.request.ActivityNewYear2022TwitterLoginReq;
import org.cybercrowd.activity.response.ActivityNewYear2022Res;

public interface ActivityService {

    ActivityNewYear2022Res activity2022NewYearSumbit(ActivityNewYear2022Req activityNewYear2022Req);

    ActivityNewYear2022Res activity2022NewYerDiscordLogin(ActivityNewYear2022DiscrodLoginReq activityNewYear2022DiscrodLoginReq);

    ActivityNewYear2022Res activity2022NewYerTwitterLogin(ActivityNewYear2022TwitterLoginReq activityNewYear2022TwitterLoginReq);

    ActivityNewYear2022Res activity2022NewYearPull(String ownerId);

    void updateActivity2022NewYearDiscordJobStatus(String discordId);

    void updateActivity2022NewYearTwitterJobStatus(String twitterId);

    void userJobDataExcel();

    void updateUserJobStatus(int start);


}
