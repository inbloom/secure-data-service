
// Init the edorg tree
$(document).ready(function() {
    // alert("edorg_tree");
    $('#edorgTree div').tree({});
    $('.jquery').each(function() {
        eval($(this).html());
    });
    $('.button').button();
});

