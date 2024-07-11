window.addEventListener("load", () => {
	if (sessionStorage.getItem("id") == null) {
		window.location.href = "index.html";
	} else {
		fetchFolders(); 
		document.getElementById("welcometext").innerHTML = "It's a pleasure seeing you again " + sessionStorage.getItem("name") + " " + sessionStorage.getItem("surname");
	}
}, false);

// Funzione per fare la chiamata AJAX
function fetchFolders() {
    fetch('GetFolders?userId='+sessionStorage.getItem("id")) 
    .then(response => response.json())
    .then(data => {
        const folderContainer = document.getElementById('folderlist');
        if(data && data.length > 0) {
			const folderList = createFolderList(data);
        	folderContainer.appendChild(folderList);
        	document.getElementById('emptytext').style.visibility = "hidden";
        	enableDragAndDrop();
		}
		fetchDocs();
    })
    .catch(error => console.error('Errore durante il fetch:', error));
}

// Funzione per fare la chiamata AJAX
function fetchDocs() {
    fetch('GetDocs?userId='+sessionStorage.getItem("id")) 
    .then(response => response.json())
    .then(data => {
        if(data && data.length > 0) {
        	data.forEach(doc => {
            	// Trova la cartella a cui appartiene il documento
	            const folderElement = document.querySelector(`[folder-id="${doc.folderId}"]`);
	            if (folderElement) {
	                // Trova l'ul figlio della cartella
	                const ul = folderElement.querySelector('ul');
	                if (ul) {
	                    // Crea un nuovo li per il documento
	                    const li = document.createElement('li');
	                    li.textContent = doc.name;
	                    li.classList.add("document");
	                    li.setAttribute('draggable', 'true');
	                    li.setAttribute('doc-id', doc.documentId); 
	                    li.setAttribute('doc-name', doc.name); 
	                    li.addEventListener('dragstart', handleDragStart);
	                    ul.appendChild(li);
	                }
	            }
        	});
		}
    })
    .catch(error => console.error('Errore durante il fetch:', error));
}

// Funzione ricorsiva per creare la lista delle cartelle
function createFolderList(folders) {
    const ul = document.createElement('ul');
    folders.forEach(folder => {
        const li = document.createElement('li');
        li.textContent = folder.folderName;
        li.setAttribute("folder-id", folder.folderId);
        li.classList.add("folder");
        
        const addSubFolderButton = document.createElement('button');
        addSubFolderButton.innerHTML = '<i class="fas fa-folder-plus icon"></i>';
        addSubFolderButton.classList.add("transparentbutton");
        addSubFolderButton.addEventListener('click', function() {
            showAddSubfolderInput(li, folder.folderId); 
        });
        li.appendChild(addSubFolderButton);
        
        const addDocumentButton = document.createElement('button');
        addDocumentButton.innerHTML = '<i class="fa-solid fa-file-circle-plus icon"> </i>';
        addDocumentButton.classList.add("transparentbutton");
        addDocumentButton.addEventListener('click', function() {
            showAddDocumentInput(li, folder.folderId);
        });
        li.appendChild(addDocumentButton);
           
        const subfolderList = createFolderList(folder.children);
        li.appendChild(subfolderList);
        
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
            addSubfolder(parentId, subfolderName, parentElement);
            inputContainer.remove();
        }
    });

    inputContainer.appendChild(input);
    inputContainer.appendChild(submitButton);
    parentElement.appendChild(inputContainer);
}

// Funzione per mostrare il campo di input per l'aggiunta di una sottocartella
function showAddDocumentInput(parentElement, parentId) {
    // Rimuovi qualsiasi input esistente
    const existingInputContainer = folderlist.querySelector('.inputcontainer');
    if (existingInputContainer) {
        existingInputContainer.remove();
    }

    // Crea un contenitore per l'input e il pulsante di invio
    const inputContainer = document.createElement('div');
    inputContainer.classList.add('inputcontainer');

    const inputName = document.createElement('input');
    inputName.type = 'text';
    inputName.placeholder = 'document\'s name';
    inputName.classList.add("adddocumenttext");
    
    const inputSummary = document.createElement('input');
    inputSummary.type = 'text';
    inputSummary.placeholder = 'document\'s summary';
    inputSummary.classList.add("adddocumenttext");
    
    const inputType = document.createElement('input');
    inputType.type = 'text';
    inputType.placeholder = 'document\'s type';
    inputType.classList.add("adddocumenttext");

    const submitButton = document.createElement('button');
    submitButton.textContent = 'add';
    submitButton.classList.add("adddocumentbutton");
    submitButton.addEventListener('click', function() {
        const documentName = inputName.value;
        const documentSummary = inputSummary.value;
        const documentType = inputType.value;
        if (documentName && documentSummary && documentType) {
            addDocument(parentId, documentName, documentSummary, documentType, parentElement);
            inputContainer.remove();
        }
    });

    inputContainer.appendChild(inputName);
    inputContainer.appendChild(inputSummary);
    inputContainer.appendChild(inputType);
    inputContainer.appendChild(submitButton);
    parentElement.appendChild(inputContainer);
}

// Funzione per fare la chiamata AJAX per aggiungere una sottocartella
function addSubfolder(parentId, subfolderName, parentElement) {
	var userId = parseInt(sessionStorage.getItem("id"));
    
    const data = new FormData();
    data.append('userId', userId);
    data.append('parentId', parentId);
    data.append('folderName', subfolderName);

    fetch('CreateFolder', {
        method: 'POST',
        body: data
    })
    .then(response => response.json())
    .then(data => {
		if(data) {
	        const newSubfolderElement = document.createElement('li');
	        newSubfolderElement.textContent = data.folderName;
	        newSubfolderElement.setAttribute("folder-id", data.folderId);
	        newSubfolderElement.classList.add("folder");
	
	        const addSubfolderButton = document.createElement('button');
	        addSubfolderButton.innerHTML = '<i class="fas fa-folder-plus icon"> </i>';
	        addSubfolderButton.classList.add("transparentbutton");
	        addSubfolderButton.addEventListener('click', function() {
	            showAddSubfolderInput(newSubfolderElement, data.folderId);
	        });
	        newSubfolderElement.appendChild(addSubfolderButton);
	        
	        const addDocumentButton = document.createElement('button');
	        addDocumentButton.innerHTML = '<i class="fa-solid fa-file-circle-plus icon"> </i>';
	        addDocumentButton.classList.add("transparentbutton");
	        addDocumentButton.addEventListener('click', function() {
	            showAddDocumentInput(newSubfolderElement, data.folderId);
	        });
	        newSubfolderElement.appendChild(addDocumentButton);
	        
	        const ul = parentElement.querySelector('ul') || document.createElement('ul');
	        ul.appendChild(newSubfolderElement);
	        parentElement.appendChild(ul);
        } else {
			//error
		}
    })
    .catch(error => console.error('Errore durante l\'aggiunta della sottocartella:', error));
}

// Funzione per fare la chiamata AJAX per aggiungere una sottocartella
function addDocument(parentId, docName, docSummary, docType, parentElement) {
	var userId = parseInt(sessionStorage.getItem("id"));
    
    const data = new FormData();
    data.append('userId', userId);
    data.append('parentId', parentId);
    data.append('docName', docName);
    data.append('docSummary', docSummary);
    data.append('docType', docType);
    
    console.log("Name: " + docName);
    console.log("Summary: " + docSummary);
    console.log("docType: " + docType);

    fetch('CreateDocument', {
        method: 'POST',
        body: data
    })
    .then(response => {
        return !response.ok ? response.json().then(errorData => {
            throw new Error(errorData.error);
        }) : response.json();
	})
    .then(data => {
		if(data) {
			document.getElementById("errorMsg").innerHTML = "";
			
	        const li = document.createElement('li');
	        li.textContent = data.name;
	        li.classList.add("document");
	        li.setAttribute('draggable', 'true');
	        li.setAttribute('doc-id', data.documentId); 
	        li.setAttribute('doc-name', data.name); 
	        li.addEventListener('dragstart', handleDragStart);
	        
	        const ul = parentElement.querySelector('ul') || document.createElement('ul');
	        ul.appendChild(li);
	        parentElement.appendChild(ul);
        } else {
			//error
		}
    })
    .catch(error => {
		document.getElementById("errorMsg").innerHTML = error;
	});
}

function enableDragAndDrop() {
    const folders = document.querySelectorAll('.folder');
    folders.forEach(folder => {
        folder.addEventListener('dragover', handleDragOver);
        folder.addEventListener('drop', handleDrop);
    });
}

function handleDragStart(event) {
    event.dataTransfer.setData('text/plain', event.target.getAttribute('doc-id'));
}

function handleDragOver(event) {
    event.preventDefault();
}

function handleDrop(event) {
    event.preventDefault();
    event.stopPropagation();
    const docId = event.dataTransfer.getData('text/plain');
    const targetFolder = event.target;
    if (targetFolder && targetFolder.getAttribute("folder-id") != null && docId) {
        const documentElement = document.querySelector(`[doc-id="${docId}"]`);
        if (documentElement) {
            const userId = parseInt(sessionStorage.getItem("id"));
            const docName = documentElement.getAttribute("doc-name");
		    const data = new FormData();
		    data.append('userId', userId);
		    data.append('docId', docId);
		    data.append('docName', docName);
		    data.append('newFolderId', targetFolder.getAttribute("folder-id"));
		
		    fetch('MoveDocument', {
		        method: 'POST',
		        body: data
		    })
		    .then(response => {
		        if (response.ok && response.status == 200) {
		            const ul = targetFolder.querySelector('ul') || document.createElement('ul');
		            ul.appendChild(documentElement);
		            targetFolder.appendChild(ul);
		            
		        } else if (response.status = 400) {
		            var message = response.json().errorMsg;
					document.getElementById("errorMsg").innerHTML = message;
		        }
		    })
		    .catch(error => {
		        document.getElementById("errorMsg").innerHTML = 'Si è verificato un errore durante lo spostamento del documento.';
		    });
        }
    }
}