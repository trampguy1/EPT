<?xml version="1.0" encoding="UTF-8"?>
<!--
********************************************************************************
 Title 		: SiamToCatStyleSheet1.xml
 Description	: A stylesheet for converting SIAM plans to CAT Plans
 Created 	: Dec 06, 2004
 Copyright	: Copyright (c) 2004 - All Rights Reserved
 Company	: NGI Systems
 Author		: Jeff Spaulding (Jeff.Spaulding@ngisystems.com)
 
 Changes
 ========
 04-Jan-2005 : Added templates to support a ProcessLibrary element
 05-Jan-2005 : Added attributes to the structure under <ProcessLibrary>, and new templates
 10-Jan-2005 : Changed the <startkey> and <endkey> tags in the mechanisms
********************************************************************************
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">		
<xsl:output method="xml" indent="yes"/>
			
<xsl:template match="/">
	<xsl:comment>Created using the SIAM to CAT Stylesheet Version 1.0</xsl:comment>
	<SIAM>
		<Plan>
  			<!--TODO: which data to use for the name attribute-->
			<xsl:attribute name="name">Untitled</xsl:attribute>	
	  		<Graph>	
				<xsl:for-each select="//*[name()='node']">			
					<xsl:variable name="itemguid">
						<xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/>
					</xsl:variable>			
				
					<!--Create the CAT Event from the SIAM node-->
					<xsl:call-template name="createEvent">
						<xsl:with-param name="itemGUID"><xsl:value-of select="@difference-id"/></xsl:with-param>
					</xsl:call-template>			
				
					<!--Check for Children-->
					<!--<xsl:variable name="childPath" select="descendant::node()[name()='child']"></xsl:variable>
					<xsl:if test="$childPath">
						<xsl:call-template name="createMechanism">
							<xsl:with-param name="path" ><xsl:value-of select="$childPath"/></xsl:with-param>
							<xsl:with-param name="startKey" ><xsl:value-of select="$itemguid"/></xsl:with-param>
						</xsl:call-template>				
					</xsl:if>-->		
				</xsl:for-each>
				<xsl:for-each select="//*[name()='link']">
					<Shape>
						<xsl:variable name="uniqueID"><xsl:value-of select="generate-id()"/></xsl:variable>
						<xsl:attribute name="id"><xsl:value-of select="$uniqueID"/></xsl:attribute>
						<xsl:attribute name="type">mechanism</xsl:attribute>		
						<itemguid><xsl:value-of select="@difference-id"/></itemguid>
						<xsl:variable name="start"><xsl:value-of select="child::node()[name()='parent']/attribute::node"/></xsl:variable>
						<xsl:variable name="end"><xsl:value-of select="child::node()[name()='child']/attribute::node"/></xsl:variable>
						<startkey><xsl:value-of select="substring($start,0,10)"/></startkey>
						<endkey><xsl:value-of select="substring($end,0,10)"/></endkey>
						<style>solid</style>
						<text/>
					</Shape>
				</xsl:for-each>

  			</Graph>
			<Logic>
				<xsl:for-each select="//*[name()='node']">
					<!--Create the CAT PlanItem event from the SIAM node-->
					<xsl:call-template name="createPlanItemEvent">
						<xsl:with-param name="itemGUID">
						<xsl:value-of select="@difference-id"/>
						<!--<xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/>-->
						</xsl:with-param> 					
					</xsl:call-template>			
				</xsl:for-each>
				<xsl:for-each select="//*[name()='link']">
					<!--Create the CAT PlanItem mechanism from the SIAM link-->
					<xsl:call-template name="createPlanItemMechanism"> 
					</xsl:call-template>
				</xsl:for-each>
			</Logic>
			<ProcessLibrary>
				<TheProcesses>
					<xsl:for-each select="//*[name()='node']">
						<xsl:call-template name="createProcess">
							<xsl:with-param name="guid" >PLE<xsl:value-of select="position()"/></xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>	
				</TheProcesses>
				<TheSignals>
					<xsl:for-each select="//*[name()='link']">
						<xsl:call-template name="createSignal">
							<xsl:with-param name="guid">Signal-<xsl:value-of select="@difference-id" /></xsl:with-param>
							<xsl:with-param name="name"><xsl:value-of select="position()"/></xsl:with-param>
						</xsl:call-template>				
					</xsl:for-each>
				</TheSignals>
			</ProcessLibrary>
		</Plan>
	</SIAM>
</xsl:template>

<!--template "createEvent" : Creates a CAT Event from a SIAM node-->
<xsl:template name="createEvent">
	<!--param "itemGUID" : a globally unique ID-->
	<xsl:param name="itemGUID" />
	<Shape>
		<xsl:variable name="temp"><xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/></xsl:variable>
		<xsl:variable name="formatedid"><xsl:value-of select="substring($temp,0,10)"/></xsl:variable>
		<xsl:attribute name="id">
			<!--<xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/>-->
			<xsl:value-of select="$formatedid"/>
		</xsl:attribute>
		<xsl:attribute name="type">event</xsl:attribute>				
		<itemguid><xsl:value-of select="$itemGUID"/></itemguid>
		<!--Add floating point precision-->
		<height><xsl:value-of select="concat(descendant::node()[name()='rect']/attribute::height, '.0', '')"/></height>
		<width><xsl:value-of select="concat(descendant::node()[name()='rect']/attribute::width, '.0', '')"/></width>
		<xpos><xsl:value-of select="concat(descendant::node()[name()='x'], '.0', '')"/></xpos>
		<ypos><xsl:value-of select="concat(descendant::node()[name()='y'], '.0', '')"/></ypos>
		<text><xsl:value-of select="child::node()[name()='title']"/></text>
	</Shape>
</xsl:template>

<!--template "createMechanism" : Creates a CAT Mechanism from a SIAM node's children or parent data-->
<xsl:template name="createMechanism">
	<!--param "path" : starting Parent or Child Element-->
	<xsl:param name="path"/>
	<!--param "startKey" : The node where this mechanism begins from-->
	<xsl:param name="startKey"/>
	<xsl:for-each select="$path">
	<Shape>
		<xsl:variable name="uniqueID"><xsl:value-of select="generate-id()"/></xsl:variable>
		<xsl:attribute name="id"><xsl:value-of select="$uniqueID"/></xsl:attribute>
		<xsl:attribute name="type">mechanism</xsl:attribute>		
		<itemguid><xsl:value-of select="$uniqueID"/></itemguid>
		<startkey><xsl:value-of select="$startKey"/></startkey>
		<endkey><xsl:value-of select="@node"/></endkey>
		<style>solid</style>
		<text/>
	</Shape>	
	</xsl:for-each>
</xsl:template>

<!--template "createPlanItemEvent" : Creates a CAT PlanItem of type "event" from a SIAM node-->
<xsl:template name ="createPlanItemEvent">
	<!--param "path" : starting Parent or Child Element-->
	<xsl:param name="itemGUID"/>
	<PlanItem>
		<xsl:attribute name="name"><xsl:value-of select="child::node()[name()='title']"/></xsl:attribute>
		<xsl:attribute name="label"></xsl:attribute>
		<xsl:attribute name="guid"><xsl:value-of select="$itemGUID"/></xsl:attribute>
		<xsl:attribute name="type">event</xsl:attribute>
		<DefCause>0.75</DefCause>
		<DefEffect>0.75</DefEffect>
		<DefInhibit>0.75</DefInhibit>
		<documentation>
			<description/>
		</documentation>
		<Schedule>
			<Probability>
				<xsl:attribute name="time">0</xsl:attribute>
				<xsl:value-of select="descendant::node()[name()='belief']"/>
			</Probability>
		</Schedule>
		<DefLeak>0.0</DefLeak>		
		<Causes>
		    <xsl:for-each select="//*[name()='link']">
		        <xsl:variable name="currentNode">
					<xsl:value-of select="descendant::node()[name()='parent']/attribute::node"/>
				</xsl:variable>	
		        <xsl:variable name="currentID">
					<xsl:value-of select="substring($currentNode,6)"/>
				</xsl:variable>	
			    <xsl:if test="$currentID=$itemGUID">		
					<Mech>
						<xsl:attribute name="guid"><xsl:value-of select="@difference-id" /></xsl:attribute>
					</Mech>
				</xsl:if>
			
			</xsl:for-each>
		</Causes>
		<Effects>
		    <xsl:for-each select="//*[name()='link']">
		        <xsl:variable name="currentNode">
					<xsl:value-of select="descendant::node()[name()='child']/attribute::node"/>
				</xsl:variable>	
		        <xsl:variable name="currentID">
					<xsl:value-of select="substring($currentNode,6)"/>
				</xsl:variable>	
			    <xsl:if test="$currentID=$itemGUID">		
					<Mech>
						<xsl:attribute name="guid"><xsl:value-of select="@difference-id" /></xsl:attribute>
					</Mech>
				</xsl:if>
			
			</xsl:for-each>
		</Effects>
		<Inhibits/>
		<Notes></Notes>
		<Delay>0</Delay>
		<Persistence>1</Persistence>
		<Continuation>0.0f</Continuation>
		<PGuid>
			PLE<xsl:value-of select="position()"/>
		 </PGuid>		
	</PlanItem>
</xsl:template>

<!--template "createPlanItemMechanism" : Creates a CAT PlanItem of type "Mechanism" from a SIAM link element-->
<xsl:template name ="createPlanItemMechanism">
	<!--param "path" : starting Parent or Child Element-->
	<xsl:param name="path"/>
	<!--param "path" : starting Parent or Child Element-->
	<xsl:param name="signalGUID"/>
	<PlanItem>
		<xsl:attribute name="name">Default</xsl:attribute>
		<xsl:attribute name="label">signal</xsl:attribute>
		<xsl:attribute name="guid"><xsl:value-of select="@difference-id" /></xsl:attribute>
		<xsl:attribute name="type">mechanism</xsl:attribute>
		<SignalType>CAUSE</SignalType> 
		<Signal>
			<xsl:attribute name="guid">Signal-<xsl:value-of select="@difference-id" /></xsl:attribute>
			<xsl:attribute name="name">default</xsl:attribute>
		</Signal>
		<ToEvent><xsl:value-of select="substring(child::node()[name()='child']/attribute::node,6,4)"/></ToEvent> 
		<FromEvent><xsl:value-of select="substring(child::node()[name()='parent']/attribute::node,6,4)"/></FromEvent> 
		<Delay>0</Delay> 
		<Persistence>1</Persistence> 
		<Continuation>0.0f</Continuation>
	</PlanItem>
</xsl:template>

<!--template "createProcess" : Creates a Process element from a node-->
<xsl:template name="createProcess">
	<xsl:param name="guid"/>
	<Process>
		<xsl:attribute name="guid"><xsl:value-of select="$guid"/></xsl:attribute>
		<xsl:attribute name="Name"><xsl:value-of select="child::node()[name()='title']"/></xsl:attribute>
		<SignalData>
			<xsl:call-template name="createModeSet">
				<xsl:with-param name="modeNum" >1</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="createModeSet">
				<xsl:with-param name="modeNum" >3</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="createModeSet">
				<xsl:with-param name="modeNum" >2</xsl:with-param>
			</xsl:call-template>
		</SignalData>
	</Process>
</xsl:template>

<!--template "createModeSet" : Creates a ModeSet element for a Process-->
<xsl:template name="createModeSet">
	<xsl:param name="modeNum"/>
	<ModeSet>
		<xsl:attribute name="mode"><xsl:value-of select="$modeNum"/></xsl:attribute>
		<!--if mode is 1 should be .75, if 2 then .8, if 3 then 1 -->
			
		
		<SignalSet>
			<!--Check for Causes-->
			<xsl:if test="$modeNum=1">		
				<xsl:attribute name="defaultSingleSignal">0.75</xsl:attribute>				
				<xsl:call-template name="checkForCauses" />
			</xsl:if>		
			<xsl:if test="$modeNum=2">		
				<xsl:attribute name="defaultSingleSignal">0.8</xsl:attribute>				
			</xsl:if>	
			<!--Check for Effects-->
			<xsl:if test="$modeNum=3">		
				<xsl:attribute name="defaultSingleSignal">1.0</xsl:attribute>				
				<xsl:call-template name="checkForEffects" />
			</xsl:if>			
		</SignalSet>
		<Inversions>
			<SignalSet/>
		</Inversions>
		<xsl:call-template name="createProtocolSet">
			<xsl:with-param name="protocolNum" >4</xsl:with-param>
			<xsl:with-param name="modeNum"><xsl:value-of select="$modeNum" /></xsl:with-param>
		</xsl:call-template>
		<xsl:call-template name="createProtocolSet">
			<xsl:with-param name="protocolNum" >6</xsl:with-param>
			<xsl:with-param name="modeNum"><xsl:value-of select="$modeNum" /></xsl:with-param>
		</xsl:call-template>
	</ModeSet>
</xsl:template>

<!--template "createProtocolSet" : Creates a ProtocolSet element for a Process-->
<xsl:template name="createProtocolSet">
	<xsl:param name="modeNum"/>
	<xsl:param name="protocolNum"/>
	<ProtocolSet>
		<xsl:attribute name="protocol"><xsl:value-of select="$protocolNum"/></xsl:attribute>
		<SignalSet>
			<xsl:if test="($modeNum=1)and($protocolNum=4)">
				<xsl:call-template name="checkForCauses" />
			</xsl:if>
			<xsl:if test="($modeNum=3)and($protocolNum=4)">
				<xsl:call-template name="checkForEffects" />
			</xsl:if>
		</SignalSet>
		<ElicitationSet/>
	</ProtocolSet>
</xsl:template>

<!--template "createSignal" : Creates a Signal element -->
<xsl:template name="createSignal">
	<xsl:param name="guid"/>
	<xsl:param name="name"/>
	<Signal>
		<xsl:attribute name="guid"><xsl:value-of select="$guid" /></xsl:attribute>
		<xsl:attribute name="name"><xsl:value-of select="$name" /></xsl:attribute>
	</Signal> 
</xsl:template>

<!--template "checkForEffects" : checks for any Effects when creating a ModeSet-->
<xsl:template name="checkForEffects">
	<!--Hold on to this variable to compare guid's-->			
	<xsl:variable name="itemguid">
		<xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/>
	</xsl:variable>	
	<!--For each node of type link-->
	<xsl:for-each select="//*[name()='link']">
		<!--If the name under the parent node == the itemguid -->
		<xsl:if test="child::node()[name()='parent']/attribute::node=$itemguid">
			<xsl:call-template name="createSignal">
				<xsl:with-param name="guid">Signal-<xsl:value-of select="@difference-id" /></xsl:with-param>
				<xsl:with-param name="name"><xsl:value-of select="position()"/></xsl:with-param>
			</xsl:call-template>	
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<!--template "checkForCauses" : checks for any Causes when creating a ModeSet-->
<xsl:template name="checkForCauses">
	<!--Hold on to this variable to compare guid's-->			
	<xsl:variable name="itemguid">
		<xsl:value-of select="@xlink:label" xmlns:xlink="http://www.w3.org/1999/xlink"/>
	</xsl:variable>	
	<!--For each node of type link-->
	<xsl:for-each select="//*[name()='link']">
		<!--If the name under the parent node == the itemguid -->
		<xsl:if test="child::node()[name()='child']/attribute::node=$itemguid">
			<xsl:call-template name="createSignal">
				<xsl:with-param name="guid">Signal-<xsl:value-of select="@difference-id" /></xsl:with-param>
				<xsl:with-param name="name"><xsl:value-of select="position()"/></xsl:with-param>
			</xsl:call-template>	
		</xsl:if>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>