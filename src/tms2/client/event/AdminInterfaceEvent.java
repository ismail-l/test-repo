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

package tms2.client.event;

import tms2.client.presenter.Presenter;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AdminInterfaceEvent extends GwtEvent<AdminInterfaceEventHandler>
{
	public static Type<AdminInterfaceEventHandler> TYPE = new Type<AdminInterfaceEventHandler>();
	
	public static final int ONLINE_USERS = 0;
	public static final int UPDATE_USER_CATEGORY = 1;
	public static final int UPDATE_USER = 2;
	public static final int UPDATE_TERMBASE = 3;
	public static final int UPDATE_PROJECT = 4;
	public static final int UPDATE_FIELD = 5;
	public static final int UPDATE_PRESET_FIELD = 6;
	public static final int UPDATE_ACCESS_RIGHT = 7;
	
	private Presenter _presenter = null;
	private int _event_type = -1;
	
	public AdminInterfaceEvent(Presenter presenter, int event_type)
	{
		_presenter = presenter;
		_event_type = event_type;
	}
	
	public Presenter getPresenter()
	{
		return _presenter;		
	}
	
	@Override
	public Type<AdminInterfaceEventHandler> getAssociatedType() 
	{
		return TYPE;
	}

	@Override
	protected void dispatch(AdminInterfaceEventHandler handler) 
	{
		switch (_event_type)
		{
			case ONLINE_USERS:
			{
				handler.getOnlineUsers(this);
				break;
			}
			case UPDATE_USER_CATEGORY:
			{
				handler.updateUserCategory(this);
				break;
			}
			case UPDATE_USER:
			{
				handler.updateUser(this);
				break;
			}
			case UPDATE_TERMBASE:
			{
				handler.updateTermBase(this);
				break;
			}
			case UPDATE_PROJECT:
			{
				handler.updateProject(this);
				break;
			}
			case UPDATE_FIELD:
			{
				handler.updateField(this);
				break;
			}
			case UPDATE_PRESET_FIELD:
			{
				handler.updatePresetField(this);
				break;
			}
			case UPDATE_ACCESS_RIGHT:
			{
				handler.updateUserAccessRight(this);
				break;
			}
		}
		
	}
}
