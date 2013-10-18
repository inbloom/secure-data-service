var oldSelectedIds = [];

function getSelectedEdOrgs() {
    var selectedIds = [];
    $("div#edorgTree input.edorgId:checked").each ( function () {
            var selectedId = $(this).attr('id');
            if( selectedId != undefined) {
                selectedIds.push(selectedId);
            }
        }
    );
    return selectedIds;
}

// Init the edorg tree
$(document).ready(function() {
    oldSelectedIds = getSelectedEdOrgs();
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

    $('.daredevel-tree input[type=checkbox]').click(function () {

        var hierarchicalMode = false;
        if ($('#hierarchical_mode').is(':checked')) {
            hierarchicalMode = true;
        }

        if (hierarchicalMode) {
            var $this = $(this);
            var $parent = $(this).parent();
            if ($this.is(':checked')) {
                console.log("was checked");
                console.log($parent);
                $parent.find('input[type=checkbox]').prop('checked', true);
            } else {
                console.log('was unchecked');
                console.log($parent);
                $parent.find('input[type=checkbox]').prop('checked', false);
            }
        }
    });

    $('#expand_all').click(function(event) {
        event.preventDefault();
        $("#edorgTree div").tree('expandAll');

    });

    $('#collapse_all').click(function(event) {
        event.preventDefault();
        $('#edorgTree div').tree('collapseAll')
    });

    $('.jquery').each(function() {
        eval($(this).html());
    });
    $('.button').button();

    $("div#edorgTree input[type=submit]").bind ('click', function () {
            var newSelectedIds = getSelectedEdOrgs();
            var removed = $(oldSelectedIds).not(newSelectedIds).get();
            var added   = $(newSelectedIds).not(oldSelectedIds).get();
            removed     = removed.join(',');
            added       = added.join(',');
            var log     = ['Added Authorization For [', added , ']. ', 'Removed Authorization For [', removed, '].'].join('');
            console.log(log);
            $( "input#application_authorization_edorgsAdded" ).val( added );
            $( "input#application_authorization_edorgsRemoved" ).val( removed );
        }
    );
});

