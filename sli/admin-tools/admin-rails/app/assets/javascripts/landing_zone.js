$("#lz_provision > input[type=radio]").each(function(i){
    $(this).click(function () {
        if(i=='custom') {
                $("#custom_ed_org").attr("disabled", "disabled");
        }
        else {
                $("#textbox1").removeAttr("disabled");
        }
      });

  });