/*
*  Autshumato Terminology Management System (TMS)
*  Free web application for the management of multilingual terminology databases (termbanks). 
*
*  Copyright (C) 2013 Centre for Text Technology (CTexT®), North-West University
*  and Department of Arts and Culture, Government of South Africa
*  Home page: http://www.nwu.co.za/ctext
*  Project page: http://autshumatotms.sourceforge.net
*   
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package tms2.client.service;

import java.util.Vector;

import tms2.client.exception.AccessControlException;
import tms2.shared.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author I. Lavangee
 *
 */
@RemoteServiceRelativePath("accessControlService")
public interface AccessControlService extends RemoteService
{
	public User signOn(String name, String password) throws AccessControlException;
	public void signOff(String authToken) throws AccessControlException;
	public void signOffUser(String authToken, String userToken) throws AccessControlException;
	public User findSignedOnUser(String authToken) throws AccessControlException;
	public boolean isUserStillSignedOn(String authToken) throws AccessControlException;
	public Vector<User> getSignedOnUsers(String authToken) throws AccessControlException;
}
