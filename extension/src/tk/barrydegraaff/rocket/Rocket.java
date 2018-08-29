/*

Copyright (C) 2018  Barry de Graaff

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.

API implementation can be reached at:
https://zimbraserver/service/extension/rocket?test=true

*/

package tk.barrydegraaff.rocket;


import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.common.service.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Cos;
import org.json.JSONArray;
import org.json.JSONObject;

public class Rocket extends ExtensionHttpHandler {

    /**
     * The path under which the handler is registered for an extension.
     *
     * @return path
     */
    @Override
    public String getPath() {
        return "/rocket";
    }

    private String adminAuthToken;
    private String adminUserId;
    private String adminUserName;
    private String adminPassword;
    private String rocketURL;

    /**
     * Processes HTTP POST requests.
     *
     * @param req  request message
     * @param resp response message
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.getOutputStream().print("tk.barrydegraaff.docconvert is installed. HTTP POST method is not supported");
    }

    /**
     * Processes HTTP GET requests.
     *
     * @param req  request message
     * @param resp response message
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String authTokenStr = null;
        //Just read a cos value to see if its a valid user
        try {
            Cookie[] cookies = req.getCookies();
            for (int n = 0; n < cookies.length; n++) {
                Cookie cookie = cookies[n];

                if (cookie.getName().equals("ZM_AUTH_TOKEN")) {
                    authTokenStr = cookie.getValue();
                    break;
                }
            }

            Account account = null;

            if (authTokenStr != null) {
                AuthToken authToken = AuthToken.getAuthToken(authTokenStr);
                Provisioning prov = Provisioning.getInstance();
                Account acct = Provisioning.getInstance().getAccountById(authToken.getAccountId());
                Cos cos = prov.getCOS(acct);
                Set<String> allowedDomains = cos.getMultiAttrSet(Provisioning.A_zimbraProxyAllowedDomains);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Please authenticate first");
                resp.getWriter().flush();
                resp.getWriter().close();
                return;
            }

        } catch (Exception ex) {
            //crafted cookie? get out you.
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Please authenticate first");
            resp.getWriter().flush();
            resp.getWriter().close();
            return;
        }

        final Map<String, String> paramsMap = new HashMap<String, String>();

        if (req.getQueryString() != null) {
            String[] params = req.getQueryString().split("&");
            for (String param : params) {
                String[] subParam = param.split("=");
                paramsMap.put(subParam[0], subParam[1]);
            }
        } else {
            //do nothing
            return;
        }

        //Initializes adminAuthToken, adminUserId, adminUserName, adminPassword, rocketURL on this instance
        this.initializeRocketAPI();
        String test;
        test = this.setUserAuthToken("o9DnAsxa4eHwwLQ7M");

        resp.addHeader("Content-Type", "application/pdf");
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + this.adminAuthToken + ".\""+this.adminUserId+ ".\""+test);
        resp.addHeader("Accept-Ranges", "none");
    }



    private String getAdminUsername() {
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream("/opt/zimbra/lib/ext/rocket/config.properties");
            prop.load(input);

            return prop.getProperty("adminuser");
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    private String getAdminPassword() {
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream("/opt/zimbra/lib/ext/rocket/config.properties");
            prop.load(input);

            return prop.getProperty("adminpassword");
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    private String getRocketURL() {
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream("/opt/zimbra/lib/ext/rocket/config.properties");
            prop.load(input);

            return prop.getProperty("rocketurl");
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    /** Implements: https://beta.zetalliance.org:443/api/v1/login
     * Since we do not store the token, we may run into:
     * Delete obsolete tokens every 1 hour #7812
     * https://github.com/RocketChat/Rocket.Chat/pull/7812
     * One work-around would be calling the logout api.
     */
    public void initializeRocketAPI() {
        HttpURLConnection connection = null;
        String inputLine;
        StringBuffer response = new StringBuffer();
        this.adminUserName = this.getAdminUsername();
        this.adminPassword = this.getAdminPassword();
        this.rocketURL = this.getRocketURL();
        try {

            String urlParameters  = "username="+this.adminUserName+"&password="+this.adminPassword;
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL    url            = new URL( this.rocketURL + "/api/v1/login" );
            connection= (HttpURLConnection) url.openConnection();
            connection.setDoOutput( true );
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            connection.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
                wr.write( postData );
            }

            if (connection.getResponseCode() == 200) {
                // get response stream
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                // feed response into the StringBuilder
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Start parsing
                JSONObject obj = new JSONObject(response.toString());
                String authToken = obj.getJSONObject("data").getString("authToken");
                String userId = obj.getJSONObject("data").getString("userId");
                this.adminAuthToken = authToken;
                this.adminUserId = userId;
                return;

            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    /** Implements: https://rocket.chat/docs/developer-guides/rest-api/users/createtoken/
     * Since we do not store the token, we may run into:
     * Delete obsolete tokens every 1 hour #7812
     * https://github.com/RocketChat/Rocket.Chat/pull/7812
     * One work-around would be calling the logout api.
     * For debugging purpose you can get the list of users by copy pasting the following in the browser console,
     * you need to be logged into rocket for it to work
     var xhr = new XMLHttpRequest();
     xhr.open('GET', 'https://rocketserver:443/api/v1/directory?query={"type": "users"}');
     xhr.setRequestHeader ("X-Auth-Token", "admin auth token");
     xhr.setRequestHeader ("X-User-Id", "admin user id");
     xhr.setRequestHeader ("Content-type", "application/json");
     xhr.send();

     */
    public String setUserAuthToken(String UserId) {
        HttpURLConnection connection = null;
        String inputLine;
        StringBuffer response = new StringBuffer();
        try {

            String urlParameters  = "{ \"userId\": \""+UserId+"\" }";
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            URL    url            = new URL( this.rocketURL + "/api/v1/users.createToken" );
            connection= (HttpURLConnection) url.openConnection();
            connection.setDoOutput( true );
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Type", "application/json");
            connection.setRequestProperty( "charset", "utf-8");
            connection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            connection.setRequestProperty( "X-Auth-Token", this.adminAuthToken);
            connection.setRequestProperty( "X-User-Id", this.adminUserId);

            connection.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
                wr.write( postData );
            }

            if (connection.getResponseCode() == 200) {
                // get response stream
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                // feed response into the StringBuilder
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Start parsing
                JSONObject obj = new JSONObject(response.toString());
                String authToken = obj.getJSONObject("data").getString("authToken");
                return authToken;

            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }



}
