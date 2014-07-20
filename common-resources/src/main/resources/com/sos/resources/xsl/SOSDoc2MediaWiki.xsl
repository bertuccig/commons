<?xml version="1.0" encoding="utf-8"?>
<!-- $Id: CreateMediaWikiFromSOSDoc.xsl 16514 2012-02-15 10:02:15Z oh $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xi="http://www.w3.org/2001/XInclude"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:jobdoc="http://www.sos-berlin.com/schema/scheduler_job_documentation_v1.1"
	xmlns:sosfn="http://www.sos-berlin.com/sosfn" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="jobdoc xhtml fn xi xs sosfn">

	<xsl:param name="sos.timestamp" select="'xxxxx'" />
	<xsl:param name="sos.doculanguage" select="'en'" />
    <xsl:param name="sos.xslt.dir" select="'./'" />
	<xsl:variable name="formattingMode" select="'includeText'" />

	<xsl:variable name="this.doc" select="/" />
	<xsl:variable name="sos.i18nFileName">
		<xsl:value-of select="concat($sos.xslt.dir, '/mediaWikiText_', $sos.doculanguage, '.xml')"></xsl:value-of>
	</xsl:variable>
	<xsl:variable name="sos.i18n" select="document($sos.i18nFileName)" />
	<xsl:output method="xml" encoding="utf-8" indent="yes"
		standalone="yes" />

	<xsl:variable name="lf" select="'&#xa;'" />
	<xsl:variable name="newline" select="'&#xa;&#x20;&#xa;'" />
	<xsl:variable name="EmptyLine" select="'&#x20;&#xa;&#x20;&#xa;'" />

	<xsl:variable name="heading" select="'=='" />
	<xsl:variable name="heading2" select="'==='" />
	<xsl:variable name="heading3" select="'===='" />
	<xsl:variable name="heading4" select="'====='" />
	<xsl:variable name="hr" select="'----'" />
	<xsl:variable name="dq" select="'&#34;'" />
	<xsl:variable name="italic" select="''''''" />
	<xsl:variable name="bold" select="''''''''" />
	<!-- <xsl:variable name="italic" select="' '" /> <xsl:variable name="bold"
		select="' '" /> -->

	<xsl:variable name="JobName">
		<xsl:value-of select="//jobdoc:job/@name" />
	</xsl:variable>

	<xsl:variable name="JobTitle">
		<xsl:value-of select="//jobdoc:job/@title" />
	</xsl:variable>

	<xsl:variable name="JobCategory">
		<xsl:value-of select="//jobdoc:job/@category" />
	</xsl:variable>

	<xsl:variable name="JobIsDeprecated">
		<xsl:value-of select="//jobdoc:job/@deprecated" />
	</xsl:variable>

	<xsl:variable name="JobIsActiveSince">
		<xsl:value-of select="//jobdoc:job/@since" />
	</xsl:variable>

	<xsl:function name="sosfn:wikilink" as="xs:string">
		<xsl:param name="arg" />
		<xsl:value-of select="concat('[[#', $arg, '|', $arg, ']]')" />
	</xsl:function>

	<xsl:function name="sosfn:i18n" as="xs:string">
		<xsl:param name="key" as="xs:string" />
		<xsl:value-of select="$sos.i18n//items/item[@id=$key] " />
	</xsl:function>

	<xsl:function name="sosfn:bold" as="xs:string">
		<xsl:param name="pText" />
		<xsl:value-of select="concat($bold, $pText, $bold)" />
	</xsl:function>

	<xsl:function name="sosfn:italic" as="xs:string">
		<xsl:param name="pText" />
		<xsl:value-of select="concat($italic, normalize-space($pText), $italic)" />
	</xsl:function>

	<xsl:function name="sosfn:heading2" as="xs:string">
		<xsl:param name="pText" />
		<xsl:value-of
			select="concat($newline, $heading2, $pText, $heading2, $newline )" />
	</xsl:function>

	<xsl:function name="sosfn:heading3" as="xs:string">
		<xsl:param name="pText" as="xs:string?" />
		<xsl:value-of
			select="concat($newline, $heading3, $pText, $heading3, $lf, $hr, $newline)" />
	</xsl:function>

	<xsl:function name="sosfn:param-heading3" as="xs:string">
		<xsl:param name="pText" as="xs:string?" />
		<xsl:value-of
			select="concat($newline, $heading3, $pText, $heading3, $lf, $hr, $newline)" />
	</xsl:function>

	<xsl:function name="sosfn:anchor" as="xs:string">
		<xsl:param name="anchortext" />
		<xsl:value-of
			select="concat('&lt;span id=', $dq, $anchortext, $dq, '>', $anchortext, '&lt;span>' )" />
	</xsl:function>

	<xsl:function name="sosfn:category">
		<xsl:param name="pText" />
		<xsl:value-of select="concat('[[Category:', $pText, ']]')" />
	</xsl:function>

	<xsl:function name="sosfn:table" as="xs:string">
		<xsl:param name="border" />
		<xsl:value-of select="concat($lf,  '{| border=&#34;', $border, '&#34;' )" />
	</xsl:function>

	<xsl:function name="sosfn:tabletitle" as="xs:string">
		<xsl:param name="pTitle" />
		<xsl:value-of select="concat($lf, '|+ ', $pTitle)" />
	</xsl:function>

	<xsl:function name="sosfn:tableend" as="xs:string">
		<xsl:value-of select="concat($lf, '|}', $lf)" />
	</xsl:function>

	<xsl:function name="sosfn:tr" as="xs:string">
		<xsl:value-of select="concat($lf, '|- ')" />
	</xsl:function>

	<xsl:function name="sosfn:td" as="xs:string">
		<xsl:param name="pText" />
		<xsl:value-of select="concat($lf, '| ', normalize-space($pText))" />
	</xsl:function>

	<xsl:function name="sosfn:th" as="xs:string">
		<xsl:param name="pText" />
		<xsl:value-of select="concat($lf, '! ', normalize-space($pText) )" />
	</xsl:function>

	<xsl:function name="sosfn:a" as="xs:string">
		<xsl:param name="pHref" />
		<xsl:param name="pText" />
		<xsl:value-of
			select="concat(' [', $pHref, ' ', normalize-space($pText), '] ')" />
	</xsl:function>

	<xsl:function name="sosfn:li" as="xs:string">
		<xsl:value-of select="'* '" />
	</xsl:function>

	<xsl:template match="/">
		<!-- <out> <xsl:copy-of select="xalan:checkEnvironment()"/> </out> -->
		<!-- <xsl:variable name="DocumentName"> <xsl:value-of select="concat('file:///c:/temp/mediaWiki/jobdoc/Job
			', $JobName, ' - ', $JobTitle, '.mediawiki.xml')"></xsl:value-of> </xsl:variable>
			<xsl:message> <xsl:value-of select="concat('DocFileName is ', $DocumentName)"></xsl:value-of>
			</xsl:message> <xsl:result-document href="{$DocumentName}" encoding="utf-8"
			indent="yes" method="xml" standalone="yes" output-version="1.0"> -->

		<xsl:element name="mediawiki">
			<xsl:attribute name="version" select="0.3" />
			<xsl:attribute name="xml:lang" select="$sos.doculanguage" />

			<siteinfo>
				<sitename>jobscheduler</sitename>
				<base>http://sourceforge.net/apps/mediawiki/jobscheduler/index.php?title=Main_Page</base>
				<generator>MediaWiki 1.15.1</generator>
				<case>first-letter</case>
			</siteinfo>
			<xsl:element name="page">
				<xsl:element name="title">
					<xsl:value-of select="concat('Job ', $JobName)" />
				</xsl:element>
				<xsl:element name="id">
				</xsl:element>
				<xsl:element name="revision">
					<xsl:element name="id">
					</xsl:element>
					<xsl:element name="timestamp">
						<xsl:value-of select="$sos.timestamp" />
					</xsl:element>
					<xsl:element name="contributor">
						<xsl:element name="username">
							<xsl:value-of select="'Soskb'" />
						</xsl:element>
						<xsl:element name="id">
							<xsl:value-of select="'8'" />
						</xsl:element>
					</xsl:element>
					<xsl:element name="text">
						<xsl:attribute name="xml:space" select="'preserve'" />
                        <!--
						<xsl:value-of select="sosfn:category('ReadyMadeJobs')" />
						<xsl:value-of select="sosfn:category($JobName)" />
                         -->
						<xsl:if test="$JobCategory != ''">
							<xsl:for-each select="fn:tokenize($JobCategory,';')">
								<xsl:variable name="tooken">
									<xsl:sequence select="." />
								</xsl:variable>
								<xsl:value-of select="sosfn:category($tooken)" />
							</xsl:for-each>

						</xsl:if>
						<xsl:value-of select="$newline" />
                        
                        
						<xsl:apply-templates select="//jobdoc:description/jobdoc:documentation" />
						<xsl:apply-templates
							select="//jobdoc:configuration/jobdoc:note[@language=$sos.doculanguage]" />

						<xsl:apply-templates
							select="//jobdoc:configuration/jobdoc:params[@id = 'job_parameter']"
							mode="includeText" />

						<xsl:apply-templates
							select="//jobdoc:configuration/jobdoc:params[@id = 'return_parameter']"
							mode="includeText" />

						<xsl:value-of
							select="concat($newline, $sos.i18n//items/item[@id='lastrevision'], '{{REVISIONMONTH}}/{{REVISIONDAY}}/{{REVISIONYEAR}}', '.')"></xsl:value-of>

						<xsl:value-of select="$newline" />
						<xsl:text>[[File:Wiki-rabbit-left.jpg]] &lt;&lt; [[JobScheduler_FAQ| back to all FAQs]]</xsl:text>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<!-- </xsl:result-document> -->
	</xsl:template>

	<xsl:template
		match="jobdoc:configuration/jobdoc:params[@id = 'return_parameter']"
		mode="includeText">
		<xsl:value-of
			select="sosfn:heading2(concat(sosfn:i18n('return.parameter'), ' ',  $JobName)) " />
		<xsl:value-of
			select="concat(sosfn:i18n('return.parameter.text'), ' ', $JobName, $newline)" />

		<xsl:value-of select="$EmptyLine" />
		<xsl:value-of select="sosfn:table('1')" />
		<xsl:value-of select="sosfn:tabletitle('List of parameter')" />
		<xsl:value-of select="sosfn:th(sosfn:i18n('parameter.table.title')) " />

		<xsl:apply-templates select="./*" mode="createtable" />

		<xsl:value-of select="sosfn:tableend()" />

		<xsl:apply-templates select="./*" />
	</xsl:template>

	<xsl:template
		match="jobdoc:configuration/jobdoc:params[@id = 'job_parameter']"
		mode="includeText">
		<xsl:value-of select="sosfn:heading2(concat(sosfn:i18n('usedby'), $JobName))" />

		<xsl:value-of select="$EmptyLine" />
		<xsl:value-of select="sosfn:table('1')" />
		<xsl:value-of select="sosfn:tabletitle('List of parameter')" />
		<xsl:value-of select="sosfn:th( sosfn:i18n('parameter.table.title'))" />

		<xsl:apply-templates select="./*" mode="createtable" />
		<xsl:value-of select="sosfn:tableend()" />

		<xsl:apply-templates select="./*" />

	</xsl:template>

	<xsl:template match="jobdoc:param" mode="createtable">
		<xsl:value-of select="sosfn:tr()" />
		<xsl:value-of select="sosfn:td(sosfn:wikilink(./@name))" />
		<xsl:value-of
			select="sosfn:td(normalize-space(./jobdoc:note[@language=$sos.doculanguage][1]/jobdoc:title)) " />
		<xsl:value-of select="sosfn:td(./@required)" />
		<xsl:value-of select="sosfn:td(./@default_value)" />
	</xsl:template>

	<xsl:template match="jobdoc:params" mode="createtable">
		<xsl:apply-templates select="./jobdoc:param" mode="createtable" />
	</xsl:template>

	<xsl:template match="jobdoc:params">
		<xsl:apply-templates select="./*" />
	</xsl:template>

	<xsl:template match="jobdoc:param">
		<xsl:variable name="CurrentParam" select="./@name" />
		<xsl:message>
			<xsl:value-of select="concat('processing param ', $CurrentParam)" />
		</xsl:message>
		<xsl:if test="./@name != '' and ./@name != '*'">
			<!--xsl:value-of
				select="concat($lf, sosfn:heading3(concat(sosfn:i18n('parameter'), ' ', sosfn:anchor($CurrentParam) , ': ',  normalize-space(./jobdoc:note[@language=$sos.doculanguage][1]/jobdoc:title))))" /-->
      <xsl:call-template name="param-heading3"/>
      <xsl:apply-templates select="./*" />
			<xsl:value-of select="$newline" />

			<xsl:value-of
				select="concat($sos.i18n//items/item[@id='datatype'], ': ', @DataType, $newline)" />
			<xsl:if test="./@default_value != ''">
				<xsl:value-of
					select="concat(sosfn:i18n('defaultvalue'), ' ', sosfn:bold(./@default_value), '.', $newline)" />
			</xsl:if>
			<xsl:if test="@usewith">
				<xsl:value-of select="concat(sosfn:i18n('usewith'), $newline)" />
				<xsl:for-each select="fn:tokenize(./@usewith,',')">
					<xsl:variable name="tooken" select="." />
					<xsl:value-of
						select="concat(sosfn:wikilink(normalize-space(.)), ' - ', normalize-space($this.doc//jobdoc:param[@name=$tooken]/jobdoc:note[@language=$sos.doculanguage][1]/jobdoc:title))" />
				</xsl:for-each>
				<xsl:value-of select="$newline" />
			</xsl:if>
		</xsl:if>

		<xsl:if test="./@required = 'true'">
			<xsl:value-of select="concat(sosfn:i18n('mandatory'), $newline )" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="xhtml:code|jobdoc:code" mode="copy">
		<xsl:variable name="pname" select="." />
		<xsl:element name="code">
			<xsl:value-of select="concat(' ', normalize-space(.), ' ')" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="title|xhtml:title|xi:title|jobdoc:title"
		mode="copy">
		<xsl:variable name="pname" select="." />
		<xsl:element name="p">
			<xsl:value-of select="concat(sosfn:italic(.), ' ')" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="title|xhtml:title|xi:title|jobdoc:title">
		<!-- <xsl:message select="concat('title ', name(.), ' ', namespace-uri(.),
			' = ', normalize-space(.))" /> -->
		<xsl:element name="p">
			<xsl:value-of select="sosfn:italic(.)" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="xhtml:paramref|jobdoc:paramref" mode="copy">
		<xsl:variable name="pname" select="." />
		<xsl:choose>
			<xsl:when test="$formattingMode='includeText'">
				<xsl:value-of select="concat(' ', sosfn:wikilink(normalize-space(.)), ' ')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="concat( ' [[Param ', normalize-space(.), '|', normalize-space(.), ']] ')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="xhtml:paramref|jobdoc:paramref">
		<xsl:variable name="pname" select="." />
		<xsl:choose>
			<xsl:when test="$formattingMode='includeText'">
				<xsl:value-of select="concat(' ', sosfn:wikilink(normalize-space(.)), ' ')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="concat( ' [[Param ', normalize-space(.), '|', normalize-space(.), ']] ')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="a|xhtml:a|jobdoc:a" mode="copy">
		<xsl:value-of select="sosfn:a(./@href, normalize-space(.)) " />
	</xsl:template>

	<xsl:template match="xhtml:note|jobdoc:note">
		<xsl:if test="@language = $sos.doculanguage ">
			<xsl:apply-templates select="./*" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="jobdoc:configuration">
		<xsl:apply-templates select="./*" />
	</xsl:template>

	<xsl:template match="xhtml:div">
		<xsl:apply-templates select="." mode="copy" />
	</xsl:template>

	<xsl:template match="jobdoc:p">
		<xsl:value-of select="$EmptyLine" />
		<xsl:apply-templates select="." mode="copy" />
	</xsl:template>
	<!-- <xsl:template match="xhtml:div" mode="copy"> <xsl:apply-templates select="."
		mode="copy" /> </xsl:template> -->
	<xsl:template match="xhtml:em|jobdoc:em" mode="copy">
		<xsl:value-of select="concat(' ', sosfn:bold(normalize-space(.)), ' ')" />
	</xsl:template>

	<xsl:template match="xhtml:strong|jobdoc:strong" mode="copy">
		<xsl:value-of select="concat(' ', sosfn:bold(normalize-space(.)), ' ')" />
	</xsl:template>

	<xsl:template match="xhtml:cmdname|jobdoc:cmdname" mode="copy">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="xhtml:tt|jobdoc:tt" mode="copy">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="xhtml:example|jobdoc:example" mode="copy">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="xhtml:br|jobdoc:br" mode="copy">
		<xsl:value-of select="$lf" />
		<xsl:if test="./@clear != 'none'">
		</xsl:if>
		<!-- <xsl:element name="br" /> -->
	</xsl:template>

	<xsl:template match="xhtml:ul|jobdoc:ul" mode="copy">
		<xsl:apply-templates select="./*" mode="copy" />
		<xsl:value-of select="$newline" />
	</xsl:template>

	<xsl:template match="text()" mode="copy">
		<!-- <xsl:message select="concat('text() ', name(.), ' ', namespace-uri(.),
			' = ', normalize-space(.))" /> -->
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="*" mode="copy">
		<!-- <xsl:message select="concat('* ', name(.), ' ', namespace-uri(.),
			' = ', normalize-space(.))" /> -->
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates mode="copy" />
		</xsl:element>
	</xsl:template>


	<xsl:template match="text()">
		<!-- <xsl:message select="concat('text() ', name(.), ' ', namespace-uri(.),
			' = ', normalize-space(.))" /> -->
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="jobdoc:releases"></xsl:template>
	<xsl:template match="jobdoc:resources"></xsl:template>

	<xsl:template match="jobdoc:documentation">
		<xsl:if test="@language = $sos.doculanguage ">
			<xsl:value-of
				select="sosfn:heading2(concat(sosfn:i18n('descriptionof'), $JobName, ' - ', $JobTitle))" />
			<xsl:if test="$JobIsDeprecated != ''">
				<xsl:value-of
					select="concat($sos.i18n//items/item[@id='deprecatedjob'], $newline)" />
				<xsl:value-of
					select="concat($JobIsDeprecated, $sos.i18n//items/item[@id='useinstead'], $newline)" />
				<xsl:value-of select="$newline" />
			</xsl:if>
			<xsl:apply-templates select="./*" />
			<xsl:value-of select="$newline" />
			<xsl:apply-templates
				select="//jobdoc:description/jobdoc:note[@language=$sos.doculanguage]" />
			<xsl:value-of select="$newline" />
			<xsl:call-template name="create_job" />
			<xsl:value-of select="$newline" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="create_job">
		<xsl:value-of select="concat(sosfn:i18n('job.example'),  $newline)" />
		<xsl:value-of select="concat('  &lt;job order=''no'' >', $lf)" />
		<xsl:value-of select="concat('     &lt;params>', $lf)" />

		<xsl:for-each select="//jobdoc:params[@id = 'job_parameter']">
			<xsl:apply-templates select="./*" mode="create_job" />
		</xsl:for-each>
		<xsl:value-of select="concat('     &lt;/params>', $lf)" />
		<xsl:value-of
			select="concat('     &lt;script language=&#34;', //jobdoc:script/@language, '&#34; java_class=&#34;', //jobdoc:script/@java_class, '&#34; />', $lf)" />
		<xsl:value-of select="concat('  &lt;/job>', $newline)" />
	</xsl:template>

	<xsl:template match="jobdoc:param" mode="create_job">
		<xsl:value-of
			select="concat( '       &lt;param name=&#34;', sosfn:wikilink(./@name), '&#34; value=&#34;',
                ./@default_value, '&#34; />', $lf)" />
	</xsl:template>

	<xsl:template match="jobdoc:params" mode="create_job">
		<xsl:apply-templates select="./jobdoc:param" mode="create_job" />
	</xsl:template>

	<xsl:template match="table|xhtml:table|jobdoc:table">
		<xsl:value-of select="$EmptyLine" />
		<xsl:value-of select="sosfn:table('1') " />

		<xsl:apply-templates select="./*" />

		<xsl:value-of select="sosfn:tableend()" />
	</xsl:template>

	<xsl:template match="tr|xhtml:tr|jobdoc:tr">
		<xsl:value-of select="sosfn:tr()" />
		<xsl:apply-templates select="./*" />
	</xsl:template>

	<xsl:template match="li|xhtml:li|jobdoc:li" mode="copy">
		<xsl:value-of select="sosfn:li()" />
		<xsl:apply-templates select="." />
		<xsl:value-of select="$EmptyLine" />
	</xsl:template>

	<xsl:template match="th|xhtml:th|jobdoc:th">
		<xsl:value-of select="sosfn:th(.) " />
	</xsl:template>

	<xsl:template match="td|xhtml:td|jobdoc:td">
		<xsl:value-of select="sosfn:td(.) " />
	</xsl:template>

	<xsl:template name="param-heading3">
		<xsl:value-of select="concat($lf, $newline, $heading3, sosfn:i18n('parameter'), ' ')" />
		<xsl:element name="span">
			<xsl:attribute name="id"><xsl:value-of select="@name" /></xsl:attribute>
			<xsl:value-of select="@name" />
		</xsl:element>
		<xsl:value-of select="concat(': ',  normalize-space(./jobdoc:note[@language=$sos.doculanguage][1]/jobdoc:title), $heading3, $lf, $hr, $newline)" />
	</xsl:template>


</xsl:stylesheet>