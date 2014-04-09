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

import static nl.captcha.Captcha.NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.servlet.SimpleCaptchaServlet;

/**
 * Servlet to generate the image.
 * @author Ismail Lavangee
 *
 */
public class CaptchaServlet extends SimpleCaptchaServlet 
{
	private static final long serialVersionUID = 6560171562324177699L;
	
	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {		
        HttpSession session = req.getSession();
        
        int width = 360;
        int height = 50;
        
        Captcha captcha = new Captcha.Builder(width, height)
	        	.addText()
	        	.addBackground(new GradiatedBackgroundProducer())
	            .gimp()
	            .addNoise()
	            .addBorder()
	            .build();

        System.out.println("Setting Captcha session attribute.");
        session.setAttribute(NAME, captcha);                
        
        CaptchaServletUtil.writeImage(resp, captcha.getImage());        
    }	
}
