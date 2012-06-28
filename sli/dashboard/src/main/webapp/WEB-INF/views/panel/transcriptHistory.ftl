<@includePanelModel panelId="transcriptHistory"/>
<#assign id = getDivId(panelConfig.id)>
</br>
<div class="ui-widget-no-border">
    <table id="${id}"></table>
</div>

<a href="#" class="transcript_expandAll">Expand All</a>

<script type="text/javascript">SLC.util.setTableId('${id}');</script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/panels/SLC.transcriptHistory.js"></script>
