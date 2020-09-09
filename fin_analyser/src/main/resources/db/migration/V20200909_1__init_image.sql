/*
Navicat PGSQL Data Transfer

Source Server         : 194
Source Server Version : 101200
Source Host           : 192.168.20.194:5432
Source Database       : image
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 101200
File Encoding         : 65001

Date: 2020-06-05 14:54:23
*/


-- ----------------------------
-- Table structure for device_model
-- ----------------------------
DROP TABLE IF EXISTS `gp_zcfz`;
CREATE TABLE  IF NOT EXISTS `gp_zcfz` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `SUMLASSET`  double,
    `MONETARYFUND`  double,
    `ACCOUNTBILLREC`  double,
    `BILLREC`  double,
    `ACCOUNTREC`  double,
    `ADVANCEPAY`  double,
    `TOTAL_OTHER_RECE`  double,
    `DIVIDENDREC`  double,
    `OTHERREC`  double,
    `INVENTORY`  double,
    `OTHERLASSET`  double,
    `SUMNONLASSET`  double,
    `LTREC`  double,
    `FIXEDASSET`  double,
    `INTANGIBLEASSET`  double,
    `LTDEFERASSET`  double,
    `DEFERINCOMETAXASSET`  double,
    `GOODWILL`  double,
    `SUMASSET`  double,
    `SUMLLIAB`  double,
    `STBORROW`  double,
    `ACCOUNTPAY`  double,
    `ACCOUNTBILLPAY`  double,
    `SALARYPAY`  double,
    `TAXPAY`  double,
    `TOTAL_OTHER_PAYABLE`  double,
    `INTERESTPAY`  double,
    `DIVIDENDPAY`  double,
    `OTHERPAY`  double,
    `NONLLIABONEYEAR`  double,
    `SUMNONLLIAB`  double,
    `LTACCOUNTPAY`  double,
    `SUMLIAB`  double,
    `SUMSHEQUITY`  double,
    `SHARECAPITAL`  double,
    `CAPITALRESERVE`  double,
    `SURPLUSRESERVE`  double,
    `RETAINEDEARNING`  double,
    `SUMPARENTEQUITY`  double,
    `SUMLIABSHEQUITY`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `gp_lrb`;
CREATE TABLE  IF NOT EXISTS `gp_lrb` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `TOTALOPERATEREVE`  double,
    `OPERATEREVE`  double,
    `TOTALOPERATEEXP`  double,
    `OPERATEEXP`  double,
    `RDEXP`  double,
    `OPERATETAX`  double,
    `SALEEXP`  double,
    `MANAGEEXP`  double,
    `FINANCEEXP`  double,
    `OPERATEPROFIT`  double,
    `NONOPERATEREVE`  double,
    `NONOPERATEEXP`  double,
    `SUMPROFIT`  double,
    `INCOMETAX`  double,
    `NETPROFIT`  double,
    `PARENTNETPROFIT`  double,
    `KCFJCXSYJLR`  double,
    `BASICEPS`  double,
    `DILUTEDEPS`  double,
    `SUMCINCOME`  double,
    `PARENTCINCOME`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `gp_xjll`;
CREATE TABLE  IF NOT EXISTS `gp_xjll` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `SALEGOODSSERVICEREC`  double,
    `OTHEROPERATEREC`  double,
    `SUMOPERATEFLOWIN`  double,
    `BUYGOODSSERVICEPAY`  double,
    `EMPLOYEEPAY`  double,
    `TAXPAY`  double,
    `OTHEROPERATEPAY`  double,
    `SUMOPERATEFLOWOUT`  double,
    `NETOPERATECASHFLOW`  double,
    `DISPFILASSETREC`  double,
    `SUMINVFLOWIN`  double,
    `BUYFILASSETPAY`  double,
    `INVPAY`  double,
    `SUMINVFLOWOUT`  double,
    `NETINVCASHFLOW`  double,
    `ACCEPTINVREC`  double,
    `LOANREC`  double,
    `SUMFINAFLOWIN`  double,
    `REPAYDEBTPAY`  double,
    `DIVIPROFITORINTPAY`  double,
    `OTHERFINAPAY`  double,
    `SUMFINAFLOWOUT`  double,
    `NETFINACASHFLOW`  double,
    `NICASHEQUI`  double,
    `CASHEQUIBEGINNING`  double,
    `CASHEQUIENDING`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;


DROP TABLE IF EXISTS `gp_zyzb`;
CREATE TABLE  IF NOT EXISTS `gp_zyzb` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `jbmgsy`  double,
    `kfmgsy`  double,
    `xsmgsy`  double,
    `mgjzc`  double,
    `mggjj`  double,
    `mgwfply`  double,
    `mgjyxjl`  double,
    `yyzsr`  double,
    `mlr`  double,
    `gsjlr`  double,
    `kfjlr`  double,
    `yyzsrtbzz`  double,
    `gsjlrtbzz`  double,
    `kfjlrtbzz`  double,
    `yyzsrgdhbzz`  double,
    `gsjlrgdhbzz`  double,
    `kfjlrgdhbzz`  double,
    `jqjzcsyl`  double,
    `tbjzcsyl`  double,
    `tbzzcsyl`  double,
    `mll`  double,
    `jll`  double,
    `sjsl`  double,
    `yskyysr`  double,
    `xsxjlyysr`  double,
    `jyxjlyysr`  double,
    `zzczzy`  double,
    `yszkzzts`  double,
    `chzzts`  double,
    `zcfzl`  double,
    `ldzczfz`  double,
    `ldbl`  double,
    `sdbl`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;


DROP TABLE IF EXISTS `gp_bfb`;
CREATE TABLE  IF NOT EXISTS `gp_bfb` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `yysr`  double,
    `yycb`  double,
    `yysjjfj`  double,
    `qjfy`  double,
    `xsfy`  double,
    `glfy`  double,
    `cwfy`  double,
    `zcjzss`  double,
    `qtjysy`  double,
    `gyjzbdsy`  double,
    `tzsy`  double,
    `yylr`  double,
    `yywsr`  double,
    `btsr`  double,
    `yywzc`  double,
    `lrze`  double,
    `sds`  double,
    `jlr`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;