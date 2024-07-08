var userId = sessionStorage.getItem("id");;
	
window.addEventListener("load", () => {
	if (userId == null) {
		window.location.href = "index.html";
	} else {
		fetchFolders(); 
		document.getElementById("welcometext").innerHTML = "It's a pleasure seeing you again " + sessionStorage.getItem("name") + " " + sessionStorage.getItem("surname");
	}
}, false);

// Funzione per fare la chiamata AJAX
function fetchFolders() {
    fetch('GetFolders?userId='+userId) // Sostituisci con il tuo endpoint
    .then(response => response.json())
    .then(data => {
        const folderContainer = document.getElementById('folderlist');
        if(data && data.length > 0) {
			const folderList = createFolderList(data);
        	folderContainer.appendChild(folderList);
        	document.getElementById('emptytext').style.visibility = "hidden";
		}
    })
    .catch(error => console.error('Errore durante il fetch:', error));
}

// Funzione ricorsiva per creare la lista delle cartelle
function createFolderList(folders) {
    const ul = document.createElement('ul');
    folders.forEach(folder => {
        const li = document.createElement('li');
        li.textContent = folder.folderName; // Aggiungi il nome della cartella
        
        const addButton = document.createElement('button');
            addButton.innerHTML = '<i class="fas fa-folder-plus"></i>'; // Utilizza l'icona di Font Awesome
            addButton.classList.add("transparentbutton");
            addButton.addEventListener('click', function() {
                showAddSubfolderInput(li, folder.id); // Passa l'ID della cartella
        });
        li.appendChild(addButton);
           
        if (folder.children && folder.children.length > 0) {
            const subfolderList = createFolderList(folder.children);
            li.appendChild(subfolderList);
        }
        ul.appendChild(li);
    });
    return ul;
}

// Funzione per mostrare il campo di input per l'aggiunta di una sottocartella
function showAddSubfolderInput(parentElement, parentId) {
    // Rimuovi qualsiasi input esistente
    const existingInputContainer = folderlist.querySelector('.inputcontainer');
    if (existingInputContainer) {
        existingInputContainer.remove();
    }

    // Crea un contenitore per l'input e il pulsante di invio
    const inputContainer = document.createElement('div');
    inputContainer.classList.add('inputcontainer');

    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'subfolder\'s name';
    input.classList.add("addsubfoldertext");

    const submitButton = document.createElement('button');
    submitButton.textContent = 'add';
    submitButton.classList.add("addsubfolderbutton");
    submitButton.addEventListener('click', function() {
        const subfolderName = input.value;
        if (subfolderName) {
            //addSubfolder(parentId, subfolderName, parentElement);
            //inputContainer.remove();
        }
    });

    inputContainer.appendChild(input);
    inputContainer.appendChild(submitButton);
    parentElement.appendChild(inputContainer);
}

// Funzione per fare la chiamata AJAX per aggiungere una sottocartella
function addSubfolder(parentId, subfolderName, parentElement) {
    const data = {
        userId: userId,
        parentId: parentId,
        subfolderName: subfolderName
    };

    fetch('AddSubfolder', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(newSubfolder => {
        const newSubfolderElement = document.createElement('li');
        newSubfolderElement.textContent = newSubfolder.name;

        const addButton = document.createElement('button');
        addButton.innerHTML = '<i class="fas fa-folder-plus"> </i>';
        addButton.classList.add("transparentbutton");
        addButton.addEventListener('click', function() {
            showAddSubfolderInput(newSubfolderElement, newSubfolder.id);
        });

        newSubfolderElement.appendChild(addButton);
        const ul = parentElement.querySelector('ul') || document.createElement('ul');
        ul.appendChild(newSubfolderElement);
        parentElement.appendChild(ul);
    })
    .catch(error => console.error('Errore durante l\'aggiunta della sottocartella:', error));
}