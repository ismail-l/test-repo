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
package tms2.server.i18n;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;

import tms2.server.logging.LogUtility;

import com.google.gwt.i18n.client.LocalizableResource.Key;
import com.google.gwt.i18n.client.Messages.DefaultMessage;

/**
 * A class to implement com.google.gwt.i18n.client.Messages derived interfaces
 * when running in a JVM.
 * 
 * Initial concept from:
 *   http://farkasgabor.blogspot.com/2011/05/testing-gwtmvp4g-application-in-jvm.html
 */
public class MessagesFactory
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends com.google.gwt.i18n.client.Messages> T createInstance(Class<T> cls)
	{
		return (T) Proxy.newProxyInstance(MessagesFactory.class.getClassLoader(),
				new Class[] { cls },
				new MessagesInvocationHandler(cls));
	}
	
	protected static class MessagesInvocationHandler<T extends com.google.gwt.i18n.client.Messages>
			implements InvocationHandler
	{
		protected Class<T> cls;
		protected static Properties _properties = null;

		public MessagesInvocationHandler(Class<T> cls)
		{
			this.cls = cls;
			
			if (_properties != null)
				return;

			InputStream is = null;
			ClassLoader loader = cls.getClassLoader();
			String propFile = cls.getName();
			
			propFile = propFile.replace('.', '/');
			propFile += ".properties";
			
			is = loader.getResourceAsStream(propFile);

			if (is != null) 
			{
				LogUtility.log(Level.INFO, "Loading properties " + propFile + ".");
				try 
				{
					// FIXME: Should probably use ResourceBundle to handle locales
					_properties = new Properties();
					_properties.load(is);
					LogUtility.log(Level.INFO, "Properties " + propFile + " has been loaded successfully.");					
				} 
				catch (Exception e) 
				{
					LogUtility.log(Level.SEVERE, "Could not load the " + propFile + " properties!", e);					
					_properties = null;
				}
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable
		{
			Object object = null;
			
			if (method.isAnnotationPresent(DefaultMessage.class))
			{
				object = method.getAnnotation(DefaultMessage.class).value();
			}
			else if (method.isAnnotationPresent(Key.class))
			{
				object = method.getAnnotation(Key.class).value();
			}
			else
			{
				object = method.getName();
			}
			
			if (object != null && _properties != null)
			{
				String message = (String)_properties.get(object);
				
				if (args.length > 0)
					return MessageFormat.format(message, args);
				else
					return message;
			}
			
			return null;
		}
	}
}
