package com.xq.secser.ssbser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class InitService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    FundService fundService;
    @Autowired
    CompanyService companyService;
    @Autowired
    FundHisService fundHisService;
    @Autowired
    Strategy strategy;


    @Value("${com.xq.secser.download.fund}")
    private boolean bDowloadFound;

    @Value("${com.xq.secser.download.company}")
    private boolean bDowloadCompany;


    @Override
    public void run(ApplicationArguments args) {
        //TODO:添加时间统计
        /**下载found数据*/
        if (bDowloadFound) {
            fundService.initFoundData();
            /**把foud原始数据解析到数据结构表里*/
            fundService.parseFund();
        }

        /**下载com数据*/
        if (bDowloadCompany) {
            companyService.initCompany();
            /**解析公司数据*/
            companyService.parseCompany();
        }

        strategy.searchGpAHh();
        strategy.searchZq();
    }
}
