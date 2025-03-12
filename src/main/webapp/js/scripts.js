// SPDX-FileCopyrightText: 2014 María Poveda Villalon <mpovedavillalon@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

//example uri

function example1(uri){
	document.getElementById("uriEx").value=uri;
	}

function checkAll(field){
	for (i = 0; i < field.length; i++){
		field[i].checked = true ;
	}
}

function uncheckAll(field){
	for (i = 0; i < field.length; i++){
		field[i].checked = false ;
	}
}