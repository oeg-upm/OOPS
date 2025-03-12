// SPDX-FileCopyrightText: 2014 María Poveda Villalon <mpovedavillalon@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

$(document).ready(function(){
	
	 $("#pitfall").click(function() {
		    $("#div1").fadeIn(800); /*changed*/
		    $("#div2").fadeOut(500); /*changed*/
		    $("#div3").fadeOut(500); /*changed*/
		    });
		    
	 $("#categoryDim").click(function() {
		    $("#div2").fadeIn(800); /*changed*/
		    $("#div1").fadeOut(500); /*changed*/
		    $("#div3").fadeOut(500); /*changed*/
		});
		    
	 $("#categoryEvCirt").click(function() {
		    $("#div3").fadeIn(800); /*changed*/
		    $("#div1").fadeOut(500); /*changed*/
		    $("#div2").fadeOut(500); /*changed*/
		});
		    
    $("form").submit(function(e) { 

        var val = $("input[type=submit][clicked=true]").val();

        if ((val == "Go to simple evaluation") || (val == "Go to advanced evaluation")){ 
    		return true;
    	}
    	else if ($('input:radio[id=pitfall]', this).is(':checked')){ //Para los pitfalls
    		
    		if ($('input:checkbox[name=pitfalls]:checked').length > 0) {// if annadido Maria
    			doValidation();
    			return true;
    		}
    		if ($('input:checkbox[name=pitfalls]:checked').length <= 0) {
    			e.preventDefault();
    			e.stopPropagation();
    			alert('ERROR: You have selected "Pitfalls Evaluation" but haven\'t checked any of the boxes. You must select at least one pitfall to evaluate.');
    		}
    		
    	}
    	else if ($('input:radio[id=categoryDim]', this).is(':checked')){
    		if ($('input:radio[name=classification]:checked').length > 0) {// if annadido Maria
    			doValidation();
    			return true;
    		}
    		if ($('input:radio[name=classification]:checked').length <= 0) {
    			e.preventDefault();
    			e.stopPropagation();
    			alert('ERROR: You have selected "Category Evaluation" but haven\'t checked any. You must select one of the different classifications to evaluate.');
    		}
    	}else if ($('input:radio[id=categoryEvCrit]', this).is(':checked')){
    		if ($('input:radio[name=classification]:checked').length > 0) {// if annadido Maria
    			doValidation();
    			return true;
    		}
    		if ($('input:radio[name=classification]:checked').length <= 0) {
    			e.preventDefault();
    			e.stopPropagation();
    			alert('ERROR: You have selected "Category Evaluation" but haven\'t checked any. You must select one of the different classifications to evaluate.');
    		}
    	}
    	else {
    		doValidation();// annadido Maria
    		return true;
    	}
    });

    $("form input[type=submit]").click(function() {
        $("input[type=submit]", $(this).parents("form")).removeAttr("clicked");
        $(this).attr("clicked", "true");
    });
	    
		    
});