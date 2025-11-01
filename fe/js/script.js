
document.addEventListener('DOMContentLoaded', () => {
    const contactsList = document.querySelector('.contacts ul');
    const chatContent = document.querySelector('.message-content');
    const messageInput = document.querySelector('.message-form .input input');
    let activeChatId = null;
    let activeBotId = null;
    let activeUserId = null;

    // Fetch workspaces and populate the contact list
    fetch("http://localhost:8080/v1/workspaces/list")
        .then(response => response.json())
        .then(data => {
            contactsList.innerHTML = ''; // Clear existing contacts
            data.paginWorkspaceResponseObj.items.forEach(workspace => {
                const contactItem = `
                    <li>
                        <a href="#" data-workspace-id="${workspace.id}">
                            <img src="img/avatar.png" alt="Avatar">
                            <div class="contact">
                                <div class="name">${workspace.name}</div>
                            </div>
                        </a>
                    </li>
                `;
                contactsList.innerHTML += contactItem;
            });
        });

    // Handle contact click to start a chat
    contactsList.addEventListener('click', (event) => {
    console.log("contact even listener triggered")
        event.preventDefault();
        const target = event.target.closest('a');
        if (target) {
            const workspaceId = target.dataset.workspaceId;
            // Create a bot and then a chat
            fetch(`http://localhost:8080/v1/create/bot?id=${workspaceId}`)
                .then(response => response.json())
                .then(botData => {
                    const botId = botData.bot_id;
                    fetch(`http://localhost:8080/v1/create/chat?botId=${botId}&userId=123`) // Using a static user ID for now
                        .then(response => response.json())
                        .then(chatData => {
                            activeChatId = chatData.id;
                            activeBotId = botId;
                            activeUserId = '123';
                            console.log('activeChatId set to:', activeChatId);
                            chatContent.innerHTML = ''; // Clear previous messages
                        });
                });
        }
    });

    // Handle message input
    const sendMessage = () => {
        console.log("text")
        console.log('activeChatId in sendMessage:', activeChatId);
        if (activeChatId) {
            const message = messageInput.value.trim();
            console.log(message)
            if (message) {
                // Display user's message immediately
                const messageElement = `
                    <div class="message me">
                        <div class="bubble">${message}</div>
                        <div class="time">Jetzt</div>
                    </div>
                `;
                chatContent.innerHTML += messageElement;
                messageInput.value = '';

                // Send message to the backend
                fetch(`http://localhost:8080/v1/input`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        user_id: activeUserId,
                        bot_id: activeBotId,
                        message: message
                    })
                })
                .then(response => response.json())
                .then(data => {
                    // Display bot's response
                    const botMessageElement = `
                        <div class="message">
                            <div class="bubble">${data.messages[0].content}</div>
                            <div class="time">Jetzt</div>
                        </div>
                    `;
                    chatContent.innerHTML += botMessageElement;
                });
            }
        }
    };

    messageInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });

    const sendButton = document.querySelector('.send-btn a');

    sendButton.addEventListener('click', (event) => {
        event.preventDefault();
        console.log("message btn click trigered")
        sendMessage();
    });
});
