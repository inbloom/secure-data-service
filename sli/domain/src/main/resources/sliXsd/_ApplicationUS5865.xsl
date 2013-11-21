<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sli="http://slc-sli/ed-org/0.1" xmlns:xs="http://www.w3.org/2001/XMLSchema">


    <xsl:output method="xml" encoding="utf-8" indent = "yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:element[not(xs:annotation)]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
            <!-- Read Write Rights for Application Elements Begin-->
            <xs:annotation>
                <xs:appinfo>
                    <sli:WriteEnforcement>
                        <sli:allowedBy>APP_AUTHORIZE</sli:allowedBy>
                        <sli:allowedBy>DEV_APP_CRUD</sli:allowedBy>
                        <sli:allowedBy>SLC_APP_APPROVE</sli:allowedBy>
                    </sli:WriteEnforcement>
                    <sli:ReadEnforcement>
                        <sli:allowedBy>APP_AUTHORIZE</sli:allowedBy>
                        <sli:allowedBy>ADMIN_ACCESS</sli:allowedBy>
                    </sli:ReadEnforcement>
                </xs:appinfo>
            </xs:annotation>
            <!-- Read Write Rights for Application Elements End-->
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>