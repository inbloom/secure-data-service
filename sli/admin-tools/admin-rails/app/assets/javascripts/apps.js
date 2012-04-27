
function changeCheck(checkboxName, checkVal) {
    checkboxes = document.getElementsByName(checkboxName);
    for(var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = checkVal;
    }
}