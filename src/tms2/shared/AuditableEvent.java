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

package tms2.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Entity that represents an auditable event.
 * @author Wildrich Fourie
 * @author Werner Liebenberg
 * @author Ismail Lavangee
 */
public class AuditableEvent implements IsSerializable 
{		
	private long _eventid = -1;
	private long _resourceid = -1;
	private long _userid = -1;
	private String _username = null;
	private int _eventtype = -1;
	private int _resourcetype = -1;
	private Date _timestamp = null;
	private String _chardata = null;
	private boolean _can_be_rendered = false;

	public static final int EVENT_CREATE = 1;
	public static final int EVENT_UPDATE = 2;
	
	public static final int RESOURCE_TYPE_RECORD = 1;
	public static final int RESOURCE_TYPE_RECORDFIELD = 2;
	public static final int RESOURCE_TYPE_INDEXFIELD = 3;
	public static final int RESOURCE_TYPE_ATTRIBUTEFIELD = 4; 
	public static final int RESOURCE_TYPE_SYNONYMFIELD = 5; 
	public static final int RESOURCE_TYPE_SYNONYMATTRIBUTEFIELD = 6;	
		
	public AuditableEvent() 
	{
		
	}
		
	public long getEventId() 
	{
		return _eventid;
	}


	public void setEventId(long eventid) 
	{
		_eventid = eventid;
	}

	public long getUserId()
	{
		return _userid;
	}

	public void setUserId(long userid) 
	{
		_userid = userid;
	}

	public String getFullUserName()
	{
		return _username;
	}
	
	public void setFullUserName(String username)
	{
		_username = username;
	}
	
	public long getResourceId()
	{
		return _resourceid;
	}

	public void setResourceId(long resourceid)
	{
		_resourceid = resourceid;
	}

	public void setEventType(int eventtype)
	{
		_eventtype = eventtype;
	}
	
	public boolean isCreateMode()
	{
		if (_eventtype == EVENT_CREATE)
			return true;
		
		return false;
	}
	
	public String getEventTypeName()
	{
		if (isCreateMode())
			return "Create";
		
		return "Update";
	}
	
	public void setResourceType(int resourcetype)
	{
		_resourcetype = resourcetype;
	}
	
	public boolean isRecordResource()
	{
		if (_resourcetype == RESOURCE_TYPE_RECORD)
			return true;
		
		return false;
	}
	
	public boolean isRecordAttributeResource()
	{
		if (_resourcetype == RESOURCE_TYPE_RECORDFIELD)
			return true;
		
		return false;
	}
	
	public boolean isTermResource()
	{
		if (_resourcetype == RESOURCE_TYPE_INDEXFIELD)
			return true;
		
		return false;
	}
	
	public boolean isTermAttributeResource()
	{
		if (_resourcetype == RESOURCE_TYPE_ATTRIBUTEFIELD)
			return true;
		
		return false;
	}
	
	public boolean isSynonymResource()
	{
		if (_resourcetype == RESOURCE_TYPE_SYNONYMFIELD)
			return true;
		
		return false;
	}
	
	public boolean isSynonymAttributeResource()
	{
		if (_resourcetype == RESOURCE_TYPE_SYNONYMATTRIBUTEFIELD)
			return true;
		
		return false;
	}
	
	public String getCharData()
	{
		return _chardata;
	}

	public void setCharData(String backupvalue) 
	{
		_chardata = backupvalue;
	}

	public Date getTimestamp() 
	{
		return _timestamp;
	}

	public void setTimestamp(Date timestamp) 
	{
		_timestamp = timestamp;
	}
	
	public void setCanBeRendered(boolean can_be_rendered)
	{
		_can_be_rendered = can_be_rendered;
	}
	
	public boolean canBeRendered()
	{
		return _can_be_rendered;
	}
}
