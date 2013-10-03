
// Init the edorg tree
$(document).ready(function() {
    // alert("edorg_tree");
    $('#edorgTree div').tree({});
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

