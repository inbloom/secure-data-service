/*
 * Function: fnAddTr
 * Purpose:  Add a TR element to a table
 * Returns:  -
 * Inputs:   object:oSettings - automatically passed by DataTables
 *           node:nTr - TR element to add to table
 *           bool:bRedraw - optional - should the table redraw - default true.
 * Usage:    var row = '<tr class="gradeX"><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td></tr>';
                     oTable.fnAddTr( $(row)[0] );
 */
$.fn.dataTableExt.oApi.fnAddTr = function ( oSettings, nTr, bRedraw ) {
    if ( typeof bRedraw == 'undefined' )
    {
        bRedraw = true;
    }
     
    var nTds = nTr.getElementsByTagName('td');
    if ( nTds.length != oSettings.aoColumns.length )
    {
        alert( 'Warning: not adding new TR - columns and TD elements must match' );
        return;
    }
     
    var aData = [];
    for ( var i=0 ; i<nTds.length ; i++ )
    {
        aData.push( nTds[i].innerHTML );
    }
     
    /* Add the data and then replace DataTable's generated TR with ours */
    var iIndex = this.oApi._fnAddData( oSettings, aData );
    nTr._DT_RowIndex = iIndex;
    oSettings.aoData[ iIndex ].nTr = nTr;
     
    oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
     
    if ( bRedraw )
    {
        this.oApi._fnReDraw( oSettings );
    }
}