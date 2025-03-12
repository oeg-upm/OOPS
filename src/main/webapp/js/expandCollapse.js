// SPDX-FileCopyrightText: 2014 María Poveda Villalon <mpovedavillalon@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

$(document).ready(function() {
    $(".accordion h3.active").toggler({initShow: "p.collapse:eq(0)"});
    
    $(".accordion").expandAll({
      trigger: "h3.active", 
      ref: "h3.active", 
      showMethod: "slideDown", 
      hideMethod: "slideUp", 
      oneSwitch : false
    });
});
