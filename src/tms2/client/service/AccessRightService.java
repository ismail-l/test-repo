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

import java.util.ArrayList;

import tms2.client.exception.TMSException;
import tms2.shared.InputModel;
import tms2.shared.Project;
import tms2.shared.Result;
import tms2.shared.wrapper.AccessRightDetailsWrapper;
import tms2.shared.wrapper.RecordEditDetailsWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author I. Lavangee
 *
 */
@RemoteServiceRelativePath("accessRightService")
public interface AccessRightService extends RemoteService 
{
	public Result<Boolean> updateAccessRights(String authToken, InputModel inputmodel, ArrayList<Project> projects, long consumer_id, boolean is_user_access_right) throws TMSException;
	public AccessRightDetailsWrapper getAccessRightDetails(String authToken, long consumer_id, boolean is_user_access_right) throws TMSException;
	public RecordEditDetailsWrapper getRecordEditDetails(String authToken, long record_id, boolean is_editing) throws TMSException;
	public InputModel getAccessRightInputModel(String authToken) throws TMSException;
}
