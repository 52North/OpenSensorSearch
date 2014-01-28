/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/** @author Yakoub
 */

package org.n52.oss.ui.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.n52.oss.ui.WebsiteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service("userAuthService")
public class OSSAuthenticationProvider implements AuthenticationProvider {

    private static Logger log = LoggerFactory.getLogger(OSSAuthenticationProvider.class);

    public class AuthToken {
        String auth_token;
        boolean isValid;
        boolean isAdmin;
    }

    private static WebsiteConfig config = new WebsiteConfig();

    public OSSAuthenticationProvider() {
        log.info("NEW {}", this);
    }

    @Override
    public Authentication authenticate(Authentication arg0) throws AuthenticationException {
        String username = arg0.getName();
        String password = arg0.getCredentials().toString();

        AuthToken token = authenticateOSS(username, password);

        if (token.auth_token != null) {
            if ( !token.isValid)
                throw new UsernameNotFoundException("Username is not validated please contact site administration!");

            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_SCRIPT_AUTHOR"));

            if (token.isAdmin)
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            final UserDetails principal = new User(username, token.auth_token, grantedAuths);
            final Authentication auth = new UsernamePasswordAuthenticationToken(principal,
                                                                                token.auth_token,
                                                                                grantedAuths);
            return auth;

        }

        throw new UsernameNotFoundException("Wrong username/password combination");
    }

    private AuthToken authenticateOSS(String username, String password) {
        try {
            HttpPost post = new HttpPost(config.getApiEndpoint() + "/user/login");
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(post);
            StringBuilder result = new StringBuilder();
            String s = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            while ( (s = reader.readLine()) != null)
                result.append(s);

            AuthToken token = new Gson().fromJson(result.toString(), AuthToken.class);
            return token;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean supports(Class< ? > arg0) {
        return arg0.equals(UsernamePasswordAuthenticationToken.class);
    }

}
