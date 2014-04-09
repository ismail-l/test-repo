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

package tms2.shared.wrapper;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

import tms2.shared.InputModel;
import tms2.shared.TermBase;
import tms2.shared.User;

/**
 * 
 * @author I. Lavangee
 *
 */
public class FilterDetailsWrapper implements IsSerializable
{
	private ArrayList<TermBase> _termbases = null;
	private ArrayList<User> _users = null;
	private InputModel _inputmodel = null;
	
	public FilterDetailsWrapper()
	{
		
	}
	
	
	public void setTermBases(ArrayList<TermBase> termbases)	
	{
		_termbases = termbases;
	}
	
	public ArrayList<TermBase> getTermBases()
	{
		return _termbases;
	}
	
	public void setUsers(ArrayList<User> users)
	{
		_users = users;
	}
	
	public ArrayList<User> getUsers()
	{
		return _users;
	}
	
	public void setInputModel(InputModel inputmodel)
	{
		_inputmodel = inputmodel;
	}
	
	public InputModel getInputModel()
	{
		return _inputmodel;
	}
}
