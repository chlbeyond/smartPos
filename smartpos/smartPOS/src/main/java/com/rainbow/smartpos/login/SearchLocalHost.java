package com.rainbow.smartpos.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SearchLocalHost {

	public List<String> hosts = new ArrayList<String>();
	public String host = "192.168.1.";

	public interface SearchFinish {
		void searchFinish(List<String> hosts);
	}

	public SearchLocalHost(SearchFinish sf) {
		for (int i = 1; i < 256; i++) {
			new MyThread(host + i).start();
			if (i == 255) {
				sf.searchFinish(hosts);
			}
		}
	}

	public List<String> getHosts() {
		return hosts;
	}

	class MyThread extends Thread {
		String ip = null;

		public MyThread(String ip) {
			super();
			this.ip = ip;
		}

		@Override
		public void run() {
			super.run();
			try {
				InetAddress ia = InetAddress.getByName(ip);
				boolean bool = ia.isReachable(1500);
				if (bool) {
					hosts.add(ip);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
