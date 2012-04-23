
function checkAll(text) {
    checkboxes = document.getElementsByName('authorized_ed_orgs[]');
    $(checkboxes).each(
        function() {
            this.checked = 1
        }
    )
}

function uncheckAll(text) {
    checkboxes = document.getElementsByName('authorized_ed_orgs[]');
    $(checkboxes).each(
        function() {
            this.checked = 0
        }
    )
}
