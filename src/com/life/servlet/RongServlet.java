package com.life.servlet;

import io.rong.Example;
import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.models.CodeSuccessResult;
import io.rong.models.GroupUserQueryResult;
import io.rong.models.TokenResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.RongDao;
import com.life.controller.UserDao;
import com.life.entity.Contact;
import com.life.entity.Datas;
import com.life.entity.Group;
import com.life.entity.RongUser;
import com.life.entity.RongUserJson;
import com.life.entity.TokenJson;
import com.life.entity.Users;
import com.life.utils.CONST;

public class RongServlet extends HttpServlet {

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

		RongDao rongDao = new RongDao();

		Reader reader = null;
		Datas datas = new Datas();
		Gson gson = new Gson();
		// String appKey = "p5tvi9dsp4od4";
		// String appSecret = "JH0s5aQ1Vc";

		// 正式
		String appKey = "n19jmcy5nety9";
		String appSecret = "SdwTI3aFmYb";
		RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
		if ("token".equals(action)) {
			String userId = request.getParameter("userId");
			String userName = request.getParameter("userName");
			String photoUrl = request.getParameter("photoUrl");
			//System.out.println("rong.do");
			//System.out.println("userId" + userId);
			//System.out.println("userName" + userName);
			//System.out.println("photoUrl" + photoUrl);

			// 获取 Token 方法
			try {

				boolean isTrue = rongDao.isExistToken(userId);

				if (!isTrue) {
					TokenResult userGetTokenResult = rongCloud.user.getToken(
							userId, userName, photoUrl);

					TokenJson tokenJson = gson.fromJson(userGetTokenResult
							.toString(), TokenJson.class);
					//System.out.println("false,token:" + tokenJson);
					if (null != tokenJson && tokenJson.getCode() == 200) {
						String tokenString = tokenJson.getToken();
						int isOK = rongDao.insertUserPhone(userId, userName,
								tokenString, userId);
						if (isOK == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("返回token成功");
							Map<String, String> maps = new HashMap<String, String>();
							maps.put("token", tokenString);
							datas.setObj(maps);
						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("返回token失败,数据库错误.");
						}
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("返回token失败,融云错误.");
					}

				} else {
					String tokenIsExist = rongDao.getTokenByUserId(userId);
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("返回token成功,已存在token.");
					Map<String, String> maps = new HashMap<String, String>();
					maps.put("token", tokenIsExist);
					datas.setObj(maps);

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("返回token失败," + e.getMessage());

				e.printStackTrace();
			}
		} else if ("getToken".equals(action)) {
			String phone = request.getParameter("phone");
			if (phone != null && !"".equals(phone)) {

				String token = rongDao.getTokenByPhone(phone);
				if (token != null) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取token成功");
					Map<String, String> map = new HashMap<String, String>();
					map.put("token", token);
					datas.setObj(map);

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("token 为null");

				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		} else if ("isNewGroup".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			if (organSeg != null && !"".equals(organSeg)) {
				//System.out.println("isNewGroupOrganSeg:" + organSeg);
				String groupName = rongDao.getTopByGroupId(organSeg);
				if (groupName != null) {
					//System.out.println("isNewGroup:" + groupName);
					if (groupName.contains("转运中") || groupName.contains("待转运")) {
						//System.out.println("isNewGroup:OK");
						datas.setResult(CONST.SEND_OK);
						datas.setMsg(groupName);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("token 为null");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("token 为null");

				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		} else if ("getGroupName".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			if (organSeg != null && !"".equals(organSeg)) {
				//System.out.println("isNewGroupOrganSeg:" + organSeg);
				String groupName = rongDao.getTopByGroupId(organSeg);
				if (groupName != null) {

					//System.out.println("isNewGroup:OK");
					datas.setResult(CONST.SEND_OK);
					datas.setMsg(groupName);

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("token 为null");

				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		} else if ("createContact".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String usersIds = request.getParameter("usersIds");
			String type = request.getParameter("type");

			if (organSeg != null && !"".equals(organSeg) && usersIds != null
					&& !"".equals(usersIds) && type != null && !"".equals(type)) {
				boolean isTransfer = false;
				if ("temp".equals(type)) {
					isTransfer = rongDao.isTransferByOrganSeg(organSeg);
				} else if ("normal".equals(type)) {
					isTransfer = false;
				}
				// 判断是否存在器官段号

				if (!isTransfer) {

					boolean isTransferGroup = rongDao
							.isTransferGroupByOrganSeg(organSeg);
					// 存在联系人,更新
					if (isTransferGroup) {
						int update = rongDao.updateTransferGroup(organSeg,
								usersIds);
						if (update == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("更新userIds成功");
						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("更新不成功:" + CONST.ERROR);
						}

					} else {
						int insert = rongDao.insertTransferGroup(organSeg,
								usersIds);
						if (insert == 1) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("插入userIds成功");
						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("插入不成功:" + CONST.ERROR);
						}

					}
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("已存在该器官状态");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		} else if ("getContact".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String phone = request.getParameter("phone");
			//System.out.println("rong.do  getContact params:organSeg:" + organSeg + ",phone:" + phone);
			if (organSeg != null && !"".equals(organSeg) && phone != null
					&& !"".equals(phone)) {

				String usersIdsStr = rongDao
						.getTransferGroupByOrganSeg(organSeg);
				String usersIdsArr[] = usersIdsStr.split("=");
				if (!"".equals(usersIdsArr[0])) {
					String[] usersIdsArray = usersIdsArr[0].split(",");
					List<Users> userses = new ArrayList<Users>();
					if ("LP20180103131749".equals(organSeg)) {
						Users users = new Users();
						users.setName("医生A");
						users.setTrueName("医生A");
						users.setPostRole("转");
						users.setPhone("18999999999");
						userses.add(users);
						users = new Users();
						users.setName("协调员A");
						users.setTrueName("协调员A");
						users.setPostRole("协");
						users.setPhone("18398888888");
						userses.add(users);
						users = new Users();
						users.setName("OPO");
						users.setTrueName("OPO");
						users.setPostRole("OPO");
						users.setPhone("15333333333");
						userses.add(users);
					} else {
						for (int i = 0; i < usersIdsArray.length; i++) {
							Users users = new UserDao().getUsersById(
									usersIdsArray[i], organSeg);
							if (i == 0) {
								users.setPostRole("转");

							}
							if (i == 1) {
								users.setPostRole("协");

							}
							if (i == 2) {
								users.setPostRole("OPO");

							}
							if (i == 0 && usersIdsArray.length == 2) {
								users.setPostRole("协");
							}
							if (i == 1 && usersIdsArray.length == 2) {
								users.setPostRole("OPO");
							}
							if (users != null) {
								if (phone.equals(users.getPhone())) {
									users.setIsCreate("0");
								} else {
									users.setIsCreate("1");
								}
								userses.add(users);
							}
						}

					}

					if (userses.size() > 0) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg(usersIdsArr[2]);
						datas.setObj(userses);
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("获取userIds失败");
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有联系人:" + CONST.ERROR);
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 获取群组
		else if ("getGroupList".equals(action)) {
			String phone = request.getParameter("phone");
			String pageStr= request.getParameter("page");
			String pageSizeStr = request.getParameter("pageSize");
			int page = 0;
			int pageSize = 10000;
			if(pageStr !=null ){
				page = Integer.parseInt(pageStr);
			}
			
			if(pageSizeStr != null){
				pageSize = Integer.parseInt(pageSizeStr);
			}

			if (phone != null && !"".equals(phone)) {

				List<Group> groups = rongDao.getGroupByPhone(phone,page,pageSize);

				if (groups.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取所有群组成功");
					datas.setObj(groups);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有群组人员");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 获取群组成员的头像信息
		else if ("getGroupInfoList".equals(action)) {
			String organSeg = request.getParameter("organSeg");

			if (organSeg != null && !"".equals(organSeg)) {

				List<Contact> groups = rongDao.getGroupByOrganSeg(organSeg);
				//System.out.println("groups:" + groups.size());
				if (groups.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取所有群组成功");
					datas.setObj(groups);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有群组人员");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 获取群组名称
		else if ("getGroup".equals(action)) {
			String organSeg = request.getParameter("organSeg");

			if (organSeg != null && !"".equals(organSeg)) {

				String groupName = rongDao.getGroupNameByOrganSeg(organSeg);
				if (!"".equals(groupName)) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg(groupName);

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("没有群组人员");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 加入群组
		else if ("addGroup".equals(action)) {
			String usersIds = request.getParameter("usersIds");
			String organSeg = request.getParameter("organSeg");
			String groupName = request.getParameter("groupName");

			if (usersIds != null && !"".equals(usersIds) && organSeg != null
					&& !"".equals(organSeg) && groupName != null
					&& !"".equals(groupName)) {
				String[] usersIdsArray = usersIds.split(",");

				try {
					CodeSuccessResult groupJoinResult = rongCloud.group.join(
							usersIdsArray, organSeg, groupName);
					if (groupJoinResult.toString().contains("200")) {
						int update = rongDao.updateTransferGroup(organSeg,
								usersIds);

						datas.setResult(CONST.SEND_OK);
						datas.setMsg("更新userIds成功,加入群组");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("加入群组失败");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 解散群组
		else if ("deleteGroup".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String phone = request.getParameter("phone");

			if (organSeg != null && !"".equals(organSeg) && phone != null
					&& !"".equals(phone)) {

				boolean isTrue = rongDao.isGroupByPhoneAndOrganSeg(phone,
						organSeg);
				isTrue = true;
				if (isTrue) {
					// 改变群组状态
					rongDao.chanceGroupStatus(organSeg);
					// int id = new UserDao().getUserIdByPhone(phone);

					// // 解散群组方法。（将该群解散，所有用户都无法再接收该群的消息。）
					try {
						CodeSuccessResult groupDismissResult = rongCloud.group
								.dismiss(phone + "", organSeg);
						if (groupDismissResult.toString().contains("200")) {
							datas.setResult(CONST.SEND_OK);
							datas.setMsg("解散群组成功");
							rongDao.deleteTransferGroup(organSeg);
						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("解散失败"
									+ groupDismissResult.getErrorMessage());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("解散失败:" + e.getMessage());
					}

				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("该成员不是创建者");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 获取群组人员
		else if ("getInnerGroup".equals(action)) {
			String organSeg = request.getParameter("organSeg");

			if (organSeg != null && !"".equals(organSeg)) {

				// 查询成员
				try {
					GroupUserQueryResult groupQueryUserResult = rongCloud.group
							.queryUser(organSeg);
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取群组人员成功");
					datas.setObj(groupQueryUserResult.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("获取群组人员错误:" + e.getMessage());
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 退出群组
		else if ("exitGroup".equals(action)) {
			String usersIds = request.getParameter("usersIds");
			String organSeg = request.getParameter("organSeg");

			if (usersIds != null && !"".equals(usersIds) && organSeg != null
					&& !"".equals(organSeg)) {

				String[] usersIdsArray = usersIds.split(",");
				CodeSuccessResult groupQuitResult;
				try {
					groupQuitResult = rongCloud.group.quit(usersIdsArray,
							organSeg);
					if (groupQuitResult.toString().contains("200")) {
						String oldUsersIds = rongDao
								.getUsersIdsByOrganSeg(organSeg);
						String newUsersIds = "";
						String[] oldUsersIdsArray = oldUsersIds.split(",");
						for (int i = 0; i < oldUsersIdsArray.length; i++) {
							if (usersIds.contains(oldUsersIdsArray[i])) {

							} else {
								newUsersIds += oldUsersIdsArray[i];
								if (i != oldUsersIdsArray.length) {
									newUsersIds += ",";
								}
							}

						}
						rongDao.updateTransferGroup(organSeg, newUsersIds);

						datas.setResult(CONST.SEND_OK);
						datas.setMsg("退出群组成功");

					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("退出群组错误:" + groupQuitResult.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("退出群组错误:" + e.getMessage());
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}

		else if ("createGroup".equals(action)) {

			String usersIds = request.getParameter("usersIds");
			String groupName = request.getParameter("groupName");
			String organSeg = request.getParameter("organSeg");
			String phone = request.getParameter("phone");
			if (usersIds != null && !"".equals(usersIds) && groupName != null
					&& !"".equals(groupName) && organSeg != null
					&& !"".equals(organSeg) && phone != null
					&& !"".equals(phone)) {
				try {
					// 插入transfer_group_temp
					boolean isTransferGroup = rongDao
							.isTransferGroupByOrganSeg(organSeg);
					// 存在联系人,更新
					if (isTransferGroup) {

						String[] usersIdsArray = usersIds.split(",");
						// usersIds 1 3 6 gruopid 1 groupName 测试1
						// 创建
						CodeSuccessResult groupCreateResult = rongCloud.group
								.create(usersIdsArray, organSeg, groupName);
						String result = groupCreateResult.toString();
						if (result.contains("200")) {
							int insert = rongDao.updateTransferGroup(organSeg,
									usersIds, groupName, phone);
							if (insert == 1) {
								datas.setResult(CONST.SEND_OK);
								datas.setMsg("更新userIds和创建群组成功成功");

							} else {
								datas.setResult(CONST.SEND_FAIL);
								datas.setMsg("创建群组成功,更新usersId不成功:"
										+ CONST.ERROR);
							}

						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("创建群组失败" + result);
						}

					} else {

						String[] usersIdsArray = usersIds.split(",");
						// usersIds 1 3 6 gruopid 1 groupName 测试1
						// 创建
						CodeSuccessResult groupCreateResult = rongCloud.group
								.create(usersIdsArray, organSeg, groupName);
						String result = groupCreateResult.toString();
						if (result.contains("200")) {
							int insert = rongDao.insertTransferGroup(organSeg,
									usersIds, groupName, phone);
							if (insert == 1) {
								datas.setResult(CONST.SEND_OK);
								datas.setMsg("插入userIds和创建群组成功成功");

							} else {
								datas.setResult(CONST.SEND_FAIL);
								datas.setMsg("创建群组成功,插入usersId不成功:"
										+ CONST.ERROR);
							}

						} else {
							datas.setResult(CONST.SEND_FAIL);
							datas.setMsg("创建群组失败" + result);
						}

					}

					// 刷新群成员
					// CodeSuccessResult groupRefreshResult =
					// rongCloud.group.refresh("1", "测试1");
					// 查询成员
					// GroupUserQueryResult groupQueryUserResult =
					// rongCloud.group.queryUser("1");

					// System.out.println("dismiss:  " +
					// groupDismissResult.toString());
					// System.out.println("queryUser:  " +
					// groupQueryUserResult.toString());
					// // 将用户加入指定群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，每个群最大至 3000 人。
					// String[] groupJoinUserId =
					// {"userId2","userid3","userId4"};
					// CodeSuccessResult groupJoinResult =
					// rongCloud.group.join(groupJoinUserId, "groupId1",
					// "TestGroup");
					// System.out.println("join:  " +
					// groupJoinResult.toString());
					// System.out.println("create:  " +
					// groupQueryUserResult.toString());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");
			}
		} else if ("sendGroupMessage".equals(action)) {
			String phone = request.getParameter("phone");
			String organSeg = request.getParameter("organSeg");

			// // 发送群组消息方法（以一个用户身份向群组发送消息，单条消息最大 128k.每秒钟最多发送 20 条消息，每次最多向 3
			// 个群组发送，如：一次向 3 个群组发送消息，示为 3 条消息。）
			String[] messagePublishGroupToGroupId = { organSeg };
			TxtMessage messagePublishGroupTxtMessage = new TxtMessage(
					"【系统消息】转运已经准备好。", "helloExtra");
			CodeSuccessResult messagePublishGroupResult = null;
			try {
				messagePublishGroupResult = rongCloud.message.publishGroup(
						phone, messagePublishGroupToGroupId,
						messagePublishGroupTxtMessage, "【系统消息】转运已经准备好。",
						"{\"pushData\":\"【系统消息】转运已经准备好。\"}", 1, 1, 1);
				messagePublishGroupTxtMessage = new TxtMessage("本次转运已创建完成。",
						"helloExtra");
				messagePublishGroupResult = rongCloud.message.publishGroup(
						phone, messagePublishGroupToGroupId,
						messagePublishGroupTxtMessage, "本次转运已创建完成。",
						"{\"pushData\":\"本次转运已创建完成。\"}", 1, 1, 1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("publishGroup:  " + messagePublishGroupResult.toString());

		} else if ("sendGroupMsg".equals(action)) {
			String phone = request.getParameter("phone");
			String organSeg = request.getParameter("organSeg");
			String content = request.getParameter("content");
			if (content == null || "".equals(content.trim())) {
				return;
			}

			// // 发送群组消息方法（以一个用户身份向群组发送消息，单条消息最大 128k.每秒钟最多发送 20 条消息，每次最多向 3
			// 个群组发送，如：一次向 3 个群组发送消息，示为 3 条消息。）
			String[] messagePublishGroupToGroupId = { organSeg };
			TxtMessage messagePublishGroupTxtMessage = new TxtMessage(content,
					"helloExtra");
			CodeSuccessResult messagePublishGroupResult = null;
			try {
				messagePublishGroupResult = rongCloud.message.publishGroup(
						phone, messagePublishGroupToGroupId,
						messagePublishGroupTxtMessage, content,
						"{\"pushData\":\"" + content + "\"}", 1, 1, 1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.out.println(e.getMessage() + ",");
			}
			if (messagePublishGroupResult != null
					&& messagePublishGroupResult.toString().contains("200")) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("成功发送");
			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("发送失败");
			}

		} else if ("sendGroupMessageStart".equals(action)) {
			String phone = request.getParameter("phone");
			String organSeg = request.getParameter("organSeg");

			// // 发送群组消息方法（以一个用户身份向群组发送消息，单条消息最大 128k.每秒钟最多发送 20 条消息，每次最多向 3
			// 个群组发送，如：一次向 3 个群组发送消息，示为 3 条消息。）
			String[] messagePublishGroupToGroupId = { organSeg };
			TxtMessage messagePublishGroupTxtMessage = new TxtMessage(
					"本次转运已开始，器官已成功放入转运箱，请各位配合并密切关注转运情况，确保器官高质转到，谢谢！",
					"helloExtra");
			CodeSuccessResult messagePublishGroupResult = null;
			try {
				messagePublishGroupResult = rongCloud.message
						.publishGroup(
								phone,
								messagePublishGroupToGroupId,
								messagePublishGroupTxtMessage,
								"本次转运已开始，器官已成功放入转运箱，请各位配合并密切关注转运情况，确保器官高质转到，谢谢！",
								"{\"pushData\":\"本次转运已开始，器官已成功放入转运箱，请各位配合并密切关注转运情况，确保器官高质转到，谢谢！\"}",
								1, 1, 1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("publishGroup:  " + messagePublishGroupResult.toString());

		}
		// 获取group信息,单个
		else if ("getGroupOrganSeg".equals(action)) {

			String organSeg = request.getParameter("organSeg");

			if (organSeg != null && !"".equals(organSeg)) {
				boolean isTrue = rongDao.isTransferGroupByOrganSeg(organSeg);
				if (isTrue) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("ok");
				} else {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("错误");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		// 获取group信息,单个  是否已经插入了
		else if ("getGroupInfoOrganSeg".equals(action)) {

			String organSeg = request.getParameter("organSeg");

			if (organSeg != null && !"".equals(organSeg)) {
				String phones = rongDao.GroupPhonesByOrganSeg(organSeg);
				if (phones != null) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg(phones);
				} else {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("错误");
				}

			} else {

				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");

			}
		}
		
 
		
		// 判断是否该用户是否在群组中
		else if ("isGroupUser".equals(action)) {

			String organSeg = request.getParameter("organSeg");
			String phone = request.getParameter("phone");

			GroupUserQueryResult groupQueryUserResult;
			try {
				groupQueryUserResult = rongCloud.group.queryUser(organSeg);
				//System.out.println(groupQueryUserResult.toString());
				if (groupQueryUserResult.toString().contains(phone)) {
					RongUserJson rongUserJson =new Gson().fromJson(groupQueryUserResult.toString(), RongUserJson.class);
					List<RongUserJson.UsersBean> userBeans = rongUserJson.getUsers();
					String codition = phone;
					for(int i=0;i<userBeans.size();i++){
						 codition += ","+userBeans.get(i).getId();
					}
					List<RongUser> usersList = new UserDao().getUsersListByPhones(codition);
					datas.setObj(usersList);
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("存在该群组");
				} else {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("不存在");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} 
		// 判断该用户所在医院的所有人员
		else if ("getUserHospital".equals(action)) {

			 
			String phone = request.getParameter("phone");

		 
			try {
			 
				 
					List<RongUser> usersList = new UserDao().getUsersListByPhoneHospital(phone);
					datas.setObj(usersList);
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("存在该群组");
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else {

			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("action 错误");

		}
		out.write(new Gson().toJson(datas));
		out.flush();
		out.close();
	}

	public static void main(String[] args) throws Exception {
		String appKey = "p5tvi9dsp4od4";// 
		String appSecret = "JH0s5aQ1Vc";// 
		RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
		String photoUrl = "http://192.168.31.239:8080/transbox/images/start.png";
		String userId = "18398850872";
		String userName = "陈杨";
		TokenResult userGetTokenResult = rongCloud.user.getToken(userId,
				userName, photoUrl);
		//System.out.println(userGetTokenResult);
	}
}
