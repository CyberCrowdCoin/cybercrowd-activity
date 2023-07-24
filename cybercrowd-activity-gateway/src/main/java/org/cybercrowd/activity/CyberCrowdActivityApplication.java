package org.cybercrowd.activity;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@MapperScan("org.cybercrowd.activity.mapper")
public class CyberCrowdActivityApplication{

    private static Logger logger = LoggerFactory.getLogger(CyberCrowdActivityApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CyberCrowdActivityApplication.class);
        logger.info("........CyberCrowdActivityApplication Run........");
    }


    @Value("${web3j.ethereum.rpc-url}")
    private String ethereumRpcUrl;

    @Bean
    public Web3j initWeb3jClient(){
        return Web3j.build(new HttpService(ethereumRpcUrl));
    }
}
