<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:wadl="http://wadl.dev.java.net/2009/02" 
    xmlns="http://docbook.org/ns/docbook" version="1.0">
    
    <xsl:output method="text" encoding="UTF-8"/>
    
    <xsl:template match="wadl:application">
<xsl:for-each select="wadl:resources/wadl:resource">
<xsl:sort select="wadl:resource" order="ascending"/>
<xsl:variable name="resourcePath" select="substring-after(@path,'v1/')"/>
<xsl:call-template name="iterateResourcePath">
<xsl:with-param name="resourcePath" select="$resourcePath"/>
</xsl:call-template>
</xsl:for-each>
    </xsl:template>
    
    <xsl:template name="iterateResourcePath">
        <xsl:param name="resourcePath"/>
<xsl:value-of select="concat($resourcePath,'&#xA;')"/>
<xsl:for-each select="wadl:resource">
<xsl:sort select="wadl:resource" order="ascending"/>
<!-- <xsl:value-of select="concat($resourcePath,'/',@path,'&#xA;')"/> -->
<xsl:variable name="nextResourcePath" select="concat($resourcePath,'/',@path)"/>
<xsl:call-template name="iterateResourcePath">
<xsl:with-param name="resourcePath" select="$nextResourcePath"/>
</xsl:call-template>
</xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
