
function verify(){
	
	var password = document.forms['registerForm']['password'].value;  
	var verfiyPassword = document.forms['registerForm']['verifyPassword'].value;  
		
	if(password != verfiyPassword || password == null || password == "" ){
		
		document.getElementById("error").innerHTML ="Please check your passwords";
		
		return false; 
	}	
		
	
}