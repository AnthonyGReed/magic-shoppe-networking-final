# Magic Shoppe Application

## Pre-requisites
Before  you begin, make sure you have an instance of MySQL installed and running on your machine. You will need a valid 
account with read/write privileges in order to run the database setup program.

I have used three external libraries for this project:
- Apache Commons https://commons.apache.org/proper/commons-lang/download_lang.cgi
- GSON https://search.maven.org/artifact/com.google.code.gson/gson/2.8.6/jar
- MySQL Connector https://www.mysql.com/products/connector/

There is no maven or gradle repository management on this project. I was trying to go as close to pure java as I could.
Please download the external libraries at the links above and include them in this project to compile it.

This project was built in IntelliJ. Build instructions below are for using IntelliJ to run the application.

To make any changes to the front end you will need Node.js installed on your machine to gain access to the Node Package 
Manager

## Set-up
### Network Constants
Before you compile anything open the NetworkConstants.java file found in ***src/main/java/config*** set up your
- application port
- application host
- mysql connection string
- mysql username
- mysql password
- schema name (default: magic_shoppe)

Then compile all the classes.

### DB Setup
Run the Installer.class located in ***src/main/java/dbSetup/***. This should build out your database with five tables:
- items
- potions
- sessions
- spells
- storeditems

## Front End
The front end uses React.js which makes transpiled static files for output. In order to make any changes to the
frontend you will have to navigate to the folder ***src/main/java/webserver/pages/frontend*** in your terminal and 
use the command

    npm install

This will use the package.json file to install all the packages needed to transpile the enclosed JSX code. The relevant
React files to view or modify when making changes to the front end are
- App.js
- containers/Shop/Shop.js
- components/Items/Items.js
- components/Main/Main.js
- components/Potions/Potions.js
- components/Scrolls/Scrolls.js

Once you have made whatever changes you are looking to make, run the command

    npm run build

This will transpile all the code into the output folder that the webserver will be looking at for its static files.

Delete the folder "node_modules" after you complete this process if you have not yet compiled the 

## Back End
Run Server.class located at ***src/main/java/webserver*** to start the application. Navigate a browser to the host and
port defined in the Network Constants file. The Application should be available.