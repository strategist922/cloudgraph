<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core"   prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"   prefix="h" %>

<rich:modalPanel id="projectLockConfirmModalPanel"
    autosized="true" resizeable="false">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Lock Projects Confirm?"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="/images/modal_close.gif"
                styleClass="hidelink" id="lock_conf_close_link" />
            <rich:componentControl for="projectLockConfirmModalPanel" attachTo="lock_conf_close_link"
                operation="hide" event="onclick" />
        </h:panelGroup>
    </f:facet>
    <h:form  id="project_lock_confirm_panel_form">
        <h:panelGrid columns="1" width="100%" border="0"
            cellpadding="2" cellspacing="2">  
			<a4j:outputPanel ajaxRendered="true" id="proj_lock_errors">
			    <h:messages id="dataEntryError" showDetail="true" showSummary="false" layout="table" errorClass="ErrorMessage" infoClass="ErrorMessage"/>
			</a4j:outputPanel>
            <h:outputText style="word-wrap: yes; FONT-WEIGHT: bold;" 
                value="The following projects will be locked, but can be individually unlocked as needed from the main projects list(s). Note: to change the set of projects to lock, you can adjust the data filter criteria. Do you with to continue?" />
	        <h:panelGrid rowClasses="FormPanelRow" 
                columnClasses="FormLabelColumn,FormControlColumn" 
                columns="4" width="95%" border="0">  
                <h:outputText styleClass="labelBold" 
                    value="Revision Name:" />
                <h:inputText
                    required="true"
                    value="#{RevisionBean.revision.revisionName}"
                    maxlength="#{RevisionBean.revisionNameMaxLength}"/>
                <h:outputText styleClass="labelBold" 
                    value="Revision Description:" />
                <h:inputTextarea
                    cols="20" rows="4"
                    value="#{RevisionBean.revision.description}"
                    validator="#{RevisionBean.validateRevisionDescriptionLength}"/>
            </h:panelGrid>
            
            <rich:panel>
	            <f:verbatim><div class="ProjectListTablePopupDiv"></f:verbatim>
         	    <rich:dataTable  
	                width="100%" cellpadding="0" cellspacing="0" 
	                rowKeyVar="row"
	                var="project" value="#{RevisionBean.projects}">	     	      	     	        
     	            <f:facet name="header">
     	                <h:outputText value="Projects to be Locked" />	
     	            </f:facet> 
				    <rich:column>                                                                                                                               
				      <h:outputText value="#{row+1}"/>                                                                                         
				    </rich:column> 
				    <rich:column sortBy="#{project.budgetYear}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
	                    <h:outputText value="#{bundle.aplsProjects_year_label}" 
	                      title="#{bundle.aplsProjects_year_tooltip}"/>
	                  </f:facet>                 
				      <h:outputText value="#{project.budgetYear}"/>                                                                                         
				    </rich:column> 
				    <rich:column sortBy="#{project.parentOrgName}">  
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_parentOrgName_label}" title="#{bundle.aplsProjects_parentOrgName_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.parentOrgName}"/>                                                                                         
				    </rich:column> 					
				    <rich:column sortBy="#{project.orgName}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_orgName_label}" title="#{bundle.aplsProjects_orgName_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.orgName}"/>                                                                                         
				    </rich:column> 					    
				    <rich:column sortBy="#{project.investmentName}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_investmentName_label}" title="#{bundle.aplsProjects_investmentName_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.investmentName}"/>                                                                                         
				    </rich:column> 		     
				    <rich:column sortBy="#{project.applicationName}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_applicationName_label}" title="#{bundle.aplsProjects_investmentName_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.applicationName}"/>                                                                                         
				    </rich:column>   
				    <rich:column sortBy="#{project.segmentName}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_segmentName_label}" title="#{bundle.aplsProjects_segmentName_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.segmentName}"/>                                                                                         
				    </rich:column> 
				    <rich:column sortBy="#{project.projectName}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_name_label}" title="#{bundle.aplsProjects_name_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.projectName}"/>                                                                                         
				    </rich:column> 
				    <rich:column sortBy="#{project.currentRevision}">                                                                                                                               
				      <f:facet name="header">                                                                                                                             
				        <h:outputText value="#{bundle.aplsProjects_currentRevision_label}" title="#{bundle.aplsProjects_currentRevision_tooltip}"/>                 
                      </f:facet>
                      <h:outputText value="#{project.currentRevision}"/>                                                                                         
				    </rich:column> 		      
			    </rich:dataTable> 
	            <f:verbatim></div></f:verbatim>
	        </rich:panel>
      
            <h:panelGrid columns="2" width="50%" border="0"
                cellpadding="2" cellspacing="2"> 
                <a4j:commandButton 
                    action="#{RevisionBean.lock}"
                    reRender="dashboard_content_panel"
                    oncomplete="javascript:closeProjectLockConfirmModalPanel()"
                    value="  Lock  ">
                </a4j:commandButton>
                <a4j:commandButton 
                    action="#{RevisionBean.lockCancel}"
                    onclick="Richfaces.hideModalPanel('projectLockConfirmModalPanel');" value="  Cancel  ">
                </a4j:commandButton>
            </h:panelGrid>
        </h:panelGrid>
    </h:form>
</rich:modalPanel>
<script type="text/javascript">
    //<![CDATA[
       function closeProjectLockConfirmModalPanel(){
            if (document.getElementById('projectLockConfirmModalPanel:dataEntryError')==null){
                 Richfaces.hideModalPanel('projectLockConfirmModalPanel');
            };
       };
    //\]\]\>
</script>	        
