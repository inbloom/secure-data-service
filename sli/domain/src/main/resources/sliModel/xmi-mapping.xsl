<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of
						select="concat(mappings/lhs-model/name,' ',mappings/lhs-model/version,' (LHS)')"></xsl:value-of>
					<xsl:value-of select="concat('&lt;','=','&gt;')"></xsl:value-of>
					<xsl:value-of
						select="concat(mappings/rhs-model/name,' ',mappings/rhs-model/version, ' (RHS)')"></xsl:value-of>
				</title>
				<link rel="StyleSheet" href="xmi-mapping.css" type="text/css"
					media="screen, print"></link>
			</head>
			<body>
				<xsl:for-each select="mappings">
					<table border='1'>
						<tr>
							<th colspan="2">
								<xsl:for-each select="lhs-model">
									<xsl:value-of select="concat(name,' ',version)"></xsl:value-of>
								</xsl:for-each>
							</th>
							<th colspan="2">
								<xsl:for-each select="rhs-model">
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
							<xsl:sort select="lhs/owner-name"></xsl:sort>
							<xsl:sort select="lhs/name"></xsl:sort>
							<xsl:sort select="rhs/owner-name"></xsl:sort>
							<xsl:sort select="rhs/name"></xsl:sort>
							<xsl:if test="not(contains(status,'ignorable'))">
								<tr>
									<xsl:for-each select="lhs">
										<xsl:choose>
											<xsl:when test="not(string(owner-exists)='true')">
												<td class="error">
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:when
												test="(string(../rhs/owner-name) = string(owner-name)) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td>
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="not(string(exists)='true')">
												<td class="error">
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:when>
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
									<xsl:for-each select="lhs-missing">
										<td>
											<xsl:text>?</xsl:text>
										</td>
										<td>
											<xsl:text>?</xsl:text>
										</td>
									</xsl:for-each>
									<xsl:for-each select="rhs">
										<xsl:choose>
											<xsl:when test="not(string(owner-exists)='true')">
												<td class="error">
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:when
												test="(string(../lhs/owner-name) = string(owner-name)) or contains(../status,'unknown')">
												<td>
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:when>
											<xsl:otherwise>
												<td class="warning">
													<xsl:value-of select="owner-name"></xsl:value-of>
												</td>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:choose>
											<xsl:when test="not(string(exists)='true')">
												<td class="error">
													<xsl:value-of select="name"></xsl:value-of>
												</td>
											</xsl:when>
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
									<xsl:for-each select="rhs-missing">
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