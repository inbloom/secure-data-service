<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
 xmlns:wadl="http://wadl.dev.java.net/2009/02"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 xmlns:html="http://www.w3.org/1999/xhtml"
 xmlns="http://www.w3.org/1999/xhtml"
>

<!-- Global variables -->
<xsl:variable name="g_resourcesBase" select="wadl:application/wadl:resources/@base"/>

<!-- Template for top-level doc element -->
<xsl:template match="wadl:application">

  <html>
    <head>
        <xsl:call-template name="getStyle"/>
        <title><xsl:call-template name="getTitle"/></title>
    </head>
    <body>

        <div id="header">
            <h1><xsl:call-template name="getTitle"/></h1>
            <xsl:call-template name="getDoc">
                <xsl:with-param name="base" 
                                select="$g_resourcesBase"/>
            </xsl:call-template>
        </div> <!-- END of "header" -->

        <div id="main">
            <div id="resource-list">
                <h2>Resources</h2>
                <ul>
                    <xsl:for-each select="wadl:resources/wadl:resource">
                        <xsl:call-template name="processResourceSummary">
                            <!-- <xsl:with-param name="resourceBase" select="$g_resourcesBase"/> -->
                            <xsl:with-param name="resourcePath" select="@path"/> 
                            <xsl:with-param name="lastResource" select="position() = last()"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </ul>
            </div> <!-- END of "resource-list" -->

            <div id="resource-details">
    
                <!-- Grammars -->
                <xsl:if test="wadl:grammars/wadl:include">
                    <h2>Grammars</h2>
                    <p>
                        <xsl:for-each select="wadl:grammars/wadl:include">
                            <xsl:variable name="href" select="@href"/>
                                <a href="{$href}">
                                    <xsl:value-of select="$href"/>
                                </a>
                                <xsl:if test="position() != last()">
                                    <br/>
                                </xsl:if> <!-- Add a spacer -->
                        </xsl:for-each>
                    </p>
                </xsl:if>

                <!-- Detail -->
                <h2>Resource Details</h2>
                <xsl:for-each select="wadl:resources">
                    <xsl:call-template name="getDoc">
                        <xsl:with-param name="base" select="$g_resourcesBase"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:for-each select="wadl:resources/wadl:resource">
                    <xsl:call-template name="processResourceDetail">
                        <xsl:with-param name="resourceBase" select="$g_resourcesBase"/>
                        <xsl:with-param name="resourcePath" select="@path"/>
                    </xsl:call-template> 
                </xsl:for-each>

            </div> <!-- END of "resource-details" -->

        </div> <!-- END of "main" -->

        <!-- <div id="footer">
        </div> --> <!-- END of "footer" -->

    </body>
  </html>
</xsl:template>

<!-- Supporting templates (functions) -->

<xsl:template name="processResourceSummary">
    <xsl:param name="resourceBase"/>
    <xsl:param name="resourcePath"/>
    <xsl:param name="lastResource"/>

    <xsl:if test="wadl:method">
        <!-- Individual resource listing -->
        <li>
            <xsl:variable name="id">
                <xsl:call-template name="getId"/>
            </xsl:variable>
            <a href="#{$id}">
                <xsl:call-template name="getFullResourcePath">
                    <xsl:with-param name="base" select="$resourceBase"/>
                    <xsl:with-param name="path" select="$resourcePath"/>
                </xsl:call-template>
            </a>
        </li>
    </xsl:if> <!-- wadl:method -->

    <!-- Call recursively for child resources -->
    <xsl:for-each select="wadl:resource">
        <xsl:variable name="base">
            <xsl:call-template name="getFullResourcePath">
                <xsl:with-param name="base" select="$resourceBase"/>
                <xsl:with-param name="path" select="$resourcePath"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="processResourceSummary">
            <xsl:with-param name="resourceBase" select="$base"/>
            <xsl:with-param name="resourcePath" select="@path"/>
            <xsl:with-param name="lastResource" select="$lastResource and position() = last()"/>
        </xsl:call-template>
    </xsl:for-each>

</xsl:template>

<xsl:template name="processResourceDetail">
    <xsl:param name="resourceBase"/>
    <xsl:param name="resourcePath"/>

    <xsl:if test="wadl:method">
    <div class="resourceDetailBlock">
        <h3>
            <xsl:variable name="id">
                <xsl:call-template name="getId"/>
            </xsl:variable>
            <a name="{$id}">
                <xsl:call-template name="getFullResourcePath">
                    <xsl:with-param name="base" select="$resourceBase"/>
                    <xsl:with-param name="path" select="$resourcePath"/>
                </xsl:call-template>
            </a>
        </h3>
        <p>
            <xsl:call-template name="getDoc">
                <xsl:with-param name="base" select="$resourceBase"/>
            </xsl:call-template>
        </p>

        <h5>Methods</h5>

        <!-- Listing of methods for each resource -->
        <xsl:for-each select="wadl:method">
        <div class="method">
            <div class="methodName">
                <xsl:variable name="name" select="@name"/>
                <xsl:variable name="id2">
                    <xsl:call-template name="getId"/>
                </xsl:variable>
                <a name="{$id2}">
                    <xsl:value-of select="$name"/>
                </a>
            </div>
            <p>
                <xsl:call-template name="getDoc">
                    <xsl:with-param name="base" select="$resourceBase"/>
                </xsl:call-template>
            </p>

            <!-- Request information for each method -->
            <h6>request</h6>
            <div class="request-listing"> 
                <xsl:choose>
                    <xsl:when test="wadl:request">
                        <xsl:for-each select="wadl:request">
                            <xsl:call-template name="getParamBlock">
                                <xsl:with-param name="style" select="'template'"/>
                            </xsl:call-template>
                            <xsl:call-template name="getParamBlock">
                                <xsl:with-param name="style" select="'matrix'"/>
                            </xsl:call-template>
                            <xsl:call-template name="getParamBlock">
                                <xsl:with-param name="style" select="'header'"/>
                            </xsl:call-template>
                            <xsl:call-template name="getParamBlock">
                                <xsl:with-param name="style" select="'query'"/>
                            </xsl:call-template>
                            <xsl:call-template name="getRepresentations"/>
                        </xsl:for-each> <!-- wadl:request -->
                    </xsl:when>
                    <xsl:when test="not(wadl:request) and (ancestor::wadl:*/wadl:param)">
                        <xsl:call-template name="getParamBlock">
                            <xsl:with-param name="style" select="'template'"/>
                        </xsl:call-template>
                        <xsl:call-template name="getParamBlock">
                            <xsl:with-param name="style" select="'matrix'"/>
                        </xsl:call-template>
                        <xsl:call-template name="getParamBlock">
                            <xsl:with-param name="style" select="'header'"/>
                        </xsl:call-template>
                        <xsl:call-template name="getParamBlock">
                            <xsl:with-param name="style" select="'query'"/>
                        </xsl:call-template>
                        <xsl:call-template name="getRepresentations"/>
                    </xsl:when>
                    <xsl:otherwise>
                        unspecified
                    </xsl:otherwise>
                </xsl:choose>
            </div> <!-- END of "request-listing" -->
                                
            <!-- Response information for each method -->
            <h6>responses</h6>
            <div class="response-listing"> 
                <xsl:choose>
                    <xsl:when test="wadl:response">
                        <xsl:for-each select="wadl:response">
                            <div class="infoBlockTitle">
                                status: 
                            </div>
                            <xsl:choose>
                                <xsl:when test="@status">
                                    <xsl:value-of select="@status"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    200 - OK
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:for-each select="wadl:doc">
                                <xsl:if test="@title">
                                    - <xsl:value-of select="@title"/>
                                </xsl:if>
                                <xsl:if test="text()">
                                    - <xsl:value-of select="text()"/>
                                </xsl:if>
                            </xsl:for-each> <!-- wadl:doc -->
                            <xsl:if test="wadl:param or wadl:representation">
                                <xsl:if test="wadl:param">
                                    <div class="infoBlockTitle">
                                        headers
                                    </div>
                                    <table>
                                        <xsl:for-each select="wadl:param[@style='header']">
                                            <xsl:call-template name="getParams"/>
                                        </xsl:for-each>
                                    </table>
                                </xsl:if>
                                <xsl:call-template name="getRepresentations"/>
                            </xsl:if>
                        </xsl:for-each> <!-- wadl:response -->
                    </xsl:when>
                    <xsl:otherwise>
                        unspecified
                    </xsl:otherwise>
                </xsl:choose>
            </div> <!-- END of "response-listing" -->

        </div> <!-- END of "method" -->
    </xsl:for-each> <!-- wadl:method -->
    </div> <!-- END of "resourceDetailBlock" -->

</xsl:if> <!-- wadl:method -->

    <!-- Call recursively for child resources -->
    <xsl:for-each select="wadl:resource">
        <xsl:variable name="base">
            <xsl:call-template name="getFullResourcePath">
                <xsl:with-param name="base" select="$resourceBase"/>
                <xsl:with-param name="path" select="$resourcePath"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="processResourceDetail">
            <xsl:with-param name="resourceBase" select="$base"/>
            <xsl:with-param name="resourcePath" select="@path"/>
        </xsl:call-template>
    </xsl:for-each> <!-- wadl:resource -->
</xsl:template>

<xsl:template name="getFullResourcePath">
    <xsl:param name="base"/>
    <xsl:param name="path"/>
    <xsl:choose>
        <xsl:when test="substring($base, string-length($base)) = '/'">
            <xsl:value-of select="$base"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="concat($base, '/')"/>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
        <xsl:when test="starts-with($path, '/')">
            <xsl:value-of select="substring($path, 2)"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$path"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="getDoc">
    <xsl:param name="base"/>
    <xsl:for-each select="wadl:doc">
        <xsl:if test="position() > 1"><br/></xsl:if>
        <xsl:if test="@title and local-name(..) != 'application'">
            <xsl:value-of select="@title"/>:
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@title = 'Example'">
                <xsl:variable name="url">
                    <xsl:choose>
                        <xsl:when test="string-length($base) > 0">
                            <xsl:call-template name="getFullResourcePath">
                                <xsl:with-param name="base" select="$base"/>
                                <xsl:with-param name="path" select="text()"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise><xsl:value-of select="text()"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <a href="{$url}"><xsl:value-of select="$url"/></a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="text()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
</xsl:template>

<xsl:template name="getId">
    <xsl:choose>
        <xsl:when test="@id">
            <xsl:value-of select="@id"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
    </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="getParamBlock">
    <xsl:param name="style"/>
    <xsl:if test="ancestor-or-self::wadl:*/wadl:param[@style=$style]">
        <div class="paramBlock">
            <div class="infoBlockTitle">
                <xsl:value-of select="$style"/> parameters:
            </div>
            <table>
                <xsl:for-each select="ancestor-or-self::wadl:*/wadl:param[@style=$style]">
                    <xsl:call-template name="getParams"/>
                </xsl:for-each>
            </table>
        </div>
    </xsl:if>
</xsl:template>

<xsl:template name="getParams">
    <tr>
        <td><strong><xsl:value-of select="@name"/></strong></td>
            <td>
                <xsl:if test="not(@type)">
                    unspecified type
                </xsl:if>
                <xsl:call-template name="getParamType">
                    <xsl:with-param name="qname" select="@type"/>
                </xsl:call-template>
                <xsl:if test="@required = 'true'"><br/>(required)</xsl:if>
                <xsl:if test="@repeating = 'true'"><br/>(repeating)</xsl:if>
                <xsl:if test="@default"><br/>default: <tt><xsl:value-of select="@default"/></tt></xsl:if>
                <xsl:if test="@fixed"><br/>fixed: <tt><xsl:value-of select="@fixed"/></tt></xsl:if>
                <xsl:if test="wadl:option">
                    <br/>options:
                    <xsl:for-each select="wadl:option">
                        <xsl:choose>
                            <xsl:when test="@mediaType">
                                <br/><tt><xsl:value-of select="@value"/> (<xsl:value-of select="@mediaType"/>)</tt>
                            </xsl:when>
                            <xsl:otherwise>
                                <tt><xsl:value-of select="@value"/></tt>
                                <xsl:if test="position() != last()">, </xsl:if>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:if>
            </td>
        <xsl:if test="wadl:doc">
            <td><xsl:value-of select="wadl:doc"/></td>
        </xsl:if>
    </tr>
</xsl:template>

<xsl:template name="getParamType">
    <xsl:param name="qname"/>
    <xsl:variable name="prefix" select="substring-before($qname,':')"/>
    <xsl:variable name="ns-uri" select="./namespace::*[name()=$prefix]"/>
    <xsl:variable name="localname" select="substring-after($qname, ':')"/>
    <xsl:choose>
        <xsl:when test="$ns-uri='http://www.w3.org/2001/XMLSchema' or $ns-uri='http://www.w3.org/2001/XMLSchema-instance'">
            <a href="http://www.w3.org/TR/xmlschema-2/#{$localname}"><xsl:value-of select="$localname"/></a>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$qname"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="getRepresentations">
    <xsl:if test="wadl:representation">
        <div class="infoBlockTitle">representations:</div>
        <table>
            <xsl:for-each select="wadl:representation">
                <tr>
                    <td><xsl:value-of select="@mediaType"/></td>
                    <xsl:if test="wadl:doc">
                        <td>
                            <xsl:call-template name="getDoc">
                                <xsl:with-param name="base" select="''"/>
                            </xsl:call-template>
                        </td>
                    </xsl:if>
                    <xsl:if test="@href or @element">
                        <td>
                            <xsl:variable name="href" select="@href"/>
                            <xsl:choose>
                                <xsl:when test="@href">
                                    <a href="{$href}"><xsl:value-of select="@element"/></a>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="@element"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </xsl:if>
                </tr>
                <xsl:call-template name="getRepresentationParamBlock">
                    <xsl:with-param name="style" select="'template'"/>
                </xsl:call-template>
        
                <xsl:call-template name="getRepresentationParamBlock">
                    <xsl:with-param name="style" select="'matrix'"/>
                </xsl:call-template>
        
                <xsl:call-template name="getRepresentationParamBlock">
                    <xsl:with-param name="style" select="'header'"/>
                </xsl:call-template>
        
                <xsl:call-template name="getRepresentationParamBlock">
                    <xsl:with-param name="style" select="'query'"/>
                </xsl:call-template>
            </xsl:for-each>
        </table>
    </xsl:if>
</xsl:template>

<xsl:template name="getRepresentationParamBlock">
    <xsl:param name="style"/>
    <xsl:if test="wadl:param[@style=$style]">
        <tr>
            <td style="padding: 0em, 0em, 0em, 2em">
                <div class="infoBlockTitle"><xsl:value-of select="$style"/> params</div>
                <table>
                    <xsl:for-each select="wadl:param[@style=$style]">
                        <xsl:call-template name="getParams"/>
                    </xsl:for-each>
                </table>
                <p/>
            </td>
        </tr>
    </xsl:if>
</xsl:template>

<xsl:template name="getStyle">
    <link rel="stylesheet" href="css/wadl.css" media="all" type="text/css" />
</xsl:template>

<xsl:template name="getTitle">
    <xsl:choose>
        <xsl:when test="wadl:doc/@title">
            <xsl:value-of select="wadl:doc/@title"/>
        </xsl:when>
        <xsl:otherwise>
            Web Application
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
