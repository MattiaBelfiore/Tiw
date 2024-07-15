window.addEventListener("load", () => {
    if (sessionStorage.getItem("id") == null) {
        window.location.href = "index.html";
    } else {
        fetchFolders(); 
        initTrashCan();
        document.getElementById("welcometext").innerHTML = "It's a pleasure seeing you again " + sessionStorage.getItem("name") + " " + sessionStorage.getItem("surname");
        
        const folderListTitle = document.getElementById("folderlisttitle");
        const folderList = document.getElementById("folderlist");
        const addRootFolderButton = document.createElement('button');
        addRootFolderButton.innerHTML = '<i class="fas fa-folder-plus icon"></i>';
        addRootFolderButton.classList.add("transparentbutton");
        addRootFolderButton.addEventListener('click', function() {
            showAddSubfolderInput(folderList, ""); 
        });
        folderListTitle.appendChild(addRootFolderButton);
    }
}, false);

// Funzione per fare la chiamata AJAX
function fetchFolders() {
	makeGetCall('GetFolders?userId='+sessionStorage.getItem("id"), showFolders);
}

function showFolders(request) {
	if (request.readyState == 4) {
		if(request.status == 200) {
			var data = JSON.parse(request.responseText);
			const folderContainer = document.getElementById('folderlist');
	        if(data && data.length > 0) {
	            const folderList = createFolderList(data);
	            folderContainer.appendChild(folderList);
	            document.getElementById('emptytext').style.visibility = "hidden";
	            document.getElementById('emptytext').classList.remove("emptytext");
	            document.getElementById('trash').style.visibility = "visible";
	            enableDragAndDrop();
	        }
	        fetchDocs();
		} else {
			var message = request.responseText;
			document.getElementById("errorMsg").textContent = message;
		}
    }
}

function fetchDocs() {
	makeGetCall('GetDocs?userId='+sessionStorage.getItem("id"), showDocs);
}

function showDocs(request) {
	if (request.readyState == 4) {
		if(request.status == 200) {
			var data = JSON.parse(request.responseText);
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
	                        li.addEventListener('dragstart', handleDragStartDoc);
	                        li.addEventListener('click', () => showDocInfo(doc.documentId, doc.name, doc.summary, doc.type, li));
	                        ul.appendChild(li);
	                    }
	                }
	            });
	        }
		} else {
			var message = request.responseText;
			document.getElementById("errorMsg").textContent = message;
		}
    }
}

function showDocInfo(docId, docName, docSummary, docType, parentElement) {
	/*makeGetCall('GetDoc?docId='+docId, (request) => {
		if (request.readyState == 4) {
			if(request.status == 200) {
				var data = JSON.parse(request.responseText);
		        if(data) {
					document.getElementById("errorMsg").innerHTML = "";
				    
				    const existingDocInfoContainer = folderlist.querySelector('.docinfocontainer');
				    if (existingDocInfoContainer) {
				        existingDocInfoContainer.remove();
				    }
				
				    // Crea un contenitore per l'input e il pulsante di invio
				    const inputContainer = document.createElement('div');
				    inputContainer.classList.add('docinfocontainer');
				
				    const documentName = document.createElement('p');
				    documentName.classList.add('docinfo');
				    documentName.innerHTML = 'Name: ' + data.name;
				    
				    const documentSummary = document.createElement('p');
				    documentSummary.classList.add('docinfo');
				    documentSummary.innerHTML = 'Summary: ' + data.summary;
				    
				    const documentType = document.createElement('p');
				    documentType.classList.add('docinfo');
				    documentType.innerHTML = 'Type: ' + data.type;
				    
				    inputContainer.appendChild(documentName);
				    inputContainer.appendChild(documentSummary);
				    inputContainer.appendChild(documentType);
				    parentElement.appendChild(inputContainer);
		        }
			} else {
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
			}
		}
	});*/
	
	document.getElementById("errorMsg").innerHTML = "";
				    
    const existingDocInfoContainer = folderlist.querySelector('.docinfocontainer');
    if (existingDocInfoContainer) {
        existingDocInfoContainer.remove();
    }

    // Crea un contenitore per l'input e il pulsante di invio
    const inputContainer = document.createElement('div');
    inputContainer.classList.add('docinfocontainer');

    const documentName = document.createElement('p');
    documentName.classList.add('docinfo');
    documentName.innerHTML = 'Name: ' + docName;
    
    const documentSummary = document.createElement('p');
    documentSummary.classList.add('docinfo');
    documentSummary.innerHTML = 'Summary: ' + docSummary;
    
    const documentType = document.createElement('p');
    documentType.classList.add('docinfo');
    documentType.innerHTML = 'Type: ' + docType;
    
    inputContainer.appendChild(documentName);
    inputContainer.appendChild(documentSummary);
    inputContainer.appendChild(documentType);
    parentElement.appendChild(inputContainer);
}

// Funzione ricorsiva per creare la lista delle cartelle
function createFolderList(folders) {
    const ul = document.createElement('ul');
    folders.forEach(folder => {
        const li = document.createElement('li');
        const span = document.createElement('span');
        li.classList.add("folder");
        li.setAttribute("folder-id", folder.folderId);
        
        span.textContent = folder.folderName;
        span.setAttribute('draggable', 'true');
        span.addEventListener('dragstart', (event) => handleDragStartFolder(event));
        li.appendChild(span);
        
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

// Funzione per mostrare il campo di input per l'aggiunta di un documento
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
    
    makePostCall('CreateFolder', data, (request) => {
		if (request.readyState == 4) {
			if(request.status == 200) {
				var data = JSON.parse(request.responseText);
		        if(data) {
		            document.getElementById('emptytext').style.visibility = "hidden";
					document.getElementById('emptytext').classList.remove("emptytext");
					document.getElementById('trash').style.visibility = "visible";
					document.getElementById("errorMsg").innerHTML = "";
					
		            const newSubfolderElement = document.createElement('li');
		            const spanElement = document.createElement('span');
		            newSubfolderElement.classList.add("folder");
		            newSubfolderElement.setAttribute("folder-id", data.folderId);
		            
		            spanElement.textContent = data.folderName;
		            spanElement.setAttribute('draggable', 'true');
		        	spanElement.addEventListener('dragstart', (event) => handleDragStartFolder(event, data.folderId));
		        	newSubfolderElement.appendChild(spanElement);
		
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
		        }
			} else {
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
			}
		}
	});
}

// Funzione per fare la chiamata AJAX per aggiungere un documento
function addDocument(parentId, docName, docSummary, docType, parentElement) {
    var userId = parseInt(sessionStorage.getItem("id"));
    
    const data = new FormData();
    data.append('userId', userId);
    data.append('parentId', parentId);
    data.append('docName', docName);
    data.append('docSummary', docSummary);
    data.append('docType', docType);
    
    makePostCall('CreateDocument', data, (request) => {
		if (request.readyState == 4) {
			if(request.status == 200) {
				var data = JSON.parse(request.responseText);
		        if(data) {
		            document.getElementById("errorMsg").innerHTML = "";
		            const li = document.createElement('li');
		            li.textContent = data.name;
		            li.classList.add("document");
		            li.setAttribute('draggable', 'true');
		            li.setAttribute('doc-id', data.documentId); 
		            li.setAttribute('doc-name', data.name); 
		            li.addEventListener('dragstart', handleDragStartDoc);
		            li.addEventListener('click', () => showDocInfo(data.documentId, data.name, data.summary, data.type, li));
		            
		            const ul = parentElement.querySelector('ul') || document.createElement('ul');
		            ul.appendChild(li);
		            parentElement.appendChild(ul);
		        }
			} else {
				var message = request.responseText;
				document.getElementById("errorMsg").textContent = message;
			}
		}
	});
}

function enableDragAndDrop() {
    const folders = document.querySelectorAll('.folder');
    folders.forEach(folder => {
        folder.addEventListener('dragover', handleDragOver);
        folder.addEventListener('drop', handleDrop);
    });
}

function handleDragStartDoc(event) {
    event.dataTransfer.setData('text/plain', "d-" + event.target.getAttribute('doc-id'));
}

function handleDragStartFolder(event) {
    event.dataTransfer.setData('text/plain', "f-" + event.target.closest('li').getAttribute('folder-id'));
}

function handleDragOver(event) {
    event.preventDefault();
}

function handleDrop(event) {
    event.stopPropagation();
    const text = event.dataTransfer.getData('text/plain');
    const targetFolder = event.target.closest(".folder");
    if (targetFolder && targetFolder.getAttribute("folder-id") != null && text.startsWith("d-")) {
		const docId = text.substring(2);
        const documentElement = document.querySelector(`[doc-id="${docId}"]`);
        if (documentElement) {
            const userId = parseInt(sessionStorage.getItem("id"));
            const docName = documentElement.getAttribute("doc-name");
            
            const data = new FormData();
            data.append('userId', userId);
            data.append('docId', docId);
            data.append('docName', docName);
            data.append('newFolderId', targetFolder.getAttribute("folder-id"));
            
            makePutCall('MoveDocument', data, (request) => {
				if (request.readyState == 4) {
					if(request.status == 200) {
						var data = JSON.parse(request.responseText);
				        if(data) {
				            document.getElementById("errorMsg").innerHTML = "";
		                    const ul = targetFolder.querySelector('ul') || document.createElement('ul');
		                    ul.appendChild(documentElement);
		                    targetFolder.appendChild(ul);
				        }
					} else {
						var message = request.responseText;
						document.getElementById("errorMsg").textContent = message;
					}
				}
			});
        }
    }
}

function initTrashCan() {
    // show element can be dropped here
    document.getElementById("trashcontainer").addEventListener("dragover", (ev) => {
        ev.preventDefault();
    });

    // handle the deletion of the file dropped
    document.getElementById("trashcontainer").addEventListener("drop", (event) => {
        const text = event.dataTransfer.getData('text/plain');
        const answer = confirm("This action can not be undone!\nDo you want to proceed?");
        
        if (answer === true) {
            // Make an AJAX call to delete the document
            if(text.startsWith('d-')) {
				const docId = text.split('-')[1];
				
				makeDeleteCall('docDeleter/'+docId, (request) => {
					if (request.readyState == 4) {
						if(request.status == 200) {
							alert("Doc successfully deleted!");
							document.getElementById("errorMsg").innerHTML = "";
		                    const documentElement = document.querySelector(`[doc-id="${docId}"]`);
		                    if (documentElement) {
		                        documentElement.remove();
		                    }
						} else {
							var message = request.responseText;
							document.getElementById("errorMsg").textContent = message;
						}
					}
				});
            }
            else if (text.startsWith('f-')) {
				const folderId = text.split('-')[1];
				makeDeleteCall('folderDeleter/'+folderId, (request) => {
					if (request.readyState == 4) {
						if(request.status == 200) {
							alert("Folder successfully deleted!");
							document.getElementById("errorMsg").innerHTML = "";
		                    const documentElement = document.querySelector(`[folder-id="${folderId}"]`);
		                    if (documentElement) {
		                        documentElement.remove();
		                    }
		                    
				            if(document.querySelectorAll('.folder').length == 0) {
								document.getElementById('emptytext').style.visibility = "visible";
								document.getElementById('emptytext').classList.add("emptytext");
								document.getElementById('trash').style.visibility = "hidden";
							}
						} else {
							var message = request.responseText;
							document.getElementById("errorMsg").textContent = message;
						}
					}
				});
			}
        }
    });
}
