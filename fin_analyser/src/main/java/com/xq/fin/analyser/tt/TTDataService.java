package com.xq.fin.analyser.tt;

import com.xq.fin.analyser.data.BfbUtil;
import com.xq.fin.analyser.model.SingleData;
import com.xq.fin.analyser.model.po.*;
import com.xq.fin.analyser.tt.vo.*;
import com.xq.fin.analyser.tt.vo.ZcfzVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/9/9
 */
@Service
public class TTDataService {
    //资产负债
    @Autowired
    private ZcfzbUtil zcfzbUtil;
    //利润表
    @Autowired
    private LrbUtil lrbUtil;
    //现金流量
    @Autowired
    private XjllUtil xjllUtil;
    //主要指标
    @Autowired
    private ZyzbUtil zyzbUtil;
    //百分比
    @Autowired
    private BfbUtil bfbUtil;


    public SingleData getData(String code) {
        SingleData singleData = new SingleData();
        singleData.getBaseInfoPo().setCode(code);

        //资产负债
        List<ZcfzVo> zcfzVoList = zcfzbUtil.getData(code);
        for (ZcfzVo zcfzVo : zcfzVoList) {
            com.xq.fin.analyser.model.po.ZcfzPo zcfzPo = new com.xq.fin.analyser.model.po.ZcfzPo();
            BeanUtils.copyProperties(zcfzVo, zcfzPo);
            singleData.getZcfzPoList().add(zcfzPo);
        }

        //利润表
        List<LrbVo> lrbVoList = lrbUtil.getData(code);
        for (LrbVo lrbVo : lrbVoList) {
            LrbPo lrbPo = new LrbPo();
            BeanUtils.copyProperties(lrbVo, lrbPo);
            singleData.getLrbPoList().add(lrbPo);

            singleData.getBaseInfoPo().setName(lrbVo.getSECURITYSHORTNAME());
        }
        //现金流量
        List<XjllVo> xjllVoList = xjllUtil.getData(code);
        for (XjllVo xjllVo : xjllVoList) {
            XjllPo xjllPo = new XjllPo();
            BeanUtils.copyProperties(xjllVo, xjllPo);
            singleData.getXjllPoList().add(xjllPo);
        }
        //主要指标
        List<ZyzbVo> zyzbVoList = zyzbUtil.getData(code);
        for (ZyzbVo zyzbVo : zyzbVoList) {
            ZyzbPo zyzbPo = new ZyzbPo();
            BeanUtils.copyProperties(zyzbVo, zyzbPo);
            singleData.getZyzbPoList().add(zyzbPo);
        }
        //百分比
        List<BfbVo> bfbVoList = bfbUtil.getData(code);
        for (BfbVo bfbVo : bfbVoList) {
            BfbPo bfbPo = new BfbPo();
            BeanUtils.copyProperties(bfbVo, bfbPo);
            singleData.getBfbPoList().add(bfbPo);
        }

        return singleData;
    }
}
