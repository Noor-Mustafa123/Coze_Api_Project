document.addEventListener('DOMContentLoaded', () => {
    const contactsList = document.querySelector('.contacts ul');
    const chatContent = document.querySelector('.message-content');
    const messageInput = document.querySelector('.message-form .input input');
    let activeChatId = null;
    let activeBotId = null;
    let activeUserId = null;

    // Fetch workspaces - this will now require authentication
    fetch("http://localhost:8080/v1/workspaces/list")
        .then(response => {
            if (response.status === 401) {
                // If unauthorized, browser should redirect to login
                window.location.href = '/login';
                return;
            }
            return response.json();
        })
        .then(data => {
            // Process workspaces and bots as before
            if (data.paginWorkspaceResponseObj.items.length > 0) {
                const firstWorkspace = data.paginWorkspaceResponseObj.items[0];
                return fetch(`http://localhost:8080/v1/list/bot?id=${firstWorkspace.id}`)
                    .then(response => {
                        if (response.status === 401) {
                            window.location.href = '/login';
                            return;
                        }
                        return response.json();
                    })
                    .then(botsData => {
                        contactsList.innerHTML = '';
                        if (botsData.length > 0) {
                            botsData.forEach(bot => {
                                const contactItem = `
                                    <li>
                                        <a href="#" data-bot-id="${bot.bot_id}">
                                            <img src="${bot.icon_url || 'img/avatar.png'}" alt="Bot Avatar">
                                            <div class="contact">
                                                <div class="name">${bot.bot_name}</div>
                                                <div class="message">${bot.description || 'No description'}</div>
                                            </div>
                                        </a>
                                    </li>
                                `;
                                contactsList.innerHTML += contactItem;
                            });
                        } else {
                            contactsList.innerHTML = '<li><div class="no-bots">No bots available</div></li>';
                        }
                    });
            } else {
                contactsList.innerHTML = '<li><div class="no-workspaces">No workspaces available</div></li>';
            }
        })
        .catch(error => {
            console.error('Error fetching workspaces or bots:', error);
            contactsList.innerHTML = '<li><div class="error">Error loading bots</div></li>';
        });

    // Rest of your existing code remains the same...

    // Handle bot click to start a chat
    contactsList.addEventListener('click', (event) => {
        console.log("bot click listener triggered");
        event.preventDefault();
        const target = event.target.closest('a');
        if (target && target.dataset.botId) {
            const botId = target.dataset.botId;
            // Create a chat with the selected bot
            fetch(`http://localhost:8080/v1/create/chat?botId=${botId}&userId=123`)
                .then(response => {
                    if (response.status === 401) {
                        window.location.href = '/login';
                        return;
                    }
                    return response.json();
                })
                .then(chatData => {
                    activeChatId = chatData.id;
                    activeBotId = botId;
                    activeUserId = '123';
                    console.log('activeChatId set to:', activeChatId);
                    chatContent.innerHTML = ''; // Clear previous messages
                })
                .catch(error => {
                    console.error('Error creating chat:', error);
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
                        // Add credentials if needed
                        // 'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({
                        user_id: activeUserId,
                        bot_id: activeBotId,
                        message: message
                    })
                })
                .then(response => {
                    if (response.status === 401) {
                        window.location.href = '/login';
                        return;
                    }
                    return response.json();
                })
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
