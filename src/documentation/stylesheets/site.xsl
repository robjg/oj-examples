<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                version="1.0">

    <xsl:output method="html" indent="yes"/>

    <xsl:param name="header"/>
    <xsl:param name="index-file"/>

<xsl:template match="html">
    <html>
        <xsl:apply-templates/>
    </html>
</xsl:template>

<xsl:template match="body">
    <body>
        <table border="0" cellspacing="0">
            <tr>
                <td id="topbar" colspan="2">
                    <xsl:copy-of select="document($header)"/>
                </td>
            </tr>
            <tr>
                <td valign="top" id="sidebar">
                    <p>
    					<xsl:variable name="this-doc" select="/html/head/title/text()"/>
                        <xsl:variable name="menu" select="document($index-file)"/>
    					<xsl:for-each select="$menu//*">
    						<xsl:if test="name(current())='page'">
 								<xsl:if test="@page-title=$this-doc">
      								<span class="selected">
										<xsl:value-of select="@index-title"/>
									</span>
								</xsl:if>
								<xsl:if test="@page-title!=$this-doc">
									<a>
          								<xsl:attribute name="href">
             								<xsl:value-of select="@index-href"/>
          								</xsl:attribute>
          								<xsl:value-of select="@index-title"/>
        							</a>
								</xsl:if>
      						<br/>
							</xsl:if>
							<xsl:if test="name(current())='group'">
								<span class="group">
									<xsl:value-of select="@group-title"/>
								</span>
      						<br/>
							</xsl:if>
    					</xsl:for-each>
                    </p>
                </td>
                <td valign="top" id="main">
                    <xsl:copy-of select="node()"/>
                </td>
            </tr>
        </table>
    </body>
</xsl:template>

<xsl:template match="head">
    <head>
        <xsl:copy-of select="node()"/>
        <style type="text/css">
#topbar {
	background: black; 
	width: 800px;
	heigh: 400px;
	padding-top: 0.5em; 
	padding-left: 0.5em;
	padding-bottom: 1em;
}

#topbar h1 {
 	color: white; 
	font: 35pt bold; 
	margin-bottom: 0;
}

#topbar a {
	color: yellow;
	text-decoration: none; 
}

#topbar a:hover {
	color: white; 
}

/* sidebar */

#sidebar {
	background: green; 
	width: 120px;
	height: 600px;
	padding-top: 0.5em;
	padding-left: 0.5em;
	padding-right: 0.5em;
}

#sidebar .group {
	color: white;
	font-weight: bold;
}

#sidebar .selected {
	color: red;
	text-decoration: none; 
	padding: 0 0 0 0.5em;
	font-size: smaller;
}

#sidebar a {
	color: yellow; 
	text-decoration: none; 
	padding: 0 0 0 0.5em;
	font-size: smaller;
}

#sidebar a:hover {
	color: white; 
}

/* main */

#main {
 	padding: 0.7em; 
	width: 680px;
	font-family: verdana helvetica, arial, sans-serif;
	font-size: 0.92em;
}
#main h1 {
	text-align: center; 
}
#main td {
	font-family: verdana helvetica, arial, sans-serif;
	font-size: 0.92em;
}
			</style>
    </head>

</xsl:template>


</xsl:stylesheet>
