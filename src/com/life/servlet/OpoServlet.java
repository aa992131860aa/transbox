package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.OpoDao;
import com.life.entity.Datas;
import com.life.entity.OpoInfo;
import com.life.entity.PostRole;
import com.life.utils.CONST;

public class OpoServlet extends HttpServlet {

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

		Gson gson = new Gson();
		Datas datas = new Datas();

		String action = request.getParameter("action");
		OpoDao opoDao = new OpoDao();
		// 验证是否存在箱子并可用
		if ("province".equals(action)) {

			List<String> lists = opoDao.getProvices();
			if (lists.size() > 0) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("获取省份成功");
				datas.setObj(lists);
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("box.do start 箱子不可用");
			}

		}
		// 获取单个opo信息
		else if ("opo".equals(action)) {

			String hospital = request.getParameter("hospital");
			OpoInfo opoInfo = opoDao.getOpo(hospital);
			if (opoInfo != null) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("获取省份成功");
				datas.setObj(opoInfo);
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("box.do start 箱子不可用");
			}

		} else if ("opoInfo".equals(action)) {
			String province = request.getParameter("province");
			if (null != province && !"".equals(province)) {
				List<OpoInfo> opoInfos = opoDao.getHospitalByProvice(province);
				if (opoInfos.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取opoInfo成功");
					datas.setObj(opoInfos);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("box.do opoInfo 获取失败");
				}
			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do opoInfo 参数错误");
			}
		}

		else if ("getPostRoles".equals(action)) {

			List<PostRole> opoInfos = opoDao.getPostRoles();
			if (opoInfos.size() > 0) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("获取opoInfo成功");
				datas.setObj(opoInfos);
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("box.do opoInfo 获取失败");
			}

		}
//		else if ("getPostRoles".equals(action)) {
//
//			List<PostRole> opoInfos = opoDao.getPostRoles();
//			if (opoInfos.size() > 0) {
//				datas.setResult(CONST.SEND_OK);
//				datas.setMsg("获取opoInfo成功");
//				datas.setObj(opoInfos);
//			} else {
//				datas.setResult(CONST.SEND_FAIL);
//				datas.setMsg("box.do opoInfo 获取失败");
//			}
//
//		} 
		else if ("dealPostRole".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String phone = request.getParameter("phone");
            String postRoleId = request.getParameter("postRoleId");
            
			opoDao.dealPostRole(organSeg,phone,Integer.parseInt(postRoleId));
		

		}

		else {
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("box.do 没有相应的action");
		}
		out.write(gson.toJson(datas));

		out.flush();
		out.close();
	}
}
