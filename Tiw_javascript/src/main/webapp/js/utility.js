function makeGetCall(url, callbackFunction) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => callbackFunction(req);
	req.open("GET", url);
  	req.send();
}

function makePostCall(url, formElement, callbackFunction) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => callbackFunction(req);
	req.open("POST", url);
  	req.send(formElement);
}

function makePutCall(url, formElement, callbackFunction) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => callbackFunction(req);
	req.open("PUT", url);
  	req.send(formElement);
}

function makeDeleteCall(url, callbackFunction) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => callbackFunction(req);
	req.open("DELETE", url);
  	req.send();
}