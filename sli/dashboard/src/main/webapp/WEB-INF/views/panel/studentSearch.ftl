<div id="banner">

<form id="dbrd_frm_search" class="student-search-form" float="right">

<input id='dbrd_inp_search_firstName' class="input-small" type="text" placeholder="First Name">
<input id='dbrd_inp_search_lastName' class="input-small" type="text" placeholder="Last Name">
<button id="dbrd_btn_name_search" class="btn" type="submit">
<input type="image" src="${CONTEXT_ROOT_PATH}/static/images/search_icon.png" height = "13px" alt="Search" />
</button>
<script>
$('#dbrd_frm_search').submit(function(e) {
  e.preventDefault()
  var firstName = $('#dbrd_inp_search_firstName').val();
  if (!firstName) {
    firstName = '';
  }
  var lastName = $('#dbrd_inp_search_lastName').val();
  if (!lastName) {
    lastName = '';
  }
  SLC.util.goToUrl('studentSearchPage', 'firstName=' + firstName + '&lastName=' + lastName);
});
</script>
</form>

   <div class="banner-label">
     DASHBOARD
   </div>

</div>