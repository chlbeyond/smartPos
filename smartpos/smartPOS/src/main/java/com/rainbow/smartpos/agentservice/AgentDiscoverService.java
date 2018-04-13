package com.rainbow.smartpos.agentservice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import com.rainbow.smartpos.login.LoginActivity;

import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

public class AgentDiscoverService extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "Discovery";
	public static final int WHO_IS_AGENT = 1;
	private static final String REMOTE_KEY = "b0xeeRem0tE!";
	public static final int AGENT_SERVICE_PORT = 2562;
	private static final int TIMEOUT_MS = 500;
	String responseText;

	// TODO: Vary the challenge, or it's not much of a challenge :)
	private static final String mChallenge = "sanyipos";
	private WifiManager mWifi;
	String ip;

	interface DiscoveryReceiver {
		void addAnnouncedServers(InetAddress[] host, int port[]);
	}

	public AgentDiscoverService(WifiManager wifi) {
//		mWifi = wifi;
//		WifiInfo wifiInf = mWifi.getConnectionInfo();
//		int ipAddress = wifiInf.getIpAddress();
//		ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
//				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
//				(ipAddress >> 24 & 0xff));
		ip = getIpAddress();

	}
	public static String getIpAddress() { 
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        String ipAddress=inetAddress.getHostAddress().toString();
                        Log.e("IP address",""+ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
        }
        return null; 
}
	@Override
	protected Void doInBackground(Void... params) {
		try {
			// socket.setSoTimeout(TIMEOUT_MS);

			listenForResponses();

		} catch (IOException e) {
			Log.e(TAG, "Could not send discovery request", e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Send a broadcast UDP packet containing a request for boxee services to
	 * announce themselves.
	 * 
	 * @throws IOException
	 */

	/**
	 * Calculate the broadcast IP we need to send the packet along. If we send
	 * it to 255.255.255.255, it never gets sent. I guess this has something to
	 * do with the mobile network not wanting to do broadcast.
	 */
	private InetAddress getBroadcastAddress() throws IOException {

		DhcpInfo dhcp = mWifi.getDhcpInfo();
		if (dhcp == null) {
			Log.d(TAG, "Could not get dhcp info");
			return null;
		}

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	/**
	 * Listen on socket for responses, timing out after TIMEOUT_MS
	 * 
	 * @param socket
	 *            socket on which the announcement request was sent
	 * @throws IOException
	 * @throws JSONException
	 */
	private void listenForResponses() throws IOException, JSONException {
		DatagramSocket serverSocket = new DatagramSocket(AGENT_SERVICE_PORT);
		serverSocket.setBroadcast(true);
		byte[] buf = new byte[1024];
		String sendData = String.format("{\"agent\":\"%s\"}", ip);
		try {
			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				serverSocket.receive(packet);
				String result = new String(packet.getData(), 0,
						packet.getLength(), "UTF-8");
				JSONObject obj = new JSONObject(result);
				if (WHO_IS_AGENT == obj.getInt("request_type")) {
					DatagramPacket sendPacket = new DatagramPacket(
							sendData.getBytes(), sendData.getBytes().length,
							packet.getAddress(), packet.getPort());
//					boolean isAgent = LoginActivity.settings.getBoolean(
//							"is_master", false);

//					if (isAgent) {
//						Log.d(TAG, "Send response " + sendData);
//						serverSocket.send(sendPacket);
//					}

				}

				Log.d(TAG, "Received request " + result);
			}
		} catch (SocketTimeoutException e) {
			Log.d(TAG, "Receive timed out");
		}
	}

	/**
	 * Calculate the signature we need to send with the request. It is a string
	 * containing the hex md5sum of the challenge and REMOTE_KEY.
	 * 
	 * @return signature string
	 */
	private String getSignature(String challenge) {
		MessageDigest digest;
		byte[] md5sum = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(challenge.getBytes());
			digest.update(REMOTE_KEY.getBytes());
			md5sum = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		StringBuffer hexString = new StringBuffer();
		for (int k = 0; k < md5sum.length; ++k) {
			String s = Integer.toHexString((int) md5sum[k] & 0xFF);
			if (s.length() == 1)
				hexString.append('0');
			hexString.append(s);
		}
		return hexString.toString();
	}

}
