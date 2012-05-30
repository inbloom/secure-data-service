<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>Ed-Fi-Core - SLC Data Store Mapping</title>
			</head>
			<body>
				<xsl:for-each select="mappings">
					<table border='1'>
						<tr>
							<th colspan="2">
								<xsl:for-each select="lhsModel">
									<xsl:value-of select="concat(name,' ',version)"></xsl:value-of>
								</xsl:for-each>
							</th>
							<th colspan="2">
								<xsl:for-each select="rhsModel">
									<xsl:value-of select="concat(name,' ',version)"></xsl:value-of>
								</xsl:for-each>
							</th>
						</tr>
						<tr>
							<th>Class</th>
							<th>Attribute</th>
							<th>Class</th>
							<th>Attribute</th>
							<th>Status</th>
							<th>Comment</th>
						</tr>
						<xsl:for-each select="mapping">
							<xsl:sort select="lhs/type"></xsl:sort>
							<xsl:sort select="lhs/name"></xsl:sort>
							<xsl:if test="true() or contains(status,'unknown')">
								<tr>
									<xsl:for-each select="lhs">
										<td>
											<xsl:value-of select="type"></xsl:value-of>
										</td>
										<td>
											<xsl:value-of select="name"></xsl:value-of>
										</td>
									</xsl:for-each>
									<xsl:for-each select="lhsMissing">
										<td>
											<xsl:text>?</xsl:text>
										</td>
										<td>
											<xsl:text>?</xsl:text>
										</td>
									</xsl:for-each>
									<xsl:for-each select="rhs">
										<td>
											<xsl:value-of select="type"></xsl:value-of>
										</td>
										<td>
											<xsl:value-of select="name"></xsl:value-of>
										</td>
									</xsl:for-each>
									<xsl:for-each select="rhsMissing">
										<td>
											<xsl:text>?</xsl:text>
										</td>
										<td>
											<xsl:text>?</xsl:text>
										</td>
									</xsl:for-each>
									<td>
										<xsl:value-of select="status"></xsl:value-of>
									</td>
									<td>
										<xsl:value-of select="comment"></xsl:value-of>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</table>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>