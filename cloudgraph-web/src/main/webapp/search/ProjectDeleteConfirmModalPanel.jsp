<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core"   prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"   prefix="h" %>

<rich:modalPanel id="deleteProjectConfirmModalPanel"
    autosized="true" resizeable="false">
    <f:facet name="header">
        <h:panelGroup>
            <h:outputText value="Delete Project Confirm?"></h:outputText>
        </h:panelGroup>
    </f:facet>
    <f:facet name="controls">
        <h:panelGroup>
            <h:graphicImage value="/images/modal_close.gif"
                styleClass="hidelink" id="del_conf_close_link" />
            <rich:componentControl for="deleteProjectConfirmModalPanel" attachTo="del_conf_close_link"
                operation="hide" event="onclick" />
        </h:panelGroup>
    </f:facet>
    <h:form  id="delete_confirm_panel_form">
        <h:panelGrid columns="1" width="100%" border="0"
            cellpadding="2" cellspacing="2">  
            <h:outputText style="width: 220px; word-wrap: yes; FONT-WEIGHT: bold;" 
                    value="The following project including business cases, costs, deliverables, milestones etc... will be deleted. Note: this operation cannot be reversed. Do you want to continue?" />
            <h:panelGrid rowClasses="FormPanelRow"
                rendered="#{ProjectEditBean.hasProject}" 
                columnClasses="FormLabelColumn,FormControlColumn,FormLabelColumn,FormControlColumn" 
                columns="4" width="95%" border="0">  
                <h:outputText styleClass="labelBold" 
                    value="#{bundle.aplsProjectEdit_name_label}:"/>
                <h:outputText
                    value="#{ProjectEditBean.project.segment.name}"/>
                <f:verbatim>&nbsp</f:verbatim>    
                <f:verbatim>&nbsp</f:verbatim>    
                
                
            </h:panelGrid>
                  
            <h:panelGrid columns="2" width="50%" border="0"
                cellpadding="2" cellspacing="2"> 
                <a4j:commandButton 
                    action="#{ProjectEditBean.delete}"
                    reRender="rqstd_prj_dtbl,aprvd_prj_dtbl"
                    onclick="Richfaces.hideModalPanel('deleteProjectConfirmModalPanel');" value="  Delete  ">
                </a4j:commandButton>
                <a4j:commandButton 
                    action="#{ProjectEditBean.cancelDelete}"
                    onclick="Richfaces.hideModalPanel('deleteProjectConfirmModalPanel');" value="  Cancel  ">
                </a4j:commandButton>
            </h:panelGrid>
        </h:panelGrid>
    </h:form>
</rich:modalPanel>
