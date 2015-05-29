package com.linux.vshow;

import java.io.File;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {
	private Socket client = null;

	public String sendMsg(String msg) {
		if (client == null) {
			connect();
		}
		try {
			if (client != null) {
				byte[] buffer = new byte[40950];
				client.getOutputStream().write(msg.getBytes());
				client.getOutputStream().flush();
				client.getInputStream().read(buffer);
				Constant.G3GTOTAL = 0;
				
//				Constant.li.writeLog("0000 服务器连接正常");
				
				return new String(buffer).trim();
			}
		} catch (Exception e) {
			closeSocket();
		}
		return "";
	}

	private void connect() {
		client = new Socket();
		try {
			client.setSoLinger(true, 0);
			client.setTcpNoDelay(true);
			client.setTrafficClass(0x04 | 0x10);
			client.setKeepAlive(true);
			InetSocketAddress isa = new InetSocketAddress(Constant.SRVIP, 8511);
			client.connect(isa, (Constant.lian - 2) * 1000);
			client.setSoTimeout((Constant.lian - 2) * 1000);
		} catch (Exception e1) {
			Constant.li.writeLog("0000 服务器连接失败");
			closeSocket();
		}
	}

	// 判断服务器Ip是否连通
	public boolean connect(String srvip) {
		closeSocket();
		client = new Socket();
		try {
			client.setSoLinger(true, 0);
			client.setTcpNoDelay(true);
			client.setTrafficClass(0x04 | 0x10);
			client.setKeepAlive(true);
			InetSocketAddress isa = new InetSocketAddress(srvip, 8511);
			client.connect(isa, (Constant.lian - 2) * 1000);
			return true;
		} catch (Exception e1) {
			closeSocket();
			return false;
		}
	}

	public void closeSocket() {
		Constant.G3GTOTAL++;
		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {

			}
		}
		client = null;

		if (Constant.G3G) {
			if (Constant.G3GTOTAL > 14) {
				Constant.G3GTOTAL = 0;
				FileWriter fw = null;
				try {
					fw = new FileWriter(new File("/sys/class/gpio_sw/PH6/data"));
					fw.write("0");
					fw.flush();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					try {
						fw.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					fw = new FileWriter(new File("/sys/class/gpio_sw/PH6/data"));
					fw.write("1");
					fw.flush();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					try {
						fw.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			}

		}

	}
}
