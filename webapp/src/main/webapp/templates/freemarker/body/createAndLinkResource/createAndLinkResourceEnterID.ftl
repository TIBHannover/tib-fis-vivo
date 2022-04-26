<#if !linkAuthors?has_content>
	<#assign linkAuthors=true />
</#if>
<#if !hasBackendEditing?has_content>
	<#assign hasBackendEditing=false />
</#if>
<form id="createAndLink" method="post">
    <#if personLabel??>
        <div class="claim-for">
            <h3><#if hasBackendEditing=true>
                <input type="radio" name="linkAuthors" value="true" <#if linkAuthors!=false>checked</#if> />
            </#if>${i18n().create_and_link_claim_for(personLabel)}</h3>
            <#if personThumbUrl??>
                <img src="${urls.base}${personThumbUrl}" />
            </#if>
            <#if hasBackendEditing=true>
                <hr />
                <input type="radio" name="linkAuthors" value="false" <#if linkAuthors=false>checked</#if>/> ${i18n().create_and_link_no_linking}
            </#if>
        </div>
    <#else>
        <input type="hidden" name="linkAuthors" value="false" />
    </#if>
    <#if showConfirmation??>
        <h2>${i18n().create_and_link_thank_you}</h2>
        ${i18n().create_and_link_finished}<br /><br />
        <#if profileUri??>
            <a href="${profileUrl(profileUri)}">${i18n().create_and_link_go_profile}</a><br /><br />
        </#if>
    </#if>
    <#if label?? && provider??>
        <h2>${i18n().create_and_link_enter(label)}</h2>
        <#switch provider>
            <#case "doi">
                ${i18n().create_and_link_enter_dois_intro}<br />
                <i>ID</i>:  10.1038/nature01234<br />
                <i>URL</i>: https://doi.org/10.1038/nature01234<br />
                <br />
                ${i18n().create_and_link_enter_dois_supported}<br /><br />
                <#break>
            <#case "pmid">
                ${i18n().create_and_link_enter_pmid_intro}<br /><br />
                ${i18n().create_and_link_enter_pmid_supported}<br /><br />
                <#break>
        </#switch>
        <textarea name="externalIds" rows="15" cols="50"></textarea><br />
        <input type="submit" value="${i18n().create_and_link_submit_ids}" class="submit" /><br />
        <input type="hidden" name="action" value="findID" />
        <#if profileUri??>
            <input type="hidden" name="profileUri" value="${profileUri!}" />
        </#if>
    </#if>
</form>
