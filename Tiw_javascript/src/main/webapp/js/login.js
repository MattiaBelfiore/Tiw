document.getElementById("loginbutton").addEventListener('click', (e) => {
	var url = "CheckLogin";
	makePost(url, e.target.closest("form"));
});

function makePost(url, formElement) {
	request = new XMLHttpRequest();
	var formData = new FormData(formElement);
	request.onreadystatechange = showResults;
	request.open("POST", url);
	request.send(formData);
}

function showResults() {
	if (request.readyState == 4) {
		switch (request.status) {
			case 200:
				var message = JSON.parse(request.responseText);
				sessionStorage.setItem('id', message.id);
				sessionStorage.setItem('username', message.username);
				sessionStorage.setItem('name', message.name);
				sessionStorage.setItem('surname', message.surname);
				sessionStorage.setItem('email', message.email);

				window.location.href = "Home.html";
				break;

			case 400: // bad request
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
				break;

			case 401: // unauthorized
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
				break;
				
			case 500: // server error
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
				break;
		}
	}
}

