<?xml version="1.0" encoding="UTF-8"?>
<!-- 
  Copyright 2001-2011 Syncro Soft SRL. All rights reserved.
 -->

<!--
    Modified documentation transform file from the Oxygen XML editor.  To use this use the
    Generate Documentation command and select a 'custom' export type, specifying this 
    xsl file.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:func="http://www.oxygenxml.com/xsdDoc/functions" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xpath-default-namespace="http://www.oxygenxml.com/ns/doc/schema-internal" 
    exclude-result-prefixes="#all">
    <xsl:param name="mainFile" required="yes"/>
    <!-- currently .tmp -->
    <xsl:param name="intermediateXmlExtension" required="yes"/>
    <xd:doc>
        <xd:desc>The oXygen family product used to generate the documentation.
        <xd:p> Possible values:
          <xd:ul>
              <xd:li>Editor (default value)</xd:li>
              <xd:li>Developer</xd:li>
          </xd:ul>
        </xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="distribution">Editor</xsl:param>
    
    <xsl:output method="xhtml" encoding="UTF-8" version="1.0" omit-xml-declaration="yes"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"
        exclude-result-prefixes="#all"/>

    <!-- something like '.html' -->
    <xsl:variable name="extension">
        <xsl:variable name="ext" select="func:substring-after-last($mainFile, '.')"/>
        <xsl:choose>
            <xsl:when test="string-length($ext) = 0">
                <xsl:text></xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('.', $ext)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="cssRelativeLocationToXSL">docHtml.css</xsl:variable>
    <xsl:variable name="cssRelativeLocationToXML">docHtml.css</xsl:variable>
    <xsl:variable name="cssCopyLocation" select="resolve-uri($cssRelativeLocationToXML, base-uri())"/>
    <xsl:variable name="schemaHierarchyFile">schHierarchy.html</xsl:variable>

    <xsl:variable name="chunkValueLocation">location</xsl:variable>
    <xsl:variable name="chunkValueNamespace">namespace</xsl:variable>
    <xsl:variable name="chunkValueComponent">component</xsl:variable>
    <xsl:variable name="chunkValueNone">none</xsl:variable>
    
    <xsl:variable name="splitInfo" select="/schemaDoc/splitInfo"></xsl:variable>

    <xd:doc>
        <xd:desc>When all the  components are in the same file (no split after some criteria) we will use FRAMES. So the index will be redirected in a $indexFile
            and the components in a $mainFile. The output file will only contain the FRAMESET</xd:desc>
    </xd:doc>
    <xsl:variable name="isChunkMode" as="xs:boolean" xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:choose>
            <xsl:when test="compare(/schemaDoc/splitInfo/@criteria, 'none') = 0"> false </xsl:when>
            <xsl:otherwise> true </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>When  NO CHUNKS we will generate a frame html. The index in one file and the content in other</xd:desc>
    </xd:doc>
    <xsl:variable name="indexFile"
        select="concat(func:substring-before-last($splitInfo/@indexLocation, $intermediateXmlExtension), $extension)"/>
    <xsl:variable name="indexFileComp"
        select="concat(func:substring-before-last($splitInfo/@indexLocation, $intermediateXmlExtension), 'comp', $extension)"/>
    <xsl:variable name="indexFileNamespace"
        select="concat(func:substring-before-last($splitInfo/@indexLocation, $intermediateXmlExtension), 'ns', $extension)"/>

    <xsl:variable name="mainFrame">mainFrame</xsl:variable>
    <xsl:variable name="indexFrame">indexFrame</xsl:variable>

    <xd:doc>
        <xd:desc>Traget for all the links. If we are using a FRAME representation of the html we need to specify which frame the reference will be opened in </xd:desc>
    </xd:doc>
    <xsl:variable name="target">
        <xsl:choose>
            <xsl:when test="boolean($isChunkMode)">
                <xsl:value-of select="$mainFrame"/>
            </xsl:when>
            <xsl:otherwise>_self</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>Used to construct an id for identifying a property of  a component. This prefix will be added to the unique component id</xd:desc>
    </xd:doc>
    <xsl:variable name="idsPrefixMap">
        <entry key="properties">properties_</entry>
        <entry key="usedBy">usedBy_</entry>
        <entry key="attributes">attributes_</entry>
        <entry key="children">children_</entry>
        <entry key="source">source_</entry>
        <entry key="instance">instance_</entry>
        <entry key="facets">facets_</entry>
        <entry key="diagram">diagram_</entry>
        <entry key="annotations">annotations_</entry>
        <entry key="constraints">identityConstraints_</entry>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>Mapping between directive types and icons. Is used in 
        the schemas hierarchy tree.</xd:desc>
    </xd:doc>
    <xsl:variable name="scHierarchyIcons">
        <entry key="import">img/Import12.jpg</entry>
        <entry key="include">img/Include12.jpg</entry>
        <entry key="redefine">img/Redefine12.jpg</entry>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>Part of the tooltip presented on a schema from the hierarchy view.</xd:desc>
    </xd:doc>
    <xsl:variable name="scHierarchyTooltip">
        <entry key="import">Imported by </entry>
        <entry key="include">Included by</entry>
        <entry key="redefine">Redefined by</entry>
    </xsl:variable>

    <xsl:variable name="buttonPrefix">button_</xsl:variable>

    <xsl:template name="createJsIdsArray">
        <xsl:param name="arrayName"/>
        <xsl:param name="nodes"/>
        <xsl:if test="count($nodes) > 0">
            <xsl:text>var </xsl:text>
            <xsl:value-of select="$arrayName"/>
            <xsl:text>= new Array(</xsl:text>
            <xsl:for-each select="$nodes">
                <xsl:if test="position()!=1">
                    <xsl:text>, &#10;&#9;&#9;&#9;&#9;</xsl:text>
                </xsl:if>
                <xsl:text>'</xsl:text>
                <xsl:value-of select="func:getDivId(.)"/>
                <xsl:text>'</xsl:text>
            </xsl:for-each>
            <xsl:text>);&#10;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:variable name="propertiesBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">propertiesBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/properties"/>
        </xsl:call-template> 
    </xsl:variable>
    <xsl:variable name="facetsBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">facetsBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/facets"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="usedByBoxes">
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">usedByBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/usedBy"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="attributesBoxes">
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">attributesBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/attributes"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="sourceBoxes">
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">sourceBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/source"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="instanceBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">instanceBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/instance"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="diagramBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">diagramBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/diagram"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="annotationBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">annotationBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/annotations"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="identityConstraintsBoxes">        
        <xsl:call-template name="createJsIdsArray">
            <xsl:with-param name="arrayName">identityConstraintsBoxes</xsl:with-param>
            <xsl:with-param name="nodes" select="/schemaDoc/*[@id]/constraints"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="javascript" xml:space="preserve">
        <xsl:value-of select="$propertiesBoxes"/>
        <xsl:value-of select="$facetsBoxes"/>
        <xsl:value-of select="$usedByBoxes"/>
        <xsl:value-of select="$sourceBoxes"/>
        <xsl:value-of select="$instanceBoxes"/>
        <xsl:value-of select="$diagramBoxes"/>
        <xsl:value-of select="$annotationBoxes"/>
        <xsl:value-of select="$attributesBoxes"/>
        <xsl:value-of select="$identityConstraintsBoxes"/>
        var button_prefix = '<xsl:value-of select="$buttonPrefix"/>';
        <xsl:text>
        /**
        * Returns an element in the current HTML document.
        *
        * @param elementID Identifier of HTML element
        * @return               HTML element object
        */
        function getElementObject(elementID) {
            var elemObj = null;
            if (document.getElementById) {
                elemObj = document.getElementById(elementID);
            }
            return elemObj;
        }
        
        /**
        * Switches the state of a collapseable box, e.g.
        * if it's opened, it'll be closed, and vice versa.
        *
        * @param boxID Identifier of box
        */
        function switchState(boxID) {
            var boxObj = getElementObject(boxID);
            var buttonObj = getElementObject(button_prefix + boxID);
            if (boxObj == null || buttonObj == null) {
                // Box or button not found
            } else if (boxObj.style.display == "none") {
                // Box is closed, so open it
                openBox(boxObj, buttonObj);
            } else if (boxObj.style.display == "block") {
                // Box is opened, so close it
                closeBox(boxObj, buttonObj);
            }
        }
        
        /**
        * Opens a collapseable box.
        *
        * @param boxObj       Collapseable box
        * @param buttonObj Button controlling box
        */
        function openBox(boxObj, buttonObj) {
            if (boxObj == null || buttonObj == null) {
                // Box or button not found
            } else {
                // Change 'display' CSS property of box
                boxObj.style.display = "block";
                
                // Change text of button
                if (boxObj.style.display == "block") {
                    buttonObj.src = "img/btM.gif";
                }
            }
        }
        
        /**
        * Closes a collapseable box.
        *
        * @param boxObj       Collapseable box
        * @param buttonObj Button controlling box
        */
        function closeBox(boxObj, buttonObj) {
            if (boxObj == null || buttonObj == null) {
                // Box or button not found
            } else {
                // Change 'display' CSS property of box
                boxObj.style.display = "none";
                
                // Change text of button
                if (boxObj.style.display == "none") {
                    buttonObj.src = "img/btP.gif";
                }
            }
        }
    
       function switchStateForAll(buttonObj, boxList) {
            if (buttonObj == null) {
                // button not found
            } else if (buttonObj.value == "+") {
                // Expand all
                expandAll(boxList);
                buttonObj.value = "-";
            } else if (buttonObj.value == "-") {
                // Collapse all
                collapseAll(boxList);
                buttonObj.value = "+";
            }
        }
        
        /**
        * Closes all boxes in a given list.
        *
        * @param boxList Array of box IDs
        */
        function collapseAll(boxList) {
            var idx;
            for (idx = 0; idx &lt; boxList.length; idx++) {
                var boxObj = getElementObject(boxList[idx]);
                var buttonObj = getElementObject(button_prefix + boxList[idx]);
                closeBox(boxObj, buttonObj);
            }
        }
            
        /**
        * Open all boxes in a given list.
        *
        * @param boxList Array of box IDs
        */
        function expandAll(boxList) {
            var idx;
            for (idx = 0; idx &lt; boxList.length; idx++) {
                var boxObj = getElementObject(boxList[idx]);
                var buttonObj = getElementObject(button_prefix + boxList[idx]);
                openBox(boxObj, buttonObj);
            }
        }
        
        /**
         * Update the message presented in the title of the html page.
         * - If the documentation was splited by namespace we present something like: "Documentation for namespace 'ns'"
         * - If the documentation was splited by location we present somehing like: "Documentation for 'Schema.xsd'"
         * - If no split we always present: "Documentation for 'MainSchema.xsd'"
         */
        function updatePageTitle(message) {
            top.document.title = message;
        }
        
          
                    
         /**
          * Finds an HTML element by its ID and makes it floatable over the normal content.
          *
          * @param x_displacement The difference in pixels to the right side of the window from 
          *           the left side of the element.
          * @param y_displacement The difference in pixels to the right side of the window from 
          *           the top of the element.          
          */
         function findAndFloat(id, x_displacement, y_displacement){

            var element = getElementObject(id);            
            
            window[id + "_obj"] = element;
            
            if(document.layers) {
               element.style = element;
            }
            
            element.current_y = y_displacement;      
            element.first_time = true;
         
            element.floatElement = function(){
               // It may be closed by an user action.
                
               // Target X and Y coordinates.
               var x, y;
               
               var myWidth = 0, myHeight = 0;
               if( typeof( window.innerWidth ) == 'number' ) {
                  //Non-IE
                  myWidth = window.innerWidth;
                  myHeight = window.innerHeight;
               } else if( document.documentElement &amp;&amp; ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
                  //IE 6+ in 'standards compliant mode'
                  myWidth = document.documentElement.clientWidth;
                  myHeight = document.documentElement.clientHeight;
               } else if( document.body &amp;&amp; ( document.body.clientWidth || document.body.clientHeight ) ) {
                  //IE 4 compatible
                  myWidth = document.body.clientWidth;
                  myHeight = document.body.clientHeight;
               }
               
               
               x = myWidth - x_displacement;
               
               var ns = (navigator.appName.indexOf("Netscape") != -1);               
               y = ns ? pageYOffset : document.documentElement &amp;&amp; document.documentElement.scrollTop ? 
                  document.documentElement.scrollTop : document.body.scrollTop;               
               y = y + y_displacement;               
               
               // The current y is the current coordinate of the floating element.
               // This should be at the limit the y target coordinate.
               this.current_y += (y - this.current_y)/1.25;
               
               // Add the pixels constant after the values
               // and move the element.
               var px = document.layers ? "" : "px";
               this.style.left =  x + px;
               this.style.top =  this.current_y + px;
                              
               setTimeout(this.id + "_obj.floatElement()", 100);
            }
            
            element.floatElement();
            return element;
          }

         /**
          * Finds an HTML element by its ID and makes it floatable over the normal content.
          *
          * @param x_displacement The difference in pixels to the right side of the window from 
          *           the left side of the element.
          * @param y_displacement The difference in pixels to the right side of the window from 
          *           the top of the element.          
          */
         function selectTOCGroupBy(id, isChunked, indexFileLocation, indexFileNamespace, indexFileComponent){

            if (!isChunked) {
             var selectIds = new Array('toc_group_by_namespace', 'toc_group_by_location', 'toc_group_by_component_type');
             // Make all the tabs invisible.
               for (i = 0; i &lt; 3; i++){
                  var tab = getElementObject(selectIds[i]);
                  tab.style.display = 'none';
               }
               var selTab = getElementObject(id);
               selTab.style.display = 'block';
            } else {
             if (id == 'toc_group_by_namespace') {
               parent.indexFrame.location = indexFileNamespace;
             } else  if (id == 'toc_group_by_location') {
               parent.indexFrame.location = indexFileLocation;
             } else  if (id == 'toc_group_by_component_type') {
              parent.indexFrame.location = indexFileComponent;
             }
            }
         }
          
</xsl:text>
    </xsl:variable>

    <xsl:function name="func:getDivId" as="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="node"/>
        <xsl:value-of
            select="concat($idsPrefixMap/*[@key=local-name($node)]/text(), $node/parent::node()/@id)"
        />
    </xsl:function>

    <xsl:function name="func:getButtonId" as="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="node"/>
        <xsl:value-of select="concat($buttonPrefix/text() , func:getDivId($node))"/>
    </xsl:function>


    <xsl:variable name="schemaTypeLabels">
        <entry key="main">Main schema</entry>
        <entry key="include">Included schema</entry>
        <entry key="import">Imported schema</entry>
    </xsl:variable>

    <xsl:variable name="componentTypeLabels">
        <entry key="Element">Element</entry>
        <entry key="Attribute">Attribute</entry>
        <entry key="Complex_Type">Complex Type</entry>
        <entry key="Element_Group">Element Group</entry>
        <entry key="Attribute_Group">Attribute Group</entry>
        <entry key="Simple_Type">Simple Type</entry>
        <entry key="Schema">Schema</entry>
        <entry key="Notation">Notation</entry>
    </xsl:variable>

    <xd:doc>
        <xd:desc>Build a title message
            <xd:ul>
                <xd:li>If the documentation was splited by namespace we present something like: "Documentation for namespace 'ns'"</xd:li>
                <xd:li>If the documentation was splited by location we present somehing like: "Documentation for 'Schema.xsd'"</xd:li>
                <xd:li>If no split we always present: "Documentation for 'MainSchema.xsd'" and this function will not be used</xd:li>
            </xd:ul>
        </xd:desc>
    </xd:doc>
    <xsl:function name="func:getTitle" as="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="ref"/>
        <xsl:param name="criteria"/>
        <xsl:variable name="message">
            <xsl:text>updatePageTitle('</xsl:text>
            <xsl:choose>
                <xsl:when test="compare($criteria, $chunkValueLocation) = 0">
                    <!-- The split is done after the location -->
                    <xsl:value-of select="func:getLocationChunkTitle($ref/@schemaLocation)"/>
                </xsl:when>
                <xsl:when test="compare($criteria, $chunkValueNamespace) = 0">
                    <!-- The split is done after the namespace -->
                    <xsl:value-of select="func:getNamespaceChunkTitle($ref/@ns)"/>
                </xsl:when>
                <xsl:when test="compare($criteria, $chunkValueComponent) = 0">
                    <!-- The split is done after the component -->
                    <xsl:value-of select="func:getComponentChunkTitle($ref/text())"/>
                </xsl:when>
            </xsl:choose>
            <xsl:text>')</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$message"/>
    </xsl:function>

    <xsl:function name="func:getNamespaceChunkTitle" as="xs:string"
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="ns"/>
        <xsl:variable name="toReturn">
            <xsl:text>Schema documentation for namespace </xsl:text>
            <xsl:value-of select="$ns"/>
        </xsl:variable>
        <xsl:value-of select="$toReturn"/>
    </xsl:function>

    <xsl:function name="func:getComponentChunkTitle" as="xs:string"
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="qName"/>
        <xsl:variable name="toReturn">
            <xsl:text>Schema documentation for component </xsl:text>
            <xsl:value-of select="$qName"/>
        </xsl:variable>
        <xsl:value-of select="$toReturn"/>
    </xsl:function>

    <xsl:function name="func:getLocationChunkTitle" as="xs:string"
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="location"/>
        <xsl:variable name="toReturn">
            <xsl:text>Schema documentation for </xsl:text>
            <xsl:value-of select="$location"/>            
        </xsl:variable>
        <xsl:value-of select="$toReturn"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>Get the title of the html page by analyzing the splitInfo element </xd:desc>
    </xd:doc>
    <xsl:function name="func:getTitleFromSplitInfo" as="xs:string"
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
        <xsl:param name="splitInfo"/>
        <xsl:choose>
            <xsl:when test="compare($splitInfo/@criteria, $chunkValueNamespace) = 0">
                <xsl:value-of select="func:getNamespaceChunkTitle($splitInfo/@value)"/>
            </xsl:when>
            <xsl:when test="compare($splitInfo/@criteria, $chunkValueLocation) = 0">
                <xsl:value-of select="func:getLocationChunkTitle($splitInfo/parent::node()/schema[compare(./schemaLocation/text(), $splitInfo/@value) = 0]/qname)"/>
            </xsl:when>
            <xsl:when test="compare($splitInfo/@criteria, $chunkValueComponent) = 0">
                <xsl:value-of select="func:getComponentChunkTitle($splitInfo/@value)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="func:getLocationChunkTitle($splitInfo/parent::node()/schema[compare(@type, 'main') = 0]/qname)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>Get the substring before the last occurence of the given substring </xd:desc>
    </xd:doc>
    <xsl:function name="func:substring-before-last" as="xs:string">
        <xsl:param name="string"/>
        <xsl:param name="searched"/>
        <xsl:variable name="toReturn">
            <xsl:choose>
                <xsl:when test="contains($string, $searched)">
                    <xsl:variable name="before"
                        select="substring-before($string, $searched)"/>
                    
                    <xsl:variable name="rec" 
                        select="func:substring-before-last(substring-after($string, $searched), $searched)"/>
                    <xsl:choose>
                        <xsl:when test="string-length($rec) = 0">
                            <xsl:value-of select="$before"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat($before, $searched, $rec)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$toReturn"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>Get the substring after the last occurence of the given substring </xd:desc>
    </xd:doc>
    <xsl:function name="func:substring-after-last" as="xs:string">
        <xsl:param name="string"/>
        <xsl:param name="searched"/>
        <xsl:variable name="toReturn">
            <xsl:choose>
                <xsl:when test="contains($string, $searched)">
                    <xsl:variable name="after"
                        select="substring-after($string, $searched)"/>
                    
                    <xsl:variable name="rec" 
                        select="func:substring-after-last($after, $searched)"/>
                    <xsl:choose>
                        <xsl:when test="string-length($rec) = 0">
                            <xsl:value-of select="$after"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$rec"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$toReturn"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>The entry point </xd:desc>
    </xd:doc>
    <xsl:template match="schemaDoc">
        <xsl:if test="exists(./schema[@type='main'])">
            <!-- This way we make sure the CSS will only be copied once-->
            <xsl:result-document href="{$cssCopyLocation}" method="text">
                <xsl:value-of disable-output-escaping="yes"
                    select="unparsed-text($cssRelativeLocationToXSL,'UTF-8')"/>
            </xsl:result-document>
        </xsl:if>

        <xsl:choose>
            <xsl:when test="boolean($isChunkMode) and exists(schema[@type='main'])">
                <xsl:result-document href="{resolve-uri($mainFile, base-uri())}" method="xhtml"
                    indent="no" exclude-result-prefixes="#all" doctype-public="-//W3C//DTD XHTML 1.0 Frameset//EN"
                    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
                    <html xmlns="http://www.w3.org/1999/xhtml">
                        <head>
                            <title>
                                <xsl:value-of select="func:getTitleFromSplitInfo(splitInfo)"/>
                            </title>
                            <link rel="stylesheet" href="{$cssRelativeLocationToXML}" type="text/css"/>
                        </head>
                        <xsl:if test="boolean($isChunkMode)">
                            <!-- When all the documentation is in one file we will generate a html with frames -->
                            <xsl:choose>
                                <xsl:when test="string-length($splitInfo/@indexLocation) = 0">
                                    <!-- The index was not included -->
                                    <frameset cols="100%">
                                        <xsl:element name="frame">
                                            <xsl:attribute name="name" select="$mainFrame"/>
                                            <xsl:attribute name="src"
                                                select="concat(func:substring-before-last(func:substring-after-last(base-uri(), '/'), $intermediateXmlExtension), $extension)"/>
                                        </xsl:element>
                                    </frameset>
                                    
                                </xsl:when>
                                <xsl:otherwise>                                    
                                    <frameset cols="20%, 80%">
                                        <xsl:element name="frame">
                                            <xsl:attribute name="name" select="$indexFrame"/>
                                            <xsl:attribute name="src">
                                                <xsl:if test="compare($splitInfo/@criteria, $chunkValueNamespace) = 0">
                                                    <xsl:value-of select="$indexFileNamespace"/>
                                                </xsl:if>
                                                <xsl:if test="compare($splitInfo/@criteria, $chunkValueLocation) = 0">
                                                    <xsl:value-of select="$indexFile"/>
                                                </xsl:if>
                                                <xsl:if test="compare($splitInfo/@criteria, $chunkValueComponent) = 0">
                                                    <xsl:value-of select="$indexFileComp"/>
                                                </xsl:if>
                                            </xsl:attribute>
                                        </xsl:element>
                                        <xsl:element name="frame">
                                            <xsl:attribute name="name" select="$mainFrame"/>
                                            <xsl:attribute name="src"
                                                select="concat(func:substring-before-last(func:substring-after-last(base-uri(), '/'), $intermediateXmlExtension), $extension)"/>
                                        </xsl:element>
                                    </frameset>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </html>
                </xsl:result-document>
            </xsl:when>
        </xsl:choose>

        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>
                    <xsl:value-of select="func:getTitleFromSplitInfo(./splitInfo)"/>
                </title>
                <link rel="stylesheet" href="{$cssRelativeLocationToXML}" type="text/css"/>
                <script type="text/javascript">
                    <xsl:comment>
                        <xsl:value-of select="$javascript" disable-output-escaping="yes"/>
                    </xsl:comment>
                </script>
            </head>
            <xsl:call-template name="main"/>
        </html>
    </xsl:template>

    <xd:doc>
        <xd:desc>Create the a link element to a component</xd:desc>
    </xd:doc>
    <xsl:template name="reference">
        <xsl:param name="ref" select="."/>
        <xsl:choose>
            <xsl:when test="exists($ref/@refId)">
                <b>
                    <a href="{concat(substring-before($ref/@base, $intermediateXmlExtension), $extension, '#', $ref/@refId)}"
                        target="{$target}">
                        <xsl:attribute name="title">
                            <xsl:choose>
                                <xsl:when test="compare('', $ref/@ns) = 0">No namespace</xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$ref/@ns"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:variable name="criteria" select="$splitInfo/@criteria"/>
                        <xsl:if test="compare($criteria, $chunkValueNone) != 0">
                            <xsl:attribute name="onclick" select="func:getTitle($ref, $criteria)"/>
                        </xsl:if>
                        <xsl:value-of select="$ref/text()"/>
                    </a>
                </b>
            </xsl:when>
            <xsl:otherwise>
                <b>
                    <xsl:copy-of select="$ref/text()"/>
                </b>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>Generates the index part.</xd:desc>
        <xd:param name="mode">
            One of:
            <xd:ul>
                <xd:li><xd:b>namespace</xd:b> to indicate it must generate a grouping by namespace</xd:li>
                <xd:li><xd:b>location</xd:b> to indicate it must generate a grouping by location</xd:li>
                <xd:li><xd:b>component</xd:b> to indicate it must generate a grouping by component</xd:li>
                <xd:li><xd:b>schHierarchy</xd:b> to indicate it must generate schema hierarchy</xd:li>
            </xd:ul>
        </xd:param>
        <xd:param name="outputFile">File where to redirect the index.</xd:param>
        <xd:param name="hasHierarchy">true if a hierarchy of the schemas will also be presented.</xd:param>
    </xd:doc>
    <xsl:template name="index">
        <xsl:param name="mode"/>
        <xsl:param name="outputFile"/>
        <xsl:param name="hasHierarchy" as="xs:boolean">true</xsl:param>
        <xsl:result-document href="{$outputFile}" method="xhtml" indent="yes"
            exclude-result-prefixes="#all" doctype-public="-//W3C//DTD XHTML 1.0 Frameset//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                    <title>
                        <xsl:value-of select="func:getTitleFromSplitInfo(splitInfo)"/>
                    </title>
                    <link rel="stylesheet" href="{$cssRelativeLocationToXML}" type="text/css"/>
                    <script type="text/javascript">
                        <xsl:comment>
                            <xsl:value-of select="$javascript" disable-output-escaping="yes"/>
                        </xsl:comment>
                    </script>
                </head>
                <body>
                    
                    <xsl:call-template name="indexContent">
                        <xsl:with-param name="mode" select="$mode"/>
                        <xsl:with-param name="hasHierarchy" select="$hasHierarchy"/>
                    </xsl:call-template>
                    
                    <xsl:if test="string-length($outputFile) > 0">
                        <!-- If we are redirecting to a different file we must also include the footer. -->
                        <xsl:call-template name="generateFooter"/>
                    </xsl:if>
                </body>
            </html>
        </xsl:result-document>
    </xsl:template>

    <xsl:function name="func:getIndexFile" as="xs:string">
        <xsl:param name="mode"></xsl:param>
        <xsl:choose>
            <xsl:when test="$mode = 'location'">
                <xsl:value-of select="$indexFile"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$mode = 'namespace'">
                        <xsl:value-of select="$indexFileNamespace"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$mode = 'schHierarchy'">
                                <xsl:value-of select="$schemaHierarchyFile"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$indexFileComp"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:template name="indexContent">
        <xsl:param name="mode"/>
        <xsl:param name="hasHierarchy" as="xs:boolean">true</xsl:param>
        <h2>
            <a id="INDEX">Table of Contents</a>
        </h2>
        <xsl:if test="$hasHierarchy">
            <p>
                <a href="{func:getIndexFile('location')}">Components</a>
                <span> | </span>
                <a href="{$schemaHierarchyFile}">Resource Hierarchy</a>
            </p>
            <hr/>
        </xsl:if>
        <xsl:if test="$mode != 'schHierarchy'">
        <div class="toc">
            <form action="none">
                <div>
                    <span> Group by: <select id="selectTOC"
                        onchange="selectTOCGroupBy(this.options[this.selectedIndex].value, {$isChunkMode}, '{func:getIndexFile('location')}', '{func:getIndexFile('namespace')}', '{func:getIndexFile('component')}');">
                        
                        <xsl:element name="option">
                            <xsl:attribute name="value">toc_group_by_namespace</xsl:attribute>
                            <xsl:if test="not($isChunkMode) or $mode = 'namespace'">
                                <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:text>Namespace</xsl:text>
                        </xsl:element>
                        <xsl:element name="option">
                            <xsl:attribute name="value">toc_group_by_location</xsl:attribute>
                            <xsl:if test="$isChunkMode and $mode = 'location'">
                                <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:text>Location</xsl:text>
                        </xsl:element>
                        <xsl:element name="option">
                            <xsl:attribute name="value">toc_group_by_component_type</xsl:attribute>
                            <xsl:if test="$isChunkMode and $mode = 'component'">
                                <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:text>Component Type</xsl:text>
                        </xsl:element>

                        </select>
                    </span>
                </div>
            </form>

            <!-- Generate links grouped by the namespace of the component-->
            <xsl:if test="not($isChunkMode) or $mode = 'namespace'">

                <xsl:variable name="boxId">groupByNs</xsl:variable>
                <div class="level1" id="toc_group_by_namespace" style="display:block">
                    <!-- This is the displayed div by default if there is no chunking or it is chunked by namespace -->
                    <div>
                        <xsl:for-each-group select="ref" group-by="@ns">
                            <xsl:variable name="ns">
                                <xsl:choose>
                                    <xsl:when test="compare('', @ns) = 0">No namespace</xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@ns"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:variable name="nsBoxId">boxIdNamespace<xsl:value-of
                                    select="position()"/></xsl:variable>
                            <div class="level2">
                                <p>
                                    <input id="button_{$nsBoxId}" type="image" value="-"
                                        src="img/btM.gif"
                                        onclick="switchState('{$nsBoxId}');" class="control"/>
                                    <span class="indexGroupTitle">
                                        <xsl:value-of select="$ns"/>
                                    </span>
                                </p>
                                <div id="{$nsBoxId}" style="display:block">
                                    <xsl:call-template name="indexGroupByComponent">
                                        <xsl:with-param name="refSeq" select="current-group()"/>
                                        <xsl:with-param name="prefix" select="$nsBoxId"/>
                                    </xsl:call-template>
                                </div>
                            </div>
                        </xsl:for-each-group>
                    </div>
                </div>

            </xsl:if>
            
            <xsl:if test="not($isChunkMode) or $mode = 'component'">
                <!-- Generate links grouped by the type of the component-->
                <!-- This is hidden by default. -->
                <xsl:variable name="boxId">groupByCType</xsl:variable>
                <div class="level1" id="toc_group_by_component_type">
                    <!-- This is the displayed div by default if there is chunking by component -->
                    <xsl:choose>
                        <xsl:when
                            test="not($isChunkMode)">
                            <xsl:attribute name="style" select="'display:none'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="style" select="'display:block'"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <div>
                        <xsl:call-template name="indexGroupByComponent">
                            <xsl:with-param name="refSeq" select="ref"/>
                        </xsl:call-template>
                    </div>
                </div>

            </xsl:if>

            <xsl:if test="not($isChunkMode) or $mode = 'location'">

                <!-- Generate links grouped by the location of the component-->
                <!-- This is hidden by default. -->
                <xsl:variable name="boxId">groupByLocation</xsl:variable>
                <div class="level1" id="toc_group_by_location">
                    <!-- This is the displayed div by default if there is chunking by namespace -->
                    <xsl:choose>
                        <xsl:when
                            test="not($isChunkMode)">
                            <xsl:attribute name="style" select="'display:none'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="style" select="'display:block'"/>
                        </xsl:otherwise>
                    </xsl:choose>

                    <div>
                        <xsl:for-each-group select="ref" group-by="@schemaLocation">
                            <xsl:variable name="schemaLocation">
                                <xsl:choose>
                                    <xsl:when test="compare('', @schemaLocation) = 0"/>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@schemaLocation"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <div class="level2">
                                <p>
                                    <input id="button_{$schemaLocation}" type="image" value="-"
                                        src="img/btM.gif"
                                        onclick="switchState('{$schemaLocation}');" class="control"/>
                                    <span class="indexGroupTitle">
                                        <xsl:value-of select="$schemaLocation"/>
                                    </span>
                                </p>
                                <div id="{$schemaLocation}" style="display:block">
                                    <xsl:call-template name="indexGroupByComponent">
                                        <xsl:with-param name="refSeq" select="current-group()"/>
                                        <xsl:with-param name="prefix" select="$schemaLocation"/>
                                    </xsl:call-template>
                                </div>
                            </div>
                        </xsl:for-each-group>
                    </div>
                </div>

            </xsl:if>

        </div>
        </xsl:if>
        
        <xsl:if test="$isChunkMode and $mode = 'schHierarchy'">
            <xsl:variable name="schemasHierarchy" select="schemaHierarchy"/>
            <xsl:if test="not(empty($schemasHierarchy))">
                <xsl:call-template name="buildSchemaHierarchy">
                    <xsl:with-param name="schemaHierarchy" select="$schemasHierarchy"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="indexGroupByComponent">
        <xsl:param name="refSeq" required="yes"/>
        <xsl:param name="prefix"/>
                
                <!-- Use the horizontal layout. -->
                <xsl:for-each-group select="$refSeq" group-by="@refType">
                    <xsl:variable name="refType" select="@refType"/>
                    <!-- Can put this function of the isChunk -->
                    <div class="horizontalLayout">
                        <xsl:call-template name="makeRoundedTable">
                            <xsl:with-param name="content">
                                
                                <div class="componentGroupTitle">
                                    <xsl:variable name="currentComponent" select="."/>
                                    <xsl:value-of
                                        select="$componentTypeLabels/*[@key=$currentComponent/@refType]"/>s
                                </div>
                                <div id="{$prefix}{$refType}" style="display:block">
                                    <xsl:for-each select="current-group()">
                                        <xsl:sort select="text()"/>
                                        <div>
                                            <xsl:call-template name="reference"/>
                                        </div>                                            
                                    </xsl:for-each>
                                </div>
                                
                            </xsl:with-param>
                        </xsl:call-template>
                    </div>
                </xsl:for-each-group>
                <!-- Back to the vertical layout for the divs. -->
                <div style="clear:left"/>        
    </xsl:template>


    <xsl:template match="schema">
        <xsl:call-template name="component">
            <xsl:with-param name="type">
             <xsl:variable name="currentSchema" select="."/>
                <xsl:value-of select="$schemaTypeLabels/*[@key=$currentSchema/@type]"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="element">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Element</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="complexType">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Complex Type</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="simpleType">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Simple Type</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="attribute">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Attribute</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="attributeGroup">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Attribute Group</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="elementGroup">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Element Group</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="notation">
        <xsl:call-template name="component">
            <xsl:with-param name="type">Notation</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:function name="func:createControl" as="item()">
        <xsl:param name="boxID"/>
        <xsl:param name="buttonID"/>
        <input id="{$buttonID}" type="image" src="img/btM.gif" value="-" onclick="switchState('{$boxID}');"
            class="control"/>
    </xsl:function>

    <xsl:template match="facets">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>Facets</b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>                    
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <table class="facetsTable">
                        <xsl:for-each select="./facet">
                            <tr>
                                <td class="firstColumn">
                                    <xsl:value-of select="@name"/>
                                </td>
                                <td width="30%">
                                    <b>
                                        <xsl:value-of select="@value"/>
                                    </b>
                                </td>
                                <td>
                                    <div class="annotation">
                                        <xsl:for-each select="annotation">
                                            <xsl:call-template name="buildAnnotation"/>                                        
                                        </xsl:for-each>
                                    </div>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="properties">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>Properties</b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>                    
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <table class="propertiesTable">
                        <xsl:for-each select="./property">
                            <tr>
                                <td class="firstColumn">
                                    <xsl:value-of select="name"/>:
                                </td>
                                <td>
                                    <b>
                                        <xsl:value-of select="value"/>
                                    </b>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="namespace">
        <tr>
            <td class="firstColumn">
                <b>Namespace</b>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="compare('', text()) != 0">
                        <xsl:value-of select="text()"/>
                    </xsl:when>
                    <xsl:otherwise>No namespace</xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="schemaLocation">
        <tr>
            <td class="firstColumn">
                <b>Schema</b>
            </td>
            <td>
                <a><xsl:attribute name="href">
                <!-- generate a hyperlink to the sub-document for the schema -->
                <xsl:value-of select="concat(replace( func:substring-after-last(text(), '/') , '\.', '_'), '.html')"/></xsl:attribute>
                <xsl:value-of select="func:substring-after-last(text(), '/')"/>
                </a>    
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="diagram">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft"><b>Diagram</b></div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td class="diagram">
                <div id="{$boxID}" style="display:block">
                    <xsl:variable name="hasMap" as="xs:boolean" select="count(map) != 0"/>
                    <xsl:variable name="diagramLoc" select="location/text()"/>
                    <xsl:variable name="isSvgImage" select="ends-with($diagramLoc, '.svg')"/>
                    <xsl:choose>
                        <xsl:when test="$isSvgImage">
                            <xsl:element name="object">
                                <xsl:attribute name="border">0</xsl:attribute>
                                <xsl:attribute name="data">
                                    <xsl:value-of select="$diagramLoc"></xsl:value-of>
                                </xsl:attribute>
                                <xsl:attribute name="type">image/svg+xml</xsl:attribute>
                            </xsl:element> 
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:element name="img">
                                <xsl:attribute name="alt">Diagram</xsl:attribute>
                                <xsl:attribute name="border">0</xsl:attribute>
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$diagramLoc"></xsl:value-of>
                                </xsl:attribute>
                                <xsl:if test="boolean($hasMap)">
                                    <xsl:attribute name="usemap">
                                        <xsl:value-of select="concat('#', map/@name)"></xsl:value-of>
                                    </xsl:attribute>
                                </xsl:if>
                            </xsl:element>
                            <xsl:if test="boolean($hasMap)">
                                <xsl:variable name="mapName" select="map/@name"/>
                                <map name='{$mapName}' id='{$mapName}'>
                                    <xsl:for-each select="map/area">
                                        <area alt="{@alt}" href="{replace(@href, concat($intermediateXmlExtension, '#'), concat($extension, '#'))}" coords="{@coords}"/>
                                    </xsl:for-each>
                                </map>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>    
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="usedBy">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>Used by</b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <table class="usedByTable">
                        <xsl:for-each-group select="./ref" group-by="@refType">
                            <tr>
                                <td class="firstColumn">
                                    <xsl:variable name="currentRef" select="."/>
                                    <xsl:value-of
                                        select="$componentTypeLabels/*[@key=$currentRef/@refType]"/>
                                    <xsl:if test="count(current-group()) > 1">
                                        <xsl:text>s</xsl:text>
                                    </xsl:if>
                                    <xsl:text> </xsl:text>
                                </td>
                                <td>
                                    <xsl:for-each select="current-group()">
                                        <xsl:sort select="text()"/>
                                        <xsl:call-template name="reference"/>
                                        <xsl:if test="position() != last()">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                    </xsl:for-each>
                                </td>
                            </tr>
                        </xsl:for-each-group>
                    </table>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="attributes">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>Attributes</b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <table class="attributesTable">
                        <thead>
                            <tr>
                                <th>QName</th>
                                <th>Type</th>
                                <th>Fixed</th>
                                <th>Default</th>
                                <th>Use</th>
                                <th>Annotation</th>
                            </tr>
                        </thead>
                        <xsl:for-each select="attr">
                            <xsl:sort select="ref/text()"/>
                            <tr>
                                <td class="firstColumn">
                                    <xsl:call-template name="reference">
                                        <xsl:with-param name="ref" select="ref"/>
                                    </xsl:call-template>
                                </td>
                                <td>
                                    <xsl:call-template name="typeEmitter">
                                        <xsl:with-param name="type" select="type"/>
                                    </xsl:call-template>
                                </td>
                                <td>
                                    <xsl:value-of select="fixed"/>
                                </td>
                                <td>
                                    <xsl:value-of select="default"/>
                                </td>
                                <td>
                                    <xsl:value-of select="use"/>
                                </td>
                                <td>
                                    <div class="annotation">
                                        <xsl:for-each select="annotations/annotation">
                                            <xsl:call-template name="buildAnnotation"/>
                                        </xsl:for-each>
                                    </div>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="component">
        <xsl:param name="type"/>
        <xsl:element name="a">
            <xsl:attribute name="id" select="@id"/>
        </xsl:element>
        <div class="componentTitle">
            <xsl:choose>
                <xsl:when test="exists(redefinedComponent)">
                    <xsl:text>redefinition of </xsl:text>
                    <xsl:value-of select="$type"/>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="reference">
                        <xsl:with-param name="ref" select="redefinedComponent"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$type"/>
                    <xsl:text> </xsl:text>
                    <span class="qname">
                        <xsl:for-each select="declarationPath/ref">
                            <xsl:call-template name="reference"/>
                            <xsl:text> / </xsl:text>
                        </xsl:for-each>
                        <xsl:if test="compare(local-name(.), 'attribute') = 0">
                            <xsl:text>@</xsl:text>
                        </xsl:if>
                        <xsl:value-of select="qname/text()"/>
                    </span>
                </xsl:otherwise>
            </xsl:choose>
        </div>

        <xsl:call-template name="makeRoundedTable">
            <xsl:with-param name="content">
                <table class="component">
                    <tbody>
                        <xsl:apply-templates select="namespace | annotations"/>
                        <xsl:apply-templates select="diagram | type | typeHierarchy | properties"/>
                        <xsl:apply-templates select="facets"/>
                        <xsl:apply-templates select="substitutionGroup | substitutionGroupAffiliation"/>
                        <xsl:apply-templates select="usedBy | model | children | attributes | contraints | instance | source"/>
                        <xsl:apply-templates select="publicid | systemid"/>
                        <xsl:apply-templates select="schemaLocation"/>
                    </tbody>
                </table>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:if test="not(boolean($isChunkMode))">
            <div class="toTop">
                <a href="#INDEX"> [ top ] </a>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="typeEmitter">
        <xsl:param name="type"/>
        <xsl:for-each select="$type/node()">
            <xsl:choose>
                <xsl:when test="compare('ref', local-name()) = 0">
                    <xsl:call-template name="reference">
                        <xsl:with-param name="ref" select="."/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="element/type | complexType/type | attribute/type | simpleType/type">
        <tr>
            <td class="firstColumn">
                <b>Type</b>
            </td>
            <td>
                <xsl:call-template name="typeEmitter">
                    <xsl:with-param name="type" select="."/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>

    <xd:doc>
        <xd:desc>Show the hierarchy type  </xd:desc>
    </xd:doc>
    <xsl:template name="hierarchyOutput">
        <xsl:param name="refs"/>
        <xsl:param name="index" as="xs:integer" select="1"
            xmlns:xs="http://www.w3.org/2001/XMLSchema"/>
        <ul>
            <li>
                <xsl:call-template name="reference">
                    <xsl:with-param name="ref" select="$refs[$index]"/>
                </xsl:call-template>

                <xsl:if test="$index &lt; count($refs)">
                    <xsl:call-template name="hierarchyOutput">
                        <xsl:with-param name="refs" select="$refs"/>
                        <xsl:with-param name="index" select="$index + 1"/>
                    </xsl:call-template>
                </xsl:if>
            </li>
        </ul>
    </xsl:template>

    <xsl:template match="typeHierarchy">
        <tr>
            <td class="firstColumn">
                <b>Type hierarchy</b>
            </td>
            <td>
                <xsl:call-template name="hierarchyOutput">
                    <xsl:with-param name="refs" select="ref"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="model">
        <tr>
            <td class="firstColumn">
                <b>Model</b>
            </td>
            <td>
                <xsl:call-template name="groupTemplate">
                    <xsl:with-param name="group" select="group[1]"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="groupTemplate">
        <xsl:param name="group" select="."/>
        <xsl:variable name="compositor">
            <xsl:value-of select="$group/@compositor"/>
        </xsl:variable>
        <xsl:variable name="separator">
            <xsl:if test="compare($compositor, 'sequence') = 0">
                <xsl:text> , </xsl:text>
            </xsl:if>
            <xsl:if test="compare($compositor, 'choice') = 0">
                <xsl:text> | </xsl:text>
            </xsl:if>
            <xsl:if test="compare($compositor, 'all') = 0">
                <xsl:text> </xsl:text>
            </xsl:if>
        </xsl:variable>

        <xsl:if test="compare($compositor, 'all') = 0">
            <xsl:text>ALL(</xsl:text>
        </xsl:if>
        <xsl:for-each
            select="$group/*[compare(local-name(.), 'group') = 0 or compare(local-name(.), 'ref') = 0]">
            <xsl:if test="position() != 1">
                <xsl:value-of select="$separator"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="compare(local-name(.), 'ref') = 0">
                    <xsl:call-template name="reference"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="nextCompositor">
                        <xsl:value-of select="@compositor"/>
                    </xsl:variable>

                    <xsl:if test="compare($compositor, $nextCompositor) != 0">
                        <xsl:text>(</xsl:text>
                    </xsl:if>

                    <xsl:call-template name="groupTemplate"/>

                    <xsl:if test="compare($compositor, $nextCompositor) != 0">
                        <xsl:text>)</xsl:text>
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>

        <xsl:if test="compare($compositor, 'all') = 0">
            <xsl:text>)</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="children">
        <tr>
            <td class="firstColumn">
                <b>Children</b>
            </td>
            <td>
                <xsl:for-each select="child">
                    <xsl:sort select="ref/text()"/>
                    <xsl:call-template name="reference">
                        <xsl:with-param name="ref" select="ref"/>
                    </xsl:call-template>
                    <xsl:if test="position() != last()">
                        <xsl:text>, </xsl:text>
                    </xsl:if>
                </xsl:for-each>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="source | instance">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>
                        <xsl:choose>
                            <xsl:when test="compare(local-name(.), 'source') = 0">Source</xsl:when>
                            <xsl:otherwise>Instance</xsl:otherwise>
                        </xsl:choose>
                     </b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <!-- Formats an XML source section-->
                    <xsl:variable name="tokens" select="token"/>
                    <xsl:call-template name="formatXmlSource">
                        <xsl:with-param name="tokens" select="$tokens"/>
                    </xsl:call-template>
                </div>
            </td>
        </tr>
    </xsl:template>


    <xsl:template match="constraints">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft">
                    <b>Identity constraints</b>
                </div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <table class="identityConstraintsTable">
                        <thead>
                            <tr>
                                <th>QName</th>
                                <th>Type</th>
                                <th>Refer</th>
                                <th>Selector</th>
                                <th>Field(s)</th>
                            </tr>
                        </thead>
                        <xsl:for-each select="constraint">
                            <tr>
                                <td>
                                    <xsl:value-of select="name"/>
                                </td>
                                <td>
                                    <xsl:value-of select="type"/>
                                </td>
                                <td>
                                    <xsl:value-of select="refer"/>
                                </td>
                                <td>
                                    <xsl:value-of select="selector"/>
                                </td>
                                <td>
                                    <xsl:value-of select="fields"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="publicid | systemid">
        <tr>
            <td class="firstColumn">
                <b>
                    <xsl:choose>
                        <xsl:when test="compare(local-name(.), 'publicid') = 0"
                            >Public ID</xsl:when>
                        <xsl:otherwise>System ID</xsl:otherwise>
                    </xsl:choose>
                </b>
            </td>
            <td><xsl:value-of select="text()"/></td>
        </tr>
    </xsl:template>

    <xsl:template match="substitutionGroup | substitutionGroupAffiliation">
        <tr>
            <td class="firstColumn">
                <b>
                    <xsl:choose>
                        <xsl:when test="compare(local-name(.), 'substitutionGroup') = 0"
                            >Substitution Group</xsl:when>
                        <xsl:otherwise>Substitution Group Affiliation</xsl:otherwise>
                    </xsl:choose>
                </b>
            </td>
            <td>
                <ul>
                    <xsl:for-each select="ref">
                        <li>
                            <xsl:call-template name="reference"/>
                        </li>
                    </xsl:for-each>
                </ul>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="annotations">
        <xsl:variable name="boxID" select="func:getDivId(.)"/>
        <tr>
            <td class="firstColumn">
                <div class="floatLeft"><b>Annotations</b></div>
                <div class="floatRight">
                    <xsl:copy-of select="func:createControl($boxID, func:getButtonId(.))"/>
                </div>
            </td>
            <td>
                <div id="{$boxID}" style="display:block">
                    <xsl:for-each select="annotation">
                        <div class="annotation">
                            <xsl:call-template name="buildAnnotation"/>
                        </div>
                    </xsl:for-each>
                </div>
            </td>
        </tr>
    </xsl:template>

    <xd:doc>
        <xd:desc>Builds an annotation representation from the context annotation </xd:desc>
    </xd:doc>
    <xsl:template name="buildAnnotation">
        <xsl:if test="exists(@source)">
                <p><a href="{@source}"><xsl:value-of select="@source"/></a></p>
        </xsl:if>
        <xsl:variable name="tokens" select="token"/>
        <xsl:choose>
            <xsl:when test="empty($tokens)">
                <xsl:for-each select="child::node()">
                    <xsl:copy-of select="." copy-namespaces="no"/>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <!-- Formats an XML source section-->
                <xsl:call-template name="formatXmlSource">
                    <xsl:with-param name="tokens" select="$tokens"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="formatXmlSource">
        <xsl:param name="tokens"/>

        <!-- I have to put the PRE in a TABLE to convince the Internet Explorer
        to wrap the PRE. In addition to putting it into a table, the css
        must contain the bloc: 
        
        pre {
             white-space: pre-wrap;       /* css-3 */
             white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
             white-space: -pre-wrap;      /* Opera 4-6 */
             white-space: -o-pre-wrap;    /* Opera 7 */
             word-wrap: break-word;       /* Internet Explorer 5.5+ */
             _white-space: pre;   /* IE only hack to re-specify in addition to            word-wrap  */
        }
        -->
        <table
            style="table-layout:fixed;white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space: -o-pre-wrap;word-wrap: break-word;_white-space:pre;"
            class="preWrapContainer">
            <tr>
                <td width="100%">
                    <pre>
                        <xsl:for-each select="$tokens">
                            <!-- The content of the token is space preserve -->
                            <xsl:element name="span">
                                <xsl:attribute name="class" select="@type"/>
                                <!-- On IE the pre-wrap does not normalize the text. Doing it here. -->
                                <xsl:choose>
                                    <xsl:when test="@type = 'tT'">
                                        <xsl:choose>
                                            <xsl:when test="text() = ' '">
                                                <!-- Just a whitespace should preserve it, 
                                                    may be it dellimits something.  -->
                                                <xsl:text xml:space="preserve"> </xsl:text>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:choose>
                                                    <xsl:when test="@xml:space = 'preserve'">
                                                        <xsl:value-of select="text()"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <!-- Because we normalize there is no need to keep the whitespace preserve -->
                                                        <xsl:attribute name="style">white-space:normal</xsl:attribute>
                                                        <xsl:value-of select="normalize-space(text())"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:otherwise>
                                        </xsl:choose>                                        
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="text()"/>
                                    </xsl:otherwise>
                                </xsl:choose>                    
                            </xsl:element>
                        </xsl:for-each>
                    </pre>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template name="makeRoundedTable">
        <xsl:param name="content" required="yes"/>
        <table class="rt">
            <tr>
                <td class="rt_cornerTopLeft"/>
                <td class="rt_lineTop"/>
                <td class="rt_cornerTopRight"/>
            </tr>
            <tr>
                <td class="rt_lineLeft"></td>
                <td class="rt_content"><xsl:copy-of select="$content"/></td>
                <td class="rt_lineRight"></td> 
            </tr>
            <tr>
                <td class="rt_cornerBottomLeft"/>
                <td class="rt_lineBottom"/>
                <td class="rt_cornerBottomRight"/>
            </tr>
        </table>


    </xsl:template>

    <xsl:template name="main">
        <body>

            <xsl:if test="not($isChunkMode) or not(exists(index))">
                <!-- The position must be absolute for the floating mechanism to work. -->
                <xsl:if test="string-length($annotationBoxes) > 0 or string-length($attributesBoxes) > 0 or string-length($diagramBoxes) > 0 or string-length($facetsBoxes) > 0 or string-length($identityConstraintsBoxes) > 0 or string-length($instanceBoxes) > 0 or string-length($propertiesBoxes) > 0 or string-length($sourceBoxes) > 0 or string-length($usedByBoxes) > 0"> 
                    <div id="global_controls" class="globalControls" style="position:absolute;right:0;">
                        <xsl:call-template name="makeRoundedTable">
                            <xsl:with-param name="content">
                                <h3>Showing:</h3>
                                <table>
                                    <xsl:if test="string-length($annotationBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, annotationBoxes);" class="control"/></span>
                                                <span class="globalControlName">Annotations</span>
                                            </td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($attributesBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, attributesBoxes);" class="control"/></span>                                
                                            <span class="globalControlName">Attributes </span></td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($diagramBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, diagramBoxes);" class="control"/></span>
                                                <span class="globalControlName">Diagrams</span>
                                            </td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($facetsBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, facetsBoxes);" class="control"/></span> 
                                            <span class="globalControlName">Facets </span></td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($identityConstraintsBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, identityConstraintsBoxes);" class="control"/></span>
                                            <span class="globalControlName">Identity Constraints</span>
                                            </td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($instanceBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, instanceBoxes);" class="control"/></span>
                                            <span class="globalControlName">Instances</span></td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($propertiesBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, propertiesBoxes);" class="control"/></span> 
                                            <span class="globalControlName">Properties </span></td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($sourceBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, sourceBoxes);" class="control"/></span>
                                            <span class="globalControlName">Source</span></td>
                                        </tr>
                                    </xsl:if>
                                    <xsl:if test="string-length($usedByBoxes) > 0">
                                        <tr>
                                            <td>
                                            <span><input type="checkbox" value="-" checked="checked" onclick="switchStateForAll(this, usedByBoxes);" class="control"/></span> 
                                            <span class="globalControlName">Used by </span></td>
                                        </tr>
                                    </xsl:if>
                                </table>
                                <div align="right">
                                <span><input type="button"  onclick="getElementObject('global_controls').style.display = 'none';" value="Close"/></span>
                                </div>
                            </xsl:with-param>
                        </xsl:call-template>
                    </div>
                </xsl:if>
            </xsl:if>
            
            <xsl:variable name="hasSchemasHierarchy" select="not(empty(schemaHierarchy))"/>
            
            <xsl:for-each select="index">
                <xsl:call-template name="indexContent">
                    <xsl:with-param name="mode">location</xsl:with-param>
                    <xsl:with-param name="hasHierarchy" select="$isChunkMode and $hasSchemasHierarchy"/>
                </xsl:call-template>
                <xsl:call-template name="index">
                    <xsl:with-param name="mode">namespace</xsl:with-param>
                    <xsl:with-param name="outputFile" select="$indexFileNamespace"/>
                    <xsl:with-param name="hasHierarchy" select="$isChunkMode and $hasSchemasHierarchy"/>
                </xsl:call-template>
                <xsl:call-template name="index">
                    <xsl:with-param name="mode">component</xsl:with-param>
                    <xsl:with-param name="outputFile" select="$indexFileComp"/>
                    <xsl:with-param name="hasHierarchy" select="$isChunkMode and $hasSchemasHierarchy"/>
                </xsl:call-template>
            </xsl:for-each>
            
            <!-- When split, the hierarchy appears with the index in the same frame -->
            <xsl:if test="$hasSchemasHierarchy and $isChunkMode">
                <xsl:call-template name="index">
                    <xsl:with-param name="mode">schHierarchy</xsl:with-param>
                    <xsl:with-param name="outputFile" select="$schemaHierarchyFile"/>
                    <xsl:with-param name="hasHierarchy" select="$hasSchemasHierarchy"/>
                </xsl:call-template>
            </xsl:if>
            
            <xsl:if test="not($isChunkMode) and $hasSchemasHierarchy">
                <xsl:call-template name="buildSchemaHierarchy">
                    <xsl:with-param name="schemaHierarchy" select="schemaHierarchy"/>
                    <xsl:with-param name="title">Resource hierarchy:</xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            
            <xsl:apply-templates select="element | complexType | attribute | simpleType | elementGroup | schema | attributeGroup | notation"/>
            
            <xsl:call-template name="generateFooter"/>

            <script type="text/javascript">
                <xsl:comment>                     
                     // The namespace is the selected option in the TOC combo.
                    
                     // Floats the toolbar.
                     var globalControls = getElementObject("global_controls"); 
                     
                     if(globalControls != null){
	                     var browser=navigator.appName;
						 var version = parseFloat(navigator.appVersion.split('MSIE')[1]);
						 
						 var IE6 = false;
						 if ((browser=="Microsoft Internet Explorer") &amp;&amp; (version &lt; 7)){
						 	IE6 = true;
						 }
	
	                     //alert (IE6 + " |V| " + version);
	                     
	                     if(IE6){
	                     	// On IE 6 the 'fixed' property is not supported. We must use javascript. 
	                         globalControls.style.position='absolute';                         
	                         // The global controls will do not exist in the TOC frame, when chunking.
	                         findAndFloat("global_controls", 225, 30);    
	                     } else {
	                      	  globalControls.style.position='fixed';                     	
	                     }
	                     
	                     globalControls.style.right='0';                       
                     }
                </xsl:comment>
            </script>
        </body>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>Generate the footer that must appear in all files.</xd:desc>
    </xd:doc>
    <xsl:template name="generateFooter">
        <div class="footer">
            <hr/>
            <div align="center">XML Schema documentation generated by <a href="http://www.oxygenxml.com" target="_parent" >
                <span class="oXygenLogo"><span class="redX">&lt;</span>o<span class="redX">X</span>ygen<span class="redX">/&gt;</span></span></a><sup>&#174;</sup> XML <xsl:value-of select="$distribution"/>.</div>
        </div>
    </xsl:template>

    <xd:doc>
        <xd:desc>Builds a hierarchy of the documented schemas based on the detected directives.</xd:desc>
        <xd:param name="mainSchema">Main schema. The hierarchy is found inside it.</xd:param>
    </xd:doc>
    <xsl:template name="buildSchemaHierarchy">
        <xsl:param name="schemaHierarchy"/>
        <xsl:param name="title"/>
            <xsl:if test="not(empty($schemaHierarchy))">
                <xsl:if test="$title != ''">
                    <p class="sHierarchyTitle"><xsl:value-of select="$title"/></p>
                </xsl:if>
                <ul class="schemaHierarchy">
                    <li>
                        <xsl:variable name="uniqueId" select="concat('sH', $schemaHierarchy/@refId)"/>
                        <p class="componentTitle">
                            <input id="button_{$uniqueId}" type="image" value="-"
                                src="img/btM.gif"
                                onclick="switchState('{$uniqueId}');" class="control"/>
                            <a href="#{$schemaHierarchy/@refId}"><xsl:value-of select="$schemaHierarchy/@schemaLocation"/></a></p>
                        <div id="{$uniqueId}" style="display:block">
                            <ul>
                                <xsl:for-each select="$schemaHierarchy">
                                    <xsl:apply-templates>
                                        <xsl:with-param name="parentSchema" select="$schemaHierarchy/@schemaLocation"/>
                                        <xsl:with-param name="uniqueId" select="$uniqueId"/>
                                        <xsl:with-param name="level">1</xsl:with-param>
                                    </xsl:apply-templates>
                                </xsl:for-each>
                            </ul>
                        </div>
                    </li>
                </ul>
            </xsl:if>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>Process a directive and output a list item. Recursion is used so that a 
        tree like representation is build using lists.</xd:desc>
        <xd:param name="parentSchema">Parent schema for this directive.</xd:param>
    </xd:doc>
    <xsl:template match="directive">
        <xsl:param name="parentSchema"/>
        <xsl:param name="uniqueId"/>
        <xsl:param name="level" as="xs:integer"/>
        
        <xsl:variable name="directive" select="."/>
        <xsl:variable name="uniqueId">
            <xsl:choose>
                <xsl:when test="exists($directive/@refId)">
                    <xsl:value-of select="concat($uniqueId, $directive/@refId)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($uniqueId, $directive/@schemaLocation)"/>
                </xsl:otherwise>
            </xsl:choose>
            
        </xsl:variable>
        <xsl:variable name="image">
            <xsl:choose>
                <xsl:when test="not($directive/@cycle)">
                    <xsl:text>img/HierarchyArrow12.jpg</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>img/HierarchyCycle12.jpg</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="hasChildren" select="not(empty($directive/directive))"/>
        <li class="schemaHierarchy">
            <p>
                <xsl:variable name="btImage">
                    <xsl:choose>
                        <xsl:when test="$level = 1">
                            <xsl:text>img/btP.gif</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>img/btM.gif</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:variable name="tooltip">
                    <!-- This is a tooltip-->
                    <xsl:value-of select="$scHierarchyTooltip/*[@key = $directive/@directiveType]/text()"/>
                    <xsl:text> '</xsl:text>
                    <xsl:value-of select="$parentSchema"/>
                    <xsl:text>'.</xsl:text>
                </xsl:variable>
                
                <xsl:if test="$hasChildren">
                    <input id="button_{$uniqueId}" type="image" value="-"
                        src="{$btImage}"
                        onclick="switchState('{$uniqueId}');" class="control"/>
                </xsl:if>
                
                <img src="{$image}">
                    <xsl:if test="$directive/@cycle">
                        <xsl:attribute name="title" select="concat($tooltip, ' Cycle detected.')"/>
                    </xsl:if>
                </img>
                <xsl:text> </xsl:text>
                <xsl:if test="not($directive/@cycle)">
                    <img src="{$scHierarchyIcons/*[@key = $directive/@directiveType]/text()}" title="{$tooltip}"/>
                    <xsl:text> </xsl:text>
                </xsl:if>
                
                <xsl:choose>
                    <xsl:when test="exists($directive/@refId)">
                        <a  href="{concat(substring-before($directive/@base, $intermediateXmlExtension), $extension, '#', $directive/@refId)}"
                            target="{$target}" title="{$tooltip}">
                            <xsl:variable name="criteria" select="$splitInfo/@criteria"/>
                            <xsl:if test="compare($criteria, $chunkValueNone) != 0">
                                <xsl:attribute name="onclick" select="func:getTitle($directive, $criteria)"/>
                            </xsl:if>
                            <xsl:value-of select="$directive/@schemaLocation"/>
                        </a>        
                    </xsl:when>
                    <xsl:otherwise><xsl:value-of select="$directive/@schemaLocation"/></xsl:otherwise>
                </xsl:choose>
            </p>
            <xsl:if test="$hasChildren">
                <xsl:variable name="style">
                    <xsl:choose>
                        <xsl:when test="$level = 1">
                            <xsl:text>display:none</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>display:block</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    </xsl:variable>
                    <div id="{$uniqueId}" style="{$style}">
                    <ul>
                        <xsl:apply-templates>
                            <xsl:with-param name="parentSchema" select="$directive/@schemaLocation"/>
                            <xsl:with-param name="uniqueId" select="$uniqueId"/>
                            <xsl:with-param name="level" select="$level + 1"/>
                        </xsl:apply-templates>
                    </ul>
                </div>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="text()"/>
</xsl:stylesheet>

