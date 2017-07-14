<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="html" indent="yes"
        omit-xml-declaration="yes"
        encoding="utf-8"
        doctype-system="about:legacy-compat"
        />

    <xsl:param name="header"/>
    <xsl:param name="index-file"/>

    <xsl:template match="html">
        <html lang="en">
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <xsl:template match="body">
        <body>
            <div id="wrapper">
                <div id="topbar">
                    <xsl:copy-of select="document($header)"/>
                </div>
                <div id="sidebar">

                    <xsl:variable name="this-doc"
                        select="/html/head/title/text()"/>
                        
                    <xsl:variable name="menu"
                        select="document($index-file)"/>

                    <ul>
                        <xsl:for-each select="$menu/index/group">
                            <li>
                                <h3>
                                    <xsl:value-of select="@group-title"/>
                                </h3>
                                <ul>
                                <xsl:for-each select="page">

                                    <xsl:if test="@page-title=$this-doc">
                                        <li class="selected">
                                            <xsl:value-of
                                                select="@index-title"/>
                                        </li>
                                    </xsl:if>
                                    <xsl:if test="@page-title!=$this-doc">
                                        <li>
                                            <a>
                                                <xsl:attribute
                                                    name="href">
                                            <xsl:value-of
                                                    select="@index-href"/>
                                        </xsl:attribute>
                                                <xsl:value-of
                                                    select="@index-title"/>
                                            </a>
                                        </li>
                                    </xsl:if>
                                </xsl:for-each>
                                </ul>
                            </li>
                        </xsl:for-each>
                    </ul>

                </div>

                <div id="main">
                    <xsl:copy-of select="node()"/>
                </div>

                <div id="footer">
                    (c) Rob Gordon 2005-2017
                </div>

            </div>
        </body>
    </xsl:template>

    <xsl:template match="head">
        <head>
            <xsl:copy-of select="node()"/>
            <link rel="StyleSheet" href="oddjob.css" type="text/css"/>
        </head>
    </xsl:template>


</xsl:stylesheet>
