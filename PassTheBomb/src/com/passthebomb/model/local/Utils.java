package com.passthebomb.model.local;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;


public class Utils {
	/**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    
    public static boolean uploadIP() {
    	String url = "jdbc:mysql://108.174.147.136:3306/apprevol_passthebomb";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "apprevol_bomb";
        String password = "passthebomb";
        try {
        	Class.forName(driver);
        	Connection conn = DriverManager.getConnection(url,userName,password);
        	System.out.println("Success");
        	conn.close();
        } catch (Exception e) {
        	System.out.println("Failed");
        	e.printStackTrace();
        	return false;
        }
        return true;
    }
    
    public static void main (String[] args) {
    	uploadIP();
    }
}
