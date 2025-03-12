// SPDX-FileCopyrightText: 2014 María Poveda Villalon <mpovedavillalon@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

function showPit(){
	document.getElementById('div1').style.display = 'block';
	document.getElementById('div2').style.display = 'none';
	document.getElementById('div3').style.display = 'none';
}
function showDim(){
	document.getElementById('div2').style.display = 'block';
	document.getElementById('div1').style.display = 'none';
	document.getElementById('div3').style.display = 'none';
}
function showEC(){
	document.getElementById('div3').style.display = 'block';
	document.getElementById('div1').style.display = 'none';
	document.getElementById('div2').style.display = 'none';
}