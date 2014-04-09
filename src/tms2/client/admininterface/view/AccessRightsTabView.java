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

package tms2.client.admininterface.view;

import java.util.ArrayList;
import java.util.Comparator;

import tms2.client.admininterface.presenter.AccessRightsTabPresenter;
import tms2.client.i18n.Internationalization;
import tms2.client.widgets.AdminTab;
import tms2.client.widgets.ExtendedListBox;
import tms2.client.widgets.UserProjectAccessPanel;
import tms2.shared.AccessRight;
import tms2.shared.Field.FieldType;
import tms2.shared.ChildAccessRight;
import tms2.shared.Term;
import tms2.shared.User;
import tms2.shared.UserCategory;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author I. Lavangee
 *
 */
public class AccessRightsTabView extends AdminTab implements AccessRightsTabPresenter.Display
{
	private static Internationalization _i18n = Internationalization.getInstance();
	
	private VerticalPanel _access_rights_panel = null;
	private VerticalPanel _user_user_cat_panel = null;
	private VerticalPanel _user_project_panel = null;
	private VerticalPanel _radio_button_panel = null;
	private VerticalPanel _rights_panel = null;
	private RadioButton _btn_users = null;
	private RadioButton _btn_user_cat = null;
	private ExtendedListBox<User> _lst_user = null;
	private ExtendedListBox<UserCategory> _lst_user_cat = null;	
	private RadioButton _btn_record_attr = null;
	private RadioButton _btn_index = null;
	private RadioButton _btn_term_attr = null;	
	private RadioButton _btn_term_sub_attr = null;	
	private Label _lbl_term = null;
	private ExtendedListBox<Term> _lst_term = null;
	private DeckPanel _grid_panel = null;	
	private DataGrid<AccessRight> _dg_recordfield = null;
	private ListDataProvider<AccessRight> _list_recordfield = null;
	private ArrayList<Column<AccessRight, Boolean>> _record_non_guest_columns = null;	
	private DataGrid<AccessRight> _dg_indexfield = null;
	private ListDataProvider<AccessRight> _list_indexfield = null;
	private ArrayList<Column<AccessRight, Boolean>> _index_non_guest_columns = null;		
	private DataGrid<ChildAccessRight> _dg_attrfield = null;
	private ListDataProvider<ChildAccessRight> _list_attrfield = null;
	private ArrayList<Column<ChildAccessRight, Boolean>> _attr_non_guest_columns = null;	
	private DataGrid<ChildAccessRight> _dg_subattrfield = null;
	private ListDataProvider<ChildAccessRight> _list_subattrfield = null;
	private ArrayList<Column<ChildAccessRight, Boolean>> _subattr_non_guest_columns = null;
	private Label _lbl_user_project_heading = null;
	private UserProjectAccessPanel _user_project_assigner = null;
	private Button _btn_project_save = null;
	private Label _lbl_access_right_heading = null;	
	private Button _btn_mark_all_read = null;
	private Button _btn_mark_all_update = null;
	private Button _btn_mark_all_export = null;
	private Button _btn_mark_all_delete = null;	
	private Button _btn_save_access_rights = null;	
		
	public AccessRightsTabView() 
	{
		super(_i18n.getConstants().admin_access_rights_label());
		
		_access_rights_panel = new VerticalPanel();
		_access_rights_panel.setSpacing(20);
		
		buildUserUserCatPanel();
		buildUserProjectPanel();
		buildRadioButtonPanel();
		buildRightsPanel();
		
		super.add(_access_rights_panel);		
	}
	
	private void buildUserUserCatPanel()
	{
		_user_user_cat_panel = new VerticalPanel();
		
		Label lbl_first_heading = new Label(_i18n.getConstants().admin_access_rights_heading(), true);
		lbl_first_heading.addStyleName("labelTextBold");
		lbl_first_heading.addStyleName("plainLabelText");
		lbl_first_heading.addStyleName("paddedBottom");
		
		_user_user_cat_panel.add(lbl_first_heading);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_user_user_cat_panel.add(lbl_dummy);
		
		Label lbl_second_heading = new Label(_i18n.getConstants().admin_access_rights_second_heading(), false);
		lbl_second_heading.addStyleName("plainLabelText");
		lbl_second_heading.addStyleName("tabHeading");
		
		_user_user_cat_panel.add(lbl_second_heading);
		
		HorizontalPanel radiobutton_panel = new HorizontalPanel();
		radiobutton_panel.setSpacing(5);
		
		_btn_users = new RadioButton("group", _i18n.getConstants().admin_access_rights_users());
		_btn_user_cat = new RadioButton("group", _i18n.getConstants().admin_access_rights_user_category());
		
		radiobutton_panel.add(_btn_users);
		radiobutton_panel.add(_btn_user_cat);
		
		_user_user_cat_panel.add(radiobutton_panel);
		
		VerticalPanel select_user_usercat_panel = new VerticalPanel();
		select_user_usercat_panel.setHeight("50px");
		select_user_usercat_panel.setSpacing(5);
		select_user_usercat_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		select_user_usercat_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		_lst_user = new ExtendedListBox<User>(false);		
		_lst_user_cat = new ExtendedListBox<UserCategory>(false);
		_lst_user_cat.setVisible(false);
		
		select_user_usercat_panel.add(_lst_user);
		select_user_usercat_panel.add(_lst_user_cat);
		
		_user_user_cat_panel.add(select_user_usercat_panel);				
		
		_access_rights_panel.add(_user_user_cat_panel);
	}
	
	private void buildUserProjectPanel()
	{
		_user_project_panel = new VerticalPanel();
		_user_project_panel.setStyleName("borderedBlock");
		_user_project_panel.setVisible(false);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_user_project_panel.add(lbl_dummy);
		
		_lbl_user_project_heading = new Label();
		_lbl_user_project_heading.addStyleName("labelTextBold");
		_lbl_user_project_heading.addStyleName("plainLabelText");
		
		_user_project_panel.add(_lbl_user_project_heading);
		
		VerticalPanel user_projects = new VerticalPanel();			
		user_projects.setSpacing(5);
		user_projects.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		user_projects.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
		_user_project_assigner = new UserProjectAccessPanel();
		_user_project_assigner.reset();
		
		_btn_project_save = new Button(_i18n.getConstants().admin_access_rights_save());		
		_btn_project_save.addStyleName("adminButton");
		
		user_projects.add(_user_project_assigner);
		user_projects.add(_btn_project_save);
		
		user_projects.setCellHorizontalAlignment(_btn_project_save, HasHorizontalAlignment.ALIGN_RIGHT);
		
		_user_project_panel.add(user_projects);	
		
		_access_rights_panel.add(_user_project_panel);		
	}
	
	private void buildRadioButtonPanel()
	{
		_radio_button_panel = new VerticalPanel();
		_radio_button_panel.setVisible(false);
		
		HorizontalPanel radio_buttons_panel = new HorizontalPanel();
		radio_buttons_panel.setSpacing(5);
		
		_btn_record_attr = new RadioButton("fields", _i18n.getConstants().admin_im_labelRecordFields());
		_btn_index = new RadioButton("fields", _i18n.getConstants().admin_im_labelIndexFields());
		_btn_term_attr = new RadioButton("fields", _i18n.getConstants().admin_im_labelAttributeFields());		
		_btn_term_sub_attr = new RadioButton("fields", _i18n.getConstants().admin_im_labelSubAttributeFields());		
		
		radio_buttons_panel.add(_btn_record_attr);
		radio_buttons_panel.add(_btn_index);
		radio_buttons_panel.add(_btn_term_attr);
		radio_buttons_panel.add(_btn_term_sub_attr);
		
		_radio_button_panel.add(radio_buttons_panel);
		
		HorizontalPanel termsdetails_panel = new HorizontalPanel();
		termsdetails_panel.setWidth("750px");
		termsdetails_panel.setStyleName("borderedBlock");
		termsdetails_panel.setSpacing(5);
		
		HorizontalPanel term_panel = new HorizontalPanel();
		term_panel.setSpacing(5);
		
		_lbl_term = new Label();
		_lbl_term.addStyleName("labelTextBold");
		_lbl_term.addStyleName("plainLabelText");
		
		_lst_term = new ExtendedListBox<Term>(false);
		_lst_term.setVisible(false);
		
		term_panel.add(_lbl_term);
		term_panel.add(_lst_term);
		
		termsdetails_panel.add(term_panel);
		
		_radio_button_panel.add(termsdetails_panel);
		
		_access_rights_panel.add(_radio_button_panel);
	}
	
	private void buildRightsPanel()
	{
		_rights_panel = new VerticalPanel();
		_rights_panel.setVisible(false);
					
		_lbl_access_right_heading = new Label();
		_lbl_access_right_heading.addStyleName("labelTextBold");
		_lbl_access_right_heading.addStyleName("plainLabelText");
		
		_rights_panel.add(_lbl_access_right_heading);
		
		Label lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_rights_panel.add(lbl_dummy);
		
		lbl_dummy = new Label();
		lbl_dummy.addStyleName("paddedBottom");
		
		_rights_panel.add(lbl_dummy);							
					
		_grid_panel = new DeckPanel();
		
		_rights_panel.add(_grid_panel);
		
		AccessRightGridFieldEntity record_grid = buildFieldGrid();
		_dg_recordfield = record_grid.getDataGrid();
		_list_recordfield = record_grid.getDataProvider();
		_record_non_guest_columns = record_grid.getNonGuestColumns();
		
		AccessRightGridFieldEntity index_grid = buildFieldGrid();
		_dg_indexfield = index_grid.getDataGrid();
		_list_indexfield = index_grid.getDataProvider();
		_index_non_guest_columns = index_grid.getNonGuestColumns();
		
		AccessRightGridChildFieldEntity attr_grid = buildChildFieldGrid();
		_dg_attrfield = attr_grid.getDataGrid();
		_list_attrfield = attr_grid.getDataProvider();
		_attr_non_guest_columns = attr_grid.getNonGuestColumns();
		
		AccessRightGridChildFieldEntity subattr_grid = buildChildFieldGrid();
		_dg_subattrfield = subattr_grid.getDataGrid();
		_list_subattrfield = subattr_grid.getDataProvider();
		_subattr_non_guest_columns = subattr_grid.getNonGuestColumns();
		
		HorizontalPanel update_access_rights_panel = new HorizontalPanel();
		update_access_rights_panel.setStyleName("borderedBlock");
		update_access_rights_panel.setSpacing(5);
		update_access_rights_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		update_access_rights_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		update_access_rights_panel.setWidth("750px");
				
		_btn_mark_all_read = new Button(_i18n.getConstants().admin_access_rights_mark_read());
		_btn_mark_all_read.addStyleName("adminButton");
		_btn_mark_all_read.setWidth("170px");
		
		_btn_mark_all_update = new Button(_i18n.getConstants().admin_access_rights_mark_update());
		_btn_mark_all_update.addStyleName("adminButton");
		_btn_mark_all_update.setWidth("170px");
		
		_btn_mark_all_export = new Button(_i18n.getConstants().admin_access_rights_mark_export());
		_btn_mark_all_export.addStyleName("adminButton");
		_btn_mark_all_export.setWidth("170px");
		
		_btn_mark_all_delete = new Button(_i18n.getConstants().admin_access_rights_mark_delete());
		_btn_mark_all_delete.addStyleName("adminButton");
		_btn_mark_all_delete.setWidth("170px");
		
		_btn_save_access_rights = new Button(_i18n.getConstants().admin_access_rights_save());
		_btn_save_access_rights.addStyleName("adminButton");
		
		update_access_rights_panel.add(_btn_mark_all_read);
		update_access_rights_panel.add(_btn_mark_all_update);				
		update_access_rights_panel.add(_btn_mark_all_export);		
		update_access_rights_panel.add(_btn_mark_all_delete);	
		update_access_rights_panel.add(_btn_save_access_rights);
	    
		_rights_panel.add(update_access_rights_panel);
				
		_access_rights_panel.add(_rights_panel);					
	}
	
	private AccessRightGridFieldEntity buildFieldGrid()
	{
		VerticalPanel access_grid_panel = new VerticalPanel();
		access_grid_panel.setWidth("750px");	
		access_grid_panel.setHeight("350px");		
		
		SimpleLayoutPanel access_rights_details = new SimpleLayoutPanel();
		access_rights_details.setWidth("750px");	
		access_rights_details.setHeight("350px");
		access_rights_details.setStyleName("borderedBlock");
		
		DataGrid<AccessRight> data_grid = new DataGrid<AccessRight>();
		data_grid.setWidth("750px");
		data_grid.setHeight("350px");
					    	    		
		ListDataProvider<AccessRight> list_provider = new ListDataProvider<AccessRight>();
		list_provider.addDataDisplay(data_grid);
		
		ListHandler<AccessRight> sorter = new ListHandler<AccessRight>(list_provider.getList());
		data_grid.addColumnSortHandler(sorter);
		
		Column<AccessRight, Number> col_fieldid = new Column<AccessRight, Number>(new NumberCell()) 
		{			
			@Override
			public Number  getValue(AccessRight object) 
			{			
				return object.getFieldId();
			}
		};
			
		col_fieldid.setSortable(true);
		sorter.setComparator(col_fieldid, new Comparator<AccessRight>()
		{			
			@Override
			public int compare(AccessRight o1, AccessRight o2) 
			{						
				if (o1.getFieldId() > o2.getFieldId())
					return 1;
				else if (o1.getFieldId() < o2.getFieldId())
					return -1;
				
				return 0;
			}
		});
			
		data_grid.setColumnWidth(col_fieldid, 40, Unit.PX);
		
		Column<AccessRight, String> col_fieldname = new Column<AccessRight, String>(new TextCell()) 
		{			
			@Override
			public String getValue(AccessRight object) 
			{			
				return object.getFieldName();
			}
		};
		
		data_grid.setColumnWidth(col_fieldname, 20, Unit.PCT);
		
		Column<AccessRight, String> col_fieldtype = new Column<AccessRight, String>(new TextCell()) 
		{			
			@Override
			public String getValue(AccessRight object) 
			{		
				FieldType field_type = null;
				if (object.isRecordAttribute())
					field_type = FieldType.TEXT_RECORD_FIELD;			
				else if (object.isIndexField())			
					field_type = FieldType.INDEX_FIELD;
				else if (object.isFieldAttribute())
					field_type = FieldType.TEXT_ATTR_FIELD;
				else if (object.isSynonymField())			
					field_type = FieldType.SYNONYM_FIELD;			
				else if (object.isPresetAttribute())			
					field_type = FieldType.PRESET_ATTR_FIELD;							
				else if (object.isFieldSubAttribute())
					field_type = FieldType.TEXT_SUB_ATTR_FIELD;
				else if (object.isPresetSubAttribute())			
					field_type = FieldType.PRESET_SUB_ATTR_FIELD;
				
				return field_type.getFieldTypeName();
			}
		};
		
		data_grid.setColumnWidth(col_fieldtype, 20, Unit.PCT);	
						
		Column<AccessRight, Boolean> col_mayread = new Column<AccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(AccessRight object) 
			{			
				return object.mayRead();
			}
		};
		
		col_mayread.setFieldUpdater(new FieldUpdater<AccessRight, Boolean>()
		{			
			@Override
			public void update(int index, AccessRight object, Boolean value) 
			{
				object.setMayRead(value);				
			}
		});
		
		data_grid.setColumnWidth(col_mayread, 50, Unit.PX);
		
		Column<AccessRight, Boolean> col_mayupdate = new Column<AccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(AccessRight object) 
			{			
				return object.mayUpdate();
			}
		};
		
		col_mayupdate.setFieldUpdater(new FieldUpdater<AccessRight, Boolean>()
		{			
			@Override
			public void update(int index, AccessRight object, Boolean value) 
			{
				object.setMayUpdate(value);				
			}
		});
					
		Column<AccessRight, Boolean> col_mayexport = new Column<AccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(AccessRight object) 
			{			
				return object.mayExport();
			}
		};
		
		col_mayexport.setFieldUpdater(new FieldUpdater<AccessRight, Boolean>()
		{			
			@Override
			public void update(int index, AccessRight object, Boolean value) 
			{
				object.setMayExport(value);				
			}
		});
							
		Column<AccessRight, Boolean> col_maydelete = new Column<AccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(AccessRight object) 
			{			
				return object.mayDelete();
			}
		};
		
		col_maydelete.setFieldUpdater(new FieldUpdater<AccessRight, Boolean>()
		{			
			@Override
			public void update(int index, AccessRight object, Boolean value) 
			{
				object.setMayDelete(value);				
			}
		});
					
															
		data_grid.addColumn(col_fieldid,  _i18n.getConstants().admin_access_rights_dg_fieldid());
		data_grid.addColumn(col_fieldname, _i18n.getConstants().admin_access_rights_dg_fieldname());		
		data_grid.addColumn(col_fieldtype, _i18n.getConstants().admin_access_rights_dg_fieldtype());
		data_grid.addColumn(col_mayread, _i18n.getConstants().admin_access_rights_dg_read());
		data_grid.addColumn(col_mayupdate, _i18n.getConstants().admin_access_rights_dg_update());
		data_grid.addColumn(col_mayexport, _i18n.getConstants().admin_access_rights_dg_export());
		data_grid.addColumn(col_maydelete, _i18n.getConstants().admin_access_rights_dg_delete());
								
		access_rights_details.add(data_grid);
		
		ArrayList<Column<AccessRight,Boolean>>non_guest_columns = new ArrayList<Column<AccessRight,Boolean>>();
		non_guest_columns.add(col_mayupdate);
		non_guest_columns.add(col_mayexport);
		non_guest_columns.add(col_maydelete);
		
		access_grid_panel.add(access_rights_details);			    
				
		VerticalPanel pager_panel = new VerticalPanel();		
		pager_panel.setSpacing(5);
		pager_panel.setWidth("750px");
		pager_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		pager_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
						
	    SimplePager.Resources pager_resources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pager_resources, false, 0, true);
	    //pager.setPageSize(25);
	    pager.setDisplay(data_grid);
		
	    pager_panel.add(pager);
	    
	    access_grid_panel.add(pager_panel);
	    
	    _grid_panel.add(access_grid_panel);
	    
	    AccessRightGridFieldEntity grid_entity = new AccessRightGridFieldEntity(data_grid, list_provider, non_guest_columns);
	    
		return grid_entity;
	}
	
	private AccessRightGridChildFieldEntity buildChildFieldGrid()
	{
		VerticalPanel access_grid_panel = new VerticalPanel();
		access_grid_panel.setWidth("750px");	
		access_grid_panel.setHeight("350px");		
		
		SimpleLayoutPanel access_rights_details = new SimpleLayoutPanel();
		access_rights_details.setWidth("750px");	
		access_rights_details.setHeight("350px");
		access_rights_details.setStyleName("borderedBlock");
		
		DataGrid<ChildAccessRight> data_grid = new DataGrid<ChildAccessRight>();
		data_grid.setWidth("750px");
		data_grid.setHeight("350px");
					    	    		
		ListDataProvider<ChildAccessRight> list_provider = new ListDataProvider<ChildAccessRight>();
		list_provider.addDataDisplay(data_grid);
		
		ListHandler<ChildAccessRight> sorter = new ListHandler<ChildAccessRight>(list_provider.getList());
		data_grid.addColumnSortHandler(sorter);
		
		Column<ChildAccessRight, Number> col_fieldid = new Column<ChildAccessRight, Number>(new NumberCell()) 
		{			
			@Override
			public Number  getValue(ChildAccessRight object) 
			{			
				return object.getFieldId();
			}
		};
			
		col_fieldid.setSortable(true);
		sorter.setComparator(col_fieldid, new Comparator<ChildAccessRight>()
		{			
			@Override
			public int compare(ChildAccessRight o1, ChildAccessRight o2) 
			{						
				if (o1.getFieldId() > o2.getFieldId())
					return 1;
				else if (o1.getFieldId() < o2.getFieldId())
					return -1;
				
				return 0;
			}
		});
			
		data_grid.setColumnWidth(col_fieldid, 40, Unit.PX);
		
		Column<ChildAccessRight, String> col_fieldname = new Column<ChildAccessRight, String>(new TextCell()) 
		{			
			@Override
			public String getValue(ChildAccessRight object) 
			{			
				return object.getFieldName();
			}
		};
		
		data_grid.setColumnWidth(col_fieldname, 20, Unit.PCT);
		
		Column<ChildAccessRight, String> col_fieldtype = new Column<ChildAccessRight, String>(new TextCell()) 
		{			
			@Override
			public String getValue(ChildAccessRight object) 
			{		
				FieldType field_type = null;
				if (object.isRecordAttribute())
					field_type = FieldType.TEXT_RECORD_FIELD;			
				else if (object.isIndexField())			
					field_type = FieldType.INDEX_FIELD;
				else if (object.isFieldAttribute())
					field_type = FieldType.TEXT_ATTR_FIELD;
				else if (object.isSynonymField())			
					field_type = FieldType.SYNONYM_FIELD;			
				else if (object.isPresetAttribute())			
					field_type = FieldType.PRESET_ATTR_FIELD;							
				else if (object.isFieldSubAttribute())
					field_type = FieldType.TEXT_SUB_ATTR_FIELD;
				else if (object.isPresetSubAttribute())			
					field_type = FieldType.PRESET_SUB_ATTR_FIELD;
				
				return field_type.getFieldTypeName();
			}
		};
		
		data_grid.setColumnWidth(col_fieldtype, 20, Unit.PCT);	
						
		Column<ChildAccessRight, Boolean> col_mayread = new Column<ChildAccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(ChildAccessRight object) 
			{			
				return object.mayRead();
			}
		};
		
		col_mayread.setFieldUpdater(new FieldUpdater<ChildAccessRight, Boolean>()
		{			
			@Override
			public void update(int index, ChildAccessRight object, Boolean value) 
			{
				object.setMayRead(value);				
			}
		});
		
		data_grid.setColumnWidth(col_mayread, 50, Unit.PX);
		
		Column<ChildAccessRight, Boolean> col_mayupdate = new Column<ChildAccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(ChildAccessRight object) 
			{			
				return object.mayUpdate();
			}
		};
		
		col_mayupdate.setFieldUpdater(new FieldUpdater<ChildAccessRight, Boolean>()
		{			
			@Override
			public void update(int index, ChildAccessRight object, Boolean value) 
			{
				object.setMayUpdate(value);				
			}
		});
					
		Column<ChildAccessRight, Boolean> col_mayexport = new Column<ChildAccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(ChildAccessRight object) 
			{			
				return object.mayExport();
			}
		};
		
		col_mayexport.setFieldUpdater(new FieldUpdater<ChildAccessRight, Boolean>()
		{			
			@Override
			public void update(int index, ChildAccessRight object, Boolean value) 
			{
				object.setMayExport(value);				
			}
		});
							
		Column<ChildAccessRight, Boolean> col_maydelete = new Column<ChildAccessRight, Boolean>(new CheckboxCell(true, false)) 
		{			
			@Override
			public Boolean getValue(ChildAccessRight object) 
			{			
				return object.mayDelete();
			}
		};
		
		col_maydelete.setFieldUpdater(new FieldUpdater<ChildAccessRight, Boolean>()
		{			
			@Override
			public void update(int index, ChildAccessRight object, Boolean value) 
			{
				object.setMayDelete(value);				
			}
		});
																			
		data_grid.addColumn(col_fieldid,  _i18n.getConstants().admin_access_rights_dg_fieldid());
		data_grid.addColumn(col_fieldname, _i18n.getConstants().admin_access_rights_dg_fieldname());		
		data_grid.addColumn(col_fieldtype, _i18n.getConstants().admin_access_rights_dg_fieldtype());
		data_grid.addColumn(col_mayread, _i18n.getConstants().admin_access_rights_dg_read());
		data_grid.addColumn(col_mayupdate, _i18n.getConstants().admin_access_rights_dg_update());
		data_grid.addColumn(col_mayexport, _i18n.getConstants().admin_access_rights_dg_export());
		data_grid.addColumn(col_maydelete, _i18n.getConstants().admin_access_rights_dg_delete());
								
		access_rights_details.add(data_grid);
		
		ArrayList<Column<ChildAccessRight,Boolean>>non_guest_columns = new ArrayList<Column<ChildAccessRight,Boolean>>();
		non_guest_columns.add(col_mayupdate);
		non_guest_columns.add(col_mayexport);
		non_guest_columns.add(col_maydelete);
		
		access_grid_panel.add(access_rights_details);			    
				
		VerticalPanel pager_panel = new VerticalPanel();		
		pager_panel.setSpacing(5);
		pager_panel.setWidth("750px");
		pager_panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		pager_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
						
	    SimplePager.Resources pager_resources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pager_resources, false, 0, true);
	    //pager.setPageSize(25);
	    pager.setDisplay(data_grid);
		
	    pager_panel.add(pager);
	    
	    access_grid_panel.add(pager_panel);
	    
	    _grid_panel.add(access_grid_panel);
	    
	    AccessRightGridChildFieldEntity grid_entity = new AccessRightGridChildFieldEntity(data_grid, list_provider, non_guest_columns);
	    
		return grid_entity;
	}
	
	private class AccessRightGridFieldEntity
	{
		private DataGrid<AccessRight> _dg_grid = null;
		private ListDataProvider<AccessRight> _list_data_provider = null;
		private ArrayList<Column<AccessRight, Boolean>> _non_guest_columns = null;
		
		public AccessRightGridFieldEntity(DataGrid<AccessRight> grid,  ListDataProvider<AccessRight> data_provider, ArrayList<Column<AccessRight, Boolean>> non_guest_columns)
		{
			_dg_grid = grid;
			_list_data_provider = data_provider;
			_non_guest_columns = non_guest_columns;
		}
		
		public DataGrid<AccessRight> getDataGrid()
		{
			return _dg_grid;
		}
		
		public ListDataProvider<AccessRight> getDataProvider()
		{
			return _list_data_provider;
		}
		
		public ArrayList<Column<AccessRight, Boolean>> getNonGuestColumns()
		{
			return _non_guest_columns;
		}
	}
	
	private class AccessRightGridChildFieldEntity
	{
		private DataGrid<ChildAccessRight> _dg_grid = null;
		private ListDataProvider<ChildAccessRight> _list_data_provider = null;
		private ArrayList<Column<ChildAccessRight, Boolean>> _non_guest_columns = null;
		
		public AccessRightGridChildFieldEntity(DataGrid<ChildAccessRight> grid,  ListDataProvider<ChildAccessRight> data_provider, ArrayList<Column<ChildAccessRight, Boolean>> non_guest_columns)
		{
			_dg_grid = grid;
			_list_data_provider = data_provider;
			_non_guest_columns = non_guest_columns;
		}
		
		public DataGrid<ChildAccessRight> getDataGrid()
		{
			return _dg_grid;
		}
		
		public ListDataProvider<ChildAccessRight> getDataProvider()
		{
			return _list_data_provider;
		}
		
		public ArrayList<Column<ChildAccessRight, Boolean>> getNonGuestColumns()
		{
			return _non_guest_columns;
		}
	}
	
	@Override
	public Label getHeadingLabel() 
	{	
		return super.getTabHeading();
	}

	@Override
	public RadioButton getUserRadioButton() 
	{
		return _btn_users;
	}

	@Override
	public RadioButton getUserCategoryRadioButton() 
	{	
		return _btn_user_cat;
	}

	@Override
	public ExtendedListBox<User> getUserListBox() 
	{	
		return _lst_user;
	}

	@Override
	public ExtendedListBox<UserCategory> getUserCategoryListBox() 
	{	
		return _lst_user_cat;
	}

	@Override
	public VerticalPanel getRightsPanel() 
	{
		return _rights_panel;
	}

	@Override
	public Label getAccessRightsLabel() 
	{
		return _lbl_access_right_heading;
	}

	@Override
	public Button getMarkAllReadButton() 
	{	
		return _btn_mark_all_read;
	}

	@Override
	public Button getMarkAllUpdateButton() 
	{	
		return _btn_mark_all_update;
	}

	@Override
	public Button getMarkAllExportButton() 
	{	
		return _btn_mark_all_export;
	}

	@Override
	public Button getMarkAllDeleteButton() 
	{		
		return _btn_mark_all_delete;
	}

	@Override
	public VerticalPanel getUserProjectPanel() 
	{
		return _user_project_panel;
	}

	@Override
	public Label getUserProjectLabel() 
	{	
		return _lbl_user_project_heading;
	}

	@Override
	public UserProjectAccessPanel getUserProjectAssigner() 
	{	
		return _user_project_assigner;
	}
	
	@Override
	public Button getUpdateAccessRightsButton()
	{
		return _btn_save_access_rights;
	}

	@Override
	public DeckPanel getGridPanel() 
	{	
		return _grid_panel;
	}

	@Override
	public RadioButton getRecordAttributeRadioButton() 
	{	
		return _btn_record_attr;
	}

	@Override
	public RadioButton getIndexRadioButton() 
	{	
		return _btn_index;
	}

	@Override
	public RadioButton getAttributeRadioButton() 
	{	
		return _btn_term_attr;
	}

	@Override
	public RadioButton getSubAttributeRadioButton() 
	{	
		return _btn_term_sub_attr;
	}

	@Override
	public ExtendedListBox<Term> getTermListBox() 
	{	
		return _lst_term;
	}

	@Override
	public DataGrid<AccessRight> getRecordAttributeDataGrid() 
	{	
		return _dg_recordfield;
	}

	@Override
	public ListDataProvider<AccessRight> getRecordAttributeDataProvider() 
	{	
		return _list_recordfield;
	}

	@Override
	public ArrayList<Column<AccessRight, Boolean>> getRecordNonGuestColumns() 
	{		
		return _record_non_guest_columns;
	}

	@Override
	public DataGrid<AccessRight> getTermDataGrid() 
	{
		return _dg_indexfield;
	}

	@Override
	public ListDataProvider<AccessRight> getTermDataProvider() 
	{	
		return _list_indexfield;
	}

	@Override
	public ArrayList<Column<AccessRight, Boolean>> getTermNonGuestColumns() 
	{	
		return _index_non_guest_columns;
	}

	@Override
	public DataGrid<ChildAccessRight> getTermAttributeDataGrid() 
	{	
		return _dg_attrfield;
	}

	@Override
	public ListDataProvider<ChildAccessRight> getTermAttributeDataProvider() 
	{	
		return _list_attrfield;
	}

	@Override
	public ArrayList<Column<ChildAccessRight, Boolean>> getTermAttributeNonGuestColumns() 
	{	
		return _attr_non_guest_columns;
	}

	@Override
	public DataGrid<ChildAccessRight> getTermSubAttributeDataGrid() 
	{	
		return _dg_subattrfield;
	}

	@Override
	public ListDataProvider<ChildAccessRight> getTermSubAttributeDataProvider() 
	{	
		return _list_subattrfield;
	}

	@Override
	public ArrayList<Column<ChildAccessRight, Boolean>> getTermSubAttributeNonGuestColumns() 
	{	
		return _subattr_non_guest_columns;
	}

	@Override
	public VerticalPanel getRadioButtonPanel() 
	{	
		return _radio_button_panel;
	}

	@Override
	public Label getTermLabel() 
	{	
		return _lbl_term;
	}

	@Override
	public Button getProjectSaveButton() 
	{		
		return _btn_project_save;
	}
}
