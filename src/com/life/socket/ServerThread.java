package com.life.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.life.controller.BoxDao;
import com.life.controller.TransferRecordDao;
import com.life.entity.Record;
import com.life.utils.CRC16M;

public class ServerThread implements Runnable {
	private String boxNoInit = "BN:00000";
	private Socket client = null;
	private int index = 0;

	public ServerThread(Socket client) {
		this.client = client;
	}

	public static int numToDes(int num) {
		int result = 0;
		if (num == 0) {
			result = 48;
		}
		if (num == 1) {
			result = 49;
		}
		if (num == 2) {
			result = 50;
		}
		if (num == 3) {
			result = 51;
		}
		if (num == 4) {
			result = 52;
		}
		if (num == 5) {
			result = 53;
		}
		if (num == 6) {
			result = 54;
		}
		if (num == 7) {
			result = 55;
		}
		if (num == 8) {
			result = 56;
		}
		if (num == 9) {
			result = 57;
		}
		return result;
	}

	public static void main(String[] args) {
		String str = "3018.383771";

	}

	public void run() {
		try {
			// 获取Socket的输出流，用来向客户端发送数据
			PrintStream out = new PrintStream(client.getOutputStream());
			// 获取Socket的输入流，用来接收从客户端发送过来的数据
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			boolean flag = true;
			while (flag) {
				// 接收从客户端发送过来的数据
				String str = buf.readLine();
				if (str == null || "".equals(str)) {
					flag = false;
				} else {
					if ("bye".equals(str)) {
						flag = false;
					} else {

						// 将接收到的字符串前面加上echo，发送到对应的客户端
						// out.println("received successfully");
						System.out.println("rec data:" + str );

						/**
						 * 1、GPS/LBS 2、BN：箱子编号，由5个数字组成
						 * 3、AP:动脉压力，由两个字节组成，DH为高字节，DL为低字节；=DH*256+DL= mmHg
						 * 4、AF:动脉流量，由两个字节组成，DH为高字节，DL为低字节；=DH*256+DL= ml/min
						 * 5、AR:动脉阻滞力，由两个字节组成，DH为高字节，DL为低字节=DH*256+DL=
						 * mmHg/ml/min
						 * 6、VP:静脉压力，由两个字节组成，DH为高字节，DL为低字节；=DH*256+DL= mmHg
						 * 7、VF:静脉流量，由两个字节组成，DH为高字节，DL为低字节；=DH*256+DL= ml/min
						 * 8、VR：静脉阻滞力，由两个字节组成，DH为高字节，DL为低字节；=DH*256+DL=
						 * mmHg/ml/min
						 * 9、CTM:保温盒温度，由两个字节组成，DH为高字节，DL为低字节；=(DH*256+DL)/100= ℃
						 * 10、BTM:电池温度，由两个字节组成，DH为高字节，DL为低字节；=(DH*256+DL)/100= ℃
						 * 11、BP:电池电量，由两个字节组成，DH为高字节，DL为低字节；=(DH*256+DL)/1000= V
						 */
						// +CGPSINFO:
						// 3018.383771,N,12022.063074,E,080319,040808.0,116.2,0.0,133.1OK;
						// BN:60000;AP:#;AF:#;AR:#;VP:#;VF:#;VR:#;CTM:#;BTM:#;BP:#;
						String[] infos = str.split(";");

						String bn = infos[1];
						BoxDao boxDao = new BoxDao();

						if (!boxNoInit.equals(bn)) {
							/**
							 * 如果有新建的转运,获取转运单号,保存到transferRecord
							 * 如果没有未建的转运,获取deviceId(暂用箱号),保存到transferRecord
							 */
							String transferId = boxDao.gainTransfering(bn);
							List<Record> recordList = new ArrayList<Record>();
							Record record = new Record();

							String gps = infos[0];
							if(gps.contains("CLBS")){
								
								//String gspStr[] = gps.split(": ")[1].split(",");

								record.setOther(str);
//								record.setLatitude(gspStr[1]);
//								record.setLongitude(gspStr[2]);
							}else{
							//String gspStr[] = gps.split(": ")[1].split(",");

							//record.setOther(str);
//							record.setLatitude(Double.parseDouble(gspStr[0])
//									/ 100 + "");
//							record.setLongitude(Double.parseDouble(gspStr[2])
//									/ 100 + "");
//							record.setAltitude(gspStr[6]);
							}
							//record.setAltitude(gspStr[6]);

							String ap = infos[2];
//							String af = infos[3];
//							String ar = infos[4];
//							String vp = infos[5];
//							String vf = infos[6];
//							String vr = infos[7];
//							String ctm = infos[8];
//							String btm = infos[9];
//							String bp = infos[10];
							System.out.println("ap:" + ap);
							// new
							// TransferRecordDao().insertRecordsHigh(recordList,"");

						} else if (boxNoInit.equals(bn)) {

							/**
							 * 获取high_box中未使用的箱号,并标为已使用
							 * 
							 */
							int boxNo = boxDao.gainHighBox();
							if (boxNo > 0) {
								int no5 = numToDes(boxNo % 10);
								int no4 = numToDes(boxNo / 10 % 10);
								int no3 = numToDes(boxNo / 100 % 10);
								int no2 = numToDes(boxNo / 1000 % 10);
								int no1 = numToDes(boxNo / 10000 % 10);

								// 双字节CRC32校验和，SUM=HEND+LEN+BN+COMND+CMD。
								int[] crcBytes = new int[9];
								crcBytes[0] = 0x7b;
								crcBytes[1] = 0x09;
								crcBytes[2] = no1;
								crcBytes[3] = no2;
								crcBytes[4] = no3;
								crcBytes[5] = no4;
								crcBytes[6] = no5;
								crcBytes[7] = 0x01;
								crcBytes[8] = 0x01;
								int powerCrc = 0x7b + 0x09 + no1 + no2 + no3
										+ no4 + no5 + 0x01 + 0x01;

								// int powerCrc = new CRC16M().updateCheckInt(
								// crcBytes, 9);

								byte[] buffer = new byte[12];
								// 7b
								buffer[0] = 0x7b;
								// len
								buffer[1] = 0x09;
								// bn
								buffer[2] = (byte) no1;
								buffer[3] = (byte) no2;
								buffer[4] = (byte) no3;
								buffer[5] = (byte) no4;
								buffer[6] = (byte) no5;
								// comnd
								buffer[7] = 0x01;
								// cmd
								buffer[8] = 0x01;
								// sum
								buffer[9] = (byte) ((powerCrc & 0xFF00) >> 8);
								buffer[10] = (byte) (powerCrc & 0x00FF);
								buffer[11] = 0x7d;
								out.write(buffer);

								// Head LEN BN COMND CMD SUM END
								// 0X7B 0x7d

							}
						}

					}
				}
			}
			out.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
