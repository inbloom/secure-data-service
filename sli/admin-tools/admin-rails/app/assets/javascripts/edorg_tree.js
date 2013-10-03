
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


    $('.jquery').each(function() {
        eval($(this).html());
    });
    $('.button').button();

    $("div#edorgTree input[type=submit]").bind ('click', function () {
            var selectedIds = [];
            $("div#edorgTree input:checked").each ( function () {
                  var selectedId = $(this).attr('id');
                  if( selectedId != undefined && selectedId != 'root') {
                      selectedIds.push(selectedId);
                  }
                }
            );
            var selected = selectedIds.join(",");
            $( "input#application_authorization_edorgs" ).val( selected );
        }
    );
});

