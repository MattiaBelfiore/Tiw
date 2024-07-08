$(document).ready(function(){
    $('#loginbutton').on('click', function(event) {
        event.preventDefault(); // Evita il submit del form di default

        // Ricava i valori delle caselle di input
        var username = $('#username').val();
        var password = $('#password').val();

        // Prepara i dati da inviare
        var data = {
            username: username,
            password: password
        };

        // Esegui la chiamata AJAX
        $.ajax({
            url: 'CheckLogin', // URL dell'endpoint
            type: 'POST', // Metodo HTTP
            contentType: 'application/json', // Tipo di contenuto
            data: JSON.stringify(data), // Dati da inviare, convertiti in JSON
            success: function(response) {
                // Gestisci la risposta in caso di successo
                console.log('Login avvenuto con successo:', response);
                // Puoi fare altre operazioni qui, come reindirizzare l'utente
            },
            error: function(xhr, status, error) {
                // Gestisci gli errori
                console.error('Errore durante il login:', error);
                // Puoi mostrare un messaggio di errore all'utente
            }
        });
    });
});
