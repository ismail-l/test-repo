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

package tms2.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;

/**
 * Label that contains a Latex formatted formula. Extends the normal Label class.
 * 
 * @author Martin Schlemmer
 */
public class FormulaLabel extends Label
{
	/**
	 * @uml.property  name="mathText"
	 */
	private String mathText = "";
	
	public FormulaLabel() {}

	public FormulaLabel(String text)
	{
		super(text);
	}

	public FormulaLabel(Element element)
	{
		super(element);
	}

	public FormulaLabel(String text, boolean wordWrap)
	{
		super(text, wordWrap);
	}
	
	@Override
	public String getText()
	{
		return this.mathText;
	}

	@Override
	public void setText(String text)
	{
		this.mathText = text;
		super.setText("$$ " + text + " $$");
		this.doMathTypeset(this.getElement());
	}

	private native void doMathTypeset(com.google.gwt.dom.client.Element e)
	/*-{
		if (!$wnd.MathJax) {
			var mathjax_script_tag = $doc.createElement("script");
			mathjax_script_tag.src = "MathJax/MathJax.js?config=TeX-AMS-MML_HTMLorMML";
			mathjax_script_tag.type = "text/javascript";
			mathjax_script_tag.charset = "utf-8";
			mathjax_script_tag.async = true;
			
			mathjax_script_tag.onload = function() {
				$wnd.MathJax.Hub.Startup.onload();
				$wnd.MathJax.Hub.Typeset(e);
			};
			
			var head = $doc.getElementsByTagName("head")[0];
			head.appendChild(mathjax_script_tag);
		}
		else
		{
			if (typeof $wnd.MathJax !== 'undefined'  &&  $wnd.MathJax != null)
			{
				if (typeof $wnd.MathJax.Hub !== 'undefined'  &&  $wnd.MathJax.Hub != null)
				{
					if (typeof $wnd.MathJax.Hub.Typeset == 'function')
						$wnd.MathJax.Hub.Typeset(e);
				}
			}
		}
	}-*/;
}
