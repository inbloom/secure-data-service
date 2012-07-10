<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of
						select="concat(mappings/lhsModel/name,' ',mappings/lhsModel/version,' (LHS)')"></xsl:value-of>
					<xsl:value-of select="concat('&lt;','=','&gt;')"></xsl:value-of>
					<xsl:value-of
						select="concat(mappings/rhsModel/name,' ',mappings/rhsModel/version, ' (RHS)')"></xsl:value-of>
				</title>
				<link rel="StyleSheet" href="xmi-mapping.css" type="text/css"
					media="screen, print"></link>
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
							<th>Feature</th>
							<th>Class</th>
							<th>Feature</th>
							<th>Status</th>
							<th>Comment</th>
						</tr>
						<xsl:for-each select="mapping">
							<xsl:sort select="lhs/type"></xsl:sort>
							<xsl:sort select="rhs/type"></xsl:sort>
							<xsl:sort select="lhs/name"></xsl:sort>
							<xsl:sort select="rhs/name"></xsl:sort>
							<xsl:if test="not(contains(status,'ignorable'))">
								<tr>
									<xsl:for-each select="lhs">
										<xsl:choose>
											<xsl:when
												test="(string(../rhs/type) = string(type)) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="type"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td>
													<xsl:value-of select="type"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:choose>
											<xsl:when
												test="(string(../rhs/name) = string(name)) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td class="warning">
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
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
										<xsl:choose>
											<xsl:when
												test="(string(../lhs/type) = string(type)) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="type"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td class="warning">
													<xsl:value-of select="type"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:choose>
											<xsl:when
												test="(translate(string(../lhs/name),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz') = translate(string(name),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td class="warning">
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
									<xsl:for-each select="rhsMissing">
										<td>
											<xsl:text>?</xsl:text>
										</td>
										<td>
											<xsl:text>?</xsl:text>
										</td>
									</xsl:for-each>
									<xsl:choose>
										<xsl:when test="status='match'">
											<td class='info'>
												<xsl:value-of select="status"></xsl:value-of>
											</td>
										</xsl:when>
										<xsl:when test="status='align'">
											<td class='error'>
												<xsl:value-of select="status"></xsl:value-of>
											</td>
										</xsl:when>
										<xsl:when test="status='unknown'">
											<td class='error'>
												<xsl:value-of select="status"></xsl:value-of>
											</td>
										</xsl:when>
										<xsl:otherwise>
											<td class='error'>
												<xsl:value-of select="status"></xsl:value-of>
											</td>
										</xsl:otherwise>
									</xsl:choose>
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