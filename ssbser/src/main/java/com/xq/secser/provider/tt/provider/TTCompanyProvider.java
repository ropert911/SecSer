package com.xq.secser.provider.tt.provider;

import com.xq.secser.constant.ComTypEnum;
import com.xq.secser.ssbser.pojo.po.CompPo;
import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.ssbser.utils.FileWirter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class TTCompanyProvider implements CompanyProvider {
    private static Logger logger = LoggerFactory.getLogger(TTCompanyProvider.class);

    //todo:有没有法根据平台设置不同的值，而不是不同的bean
    @Value("${com.xq.secser.download.path}")
    private String fileApath;
    private static RestTemplate restTemplate = new RestTemplate();

    private void getAllCom(ComTypEnum ft) {
        String pattern = "http://fund.eastmoney.com/Company/home/gspmlist?fundType=%d";
        String url = String.format(pattern, ft.getCode());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = data.replace("&nbsp;", "");

        FileWirter.writeFile(getFilePath(ft), data);
    }

    @Override
    public void initCompany() {
        for (ComTypEnum cte : ComTypEnum.values()) {
            getAllCom(cte);
        }
    }

    @Override
    public List<CompPo> parseCompany() {
        List<CompPo> compPosList = new ArrayList<>();

        for (ComTypEnum cte : ComTypEnum.values()) {
            List<CompPo> compList = parseCompFile(cte);
            compPosList.addAll(compList);
        }

        return compPosList;
    }

    public String getFilePath(ComTypEnum ft) {
        String fileFullName = fileApath + File.separator + ft.getAbb() + ".xml";
        logger.debug("file path={}", fileFullName);
        return fileFullName;
    }

    public List<CompPo> parseCompFile(ComTypEnum ft) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<CompPo> compPoList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(getFilePath(ft)));
            Element root = document.getDocumentElement();
            // 获取孩子元素
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    if ("tbody".equals(node.getNodeName())) {
                        NodeList theadChildList = node.getChildNodes();
                        for (int j = 0; j < theadChildList.getLength(); ++j) {
                            Node thSubNode = theadChildList.item(j);
                            if ("tr".equals(thSubNode.getNodeName())) {
                                CompPo comp = CompPo.builder().build();
                                NodeList itemNameList = thSubNode.getChildNodes();
                                for (int k = 0; k < itemNameList.getLength(); ++k) {
                                    Node itemNode = itemNameList.item(k);
                                    if ("td".equals(itemNode.getNodeName())) {
                                        String content = itemNode.getTextContent().replaceAll("\\s*", "");
                                        switch (k) {
                                            //ordernum
                                            case 1:
                                                comp.setOrdernum(Long.valueOf(content));
                                                break;
                                            //name
                                            case 3:
                                                String ocontent = itemNode.getTextContent();
                                                NodeList l = itemNode.getChildNodes();
                                                for (int f = 0; f < l.getLength(); ++f) {
                                                    Node itemNode2 = l.item(f);
                                                    if ("a".equals(itemNode2.getNodeName())) {
                                                        NamedNodeMap amap = itemNode2.getAttributes();
                                                        Node chref = amap.getNamedItem("href");
                                                        String href = chref.getTextContent();
                                                        String comanycode = href.replaceAll("[^\\d]+", "");
                                                        comp.setComcode(comanycode);
                                                    }
                                                }
                                                comp.setName(content);
                                                break;
                                            //es time
                                            case 7:
                                                try {
                                                    java.util.Date d = format.parse(content);
                                                    comp.setEstime(new java.sql.Date(d.getTime()));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                break;
                                            //scale
                                            case 11:
                                                if (!"-".equals(content)) {
                                                    comp.setScale(Double.valueOf(content.substring(0, content.length() - 5).replace(",", "")));
                                                }
                                                break;
                                            //found number
                                            case 13:
                                                comp.setFnnum(Long.valueOf(content));
                                                break;
                                            //manger number
                                            case 15:
                                                comp.setManagernum(Long.valueOf(content));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                comp.setFt(ft.getAbb());
                                compPoList.add(comp);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return compPoList;
    }
}
