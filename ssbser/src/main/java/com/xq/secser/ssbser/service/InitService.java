package com.xq.secser.ssbser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.ssbser.pojo.po.CompPo;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.po.IComp;
import com.xq.secser.ssbser.pojo.po.IFund;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class InitService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    FundProvider fundProvider;
    @Autowired
    CompanyProvider companyProvider;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Value("${com.xq.secser.download.fund}")
    private boolean bDowloadFound;

    @Value("${com.xq.secser.download.company}")
    private boolean bDowloadCompany;


    @Override
    public void run(ApplicationArguments args) {
        //TODO:添加时间统计
        //下载found数据
        if (bDowloadFound) {
            fundProvider.initFoundData();
        }

        //下载com数据
        if (bDowloadCompany) {
            companyProvider.initCompany();
        }

        //把foud原始数据解析到数据结构表里
//        parseFund();

        //解析公司数据
//        parseCompany();

        search();
    }


    private void parseFund() {
        List<FoundPo> foundPoList = fundProvider.parseFund();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }

    private void parseCompany() {
        List<CompPo> compPoList = companyProvider.parseCompany();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IComp iComp = session.getMapper(IComp.class);
            iComp.insertCompBatch(compPoList);
        } finally {
        }
    }

    private void search() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            IComp iComp = session.getMapper(IComp.class);

            Map<String, CompPo> comMap = new HashMap<>();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d2014 = format.parse("2014-06-06");
            List<CompPo> compPoList = iComp.getTopN("gp", 30);
            compPoList.stream().filter(item -> item.getEstime().compareTo(d2014) < 0).forEach(item -> comMap.put(item.getComcode(), item));
            compPoList = iComp.getTopN("hh", 30);
            compPoList.stream().filter(item -> item.getEstime().compareTo(d2014) < 0).forEach(item -> comMap.put(item.getComcode(), item));


            List<FoundPo> foundPoResult = new ArrayList<>();
            List<FoundPo> foundPoList = iFund.getByLevel("gp", 3.5);
            foundPoList.forEach(item -> {
                if (null != comMap.get(item.getComcode())) {
                    foundPoResult.add(item);
                }
            });
            foundPoList = iFund.getByLevel("hh", 3.5);
            foundPoList.forEach(item -> {
                if (null != comMap.get(item.getComcode())) {
                    foundPoResult.add(item);
                }
            });

            //过滤
            List<FoundPo> filtrResult = foundPoResult.stream().filter(s -> s.getL1y() >= 30).filter(s -> s.getL3y() >= 50).collect(Collectors.toList());
            //去重
            List<FoundPo> unique = filtrResult.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
            );

            //排序
            unique.sort(Comparator.comparing(FoundPo::getL3y));
            Collections.reverse(unique);

            logger.info("size={}", unique.size());
            for (FoundPo item : unique) {
                logger.info("code={} name={}, levle={}, ly={}% l3y={}% ccode={},cname={}",
                        item.getCode(), item.getName(), item.getLevel(),
                        item.getL1y(), item.getL3y(),
                        item.getComcode(), comMap.get(item.getComcode()).getName());
            }
            /*
            select * from fund  left outer join comp on fund.comcode = comp.comcode
            where
                fund.ft in("gp","hh") 			#类型为股票和混合
                and comp.ft in("gp","hh")
                and fund.level>=3.5            #机构为4星级以上
                and fund.l1y>=30             #近一年30%
                and fund.l3y>=50             #近3年50%
                and comp.estime<='2014-06-06'  #2014年前的基金公司，穿越牛熊周期
                and comp.ordernum<= 30      #基金公司规模在前20名
            group by code  order by l3y desc
             */

            getHis();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    private void getHis() {
        //阶段 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jdndzf&code=162006&rt=0.5906414726539972
        //季度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=quarterzf&code=162006&rt=0.13838623850422516
        //年度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=yearzf&code=162006&rt=0.35966013707740463
        String data = "var qapidata={ content:\"<table class='w782 comm jndxq'><thead><tr><th class='first'></th><th>19年4季度</th><th>19年3季度</th><th>19年2季度</th><th>19年1季度</th><th>18年4季度</th><th>18年3季度</th><th>18年2季度</th><th>18年1季度</th></tr></thead><tbody><tr><td class='tdgray'>阶段涨幅</td><td class='tor red bold'>12.63%</td><td class='tor red bold'>7.03%</td><td class='tor grn bold'>-6.58%</td><td class='tor red bold'>18.77%</td><td class='tor grn bold'>-10.18%</td><td class='tor grn bold'>-7.58%</td><td class='tor grn bold'>-3.04%</td><td class='tor grn bold'>-3.71%</td></tr><tr><td  class='tdgray'>同类平均</td><td class='tor red bold'>6.05%</td><td class='tor red bold'>7.06%</td><td class='tor grn bold'>-1.03%</td><td class='tor red bold'>17.48%</td><td class='tor grn bold'>-6.55%</td><td class='tor grn bold'>-3.51%</td><td class='tor grn bold'>-4.01%</td><td class='tor grn bold'>-0.57%</td></tr><tr><td  class='tdgray'>沪深300</td><td class='tor red bold'>5.44%</td><td class='tor grn bold'>-0.29%</td><td class='tor grn bold'>-1.21%</td><td class='tor red bold'>28.62%</td><td class='tor grn bold'>-12.45%</td><td class='tor grn bold'>-2.05%</td><td class='tor grn bold'>-9.94%</td><td class='tor grn bold'>-3.28%</td></tr><tr><td  class='tdgray'>同类排名</td><td>432<span class='gray'>|</span>3407</td><td>937<span class='gray'>|</span>3279</td><td>2749<span class='gray'>|</span>3201</td><td>1477<span class='gray'>|</span>3053</td><td>1991<span class='gray'>|</span>2975</td><td>2271<span class='gray'>|</span>2970</td><td>1413<span class='gray'>|</span>2983</td><td>2233<span class='gray'>|</span>2933</td></tr><tr><td  class='tdgray'>四分位排名<img onmousemove='document.getElementById(&quot;jzdfDiv&quot;).style.display=&quot;&quot;;' src='http://j5.dfcfw.com/j1/images/faq.gif' ><div id='jzdfDiv' class='gzpmTips' style='display: none;'><span>四分位排名是将同类基金按涨幅大小顺序排列，然后分为四等分，每个部分大约包含四分之一即25%的基金，基金按相对排名的位置高低分为：优秀、良好、一般、不佳。<a href='javascript:void(0)' target='_self' title='关闭' class='promptbox' onclick='document.getElementById(&quot;jzdfDiv&quot;).style.display=&quot;none&quot;;'>[×关闭]</a></span></div></td><td><table class='tbsi'><tr><td class='tdblack'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr></table><p class='sifen'>优秀</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr></table><p class='sifen'>良好</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr></table><p class='sifen'>不佳</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr></table><p class='sifen'>良好</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr><tr><td class='tdno'></td></tr></table><p class='sifen'>一般</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr></table><p class='sifen'>不佳</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr></table><p class='sifen'>良好</p></td><td><table class='tbsi'><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdno'></td></tr><tr><td class='tdblack'></td></tr></table><p class='sifen'>不佳</p></td></tr></tbody></table>\"};";
        data = data.replaceAll("var qapidata=", "");

//        logger.info("{}",data);
        data = data.replaceAll(";$", "");
//        logger.info("{}",data);
        data = data.replaceAll("<img.+gif' >", "");
//        logger.info("{}",data);

        JSONObject configJson = JSON.parseObject(data);
        String content = configJson.getString("content");
//        logger.info("content={}", content);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
            Document document = builder.parse(is);
            NodeList nodelist = document.getElementsByTagName("table");
            Node table = nodelist.item(0);
            //季度
            Node n1 = table.getFirstChild().getFirstChild().getFirstChild();
            Node n2 = n1.getNextSibling();
            Node n3 = n2.getNextSibling();
            Node n4 = n3.getNextSibling();
            Node n5 = n4.getNextSibling();
            Node n6 = n5.getNextSibling();
            Node n7 = n6.getNextSibling();
            Node n8 = n7.getNextSibling();
            Node n9 = n8.getNextSibling();
            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}",n1.getTextContent()
                    ,n2.getTextContent()
                    ,n3.getTextContent()
                    ,n4.getTextContent()
                    ,n5.getTextContent()
                    ,n6.getTextContent()
                    ,n7.getTextContent()
                    ,n8.getTextContent()
                    ,n9.getTextContent()
            );
            Node tbodyNode = table.getLastChild();
            //阶段涨幅
            Node tr1Node= tbodyNode.getFirstChild();
            n1 = tr1Node.getFirstChild();
            n2 = n1.getNextSibling();
            n3 = n2.getNextSibling();
            n4 = n3.getNextSibling();
            n5 = n4.getNextSibling();
            n6 = n5.getNextSibling();
            n7 = n6.getNextSibling();
            n8 = n7.getNextSibling();
            n9 = n8.getNextSibling();
            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}",n1.getTextContent()
                    ,n2.getTextContent()
                    ,n3.getTextContent()
                    ,n4.getTextContent()
                    ,n5.getTextContent()
                    ,n6.getTextContent()
                    ,n7.getTextContent()
                    ,n8.getTextContent()
                    ,n9.getTextContent()
            );
            //沪深300
            Node tr3Node= tr1Node.getNextSibling().getNextSibling();
            n1 = tr3Node.getFirstChild();
            n2 = n1.getNextSibling();
            n3 = n2.getNextSibling();
            n4 = n3.getNextSibling();
            n5 = n4.getNextSibling();
            n6 = n5.getNextSibling();
            n7 = n6.getNextSibling();
            n8 = n7.getNextSibling();
            n9 = n8.getNextSibling();
            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}",n1.getTextContent()
                    ,n2.getTextContent()
                    ,n3.getTextContent()
                    ,n4.getTextContent()
                    ,n5.getTextContent()
                    ,n6.getTextContent()
                    ,n7.getTextContent()
                    ,n8.getTextContent()
                    ,n9.getTextContent()
            );
            //同类排名
            Node tr4Node= tr3Node.getNextSibling();
            n1 = tr4Node.getFirstChild();
            n2 = n1.getNextSibling();
            n3 = n2.getNextSibling();
            n4 = n3.getNextSibling();
            n5 = n4.getNextSibling();
            n6 = n5.getNextSibling();
            n7 = n6.getNextSibling();
            n8 = n7.getNextSibling();
            n9 = n8.getNextSibling();
            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}",n1.getTextContent()
                    ,n2.getTextContent()
                    ,n3.getTextContent()
                    ,n4.getTextContent()
                    ,n5.getTextContent()
                    ,n6.getTextContent()
                    ,n7.getTextContent()
                    ,n8.getTextContent()
                    ,n9.getTextContent()
            );
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
