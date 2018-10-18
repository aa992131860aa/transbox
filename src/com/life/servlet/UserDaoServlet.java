package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.SmsDao;
import com.life.controller.UserDao;
import com.life.entity.Datas;
import com.life.entity.HospitalZone;
import com.life.entity.OpoInfo;
import com.life.entity.Push;
import com.life.entity.Role;
import com.life.entity.UserInfo;
import com.life.entity.UserSitePush;
import com.life.entity.Users;
import com.life.utils.CONST;
import com.life.utils.WechatOpen;

public class UserDaoServlet extends HttpServlet {

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
		String action = request.getParameter("action");
		UserDao userDao = new UserDao();
		Gson gson = new Gson();
		//System.out.println("action:" + action);
		if (action != null) {

			if ("setPushSite".equals(action)) {

				Datas datas = new Datas();
				String phone = request.getParameter("phone");
				String type = request.getParameter("type");
				String status = request.getParameter("status");
				if (null != phone && !"".equals(phone) && null != type
						&& !"".equals(type) && null != status
						&& !"".equals(status)) {

					int result = userDao.setPushSite(phone, type, Integer
							.parseInt(status));
					if (result >= 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("更新自动成功");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("更新自动失败");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("更新自动失败");
				}

				out.write(gson.toJson(datas));

			}
			// 设置是否自动更新
			else if ("update".equals(action)) {

				Datas datas = new Datas();
				String phone = request.getParameter("phone");
				String isUpdate = request.getParameter("isUpdate");
				if (null != phone && !"".equals(phone) && null != isUpdate
						&& !"".equals(isUpdate)) {

					int result = userDao.updateByPhone(phone, Integer
							.parseInt(isUpdate));
					if (result == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("更新自动成功");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("更新自动失败");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("更新自动失败");
				}

				out.write(gson.toJson(datas));

			}
			// 获取角色列表
			else if ("role".equals(action)) {
				Datas datas = new Datas();
				List<Role> role = userDao.getRoleList();
				if (role.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取角色列表成功");
					datas.setObj(role);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("获取角色列表失败");
				}

				out.write(gson.toJson(datas));

			} else if ("roleInfo".equals(action)) {

				Datas datas = new Datas();
				String phone = request.getParameter("phone");
				if (null != phone && !"".equals(phone)) {
					Role role = userDao.getRoleInfo(phone);
					if (role != null) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获取角色列表成功");
						datas.setObj(role);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("获取角色列表失败");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("获取角色列表失败");
				}

				out.write(gson.toJson(datas));

			}
			//微信小程序的openId
			else if("gainOpenId".equals(action)){
				Datas datas = new Datas();
		 
				String js_code = request.getParameter("js_code");
				String result =new SmsDao().getOpenId(CONST.WECHAT_APP_ID, CONST.WECHAT_SECRET, js_code);
				WechatOpen wechatOpen = new Gson().fromJson(result, WechatOpen.class);
				if(wechatOpen.getOpenid()!=null){
					UserInfo userInfo =userDao.checkOpenId(wechatOpen.getOpenid());
					if(userInfo!=null){
						userInfo.setSessionKey(wechatOpen.getSession_key());
						datas.setResult(CONST.SEND_OK);
						datas.setMsg(wechatOpen.getOpenid());
						datas.setObj(userInfo);
					}else{
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg(wechatOpen.getOpenid());
					}
				}else{
					datas.setResult(CONST.NO_REGISTER);
					datas.setMsg(result);
				}
				out.write(gson.toJson(datas));
				 
			}
			// 手机验证码登录
			else if ("phone".equals(action)) {
				Datas datas = new Datas();
				String phone = request.getParameter("phone");
				if (phone == null || phone == "") {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				} else {
					Map<String, String> map = new HashMap<String, String>();

					boolean isExistPhone = userDao.isExistPhone(phone);

					// 0为重复
					if (isExistPhone) {
						boolean isExistPhoneConfirm = userDao
								.isExistPhoneConfirm(phone);
						// 认证成功
						if (isExistPhoneConfirm) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("登录认证成功");
							map = userDao.getUserByPhone(phone);
							map.put("status", "0");

							datas.setObj(map);
						} else {
							//是否为绑定的手机号  
							if(userDao.isBindPhoneConfirm(phone)){
								datas.setResult(CONST.SEND_OK);
								datas.setMsg("登录成功");
								map.put("status", "1");
								datas.setObj(map);
							}else{
								datas.setResult(CONST.SEND_OK);
								datas.setMsg("手机号重复,未认证");
								map.put("status", "2");
								datas.setObj(map);
							}
							
							
						}

					} else {
						 
						
						int isOK = userDao.insertUserPhone(phone);
						if (isOK == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("登录成功");
							map.put("status", "1");
							datas.setObj(map);
						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("登录失败");
						}
					}
				}

				out.write(gson.toJson(datas));

			}else if("gainSite".equals(action)){
				String phone = request.getParameter("phone");
				UserSitePush usp = userDao.getSite(phone);
				Datas datas = new Datas();
				if(usp!=null){
					datas.setResult(CONST.SEND_OK);
					datas.setObj(usp);
				}else{
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("查询设置失败");
				}
				out.write(gson.toJson(datas));
			}

			else if ("wechat".equals(action)) {
				// String wechatName = request.getParameter("wechatName");
				// String wechatUrl = request.getParameter("wechatUrl");
				String wechatUuid = request.getParameter("wechatUuid");
				Datas datas = new Datas();
				int flag = 0;
				if (wechatUuid == null || "".equals(wechatUuid)) {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("参数错误");
					flag = -1;
				} else {
					flag = -1;
					boolean isTrue = userDao.isExistWechat(wechatUuid);
					//System.out.println("wechatUuid:" + wechatUuid);
					Map<String, String> map = new HashMap<String, String>();
					// 已登录过的微信
					if (isTrue) {
						boolean isTrueConfirm = userDao
								.isExistWechatConfirm(wechatUuid);

						if (isTrueConfirm) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("微信认证成功");
							map = userDao.getUserByWechatUuid(wechatUuid);
							map.put("status", "0");
							datas.setObj(map);
						} else {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("微信重复,未认证");
							map.put("status", "2");
							datas.setObj(map);
						}

					} else {

						datas.setResult(CONST.SEND_OK);
						datas.setMsg("请求成功");
						map.put("status", "1");
						datas.setObj(map);

					}

				}
				if (flag == 0) {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("未知错误");
				}
				out.write(gson.toJson(datas));

			} else if ("confirmPhone".equals(action)) {
				String phone = request.getParameter("phone");
				String trueName = request.getParameter("trueName");
				String hospital = request.getParameter("hospital");
				String roleId = request.getParameter("roleId");

//				System.out.println("confirmPhone");
//				System.out.println("phone:" + phone);
//				System.out.println("trueName:" + trueName);
//				System.out.println("hospital:" + hospital);
//				System.out.println("roleId:" + roleId);

				Datas datas = new Datas();
				if (phone == null || "".equals(phone) || trueName == null
						|| "".equals(trueName) || hospital == null
						|| "".equals(hospital)) {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("参数错误");
				} else {

					boolean isPhone = userDao.isExistPhone(phone);
					// 存在手机
					if (isPhone) {
						// 插入手机验证的信息
						int isOK = userDao.updateUserConfirmByPhone(phone,
								trueName, hospital, roleId);
						// 更新系统读取的位置
						userDao.updatePushPostionAndFirst(phone);
						if (isOK == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("验证登录成功");
						} else if (isOK == 0) {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("数据库错误");
						} else if (isOK == -1) {
							datas.setResult(CONST.NO_REGISTER);
							datas.setMsg("没有权限注册");

						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("未知错误");
						}

					} else {
						// 插入手机验证的信息
						int isOK = userDao.insertUserConfirmByPhone(phone,
								trueName, hospital, roleId);
						// 更新系统读取的位置
						userDao.updatePushPostionAndFirst(phone);
						if (isOK == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("验证登录成功");
						} else if (isOK == 0) {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("数据库错误");
						} else if (isOK == -1) {
							datas.setResult(CONST.NO_REGISTER);
							datas.setMsg("没有权限注册");

						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("未知错误");
						}
					}
				}
				out.write(gson.toJson(datas));

			} else if ("confirmWechat".equals(action)) {
				String phone = request.getParameter("phone");
				String trueName = request.getParameter("trueName");
				String hospital = request.getParameter("hospital");
				String wechatUuid = request.getParameter("wechatUuid");
				String wechatName = request.getParameter("wechatName");
				String wechatUrl = request.getParameter("wechatUrl");
				String roleId = request.getParameter("roleId");
				String openId = request.getParameter("openId");
				Datas datas = new Datas();
				if (phone == null || "".equals(phone) || trueName == null
						|| "".equals(trueName) || hospital == null
						|| "".equals(hospital)) {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("参数错误");
				} else {

					// 插入微信
					boolean isPhone = userDao.isExistPhone(phone);
					int isOK = -1;
					//System.out.println("confirmWechat:" + isPhone);
					if (isPhone) {
						isOK = userDao.updateUserConfirmByWechatPhone(phone,
								trueName, hospital, wechatUuid, wechatName,
								wechatUrl, roleId,openId);

					} else {

						isOK = userDao.insertUserConfirmByWechat(phone,
								trueName, hospital, wechatUuid, wechatName,
								wechatUrl, roleId,openId);
					}
					// 更新系统读取的位置
					userDao.updatePushPostionAndFirst(phone);

					if (isOK == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("验证登录成功");
						// 删除没有微信信息的用户
						// userDao.deletePhoneNoWechat(phone);
					} else if (isOK == 0) {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("数据库错误");
					} else if (isOK == -1) {
						datas.setResult(CONST.NO_REGISTER);
						datas.setMsg("没有权限注册");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("未知错误");
					}
				}
				out.write(gson.toJson(datas));
				//System.out.println(gson.toJson(datas));

			} else if ("hospital".equals(action)) {
				String province = request.getParameter("province");
				List<String> lists = new ArrayList<String>();
				Datas datas = new Datas();
				if (province == null || "".equals(province)) {
					lists = userDao.getProvices();
					//System.out.println("获取省份");
					if (lists.size() == 0) {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("请求医院省份失败");
					} else {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("请求医院省份成功");
						datas.setObj(lists);
					}
				} else if (!"".equals(province)) {
					lists = userDao.getHospitalByProvice(province);
					if (lists.size() == 0) {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("请求医院失败");
					} else {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("请求医院成功");
						datas.setObj(lists);
					}
				}

				out.write(gson.toJson(datas));
			} else if ("bindPhone".equals(action)) {
				String phone = request.getParameter("phone");
				String bind = request.getParameter("bind");

				Datas datas = new Datas();
				if (phone != null && !"".equals(phone) && bind != null
						&& !"".equals(bind)) {

					int isOk = userDao.updatePhoneUrlBind(phone, bind);
					if (isOk == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("绑定手机成功");
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("数据库错误");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("pushCount".equals(action)) {
				String phone = request.getParameter("phone");

				Datas datas = new Datas();
				if (phone != null && !"".equals(phone)) {
					List<Push> pushes = userDao.getPushCount(phone);
			
					if (pushes.size() > 0) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("count", pushes.get(0).getCount());
						map.put("content",pushes.get(0).getContent());
						map.put("createTime", pushes.get(0).getCreateTime());
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获取未读数成功");
						datas.setObj(map);
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("count", 0);
						map.put("content","");
						map.put("createTime", "");
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("没有count数据");
						datas.setObj(map);
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("getPushList".equals(action)) {
				String phone = request.getParameter("phone");
				String pageStr = request.getParameter("page");
				String pageSizeStr = request.getParameter("pageSize");
				Datas datas = new Datas();
				try {

					if (phone != null && !"".equals(phone) && pageStr != null
							&& !"".equals(pageStr) && pageSizeStr != null
							&& !"".equals(pageSizeStr)) {
						int page = Integer.parseInt(pageStr);
						int pageSize = Integer.parseInt(pageSizeStr);
						List<Push> pushes = userDao.getPushList(phone, page,
								pageSize);
						List<Push> pushTrue = new ArrayList<Push>();

						if (pushes.size() > 0) {

							for (int i = 0; i < pushes.size(); i++) {
								Push oldPush = pushes.get(i);
								// System.out.println("phone:"+oldPush.getPhone());
								// 不是添加好友消息
								if (oldPush.getPhone() == null
										|| "".equals(oldPush.getPhone())) {
									// System.out.println("phone1:"+oldPush.getPhone());
									pushTrue.add(oldPush);
								} else {
									Push pushNew = userDao
											.getUserByPushPhone(oldPush
													.getPhone());

									if (pushNew != null) {
										pushNew.setId(oldPush.getId());
										pushNew
												.setContent(oldPush
														.getContent());
										pushNew.setPushId(oldPush.getPushId());
										pushNew.setCreateTime(oldPush
												.getCreateTime());
										pushNew.setType(oldPush.getType());
										pushNew
												.setOtherId(oldPush
														.getOtherId());
										pushTrue.add(pushNew);
									}
								}
							}
							if (pushTrue.size() > 0) {

								// 处理已删除的消息
								// List<Push> pushInfo =
								// userDao.isDelPush(pushTrue,phone);

								datas.setResult(CONST.SEND_OK);
								datas.setMsg("获取系统消息列表成功");
								datas.setObj(pushTrue);
							} else {
								datas.setResult(CONST.SEND_FAIL);
								datas.setMsg("没有push数据");
							}

						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("没有push数据");
						}

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("参数错误");
					}

				} catch (Exception e) {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误转换错误");
				}

				out.write(gson.toJson(datas));
			}
			// 删除系统消息
			else if ("deleteSystemNews".equals(action)) {
				//System.out.println("user.do updateSystemPosition");
				String content = request.getParameter("content");
				String type = request.getParameter("type");
				String pushId = request.getParameter("pushId");
				String phone = request.getParameter("phone");

				Datas datas = new Datas();
				if (pushId != null && !"".equals(pushId)) {
					int isOk = 0;
					// 删除好友
					
						isOk = userDao.deletePush(Integer.parseInt(pushId));
					

					if (isOk == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("删除成功");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有删除成功");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("updateSystemPosition".equals(action)) {
				//System.out.println("user.do updateSystemPosition");
				String phone = request.getParameter("phone");

				Datas datas = new Datas();
				if (phone != null && !"".equals(phone)) {

					int isOk = userDao.updatePushPostion(phone);
				 
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("更新成功");

					 

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("personInfo".equals(action)) {
				String phone = request.getParameter("phone");

				Datas datas = new Datas();

				if (phone != null && !"".equals(phone)) {

					Map<String, String> map = userDao.getUserByPhone(phone);
					if (map.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获取人员信息成功");
						datas.setObj(map);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有个人信息数据");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("personInfoUser".equals(action)) {
				String phone = request.getParameter("phone");
				String contactPhone = request.getParameter("contactPhone");
				Datas datas = new Datas();

				if (phone != null && !"".equals(phone) && contactPhone != null
						&& !"".equals(contactPhone)) {

					Map<String, String> map = userDao
							.getUserByPhoneAndContactPhone(phone, contactPhone);
					if (map.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获取人员信息成功");
						datas.setObj(map);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有个人信息数据");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("personInfos".equals(action)) {
				String phone = request.getParameter("phone");
				String contactPhone = request.getParameter("contactPhone");

				Datas datas = new Datas();

				if (phone != null && !"".equals(phone) && contactPhone != null
						&& !"".equals(contactPhone)) {

					List<Map<String, String>> maps = userDao.getUserByPhone(
							phone, contactPhone);
					if (maps.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获取人员信息成功");
						datas.setObj(maps);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有个人信息数据");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}

				out.write(gson.toJson(datas));
			} else if ("getHospitalAddress".equals(action)) {
				String hospitalName = request.getParameter("hospitalName");
				Datas datas = new Datas();
				if (hospitalName != null && !"".equals(hospitalName)) {
					String address = userDao
							.getAddressByHospitalName(hospitalName);
					if (!"".equals(address)) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获得成功");
						Map<String, String> map = new HashMap<String, String>();
						map.put("address", address);
						datas.setObj(map);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有改地址");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}
				out.write(gson.toJson(datas));
			}

			else if ("getTransferPerson".equals(action)) {
				String phone = request.getParameter("phone");
				Datas datas = new Datas();
				if (phone != null && !"".equals(phone)) {
					List<String> transferPersons = userDao
							.getTransferPersonByPhone(phone);
					if (transferPersons.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获得成功");
						datas.setObj(transferPersons);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有转运人");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}
				out.write(gson.toJson(datas));
			}

			else if ("getStartAddress".equals(action)) {
				String phone = request.getParameter("phone");
				Datas datas = new Datas();
				if (phone != null && !"".equals(phone)) {
					List<String> transferPersons = userDao
							.getStartAddressByPhone(phone);
					if (transferPersons.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("获得成功");
						datas.setObj(transferPersons);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("没有转运人");
					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("参数错误");
				}
				out.write(gson.toJson(datas));
			} else if ("usersList".equals(action)) {

				Datas datas = new Datas();

				List<Users> userses = userDao.getUsersList();
				if (userses.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获得成功");
					datas.setObj(userses);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有转运人");
				}

				out.write(gson.toJson(datas));
			} else if ("usersPadList".equals(action)) {

				Datas datas = new Datas();
				String hospitalName = request.getParameter("hospitalName");

				List<Users> userses = userDao.getUsersPadList(hospitalName);
				if (userses.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获得成功");
					datas.setObj(userses);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有转运人");
				}

				out.write(gson.toJson(datas));
			} else if ("opoPadList".equals(action)) {

				Datas datas = new Datas();
				String hospitalName = request.getParameter("hospitalName");

				List<OpoInfo> userses = userDao.getOpoPadList(hospitalName);
				if (userses.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获得成功");
					datas.setObj(userses);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有转运人");
				}

				out.write(gson.toJson(datas));
			} else if ("oposPadList".equals(action)) {

				Datas datas = new Datas();

				List<OpoInfo> userses = userDao.getOposPadList();
				if (userses.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获得成功");
					datas.setObj(userses);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有转运人");
				}

				out.write(gson.toJson(datas));
			}

			else if ("log".equals(action)) {

				Datas datas = new Datas();
				String content = request.getParameter("content");
				userDao.insertLog(content);

			}else if ("loginWeb".equals(action)) {

				Datas datas = new Datas();
				String pwd = request.getParameter("pwd");
				String hospital = request.getParameter("hospital");

				boolean  isTrue = userDao.loginWeb(hospital, pwd);
				if (isTrue) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("登录成功");
					//datas.setObj(userses);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("登录失败");
				}

				out.write(gson.toJson(datas));
			}

		}

		else {
			Datas datas = new Datas();
			datas.setResult(CONST.SEND_FAIL);
			datas.setMsg("action 为null");
			out.write(new Gson().toJson(datas));
			//System.out.println("action 为null");
		}

		out.flush();
		out.close();
	}

}
