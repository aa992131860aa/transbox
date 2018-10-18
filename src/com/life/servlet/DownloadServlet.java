package com.life.servlet;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.life.utils.WordUtils;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class DownloadServlet extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			doMain(request, response);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	public static void main(String[] args) {

	}

	/**
	 * 读取数据库数据
	 */
	private List<Object> getSqlInfo(HttpServletRequest request) {
		Object[] o = new Object[1];
		String transfer_id = request.getParameter("transfer_id");
		o[0] = transfer_id;

		// 查询
		String sql = "select t.transferid t_transferid,t.transferNumber t_transferNumber,t.organCount t_organCount,"
				+ "t.boxPin t_boxPin, t.fromCity t_fromCity,t.toHospName t_toHospName,t.tracfficType t_tracfficType,t.tracfficNumber t_tracfficNumber,t.deviceType"
				+ " t_deviceType,t.getOrganAt t_getOrganAt,t.startAt t_startAt,t.endAt t_endAt,t.`status` t_status,t.createAt "
				+ "t_createAt,t.modifyAt t_modifyAt,b.boxid b_boxid,b.deviceId b_deviceId,b.qrcode b_qrcode,b.model b_model,"
				+ "b.transferStatus b_transferStatus,b.`status` b_status,b.createAt b_createAt,b.modifyAt b_modifyAt"
				+ ",o.organid o_organid,o.segNumber o_segNumber,o.type o_type,o.bloodType o_bloodType,o.bloodSampleCount"
				+ " o_bloodSampleCount,o.organizationSampleType o_organizationSampleType,o.organizationSampleCount "
				+ "o_organizationSampleCount,o.createAt o_createAt,o.modifyAt o_modifyAt,h.hospitalid h_hospitalid,h.`name`"
				+ " h_name,h.district h_district,h.address h_address,h.grade h_grade,h.remark h_remark,h.`status` h_status,"
				+ "h.createAt h_createAt,h.modifyAt h_modifyAt,h.account_id h_account_id,tp.transferPersonid tp_transferPersonid,"
				+ "tp.`name` tp_name,tp.phone tp_phone,tp.organType tp_organType,tp.createAt tp_createAt,tp.modifyAt tp_modifyAt,"
				+ "op.opoid op_opoid,op.`name` op_name,op.district op_district,op.address op_address,op.grade op_grade,"
				+ "op.contactPerson op_contactPerson,op.contactPhone op_contactPhone,op.remark op_remark,op.createAt "
				+ "op_createAt,op.modifyAt op_modifyAt from transfer t,organ o,box b,hospital h,transferPerson tp,opo op where  b.boxid = t.box_id and h.hospitalid = t.to_hosp_id and o.organid "
				+ "= t.organ_id and tp.transferPersonid = t.transferPerson_id and op.opoid = t.opo_id and t.transferid = ?";
          //System.out.println(sql);
          //System.out.println(transfer_id);
		return new ConnectionDB().excuteQuery(sql, o);
	}

	/**
	 * 保存温度 最后一条平均温度记录
	 */
	private Object getSqlAvgTemperature(String transboxId) {
		Object[] o = new Object[1];
		o[0] = transboxId;
		String sql = "select avgTemperature from transferRecord where transfer_id = ? order by createAt desc limit 0,1";
		return new ConnectionDB().executeQuerySingle(sql, o);

	}
	/**
	 * <0 或者 >6的温度
	 */
	private Object getSqlExceptionTemperature(String transboxId) {
		Object[] o = new Object[1];
		o[0] = transboxId;
		String sql = "select count(transferRecordid) count from transferRecord where transfer_id = ?";
		return new ConnectionDB().executeQuerySingle(sql, o);

	}
	/**
	 * 运输过程中突发情况  碰撞
	 * 数据格式为json collisionInfo
	 */
	private Object getSqlCollisionInfo(String transboxId) {
		Object[] o = new Object[1];
		o[0] = transboxId;
		String sql = "select count(transferRecordid) from transferRecord where type & 4 and transfer_id=? ";
		return new ConnectionDB().executeQuerySingle(sql, o);

	}
	/**
	 * 运输过程中突发情况  打开
	 * 数据格式为json collisionInfo
	 */
	private Object getSqlOpenInfo(String transboxId) {
		Object[] o = new Object[1];
		o[0] = transboxId;
		String sql = "select count(transferRecordid) from transferRecord where type & 8 and transfer_id=? ";
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
		// System.out.println(fullFileName);
		// 读取文件
		FileInputStream in = new FileInputStream(fullFileName);
		OutputStream out = response.getOutputStream();

		// 写文件
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}

	}

	private void doMain(HttpServletRequest request, HttpServletResponse response) throws DocumentException, IOException{
		
		    List<Object> infos  = getSqlInfo(request);
		    Map<String, Object> map = null;  
		    if(infos!=null&&infos.size()==1){
		    	map = (Map<String, Object>) infos.get(0);
		    }
		   
		    //器官捐献者编号
		    String o_segNumber = (String) map.get("o_segNumber");
		    String fileName = (String) map.get("t_transferNumber");
		   
		    
		    WordUtils wordUtils = new WordUtils();
		    String   dirPath = getServletContext().getRealPath("/");
		   
	        String filePath = dirPath+File.separator+"download"+File.separator+fileName+".doc";
	        String ttfPath = dirPath +File.separator+"ttf"+File.separator+"c.TTF";
	        wordUtils.openDocument(filePath,ttfPath);

	        //第一级标题样式
	        RtfParagraphStyle rtfGsBt1 = RtfParagraphStyle.STYLE_HEADING_1;
	        rtfGsBt1.setAlignment(Element.ALIGN_LEFT);
	        rtfGsBt1.setStyle(Font.BOLD);
	        rtfGsBt1.setSize(15);

	        //第二级标题样式
	        RtfParagraphStyle rtfGsBt2 = RtfParagraphStyle.STYLE_HEADING_2;
	        rtfGsBt2.setAlignment(Element.ALIGN_LEFT);
	        rtfGsBt2.setStyle(Font.BOLD);
	        rtfGsBt2.setSize(13);
	        // 设置中文字体 
	        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", 
	        "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED); 
	        // 标题字体风格 
	        Font titleFont = new Font(bfChinese, 12, Font.BOLD); 
	        // 正文字体风格 
	        Font contextFont = new Font(bfChinese, 10, Font.NORMAL); 

	        wordUtils.insertTitle("XX/T XXXXX--XXXX", CONST.WORD_SIZE, Font.NORMAL, Element.ALIGN_RIGHT);
	        wordUtils.insertTitle("附录", CONST.WORD_SIZE, Font.NORMAL, Element.ALIGN_CENTER);
	        wordUtils.insertTitle("（资料性附录）", CONST.WORD_SIZE, Font.NORMAL, Element.ALIGN_CENTER);
	        wordUtils.insertTitle("器官运输记录单", CONST.WORD_SIZE, Font.NORMAL, Element.ALIGN_CENTER);
	        wordUtils.insertImg(CONST.URL_PATH+"images/demo.png", Element.ALIGN_RIGHT, 400, 400, 50, 50, 50, 0);
	        wordUtils.insertTitle("器官捐献者编号: "+o_segNumber, CONST.WORD_SIZE, Font.NORMAL, Element.ALIGN_LEFT);
	        wordUtils.insertTitle("器官运输记录单",16, Font.BOLD, Element.ALIGN_CENTER);
	        //插入表格
	        wordUtils.getDocument().add(insertComplexTable(map));

	        wordUtils.closeDocument();
	        
	        response.reset();
	        //下载
	        downloadDoc(request, response, fileName+".doc");
	}

	/**
	 * @return 复合表格的简单例子
	 * @throws DocumentException
	 */
	public Table insertComplexTable(Map<String, Object> map) throws DocumentException {
		
		    String t_transferid = (String) map.get("t_transferid"); 
		    
		    //器官类型
		    String o_type = (String) map.get("o_type");
		    Integer t_organCount = (Integer) map.get("t_organCount");
		    String o_bloodType = (String) map.get("o_bloodType");
			
		    //获取时间
		    Timestamp t_getOrganAt = (Timestamp) map.get("t_getOrganAt");
			//平均温度
			String avgTemperature =  (String) getSqlAvgTemperature(t_transferid);
			if(avgTemperature==null || "".equals(avgTemperature)){
				avgTemperature = "0";
			}
			avgTemperature +="°C"; 
			Integer o_organizationSampleCount = (Integer) map.get("o_organizationSampleCount");
			Integer o_bloodSampleCount = (Integer) map.get("o_bloodSampleCount");
			
			//运输起始地
			String t_fromCity = (String) map.get("t_fromCity");
			String h_name = (String) map.get("h_name");
			String t_tracfficType = (String) map.get("t_tracfficType");
			String t_tracfficNumber = (String) map.get("t_tracfficNumber");
			
			//器官获取组织
			String op_name = (String) map.get("op_name");
			String op_contactPerson = (String) map.get("op_contactPerson");
			String op_contactPhone = (String) map.get("op_contactPhone");
			
			//器官接收医院
			String h_name2 = (String) map.get("h_name");
			String tp_name = (String) map.get("tp_name");
			String tp_phone = (String) map.get("tp_phone");
			
			//运输过程中突发情况
			Long t_collisionInfo = (Long) getSqlCollisionInfo(t_transferid);
			Long t_open = (Long) getSqlOpenInfo(t_transferid);
			Long t_temperature = (Long) getSqlExceptionTemperature(t_transferid);
			
			//填表人
			String tp_name2 = (String) map.get("tp_name");
			Timestamp t_endAt = (Timestamp) map.get("t_endAt");
			
		Table table = new Table(14);
		int width[] = { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 };
		table.setWidths(width);// 设置系列所占比例
		table.setWidth(100);
		table.setAutoFillEmptyCells(true);
		table.setAlignment(Element.ALIGN_CENTER);// 居中显示
		table.setAlignment(Element.ALIGN_MIDDLE);// 垂直居中显示
		table.setBorder(1000);
		table.setBorderWidth(1);// 边框宽度
		// table.setSpacing(2);
		table.setPadding(18);
		table.setBorderColor(new Color(0, 0, 0));// 边框颜色

		// 器官信息
		Cell cell11 = new Cell(new Phrase("器官信息"));
		cell11.setColspan(2);// 设置当前单元格占据的列数
		cell11.setRowspan(4);
		cell11.setVerticalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell11);

		Cell cell12 = new Cell(new Phrase("器官类型"));
		cell12.setColspan(4);
		cell12.setRowspan(1);
		cell12.setVerticalAlignment(Element.ALIGN_CENTER);
		cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell12);

		Cell cell13 = new Cell(new Phrase("数量"));
		cell13.setColspan(4);
		cell13.setRowspan(1);
		cell13.setVerticalAlignment(Element.ALIGN_CENTER);
		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell13);

		Cell cell14 = new Cell(new Phrase("供体血型"));
		cell14.setColspan(4);
		cell14.setRowspan(1);
		cell14.setVerticalAlignment(Element.ALIGN_CENTER);
		cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell14);

		Cell cell22 = new Cell(new Phrase(o_type));
		cell22.setColspan(4);
		cell22.setRowspan(1);
		cell22.setVerticalAlignment(Element.ALIGN_CENTER);
		cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell22);

		Cell cell23 = new Cell(new Phrase(t_organCount+""));
		cell23.setColspan(4);
		cell23.setRowspan(1);
		cell23.setVerticalAlignment(Element.ALIGN_CENTER);
		cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell23);

		Cell cell24 = new Cell(new Phrase(o_bloodType));
		cell24.setColspan(4);
		cell24.setRowspan(1);
		cell24.setVerticalAlignment(Element.ALIGN_CENTER);
		cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell24);

		// 器官信息2

		Cell cell32 = new Cell(new Phrase("获取时间"));
		cell32.setColspan(3);
		cell32.setRowspan(1);
		cell32.setVerticalAlignment(Element.ALIGN_CENTER);
		cell32.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell32);

		Cell cell33 = new Cell(new Phrase("保存温度"));
		cell33.setColspan(3);
		cell33.setRowspan(1);
		cell33.setVerticalAlignment(Element.ALIGN_CENTER);
		cell33.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell33);

		Cell cell34 = new Cell(new Phrase("配型样本"));
		cell34.setColspan(3);
		cell34.setRowspan(1);
		cell34.setVerticalAlignment(Element.ALIGN_CENTER);
		cell34.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell34);

		Cell cell35 = new Cell(new Phrase("血液样本"));
		cell35.setColspan(3);
		cell35.setRowspan(1);
		cell35.setVerticalAlignment(Element.ALIGN_CENTER);
		cell35.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell35);

		Cell cell42 = new Cell(new Phrase(t_getOrganAt!=null?t_getOrganAt.toString():""));
		cell42.setColspan(3);
		cell42.setRowspan(1);
		cell42.setVerticalAlignment(Element.ALIGN_CENTER);
		cell42.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell42);

		Cell cell43 = new Cell(new Phrase(avgTemperature));
		cell43.setColspan(3);
		cell43.setRowspan(1);
		cell43.setVerticalAlignment(Element.ALIGN_CENTER);
		cell43.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell43);

		Cell cell44 = new Cell(new Phrase(o_organizationSampleCount+""));
		cell44.setColspan(3);
		cell44.setRowspan(1);
		cell44.setVerticalAlignment(Element.ALIGN_CENTER);
		cell44.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell44);

		Cell cell45 = new Cell(new Phrase(o_bloodSampleCount+""));
		cell45.setColspan(3);
		cell45.setRowspan(1);
		cell45.setVerticalAlignment(Element.ALIGN_CENTER);
		cell45.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell45);

		// 运输信息
		Cell cell51 = new Cell(new Phrase("运输信息"));
		cell51.setColspan(2);// 设置当前单元格占据的列数
		cell51.setRowspan(2);
		cell51.setVerticalAlignment(Element.ALIGN_CENTER);
		cell51.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell51);

		Cell cell52 = new Cell(new Phrase("运输起始地"));
		cell52.setColspan(3);
		cell52.setRowspan(1);
		cell52.setVerticalAlignment(Element.ALIGN_CENTER);
		cell52.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell52);

		Cell cell53 = new Cell(new Phrase("运输目的地"));
		cell53.setColspan(3);
		cell53.setRowspan(1);
		cell53.setVerticalAlignment(Element.ALIGN_CENTER);
		cell53.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell53);

		Cell cell54 = new Cell(new Phrase("运输方式"));
		cell54.setColspan(3);
		cell54.setRowspan(1);
		cell54.setVerticalAlignment(Element.ALIGN_CENTER);
		cell54.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell54);

		Cell cell55 = new Cell(new Phrase("车牌号/列车车次/航班号等"));
		cell55.setColspan(3);
		cell55.setRowspan(1);
		cell55.setVerticalAlignment(Element.ALIGN_CENTER);
		cell55.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell55);
		

		Cell cell62 = new Cell(new Phrase(t_fromCity));
		cell62.setColspan(3);
		cell62.setRowspan(1);
		cell62.setVerticalAlignment(Element.ALIGN_CENTER);
		cell62.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell62);

		Cell cell63 = new Cell(new Phrase(h_name));
		cell63.setColspan(3);
		cell63.setRowspan(1);
		cell63.setVerticalAlignment(Element.ALIGN_CENTER);
		cell63.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell63);

		Cell cell64 = new Cell(new Phrase(t_tracfficType));
		cell64.setColspan(3);
		cell64.setRowspan(1);
		cell64.setVerticalAlignment(Element.ALIGN_CENTER);
		cell64.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell64);

		Cell cell65 = new Cell(new Phrase(t_tracfficNumber));
		cell65.setColspan(3);
		cell65.setRowspan(1);
		cell65.setVerticalAlignment(Element.ALIGN_CENTER);
		cell65.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell65);

		// 联系信息

		Cell cell71 = new Cell(new Phrase("联系信息"));
		cell71.setColspan(2);// 设置当前单元格占据的列数
		cell71.setRowspan(4);
		cell71.setVerticalAlignment(Element.ALIGN_CENTER);
		cell71.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell71);

		Cell cell72 = new Cell(new Phrase("器官获取组织"));
		cell72.setColspan(4);
		cell72.setRowspan(1);
		cell72.setVerticalAlignment(Element.ALIGN_CENTER);
		cell72.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell72);

		Cell cell73 = new Cell(new Phrase("联系人"));
		cell73.setColspan(4);
		cell73.setRowspan(1);
		cell73.setVerticalAlignment(Element.ALIGN_CENTER);
		cell73.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell73);

		Cell cell74 = new Cell(new Phrase("电话"));
		cell74.setColspan(4);
		cell74.setRowspan(1);
		cell74.setVerticalAlignment(Element.ALIGN_CENTER);
		cell74.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell74);

		Cell cell82 = new Cell(new Phrase(op_name));
		cell82.setColspan(4);
		cell82.setRowspan(1);
		cell82.setVerticalAlignment(Element.ALIGN_CENTER);
		cell82.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell82);

		Cell cell83 = new Cell(new Phrase(op_contactPerson));
		cell83.setColspan(4);
		cell83.setRowspan(1);
		cell83.setVerticalAlignment(Element.ALIGN_CENTER);
		cell83.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell83);

		Cell cell84 = new Cell(new Phrase(op_contactPhone));
		cell84.setColspan(4);
		cell84.setRowspan(1);
		cell84.setVerticalAlignment(Element.ALIGN_CENTER);
		cell84.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell84);

		// 联系信息2	
		Cell cell92 = new Cell(new Phrase("器官接收医院"));
		cell92.setColspan(4);
		cell92.setRowspan(1);
		cell92.setVerticalAlignment(Element.ALIGN_CENTER);
		cell92.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell92);

		Cell cell93 = new Cell(new Phrase("联系人"));
		cell93.setColspan(4);
		cell93.setRowspan(1);
		cell93.setVerticalAlignment(Element.ALIGN_CENTER);
		cell93.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell93);

		Cell cell94 = new Cell(new Phrase("电话"));
		cell94.setColspan(4);
		cell94.setRowspan(1);
		cell94.setVerticalAlignment(Element.ALIGN_CENTER);
		cell94.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell94);

		Cell cell102 = new Cell(new Phrase(h_name2));
		cell102.setColspan(4);
		cell102.setRowspan(1);
		cell102.setVerticalAlignment(Element.ALIGN_CENTER);
		cell102.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell102);

		Cell cell103 = new Cell(new Phrase(tp_name));
		cell103.setColspan(4);
		cell103.setRowspan(1);
		cell103.setVerticalAlignment(Element.ALIGN_CENTER);
		cell103.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell103);

		Cell cell104 = new Cell(new Phrase(tp_phone));
		cell104.setColspan(4);
		cell104.setRowspan(1);
		cell104.setVerticalAlignment(Element.ALIGN_CENTER);
		cell104.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell104);

		// 完成信息
		Cell cell111 = new Cell(new Phrase("完成信息"));
		cell111.setColspan(2);// 设置当前单元格占据的列数
		cell111.setRowspan(2);
		cell111.setVerticalAlignment(Element.ALIGN_CENTER);
		cell111.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell111);

		Cell cell112 = new Cell(new Phrase("运输过程中突发情况"));
		cell112.setColspan(4);
		cell112.setRowspan(1);
		cell112.setVerticalAlignment(Element.ALIGN_CENTER);
		cell112.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell112);
        String exceptionInfo = "温度异常(小于0,大于6):  "+t_temperature+"次\n";
              exceptionInfo += "                碰撞:  "+t_collisionInfo+"次\n";
              exceptionInfo += "                打开:  "+t_open+"次";
		Cell cell113 = new Cell(new Phrase(exceptionInfo));
		cell113.setColspan(8);
		cell113.setRowspan(1);
		cell113.setVerticalAlignment(Element.ALIGN_CENTER);
		cell113.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell113);

		Cell cell122 = new Cell(new Phrase("开箱后检验情况"));
		cell122.setColspan(4);
		cell122.setRowspan(1);
		cell122.setVerticalAlignment(Element.ALIGN_CENTER);
		cell122.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell122);

		Cell cell123 = new Cell(new Phrase(""));
		cell123.setColspan(8);
		cell123.setRowspan(1);
		cell123.setVerticalAlignment(Element.ALIGN_CENTER);
		cell123.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell123);

		// 底部消息
		Cell cell131 = new Cell(new Phrase("填表人"));
		cell131.setColspan(3);// 设置当前单元格占据的列数
		cell131.setRowspan(2);
		cell131.setVerticalAlignment(Element.ALIGN_CENTER);
		cell131.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell131);

		Cell cell141 = new Cell(new Phrase(tp_name2));
		cell141.setColspan(4);// 设置当前单元格占据的列数
		cell141.setRowspan(2);
		cell141.setVerticalAlignment(Element.ALIGN_CENTER);
		cell141.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell141);

		Cell cell151 = new Cell(new Phrase("日期"));
		cell151.setColspan(3);// 设置当前单元格占据的列数
		cell151.setRowspan(2);
		cell151.setVerticalAlignment(Element.ALIGN_CENTER);
		cell151.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell151);

		Cell cell161 = new Cell(new Phrase(t_endAt!=null?t_endAt.toString():""));
		cell161.setColspan(4);// 设置当前单元格占据的列数
		cell161.setRowspan(2);
		cell161.setVerticalAlignment(Element.ALIGN_CENTER);
		cell161.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell161);
		return table;
	}
}

