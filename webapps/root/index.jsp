<!-- 
*
* Enterprise RCP/OSGi Applications Tutorial root HTML file
*
* Copyright (c) 2011 David Orme
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     David Orme - initial implementation
*
* Note:
* Yes, I know this HTML/JSP does a whole lot of deprecated stuff.  It's
* just a quick-and-dirty way to get lots of tutorial materials out to
* lots of people as quickly as possible.  :-)
* 
-->
<html>
<%@ page import="java.net.*,java.util.*" %>
<%
   int serverPort = request.getServerPort();
   Enumeration<NetworkInterface> nets = 
            NetworkInterface.getNetworkInterfaces();

   String sourceUrlTable="";

   for (NetworkInterface netint : Collections.list(nets)) {
      String displayName = netint.getDisplayName();
      if ("lo".equals(displayName)) continue;

      Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
   
      for (InetAddress inetAddress : Collections.list(inetAddresses)) {
         if (inetAddress instanceof Inet4Address) {
            sourceUrlTable += "<tr><td>" + displayName + 
                   ":</td><td>http:/" + inetAddress + ":" + 
                   serverPort + "/building_block_tut.zip</td></tr>\n";
         }
      }
   }
   %>
<head>
  <title>Architecture Patterns and Code Templates for Enterprise RCP/OSGi Applications</title>
</head>
<body background="slides/src-icons/building-blocks-background.jpg">

<table align="center" width="90%" border="0"><tr><td>

  <img src="slides/src-icons/blocks-cropped.jpg" align="right"/>

  <h1>Architecture Patterns and Code Templates for Enterprise RCP/OSGi Applications</h1>

  <p><b>By David Orme and Patrick Paulin</b><br/>
  EclipseCon 2011<br/>
    Licensed under the terms of the 
    <a href="epl-v10.html">Eclipse Public License</a>.</p>

  <h2>You can share with your neighbor!</h2>

  <p>If you are connected to the network, you can give your neighbor a
    copy of this lab by (a) placing the zip file you downloaded into
    the <font face="courier">webapps/root</font> folder and (b) having
    him/her point their web browser at the web address corresponding
    with your wireless network interface.  If you are connected, the
    correct URL should be listed below:</p>

  <table border="0">
      <%= sourceUrlTable %>
  </table>

  <h2>Prerequisites</h2>

  <p>These labs assume that the following prerequisites are installed
  and configured.</p>

  <ul>
    <li><a target="eclipse" href="http://www.eclipse.org/downloads/">
	Eclipse 3.6.2</a> 
      or later.</li>
    <li><a href="maven-dist/apache-maven-3.0.3-bin.zip">Maven 3.x</a>,
      with <a href="http://maven.apache.org/download.html#Installation">all
      environment variables configured</a>.</li>
    <li>Optional: Chrome, Firefox, or Safari for SVG content on this
      web site to work properly.</li>
  </ul>

  <h2>Introduction</h2>

  <p>As promised, this session continues the discission around
    the <i>OSGi Building Block</i> pattern that we began 
    <a target="visualeditor" href="http://www.coconut-palm-software.com/the_new_visual_editor/doku.php?id=blog:the_osgi_and_rcp_impedance_mismatch">
    on Dave's blog</a>.  In this session, we will describe how to
    apply this pattern to make OSGi and RCP development much simpler
    to understand.</p>

  <p>Since the ratio of instructors to attendees is expected to be
  low, we ask that attendees who complete labs ahead of others please
  kindly get up and assist those who might need or want help.</p>

  <p>At the beginning of each lab, please click on the appropriate
  link below and follow the directions.</p>

  <h2>Lab Instructions</h2>
  <ul>
    <li><a href="lab-1-instructions.pdf">Lab 1 - Creating a properly shaped RCP application</a></li>
    <li><a href="lab-2-instructions.pdf">Lab 2 - Auto-updating an RCP application</a></li>
    <li><a href="lab-3-instructions.pdf">Lab 3 - Building with Maven/Tycho</a></li>
    <li><a href="lab-4-instructions.pdf">Lab 4 - Consuming your own build</a></li>
  </ul>

  <h2>Summary/Conclusion</h2>

  <p><b>Thank you</b></p>

  <p>Thank you for joining us for <i>Architecture Patterns and Code
  Templates for Enterprise RCP/OSGi Applications.</i>  The following
  may answer a few remaining questions you may have concerning why we
  approached the lab as we did.  If you have any questions, please
  don't hesitate to contact us.  We are both available for Eclipse
  RCP and OSGi consultancy engagements.</p>

  <p>
    <blockquote>
      David Orme &lt;djo@coconut-palm-software.com><br/>
      Patrick Paulin &lt;patrick@modumind.com>
    </blockquote>
  </p>

  <p><b>Why a web application?</b></p>

  <p>This lab's materials are distributed as a web
  container/application:

    <ul>
      <li>To demonstrate a technique: By having your CI build location
      inside a web container's namespace, builds that output P2
      repositories automatically become available/consumable by
      downstream builds at well-known URLs.</li><br/>

      <li>For this lab: No matter where you unpack the labs in your
      file system, HTTP URLs will always be rooted at the same place
      in the HTTP namespace.  Thus, our lab instructions could always
      use the same path, regardless of the attendee's specific
      platform or Java environment.</li>
    </ul>

  </p>

  <p><b>Where can I get the slide deck for this session?</b></p>

  <p>Here:</p>

  <ul>
    <li><a href="slides/RCP%20Arch%20Patterns.svg">Session Slides</a></li>
  </ul>

</td></tr></table>
  
</body>
</html>
