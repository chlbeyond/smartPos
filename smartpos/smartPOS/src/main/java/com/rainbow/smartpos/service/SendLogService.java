package com.rainbow.smartpos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.rainbow.smartpos.Restaurant;
import com.sanyipos.sdk.core.AgentRequests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SendLogService extends Service {

	public static class SendLogServiceBinder extends Binder {

		private SendLogService service;

		public SendLogServiceBinder(SendLogService service) {
			this.service = service;
		}

		public SendLogService getService() {
			return service;
		}
	}

	private static final int BUFFER_SIZE = 1024;
	private Timer timer = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new SendLogServiceBinder(this);
	}

	public void startSend() {
		if (timer == null) {
			final String url = AgentRequests.port_9090_url + Restaurant.uploadLogUrl;
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					String inputFile = Restaurant.operationLogTxtName;
					String outFile = inputFile.substring(0, inputFile.length() - 4) + ".zip";
					try {
						if (new File(inputFile).exists()) {
							synchronized (Restaurant.operationLogTxtName) {
								zip(new String[] { inputFile }, outFile);
								// new File(inputFile).delete();
								new File(inputFile).renameTo(new File(inputFile + "." + System.currentTimeMillis()));
							}
							upload(outFile, url);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// TODO Auto-generated method stub
				}
				// }, 1000 * 60, 1000 * 60 * 5);
			}, 0, 1000 * 60);
		}
	}

	public static void zip(String[] files, String zipFile) throws IOException {
		BufferedInputStream origin = null;
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		try {
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				try {
					ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {

						out.write(data, 0, count);
					}
				} finally {
					origin.close();
				}
			}
		} finally {
			out.close();
		}
	}

	protected void upload(String path, String url) {
		try {
			String BOUNDARY = "" + System.currentTimeMillis(); // 定义数据分隔线
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			conn.setRequestProperty("sanyi-shop-id", Long.toString(Restaurant.shopId));
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			File file = new File(path);
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file0\";filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			// out.write("\r\n".getBytes()); //多个文件时，二个文件之间加入这个
			in.close();
			out.write(end_data);
			out.flush();
			out.close();

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String s = reader.readLine();
			String line = null;
//			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
			file.delete();
		} catch (Exception e) {
//			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
	}

	private void copy(File src, OutputStream output) throws IOException {
		InputStream in = new FileInputStream(src);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			output.write(buf, 0, len);
		}
		in.close();
	}

	public void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {

		}
	}
}
