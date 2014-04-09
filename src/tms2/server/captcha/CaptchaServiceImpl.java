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

package tms2.server.captcha;

import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import tms2.client.exception.TMSException;
import tms2.client.service.CaptchaService;
import tms2.server.accesscontrol.AccessControlledRemoteService;

/**
 * Service to compare the image text and the user entered text.
 * @author Ismail Lavangee
 *
 */
public class CaptchaServiceImpl extends AccessControlledRemoteService implements CaptchaService
{
	private static final long serialVersionUID = -589202680422127806L;

	@Override
	public boolean validateCaptcha(String user_captcha) throws TMSException
	{
		HttpSession session = getCurrentUserSession();

	    Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
	       
	    if (captcha == null)	    	    
	    	throw new TMSException("Captcha is null");	    
	    
	    return captcha.isCorrect(user_captcha);						
	}
}
