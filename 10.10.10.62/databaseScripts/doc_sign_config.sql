/*
SQLyog Ultimate v9.01 
MySQL - 5.7.11 : Database - emp_pledge_app
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Data for the table `doc_sign_config` */

insert  into `doc_sign_config`(`SL_NO`,`DOC_CODE`,`DSC_TOKEN_ID`,`ENABLE_STATUS`,`SIGN_INFO`,`SIGN_STATUS`,`SIGN_DISPLAY_INFO`,`SIGN_ORDER`) values (10,'BCAAANDB','',1,'{\"x\":\"350\",\"y\":\"86\",\"width\":\"150\",\"height\":\"70\",\"signPage\":\"P\",\"pages\":\"[7]\"}','1','[{\"displayMsg\":\"Digitally signed by $$name$$ \\n\"},{\"displayMsg\":\"Agent ID/MID: $$empid$$\\n\"},{\"displayMsg\":\"Bank: $$bank$$\\n\"},{\"displayMsg\":\"Date: $$date$$ IST\"}]','1'),(11,'BCAAANDB','',1,'{\"x\":\"350\",\"y\":\"205\",\"width\":\"190\",\"height\":\"70\",\"signPage\":\"P\",\"pages\":\"[7]\"}','1','','2'),(12,'BCAAANDB','',1,'{\"x\":\"100\",\"y\":\"86\",\"width\":\"190\",\"height\":\"70\",\"signPage\":\"P\",\"pages\":\"[7]\"}	','1','[{\"displayMsg\":\"Digitally signed by i25RMCS \\n\"},{\"displayMsg\":\"Date: $$date$$ IST\"}]','3'),(13,'BCAAANDB','INTBCAA',1,'{\"x\":\"100\",\"y\":\"86\",\"width\":\"190\",\"height\":\"70\",\"signPage\":\"P\",\"pages\":\"[7]\"}	','1','[{\"displayMsg\":\"Digitally signed by i25RMCS \\n\"},{\"displayMsg\":\"Date : $$date$$ IST\\n\"},{\"displayMsg\":\"Location : INDIA\"}]','3');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
