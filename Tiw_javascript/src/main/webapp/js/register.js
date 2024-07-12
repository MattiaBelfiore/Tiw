document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    var password = form.querySelector("#password").value;
    var confirmPassword = form.querySelector("#rpt_password").value;
    var errorMsg = form.querySelector("#errorMsg");
    // Clear previous error messages
    errorMsg.textContent = "";
    
    // Check if passwords match
    if (password !== confirmPassword) {
        errorMsg.textContent = "Passwords do not match.";
        return;
    }

    // Check if all fields are filled
    var inputs = form.querySelectorAll("input[required]");
    for (var input of inputs) {
        if (!input.value.trim()) {
            errorMsg.textContent = "All fields are required.";
            return;
        }
    }
    // If all validations pass, make the POST request
    var url = "RegistrationAgent";
    makePost(url, form);
});

function makePost(url, formElement) {
    var request = new XMLHttpRequest();
    var formData = new FormData(formElement);
    request.onreadystatechange = function() {
        if (request.readyState == 4) {
            showRegistrationResults(request);  // Passa 'request' come parametro
        }
    };
    request.open("POST", url);
    request.send(formData);
}

function showRegistrationResults(request) {
    if (request.readyState == 4) {
        switch (request.status) {
            case 200:
				//alert("user registered correctly!");
                window.location.href = "index.html";
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