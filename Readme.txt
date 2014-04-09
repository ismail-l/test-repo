-================= Autshumato TMS =================-

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

Autshumato Terminology Management Solution (TMS) is a free web application for the management of multilingual terminology databases (termbanks).

Version: 2.0.0
Platform: The application is web based and in essence platform independent. It is however recommended that the application be accessed using the latest version of the Mozilla Firefox browser or Google Chrome.
License: Apache License version 2.0
The license is contained in the "APACHE LICENSE-2.0.txt" document. The components used and equivalent licenses are listed in the "TMS Components & Licenses.txt" document. 
The licenses for the various components are also available in the "/Documentation/Licenses/" directory.

Project page: http://sourceforge.net/projects/autshumatotms
Developer: Centre for Text Technology (CTexT®) at the North-West University (Potchefstroom Campus) in collaboration with the University of Pretoria.

Documentation:
Readme: This document
Server Setup: TMS Server Setup.odt
Help Guide: /Help/index.html
Components & Licenses: TMS Components & Licenses.txt
License: APACHE LICENSE-2.0.txt
Component Licenses: /Documentation/Licenses/
Scripts: /Documentation


The complete source code and binaries can be downloaded from the project page. The project page also allows users to notify the project team of bugs and submit feature requests. Check it regularly for the latest distribution.

-=== Build ===-
To build the application from the source code the following components are required:
- Eclipse IDE
	Although the application was created using Eclipse most IDEs should be able to build the application if all the requirements are met.
- PostgreSQL 8.4 (or later)
	The database for the system is a PostgreSQL database.
	This can be downloaded from: http://www.postgresql.org/
- Google Web Toolkit SDK 2.5.1 or later
	This can be downloaded from: http://code.google.com/webtoolkit/
	Complete installation instructions can also be found at the abovementioned website.

Download and install Eclipse or the IDE of your choice. 

Go to the Google Web Toolkit website (http://code.google.com/webtoolkit/), download and install the GWT SDK and all relevant plugins. Detailed steps on how this is done are available on the website. 

Download and extract the Autshumato TMS source code from the project page: http://sourceforge.net/projects/autshumatotms

Download PostgreSQL from http://www.postgresql.org/. Install PostgreSQL and define the default user as "postgres" with the password of your choice. 

Run the pgAdmin tool that should have been installed with PostgreSQL. Use this tool to create a new database named "tms2". Open the SQL tool of pgAdmin and load the file named SQL.DACB2.TMS-DB-Cleaned-with-Fields.sql and run this script.

The default database will now load into PostgreSQL. This database contains the default administrator user, user categories, fields and presets.

Using this tool load the file named SQL.DACB2.TMS-Create-Role-Groups.sql and run this script. Scan this file for this comment "-- Change this password to match the postgres user password" and change the password to the password of the postgres user.

Take note if your database system has the following group roles and roles:

	* guest_group
	* user_group
	* guest_role
	* user_role
	
If this is true, scan the file for this comment "-- This can be commented out if Postgres already has this ROLE". Comment the single line just after this comment and then run the script.

Open a terminal (or console) and run the script named Script.DACB2.vacuum_analyze. This script takes 3 arguments namely, the database name [tms2], the super user name [postgres] and the password of this super user[password].

Edit the "WEB-INF/classes/AppConfig.properties" file and change the password to the password you specified when installing PostgreSQL. (The username can also be changed in this way.)

Take note that all the scripts can be found at /Documentation.

Start Eclipse (or your choice IDE) and import the TMS project into a workspace.
If everything has been installed and configured correctly, you should be able to run the TMS project as a web application.
The first time that you run the application in development mode your browser will prompt you to install the gwt plugin. Once installation has been completed, the system will load and run in the browser.

If you have any questions or problems post a message on the project page.


-=== Run ===-
- The application must have been set up on a server with the database and configuration files properly implemented. This procedure is available in the "/Documentation/TMS Server Setup.odt" document.
- Internet browser
	We recommend using the latest Mozilla Firefox browser or Google Chrome.
- Address to the application hosted on a server.
	You should be provided with the address of the application on a server from the person responsible for installing the application.


-=== Using the Application ===-
For a complete guide on using the Autshumato TMS refer to the Help documentation, which can be found in the /Help/ directory. The Help guide is also be available through the application.

The system can be accessed by anyone but without a valid user account you will have limited functionality. A signed-on user will enjoy most of the functionality determined by the access rights of the specific user or user category to which the user belongs.

The administrator of the system is responsible for creating users, user categories, fields and presets, as well as the assigning of access rights.


-=== Questions, Errors, etc. ===-
Kindly post any questions or bug reports on the project page.
