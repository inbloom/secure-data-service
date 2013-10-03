
// Init the edorg tree
$(document).ready(function() {
    // alert("edorg_tree");
    $('#edorgTree div').tree({
        dnd: false,
        // we don't want the usual special behavior in regards to hierarchy -- nodes should be selectable independently
        // of their children
        onCheck: {
          ancestors: null, 
          descendants: null
        },
        onUncheck: {
          ancestors: null,
          descendants: null
        }
    });
    $('.jquery').each(function() {
        eval($(this).html());
    });
    $('.button').button();
});

