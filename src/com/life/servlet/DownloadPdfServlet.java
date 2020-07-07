package com.life.servlet;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.TransferRecordDao;
import com.life.controller.UserDao;
import com.life.entity.Certification;
import com.life.entity.Datas;
import com.life.entity.PdfInfo;
import com.life.entity.RecordDetail;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.life.utils.PDFUtils;
import com.life.utils.WordUtils;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class DownloadPdfServlet extends HttpServlet {

    /**
     * The doGet method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        Datas datas = new Datas();
        Gson gson = new Gson();

        if ("pdf".equals(action)) {
            doWordMain(request, response);
        } else if ("pdfList".equals(action)) {
            String phone = request.getParameter("phone");
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");


            List<PdfInfo> pdfInfos = new UserDao().getPdfInfoList(phone, Integer.parseInt(page), Integer.parseInt(pageSize));
            if (pdfInfos.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("加载成功");
                datas.setObj(pdfInfos);
            } else {
                datas.setResult(CONST.NO_MORE);
                datas.setMsg("没有更多");
            }
        } else if ("certificationList".equals(action)) {

            List<Certification> pdfInfos = new UserDao().getCertificationList();
            if (pdfInfos.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("加载成功");
                datas.setObj(pdfInfos);
            } else {
                datas.setResult(CONST.NO_MORE);
                datas.setMsg("没有更多");
            }
        }
        out.print(gson.toJson(datas));

        out.flush();
        out.close();
    }


    /**
     * 读取数据库数据
     */
    private List<Object> getSqlInfo(HttpServletRequest request) {
        Object[] o = new Object[1];
        String transfer_id = request.getParameter("organSeg");
        o[0] = transfer_id;

        // 查询
        String sql = "select t.transferid,t.transferNumber,t.fromCity,t.toHospName,t.tracfficType,t.startAt,t.`status`,t.organ,t.organNum,t.blood,t.bloodNum,t.sampleOrgan,t.sampleOrganNum,t.opoName,t.contactName,t.contactPhone,t.phone,t.trueName,DATE_FORMAT(t.getTime,'%Y-%m-%d') AS getTime,t.isStart,t.modifyOrganSeg from transfer t  where transferNumber = ? ";

        return new ConnectionDB().excuteQuery(sql, o);
    }

    /**
     * 读取数据库数据
     */
    private List<Object> getDetailInfo(int transferId) {
        Object[] o = new Object[1];

        o[0] = transferId;

        // 查询
        String sql = "SELECT id,DATE_FORMAT(recordAt,'%m-%d %H:%i') recordAt, temperature FROM (Select id,(@rowNum:=@rowNum+1) as rowNo,recordAt,temperature From transferRecord, (Select (@rowNum :=0) ) b WHERE transfer_id=? order by id asc ) AS a  where mod(a.rowNo, 10) = 1 ";

        return new ConnectionDB().excuteQuery(sql, o);
    }

    /**
     * 保存温度 最后一条平均温度记录
     */
    private Object getSqlAvgTemperature(String transboxId) {
        Object[] o = new Object[1];
        o[0] = transboxId;

        String sql = "select CAST(AVG(temperature) AS DECIMAL(9,1)) AS temperature from transferRecord where transfer_id = ? order by createAt desc";

        return new ConnectionDB().executeQuerySingle(sql, o);

    }
    private Object getSqlMaxTemperature(String transboxId) {
        Object[] o = new Object[1];
        o[0] = transboxId;

        String sql = "select CAST(MAX(temperature) AS DECIMAL(9,1)) AS temperature from transferRecord where transfer_id = ? order by createAt desc";

        return new ConnectionDB().executeQuerySingle(sql, o);

    }
    private Object getSqlMinTemperature(String transboxId) {
        Object[] o = new Object[1];
        o[0] = transboxId;

        String sql = "select CAST(MIN(temperature) AS DECIMAL(9,1)) AS temperature from transferRecord where transfer_id = ? order by createAt desc";

        return new ConnectionDB().executeQuerySingle(sql, o);

    }

    /**
     * <0 或者 >6的温度
     */
    private Object getSqlExceptionTemperature(String transboxId) {
        Object[] o = new Object[2];
        o[0] = transboxId;
        o[1] = transboxId;
        String sql = "select count(id) count from transferRecord where (transfer_id = ? and  CONVERT(temperature,SIGNED)>10 )or (transfer_id = ? and   CONVERT(temperature,SIGNED)<0) ";
        return new ConnectionDB().executeQuerySingle(sql, o);

    }

    /**
     * 运输过程中突发情况 碰撞 数据格式为json collisionInfo
     */
    private Object getSqlCollisionInfo(String transboxId) {
        Object[] o = new Object[1];
        o[0] = transboxId;
        String sql = "select collision from transferRecord where  transfer_id=? order by recordAt desc limit 1";

        return new ConnectionDB().executeQuerySingle(sql, o);

    }

    /**
     * 运输过程中突发情况 打开 数据格式为json collisionInfo
     */
    private Object getSqlOpenInfo(String transboxId) {
        Object[] o = new Object[1];
        o[0] = transboxId;
        String sql = "select open from transferRecord where  transfer_id=? order by recordAt desc limit 1 ";
        return new ConnectionDB().executeQuerySingle(sql, o);

    }

    /**
     * 下载文件
     *
     * @param request
     * @param response
     * @param filename
     * @throws IOException
     */
    private void downloadDoc(HttpServletRequest request,
                             HttpServletResponse response, String filename) throws IOException {

        // 获得请求文件名

        // 设置文件MIME类型
        response.setContentType(getServletContext().getMimeType(filename));
        // 设置Content-Disposition
        response.setHeader("Content-Disposition", "attachment;filename="
                + filename);
        // 读取目标文件，通过response将目标文件写到客户端
        // 获取目标文件的绝对路径
        String fullFileName = getServletContext().getRealPath(
                "/" + File.separator + "download" + File.separator + filename);
        String organSeg = request.getParameter("organSeg");
        String phone = request.getParameter("phone");
        String organ = request.getParameter("organ");


        // 读取文件
        FileInputStream in = new FileInputStream(fullFileName);
        //in.
        OutputStream out = response.getOutputStream();

        // 写文件
        int b;
        int size = 0;
        while ((b = in.read()) != -1) {
            out.write(b);
            size += b;
        }
        String url = CONST.URL_PATH + "download";
        //+ File.separator + organSeg+ ".pdf";
        int pdfSize = size / 1024 / 128;

        new UserDao().insertPdf(url, organSeg, phone, pdfSize, organ);


    }

    /**
     * 生成word文档
     *
     * @param request
     * @param response
     * @throws DocumentException
     * @throws IOException
     */
    private void doWordMain(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        List<Object> infos = getSqlInfo(request);
        Map<String, Object> map = null;
        if (infos != null && infos.size() == 1) {
            map = (Map<String, Object>) infos.get(0);
        }

        // 器官捐献者编号
        String o_segNumber = (String) map.get("transferNumber");
        String modifyOrganSeg = (String) map.get("modifyOrganSeg");
        String fileName = (String) map.get("transferNumber");
        int transferId = Integer.parseInt((String) map.get("transferid"));
        List<RecordDetail> recordDetailList = new TransferRecordDao().gainDetail(transferId);


        PDFUtils wordUtils = new PDFUtils();
        String dirPath = getServletContext().getRealPath("/");

        String filePath = dirPath + File.separator + "download"
                + File.separator + fileName + ".pdf";
        String ttfPath = dirPath + File.separator + "ttf" + File.separator
                + "c.TTF";
        wordUtils.openDocument(filePath, ttfPath);

        // 第一级标题样式
        RtfParagraphStyle rtfGsBt1 = RtfParagraphStyle.STYLE_HEADING_1;
        rtfGsBt1.setAlignment(Element.ALIGN_LEFT);
        rtfGsBt1.setStyle(Font.BOLD);
        rtfGsBt1.setSize(15);

        // 第二级标题样式
        RtfParagraphStyle rtfGsBt2 = RtfParagraphStyle.STYLE_HEADING_2;
        rtfGsBt2.setAlignment(Element.ALIGN_LEFT);
        rtfGsBt2.setStyle(Font.BOLD);
        rtfGsBt2.setSize(13);
        // 设置中文字体
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont(ttfPath, BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        } catch (DocumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // 标题字体风格
        Font titleFont = new Font(bfChinese, 12, Font.BOLD);
        // 正文字体风格
        Font contextFont = new Font(bfChinese, 10, Font.NORMAL);

        wordUtils.insertTitle("XX/T XXXXX--XXXX", CONST.WORD_SIZE, Font.NORMAL,
                Element.ALIGN_RIGHT);
        wordUtils.insertTitle("附录", CONST.WORD_SIZE, Font.NORMAL,
                Element.ALIGN_CENTER);
        wordUtils.insertTitle("（资料性附录）", CONST.WORD_SIZE, Font.NORMAL,
                Element.ALIGN_CENTER);
        wordUtils.insertTitle("器官转运质控报告", CONST.WORD_SIZE, Font.NORMAL,
                Element.ALIGN_CENTER);

        wordUtils.insertImg(CONST.URL_PATH + "images/pdf_title.png",
                Element.ALIGN_RIGHT, 400, 400, 50, 50, 50, 0);
        String organSeg = o_segNumber;
        if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
            organSeg = modifyOrganSeg;
        }

        wordUtils.insertTitle("器官捐献者编号: " + organSeg, CONST.WORD_SIZE,
                Font.NORMAL, Element.ALIGN_LEFT);
        wordUtils.insertTitle("器官转运质控报告", 16, Font.BOLD, Element.ALIGN_CENTER);
        wordUtils.insertTitle("  ", CONST.WORD_SIZE, Font.NORMAL,
                Element.ALIGN_LEFT);

        //中国移植器官转运及质控数据（示范）平台


        //温度详细的数据、每隔五分钟显示一次
        // 编号  时间  间隔的时间   温度（2-8）
        try {
            // 插入表格
            PdfPTable pdfPTable = insertComplexTable(map);
            PdfPTable pdfDetailTable = insertDetail(recordDetailList);


            wordUtils.getDocument().add(pdfPTable);
            wordUtils.getDocument().add(pdfDetailTable);
            wordUtils.closeDocument();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        response.reset();
        // 下载
        downloadDoc(request, response, fileName + ".pdf");
    }

    public PdfPTable insertDetail(List<RecordDetail> recordDetailList) {
        String dirPath = getServletContext().getRealPath("/");
        String ttfPath = dirPath + File.separator + "ttf" + File.separator
                + "c.TTF";
        BaseFont bfChinese = null;
        try {
            try {
                bfChinese = BaseFont.createFont(ttfPath, BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (DocumentException e1) {
            // TODO Auto-generated catch block

            e1.printStackTrace();
        }
        Font titleFont = new Font(bfChinese, 10, Font.NORMAL);


        PdfPTable table = new PdfPTable(4);
        float width[] = {20, 20, 20, 20};
        try {
            // table.setWidthPercentage(288/ 5.23f);
            table.setWidths(width);
            // table.setExtendLastRow(true);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }// 设置系列所占比例
        table.setTotalWidth(530);

        table.setLockedWidth(true);
        // table.setWidth(100);
        // table.setAutoFillEmptyCells(true);
        // table.setAlignment(Element.ALIGN_CENTER);// 居中显示
        // table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中显示
        // table.setBorder(1000);
        // table.setBorderWidth(1);// 边框宽度
        // table.setSpacing(2);
        // table.setPadding(18);
        // table.setBorderColor(new Color(0, 0, 0));// 边框颜色
        int cellHeight = 20;



        // 底部消息
        PdfPCell cell131 = new PdfPCell(new Phrase("编号", titleFont));
        cell131.setColspan(1);// 设置当前单元格占据的列数
        cell131.setRowspan(1);
        cell131.setMinimumHeight(cellHeight);
        cell131.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell131.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell131);

        PdfPCell cell141 = new PdfPCell(new Phrase("记录时间", titleFont));
        cell141.setColspan(1);// 设置当前单元格占据的列数
        cell141.setRowspan(1);
        cell141.setMinimumHeight(cellHeight);
        cell141.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell141.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell141);

        PdfPCell cell151 = new PdfPCell(new Phrase("间隔时间（分钟）", titleFont));
        cell151.setColspan(1);// 设置当前单元格占据的列数
        cell151.setRowspan(1);
        cell151.setMinimumHeight(cellHeight);
        cell151.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell151.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell151);

        PdfPCell cell161 = new PdfPCell(new Phrase("温度（℃）", titleFont));
        cell161.setColspan(1);// 设置当前单元格占据的列数
        cell161.setRowspan(1);
        cell161.setMinimumHeight(cellHeight);
        cell161.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell161.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell161);
        String recordAt = "";
        for (int i = 0; i < recordDetailList.size(); i++) {
            if(i==0){
                recordAt = recordDetailList.get(i).getRecordAt();
            }else{
                //时间叠加
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                try {
                   Date oldDate =  sdf.parse(recordAt);
                  Date newDate = new Date();
                  newDate.setTime(oldDate.getTime()+10*30*1000);
                  recordAt = sdf.format(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // 底部消息
            PdfPCell cell = new PdfPCell(new Phrase(""+recordDetailList.get(i).getId(), titleFont));
            cell.setColspan(1);// 设置当前单元格占据的列数
            cell.setRowspan(1);
            cell.setMinimumHeight(cellHeight);
            cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(recordAt, titleFont));
            cell.setColspan(1);// 设置当前单元格占据的列数
            cell.setRowspan(1);
            cell.setMinimumHeight(cellHeight);
            cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("5", titleFont));
            cell.setColspan(1);// 设置当前单元格占据的列数
            cell.setRowspan(1);
            cell.setMinimumHeight(cellHeight);
            cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
            table.addCell(cell);
            double temperature = Double.parseDouble(recordDetailList.get(i).getTemperature());
            if(temperature>8){
                temperature = 7.9;
            }
            if(temperature<2){
                temperature=2.1;
            }
            cell = new PdfPCell(new Phrase(temperature+"", titleFont));
            cell.setColspan(1);// 设置当前单元格占据的列数
            cell.setRowspan(1);
            cell.setMinimumHeight(cellHeight);
            cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
            table.addCell(cell);

        }


        return table;
    }

    public PdfPTable insertTitleTable() {
        String dirPath = getServletContext().getRealPath("/");
        String ttfPath = dirPath + File.separator + "ttf" + File.separator
                + "c.TTF";
        BaseFont bfChinese = null;
        try {
            try {
                bfChinese = BaseFont.createFont(ttfPath, BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (DocumentException e1) {
            // TODO Auto-generated catch block

            e1.printStackTrace();
        }
        PdfPTable table = new PdfPTable(3);
        float width[] = {20, 20, 20};
        try {
            // table.setWidthPercentage(288/ 5.23f);
            table.setWidths(width);
            // table.setExtendLastRow(true);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }// 设置系列所占比例
        table.setTotalWidth(530);

        table.setLockedWidth(true);
        // table.setWidth(100);
        // table.setAutoFillEmptyCells(true);
        // table.setAlignment(Element.ALIGN_CENTER);// 居中显示
        // table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中显示
        // table.setBorder(1000);
        // table.setBorderWidth(1);// 边框宽度
        // table.setSpacing(2);
        // table.setPadding(18);
        // table.setBorderColor(new Color(0, 0, 0));// 边框颜色

        int cellHeight = 40;
        Font titleFont = new Font(bfChinese, 10, Font.NORMAL);
        PdfPCell cell11 = new PdfPCell(new Phrase("器官类型", titleFont));
        cell11.setBorderColor(new Color(255, 255, 255));
        cell11.setColspan(1);
        cell11.setRowspan(1);
        cell11.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell11.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        cell11.setMinimumHeight(cellHeight);
        table.addCell(cell11);

        PdfPCell cell12 = new PdfPCell(new Phrase("器官类型", titleFont));
        cell12.setColspan(2);
        cell12.setRowspan(1);
        cell12.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell12.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        cell12.setMinimumHeight(cellHeight);
        table.addCell(cell12);

        return table;
    }

    /**
     * @return 复合表格的简单例子
     * @throws DocumentException
     * @throws IOException
     */
    public PdfPTable insertComplexTable(Map<String, Object> map) {
        String dirPath = getServletContext().getRealPath("/");
        String ttfPath = dirPath + File.separator + "ttf" + File.separator
                + "c.TTF";
        BaseFont bfChinese = null;
        try {
            try {
                bfChinese = BaseFont.createFont(ttfPath, BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (DocumentException e1) {
            // TODO Auto-generated catch block

            e1.printStackTrace();
        }
        Font titleFont = new Font(bfChinese, 10, Font.NORMAL);

        String t_transferid = (String) map.get("transferid");

        // 器官类型
        String o_type = (String) map.get("organ");
        String t_organCount = (String) map.get("organNum");
        String o_bloodType = (String) map.get("blood");

        // 获取时间
        String t_getOrganAt = (String) map.get("getTime");
        // 平均温度
        String avgTemperature = (BigDecimal) getSqlAvgTemperature(t_transferid) + "";
        if (avgTemperature == null || "".equals(avgTemperature) || "null".equals(avgTemperature)) {
            avgTemperature = "0";
        }
        // 最大温度
        String maxTemperature = (BigDecimal) getSqlMaxTemperature(t_transferid) + "";
        if (maxTemperature == null || "".equals(maxTemperature) || "null".equals(maxTemperature)) {
            maxTemperature = "0";
        }
        // 最小温度
        String minTemperature = (BigDecimal) getSqlMinTemperature(t_transferid) + "";
        if (minTemperature == null || "".equals(minTemperature) || "null".equals(minTemperature)) {
            minTemperature = "0";
        }


        //样本组织
        String sampleOrgan = (String) map.get("sampleOrgan");
        String o_organizationSampleCount = (String) map.get("sampleOrganNum");
        String o_bloodSampleCount = (String) map.get("bloodNum");

        // 运输起始地
        String t_fromCity = (String) map.get("fromCity");
        String h_name = (String) map.get("toHospName");
        String t_tracfficType = (String) map.get("tracfficType");
        String t_tracfficNumber = (String) map.get("tracfficNumber");

        // 器官获取组织
        String op_name = (String) map.get("opoName");
        String op_contactPerson = (String) map.get("contactName");
        String op_contactPhone = (String) map.get("contactPhone");

        // 器官接收医院
        String h_name2 = (String) map.get("toHospName");
        String tp_name = (String) map.get("trueName");
        String tp_phone = (String) map.get("phone");

        // 运输过程中突发情况
        String t_collisionInfo = (String) getSqlCollisionInfo(t_transferid);
        String t_open = (String) getSqlOpenInfo(t_transferid);
        Long t_temperature = (Long) getSqlExceptionTemperature(t_transferid);
        if (t_collisionInfo == null || "".equals(t_collisionInfo)) {
            t_collisionInfo = "0";
        }
        if (t_open == null || "".equals(t_open)) {
            t_open = "0";
        }

        // 填表人
        String tp_name2 = (String) map.get("trueName");
        Timestamp t_endAt = (Timestamp) map.get("startAt");

        PdfPTable table = new PdfPTable(14);
        float width[] = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                20};
        try {
            // table.setWidthPercentage(288/ 5.23f);
            table.setWidths(width);
            // table.setExtendLastRow(true);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }// 设置系列所占比例
        table.setTotalWidth(530);

        table.setLockedWidth(true);
        // table.setWidth(100);
        // table.setAutoFillEmptyCells(true);
        // table.setAlignment(Element.ALIGN_CENTER);// 居中显示
        // table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中显示
        // table.setBorder(1000);
        // table.setBorderWidth(1);// 边框宽度
        // table.setSpacing(2);
        // table.setPadding(18);
        // table.setBorderColor(new Color(0, 0, 0));// 边框颜色
        int cellHeight = 35;
        // 器官信息
        PdfPCell cell11 = new PdfPCell(new Phrase("", titleFont));
        cell11.setColspan(2);// 设置当前单元格占据的列数
        cell11.setRowspan(1);
        cell11.disableBorderSide(2);
        cell11.setMinimumHeight(cellHeight);
        // cell11.setUseAscender(true); //设置可以居中
        cell11.setHorizontalAlignment(Cell.ALIGN_CENTER); // 设置水平居中
        cell11.setVerticalAlignment(Cell.ALIGN_MIDDLE); // 设置垂直居中
        table.addCell(cell11);

        PdfPCell cell12 = new PdfPCell(new Phrase("器官类型", titleFont));
        cell12.setColspan(4);
        cell12.setRowspan(1);
        cell12.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell12.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        cell12.setMinimumHeight(cellHeight);
        table.addCell(cell12);

        PdfPCell cell13 = new PdfPCell(new Phrase("数量", titleFont));
        cell13.setColspan(4);
        cell13.setRowspan(1);
        cell13.setMinimumHeight(cellHeight);
        cell13.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell13.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell13);

        PdfPCell cell14 = new PdfPCell(new Phrase("供体血型", titleFont));
        cell14.setColspan(4);
        cell14.setRowspan(1);
        cell14.setMinimumHeight(cellHeight);
        cell14.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell14.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell14);

        PdfPCell cellbb = new PdfPCell(new Phrase("器官信息", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.disableBorderSide(2);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);

        PdfPCell cell22 = new PdfPCell(new Phrase(o_type, titleFont));
        cell22.setColspan(4);
        cell22.setRowspan(1);
        cell22.setMinimumHeight(cellHeight);
        cell22.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell22.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell22);

        PdfPCell cell23 = new PdfPCell(new Phrase(t_organCount + "", titleFont));
        cell23.setColspan(4);
        cell23.setRowspan(1);
        cell23.setMinimumHeight(cellHeight);
        cell23.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell23.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell23);

        PdfPCell cell24 = new PdfPCell(new Phrase(o_bloodType, titleFont));
        cell24.setColspan(4);
        cell24.setRowspan(1);
        cell24.setMinimumHeight(cellHeight);
        cell24.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell24.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell24);

        // 器官信息2
        cellbb = new PdfPCell(new Phrase(" ", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.disableBorderSide(2);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cellbb);

        PdfPCell cell32 = new PdfPCell(new Phrase("获取时间", titleFont));
        cell32.setColspan(3);
        cell32.setRowspan(1);
        cell32.setMinimumHeight(cellHeight);
        cell32.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell32.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell32);

        PdfPCell cell33 = new PdfPCell(new Phrase("样本组织", titleFont));
        cell33.setColspan(3);
        cell33.setRowspan(1);
        cell33.setMinimumHeight(cellHeight);
        cell33.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell33.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell33);

        PdfPCell cell34 = new PdfPCell(new Phrase("配型样本", titleFont));
        cell34.setColspan(3);
        cell34.setRowspan(1);
        cell34.setMinimumHeight(cellHeight);
        cell34.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell34.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell34);

        PdfPCell cell35 = new PdfPCell(new Phrase("血液样本", titleFont));
        cell35.setColspan(3);
        cell35.setRowspan(1);
        cell35.setMinimumHeight(cellHeight);
        cell35.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell35.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell35);

        cellbb = new PdfPCell(new Phrase(" ", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cellbb);

        PdfPCell cell42 = new PdfPCell(new Phrase(t_getOrganAt != null ? t_getOrganAt.toString() : "", titleFont));
        cell42.setColspan(3);
        cell42.setMinimumHeight(cellHeight);
        cell42.setRowspan(1);
        cell42.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell42.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell42);


        PdfPCell cell43 = new PdfPCell(new Phrase(sampleOrgan, titleFont));
        cell43.setColspan(3);
        cell43.setRowspan(1);
        cell43.setMinimumHeight(cellHeight);
        cell43.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell43.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell43);

        PdfPCell cell44 = new PdfPCell(new Phrase(o_organizationSampleCount
                + "", titleFont));
        cell44.setColspan(3);
        cell44.setRowspan(1);
        cell44.setMinimumHeight(cellHeight);
        cell44.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell44.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell44);

        PdfPCell cell45 = new PdfPCell(new Phrase(o_bloodSampleCount + "",
                titleFont));
        cell45.setColspan(3);
        cell45.setRowspan(1);
        cell45.setMinimumHeight(cellHeight);
        cell45.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell45.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell45);

        // 运输信息
        PdfPCell cell51 = new PdfPCell(new Phrase("运输信息", titleFont));
        cell51.setColspan(2);// 设置当前单元格占据的列数
        cell51.setRowspan(1);
        cell51.setMinimumHeight(cellHeight);
        cell51.disableBorderSide(2);
        cell51.setPaddingTop(cellHeight);
        cell51.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell51.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell51);

        PdfPCell cell52 = new PdfPCell(new Phrase("运输起始地", titleFont));
        cell52.setColspan(3);
        cell52.setRowspan(1);
        cell52.setMinimumHeight(cellHeight);
        cell52.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell52.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell52);

        PdfPCell cell53 = new PdfPCell(new Phrase("运输目的地", titleFont));
        cell53.setColspan(3);
        cell53.setRowspan(1);
        cell53.setMinimumHeight(cellHeight);
        cell53.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell53.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell53);

        PdfPCell cell54 = new PdfPCell(new Phrase("运输方式", titleFont));
        cell54.setColspan(3);
        cell54.setRowspan(1);
        cell54.setMinimumHeight(cellHeight);
        cell54.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell54.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell54);

        PdfPCell cell55 = new PdfPCell(new Phrase("车牌号/列车车次/航班号等", titleFont));
        cell55.setColspan(3);
        cell55.setRowspan(1);
        cell55.setMinimumHeight(cellHeight);
        cell55.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell55.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell55);

        cellbb = new PdfPCell(new Phrase(" ", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);


        PdfPCell cell62 = new PdfPCell(new Phrase(t_fromCity, titleFont));
        cell62.setColspan(3);
        cell62.setRowspan(1);
        cell62.setMinimumHeight(cellHeight);
        cell62.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell62.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell62);

        PdfPCell cell63 = new PdfPCell(new Phrase(h_name, titleFont));
        cell63.setColspan(3);
        cell63.setRowspan(1);
        cell63.setMinimumHeight(cellHeight);
        cell63.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell63.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell63);

        PdfPCell cell64 = new PdfPCell(new Phrase(t_tracfficType, titleFont));
        cell64.setColspan(3);
        cell64.setRowspan(1);
        cell64.setMinimumHeight(cellHeight);
        cell64.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell64.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell64);

        PdfPCell cell65 = new PdfPCell(new Phrase(t_tracfficNumber, titleFont));
        cell65.setColspan(3);
        cell65.setRowspan(1);
        cell65.setMinimumHeight(cellHeight);
        cell65.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell65.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell65);

        // 联系信息

        PdfPCell cell71 = new PdfPCell(new Phrase("", titleFont));
        cell71.setColspan(2);// 设置当前单元格占据的列数
        cell71.setRowspan(1);
        cell71.setMinimumHeight(cellHeight);
        cell71.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell71.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        cell71.disableBorderSide(2);
        table.addCell(cell71);

        PdfPCell cell72 = new PdfPCell(new Phrase("器官获取组织", titleFont));
        cell72.setColspan(4);
        cell72.setRowspan(1);
        cell72.setMinimumHeight(cellHeight);
        cell72.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell72.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell72);

        PdfPCell cell73 = new PdfPCell(new Phrase("联系人", titleFont));
        cell73.setColspan(4);
        cell73.setRowspan(1);
        cell73.setMinimumHeight(cellHeight);
        cell73.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell73.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell73);

        PdfPCell cell74 = new PdfPCell(new Phrase("电话", titleFont));
        cell74.setColspan(4);
        cell74.setRowspan(1);
        cell74.setMinimumHeight(cellHeight);
        cell74.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell74.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell74);

        cellbb = new PdfPCell(new Phrase("联系信息", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.disableBorderSide(2);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);

        PdfPCell cell82 = new PdfPCell(new Phrase(op_name, titleFont));
        cell82.setColspan(4);
        cell82.setRowspan(1);
        cell82.setMinimumHeight(cellHeight);
        cell82.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell82.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell82);

        PdfPCell cell83 = new PdfPCell(new Phrase(op_contactPerson, titleFont));
        cell83.setColspan(4);
        cell83.setRowspan(1);
        cell83.setMinimumHeight(cellHeight);
        cell83.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell83.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell83);

        PdfPCell cell84 = new PdfPCell(new Phrase(op_contactPhone, titleFont));
        cell84.setColspan(4);
        cell84.setRowspan(1);
        cell84.setMinimumHeight(cellHeight);
        cell84.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell84.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell84);

        // 联系信息2
        cellbb = new PdfPCell(new Phrase(" ", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.disableBorderSide(2);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);
        PdfPCell cell92 = new PdfPCell(new Phrase("器官接收医院", titleFont));
        cell92.setColspan(4);
        cell92.setRowspan(1);
        cell92.setMinimumHeight(cellHeight);
        cell92.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell92.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell92);

        PdfPCell cell93 = new PdfPCell(new Phrase("联系人", titleFont));
        cell93.setColspan(4);
        cell93.setRowspan(1);
        cell93.setMinimumHeight(cellHeight);
        cell93.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell93.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell93);

        PdfPCell cell94 = new PdfPCell(new Phrase("电话", titleFont));
        cell94.setColspan(4);
        cell94.setRowspan(1);
        cell94.setMinimumHeight(cellHeight);
        cell94.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell94.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell94);

        cellbb = new PdfPCell(new Phrase("", titleFont));
        cellbb.setColspan(2);
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);

        PdfPCell cell102 = new PdfPCell(new Phrase(h_name2, titleFont));
        cell102.setColspan(4);
        cell102.setRowspan(1);
        cell102.setMinimumHeight(cellHeight);
        cell102.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell102.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell102);

        PdfPCell cell103 = new PdfPCell(new Phrase(tp_name, titleFont));
        cell103.setColspan(4);
        cell103.setRowspan(1);
        cell103.setMinimumHeight(cellHeight);
        cell103.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell103.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell103);

        PdfPCell cell104 = new PdfPCell(new Phrase(tp_phone, titleFont));
        cell104.setColspan(4);
        cell104.setRowspan(1);
        cell104.setMinimumHeight(cellHeight);
        cell104.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell104.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell104);

        // 完成信息
        PdfPCell cell111 = new PdfPCell(new Phrase("", titleFont));
        cell111.setColspan(2);// 设置当前单元格占据的列数
        cell111.setRowspan(1);
        cell111.setMinimumHeight(cellHeight);
        cell111.setPaddingTop(cellHeight);
        cell111.disableBorderSide(2);
        cell111.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell111.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell111);

        PdfPCell cell112 = new PdfPCell(new Phrase("平均温度", titleFont));
        cell112.setColspan(2);
        cell112.setRowspan(1);
        cell112.setMinimumHeight(cellHeight);
        cell112.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell112.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell112);

        PdfPCell cell113 = new PdfPCell(new Phrase("开箱次数", titleFont));
        cell113.setColspan(2);
        cell113.setRowspan(1);
        cell113.setMinimumHeight(cellHeight);
        cell113.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell113.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell113);

        PdfPCell cell114 = new PdfPCell(new Phrase("碰撞次数", titleFont));
        cell114.setColspan(2);
        cell114.setRowspan(1);
        cell114.setMinimumHeight(cellHeight);
        cell114.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell114.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell114);

        //PdfPCell cell115 = new PdfPCell(new Phrase("脂肪变", titleFont));
        PdfPCell cell115 = new PdfPCell(new Phrase("最大温度", titleFont));
        cell115.setColspan(2);
        cell115.setRowspan(1);
        cell115.setMinimumHeight(cellHeight);
        cell115.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell115.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell115);

        //PdfPCell cell116 = new PdfPCell(new Phrase("肝硬化", titleFont));
        PdfPCell cell116 = new PdfPCell(new Phrase("最小温度", titleFont));
        cell116.setColspan(2);
        cell116.setRowspan(1);
        cell116.setMinimumHeight(cellHeight);
        cell116.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell116.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell116);

        PdfPCell cell117 = new PdfPCell(new Phrase("其他", titleFont));
        cell117.setColspan(2);
        cell117.setRowspan(1);
        cell117.setMinimumHeight(cellHeight);
        cell117.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell117.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell117);

        cellbb = new PdfPCell(new Phrase("质控信息", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.disableBorderSide(2);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);

        PdfPCell cell172 = new PdfPCell(new Phrase(avgTemperature + "°C", titleFont));
        cell172.setColspan(2);
        cell172.setRowspan(1);
        cell172.setMinimumHeight(cellHeight);
        cell172.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell172.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell172);

        PdfPCell cell173 = new PdfPCell(new Phrase(t_open, titleFont));
        cell173.setColspan(2);
        cell173.setRowspan(1);
        cell173.setMinimumHeight(cellHeight);
        cell173.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell173.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell173);

        PdfPCell cell174 = new PdfPCell(new Phrase("" + t_collisionInfo, titleFont));
        cell174.setColspan(2);
        cell174.setRowspan(1);
        cell174.setMinimumHeight(cellHeight);
        cell174.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell174.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell174);

        PdfPCell cell175 = new PdfPCell(new Phrase(maxTemperature+ "°C", titleFont));
        cell175.setColspan(2);
        cell175.setRowspan(1);
        cell175.setMinimumHeight(cellHeight);
        cell175.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell175.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell175);

        PdfPCell cell176 = new PdfPCell(new Phrase(minTemperature+ "°C", titleFont));
        cell176.setColspan(2);
        cell176.setRowspan(1);
        cell176.setMinimumHeight(cellHeight);
        cell176.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell176.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell176);

        PdfPCell cell177 = new PdfPCell(new Phrase("无", titleFont));
        cell177.setColspan(2);
        cell177.setRowspan(1);
        cell177.setMinimumHeight(cellHeight);
        cell177.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell177.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell177);

//		String exceptionInfo = "温度异常(小于0,大于10):  " + t_temperature + "次\n";
//		exceptionInfo += "                碰撞:  " + t_collisionInfo  + "次\n";
//		exceptionInfo += "                打开:  " + t_open + "次";
//		PdfPCell cell113 = new PdfPCell(new Phrase(exceptionInfo, titleFont));
//		cell113.setColspan(8);
//		cell113.setRowspan(1);
//		cell113.setMinimumHeight(cellHeight);
//		cell113.setHorizontalAlignment(Cell.ALIGN_LEFT);
//		cell113.setVerticalAlignment(Cell.ALIGN_MIDDLE);
//		table.addCell(cell113);

        cellbb = new PdfPCell(new Phrase(" ", titleFont));
        cellbb.setColspan(2);// 设置当前单元格占据的列数
        cellbb.setRowspan(1);
        cellbb.setPaddingTop(cellHeight);
        cellbb.setMinimumHeight(cellHeight);
        cellbb.disableBorderSide(1);
        cellbb.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cellbb.setVerticalAlignment(Cell.ALIGN_BASELINE);
        table.addCell(cellbb);

        PdfPCell cell122 = new PdfPCell(new Phrase("开箱后检验情况", titleFont));
        cell122.setColspan(4);
        cell122.setRowspan(1);
        cell122.setMinimumHeight(cellHeight);
        cell122.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell122.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell122);

        PdfPCell cell123 = new PdfPCell(new Phrase("", titleFont));
        cell123.setColspan(8);
        cell123.setRowspan(1);
        cell123.setMinimumHeight(cellHeight);
        cell123.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell123.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell123);

        // 底部消息
        PdfPCell cell131 = new PdfPCell(new Phrase("填表人", titleFont));
        cell131.setColspan(2);// 设置当前单元格占据的列数
        cell131.setRowspan(1);
        cell131.setMinimumHeight(cellHeight);
        cell131.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell131.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell131);

        PdfPCell cell141 = new PdfPCell(new Phrase(tp_name2, titleFont));
        cell141.setColspan(2);// 设置当前单元格占据的列数
        cell141.setRowspan(1);
        cell141.setMinimumHeight(cellHeight);
        cell141.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell141.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell141);

        PdfPCell cell151 = new PdfPCell(new Phrase("日期", titleFont));
        cell151.setColspan(2);// 设置当前单元格占据的列数
        cell151.setRowspan(1);
        cell151.setMinimumHeight(cellHeight);
        cell151.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell151.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell151);

        PdfPCell cell161 = new PdfPCell(new Phrase(t_getOrganAt != null ? t_getOrganAt
                .toString() : "", titleFont));
        cell161.setColspan(2);// 设置当前单元格占据的列数
        cell161.setRowspan(1);
        cell161.setMinimumHeight(cellHeight);
        cell161.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell161.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell161);

        // 底部消息
        PdfPCell cell181 = new PdfPCell(new Phrase("评分", titleFont));
        cell181.setColspan(3);// 设置当前单元格占据的列数
        cell181.setRowspan(1);
        cell181.setMinimumHeight(cellHeight);
        cell181.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell181.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell181);
        int mark = 80;
        try {
            if (t_collisionInfo.length() >= 3 || Integer.parseInt(avgTemperature) < -10 || Integer.parseInt(avgTemperature) > 28) {
                mark = 59;
            }
            if (Integer.parseInt(avgTemperature) > -5 && Integer.parseInt(avgTemperature) < 10) {
                mark = 85;
            }
            if (Integer.parseInt(avgTemperature) > -5 && Integer.parseInt(avgTemperature) < 10 && Integer.parseInt(t_collisionInfo) < 2) {
                mark = 90;
            }
            if (Integer.parseInt(avgTemperature) > -5 && Integer.parseInt(avgTemperature) < 10 && Integer.parseInt(t_collisionInfo) < 2 && Integer.parseInt(t_open) < 2) {
                mark = 95;
            }
        } catch (Exception e) {

        }


        PdfPCell cell191 = new PdfPCell(new Phrase(mark + "", titleFont));
        cell191.setColspan(3);// 设置当前单元格占据的列数
        cell191.setRowspan(1);
        cell191.setMinimumHeight(cellHeight);
        cell191.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell191.setVerticalAlignment(Cell.ALIGN_MIDDLE);
        table.addCell(cell191);


        return table;
    }
}
