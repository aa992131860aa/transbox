package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.WorkloadDao;
import com.life.entity.Datas;
import com.life.entity.Users;
import com.life.entity.Workload;
import com.life.entity.WorkloadDoctor;
import com.life.entity.WorkloadNurse;
import com.life.entity.WorkloadPersonInfo;
import com.life.utils.CONST;

public class WorkloadServlet extends HttpServlet {

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
		WorkloadDao workloadDao = new WorkloadDao();

		// 获取护士和医师的工作量统计
		if ("workloadLiver".equals(action)) {
			String phone = request.getParameter("phone");
			String time = request.getParameter("time");
			String roleId = request.getParameter("roleId");
			if (phone != null && !"".equals(phone) && roleId != null
					&& !"".equals(roleId)) {

				if (time == null || "".equals(time)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					time = sdf.format(new Date());
				}

				// 护士统计
				if (CONST.NURSE.equals(roleId)) {

					List<Workload> workloadNurse = workloadDao
							.getWorkloadNurse(phone, time);
					datas.setResult(CONST.SEND_OK);
					datas.setObj(workloadNurse);

				}
				// 医师统计
				else if (CONST.DOCTOR.equals(roleId)) {

					List<Workload> workloadDoctor = workloadDao
							.getWorkloadDoctor(phone, time);
					datas.setResult(CONST.SEND_OK);
					datas.setObj(workloadDoctor);

				}

			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do start 参数错误");
			}

		}
		//根据科室协调员获取工作量
		else if ("workloadAllLiver".equals(action)) {
			String contactPhone = request.getParameter("contactPhone");
			String time = request.getParameter("time");

			if (contactPhone != null && !"".equals(contactPhone)) {
				if (time == null || "".equals(time)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					time = sdf.format(new Date());
				}
				
				List<WorkloadPersonInfo> workloadPersonInfos = new ArrayList<WorkloadPersonInfo>();
				List<Users> users = workloadDao.getUsersByContactPhone(contactPhone);
				for(int i=0;i<users.size();i++){
					List<Workload> workloadDoctor = workloadDao
					.getWorkloadDoctor(users.get(i).getPhone(), time);
					for(int j=workloadDoctor.size()-1;j>=0;j--){
						if(workloadDoctor.get(j).getCount()==0){
							workloadDoctor.remove(j);
						}
					}
					if(workloadDoctor.size()>0){
						WorkloadPersonInfo workloadPersonInfo = new WorkloadPersonInfo();
						workloadPersonInfo.setPhone(users.get(i).getPhone());
						workloadPersonInfo.setTrueName(users.get(i).getTrueName());
						workloadPersonInfo.setRoleName(users.get(i).getPostRole());
						workloadPersonInfo.setTime(time);
						if("0".equals(users.get(i).getIsUploadPhoto())){
							workloadPersonInfo.setPhotoUrl(users.get(i).getWechatUrl());
						}else{
							workloadPersonInfo.setPhotoUrl(users.get(i).getPhotoFile());
						}
						workloadPersonInfo.setWorkloads(workloadDoctor);
						workloadPersonInfos.add(workloadPersonInfo);
						
					}
				}
				
//				List<WorkloadPersonInfo> workloadPersonInfos = workloadDao
//						.getWorkload(contactPhone, time);
				if (workloadPersonInfos.size() > 0) {

					datas.setResult(CONST.SEND_OK);
					datas.setObj(workloadPersonInfos);
				} else {
					datas.setResult(CONST.NO_MORE);
					datas.setMsg("没有信息");
				}
			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do start 参数错误");
			}

		}
		//同一医院统计
		else if ("workloadHospitalLiver".equals(action)) {
		 
			String time = request.getParameter("time");
			String hospital = request.getParameter("hospital");
			 
				if (time == null || "".equals(time)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					time = sdf.format(new Date());
				}
				List<WorkloadPersonInfo> workloadPersonInfos = new ArrayList<WorkloadPersonInfo>();
				List<Users> users = workloadDao.getUsersByHospital(hospital);
				for(int i=0;i<users.size();i++){
					List<Workload> workloadDoctor = workloadDao
					.getWorkloadDoctor(users.get(i).getPhone(), time);
					for(int j=workloadDoctor.size()-1;j>=0;j--){
						if(workloadDoctor.get(j).getCount()==0){
							workloadDoctor.remove(j);
						}
					}
					if(workloadDoctor.size()>0){
						WorkloadPersonInfo workloadPersonInfo = new WorkloadPersonInfo();
						workloadPersonInfo.setPhone(users.get(i).getPhone());
						workloadPersonInfo.setTrueName(users.get(i).getTrueName());
						workloadPersonInfo.setRoleName(users.get(i).getPostRole());
						workloadPersonInfo.setTime(time);
						if("0".equals(users.get(i).getIsUploadPhoto())){
							workloadPersonInfo.setPhotoUrl(users.get(i).getWechatUrl());
						}else{
							workloadPersonInfo.setPhotoUrl(users.get(i).getPhotoFile());
						}
						workloadPersonInfo.setWorkloads(workloadDoctor);
						workloadPersonInfos.add(workloadPersonInfo);
					}
				}
				//获取同一医院的所有人的信息
//				List<WorkloadPersonInfo> workloadPersonInfos = workloadDao
//						.getWorkloadHospital(hospital, time);
				if (workloadPersonInfos.size() > 0) {

					datas.setResult(CONST.SEND_OK);
					datas.setObj(workloadPersonInfos);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有信息");
				}
			 

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
