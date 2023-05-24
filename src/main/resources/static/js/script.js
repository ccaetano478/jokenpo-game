'use strict'

let stompClient
let username
let gameId
const url = 'http://localhost:8080';
let gameStatus;
let movement = [];

const connectSocket = () =>{
    let socket = new SockJS(url + "/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},
        function () {
            stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log("Voltou" + data);
            displayResponse(data);
        })
    })
}

const displayResponse = (data) => {
    const login = document.querySelector('#login')
    const waitingPage = document.querySelector('#waiting-players')
    const chatPage = document.querySelector('#chat-page')

    if(data.player2 == null || data.player2 == undefined){
        login.classList.add('hide')
        waitingPage.classList.remove('hide')
        waitingPage.style.color = "white"
        return
    }

    if (data.status == "MOVE"){
        showMove(data)
    }
    login.classList.add('hide')
    waitingPage.classList.add('hide')
    chatPage.classList.remove('hide')
    gameStatus = "on";
}

const registerMovement = (input) => {
    if(movement.length <= 2){
        movement.push(input)
    }
    if(movement.length === 2){
        showWinner(movement)
    }
}

const setInput = (inputKey) => {
    let playerInput = {"login": username, "input": inputKey, "gameId": gameId}
    registerMovement(playerInput)
    stompClient.send("/app/move", {}, JSON.stringify(playerInput))
}

const showWinner = (playersMoves) => {

    $.ajax({
        url: url + "/game/ws",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            playersMoves
        }),
        success: function (data) {
            const chat = document.getElementById("chat")
            chat.innerHTML = `O vencedor é ${data.winner}`
            chat.color = "white"

        },
        error: function (error) {
            console.log(error);
        }
    })

}


const showMove = (data) => {
    const message = data

    const chatCard = document.createElement('div')
    chatCard.className = 'card-body'

    const flexBox = document.createElement('div')
    flexBox.className = 'd-flex justify-content-end mb-4'
    chatCard.appendChild(flexBox)

    const messageElement = document.createElement('div')
    messageElement.className = 'msg_container_send'

    flexBox.appendChild(messageElement)

    if (message.type === 'CONNECT') {
        messageElement.classList.add('event-message')
        message.input = message.from + ' connected!'
    } else if (message.type === 'DISCONNECT') {
        messageElement.classList.add('event-message')
        message.input = message.from + ' left!'
    } else {
        messageElement.classList.add('chat-message')

        const avatarContainer = document.createElement('div')
        avatarContainer.className = 'img_cont_msg'
        const avatarElement = document.createElement('div')
        avatarElement.className = 'circle user_img_msg'
        const avatarText = document.createTextNode(message.playerTurn.login)
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.playerTurn.login)
        avatarContainer.appendChild(avatarElement)

        messageElement.style['background-color'] = getAvatarColor(message.playerTurn.login)

        flexBox.appendChild(avatarContainer)

        const time = document.createElement('span')
        time.className = 'msg_time_send'
        time.innerHTML = message.time
        messageElement.appendChild(time)

    }

    messageElement.innerHTML = message.input

    const chat = document.querySelector('#chat')
    chat.appendChild(flexBox)
    chat.scrollTop = chat.scrollHeight
}

const getAvatarColor = (messageSender) => {
    const colours = ['#2196F3', '#32c787', '#1BC6B4', '#A1B4C4']
    const index = Math.abs(hashCode(messageSender) % colours.length)
    return colours[index]
}

const hashCode = (str) => {
    let hash = 0
    for (let i = 0; i < str.length; i++) {
       hash = str.charCodeAt(i) + ((hash << 5) - hash)
    }
    return hash
}


const joinGame = () => {
    username = document.querySelector('#username').value.trim()
    //mostrar tela de aguardando novo jogador
    if (username) {
        

        $.ajax({
            url: url + "/game/join",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": username
            }),
            success: function (data) {

                console.log(data);
                gameId = data.id;
                //reset();
                connectSocket(gameId);
                alert("Você ingressou no jogo de id: " + data.id);
                displayResponse(data)

            },
            error: function (error) {
                console.log(error);
            }
        })
    }else{
        alert("Por favor insira o login")
    }

}



const createGame = (event) => {
    //pegar meu username
    username = document.querySelector('#username').value.trim()
    //mostrar tela de aguardando novo jogador
    if (username) {
        

        $.ajax({
            url: url + "/game/start",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": username
            }),
            success: function (data) {

                console.log(data);
                gameId = data.id;
                //reset();
                connectSocket(gameId);
                alert("Jogo criado com o id: " + data.id);
                displayResponse(data)

            },
            error: function (error) {
                console.log(error);
            }
        })
    }else{
        alert("Por favor insira o login")
    }
    
    
    //colocar como player 1 o jogador que criou o jogo 
    //criar um id para o jogo e colocar o jogo dentro de um array de jogos disponiveis 
    //mudar o status do jogo para criado//aguardando jogador 2 e inserir o game dentro do array de games

}

// const connect = (event) => {
//     username = document.querySelector('#username').value.trim()
//
//     if (username) {
//         const login = document.querySelector('#login')
//         login.classList.add('hide')
//
//         const chatPage = document.querySelector('#chat-page')
//         chatPage.classList.remove('hide')
//
//         const socket = new SockJS('/ws')
//         stompClient = Stomp.over(socket)
//         stompClient.connect({}, onConnected, onError)
//     }
//     event.preventDefault()
// }
//
// const onConnected = () => {
//     stompClient.subscribe('/topic/public', onMessageReceived)
//     stompClient.send("/app/chat.newUser",
//         {},
//         JSON.stringify({from: username, type: 'CONNECT'})
//     )
//     const status = document.querySelector('#status')
//     status.className = 'hide'
// }
//
// const onError = (error) => {
//     const status = document.querySelector('#status')
//     status.innerHTML = 'Could not find the connection you were looking for. Move along. Or, Refresh the page!'
//     status.style.color = 'red'
// }
//
// const sendMessage = (event) => {
//     event.preventDefault();
//     console.log(input)
//     let messageContent = input
//
//     if (messageContent && stompClient) {
//         const chatMessage = {
//             type: 'CHAT',
//             from: username,
//             input: messageContent
//
//         }
//         stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage))
//         messageContent = ""
//     }
// }
//
//
// const onMessageReceived = (payload) => {
//     const message = JSON.parse(payload.body);
//     console.log(message.type)
//
//
//
//     const chatCard = document.createElement('div')
//     chatCard.className = 'card-body'
//
//     const flexBox = document.createElement('div')
//     flexBox.className = 'd-flex justify-content-end mb-4'
//     chatCard.appendChild(flexBox)
//
//     const messageElement = document.createElement('div')
//     messageElement.className = 'msg_container_send'
//
//     flexBox.appendChild(messageElement)
//
//     if (message.type === 'CONNECT') {
//         messageElement.classList.add('event-message')
//         message.input = message.from + ' connected!'
//     } else if (message.type === 'DISCONNECT') {
//         messageElement.classList.add('event-message')
//         message.input = message.from + ' left!'
//     } else {
//         messageElement.classList.add('chat-message')
//
//         const avatarContainer = document.createElement('div')
//         avatarContainer.className = 'img_cont_msg'
//         const avatarElement = document.createElement('div')
//         avatarElement.className = 'circle user_img_msg'
//         const avatarText = document.createTextNode(message.from[0])
//         avatarElement.appendChild(avatarText);
//         avatarElement.style['background-color'] = getAvatarColor(message.from)
//         avatarContainer.appendChild(avatarElement)
//
//         messageElement.style['background-color'] = getAvatarColor(message.from)
//
//         flexBox.appendChild(avatarContainer)
//
//         const time = document.createElement('span')
//         time.className = 'msg_time_send'
//         time.innerHTML = message.time
//         messageElement.appendChild(time)
//
//     }
//
//     messageElement.innerHTML = message.input
//
//     const chat = document.querySelector('#chat')
//     chat.appendChild(flexBox)
//     chat.scrollTop = chat.scrollHeight
// }
//
// const setInput = (obj) => {
//     input = obj
//     console.log(input);
// }
//
// const hashCode = (str) => {
//     let hash = 0
//     for (let i = 0; i < str.length; i++) {
//        hash = str.charCodeAt(i) + ((hash << 5) - hash)
//     }
//     return hash
// }
//
//
// const getAvatarColor = (messageSender) => {
//     const colours = ['#2196F3', '#32c787', '#1BC6B4', '#A1B4C4']
//     const index = Math.abs(hashCode(messageSender) % colours.length)
//     return colours[index]
// }

/*
const loginForm = document.querySelector('#login-form')
loginForm.addEventListener('submit', connect, true) 
const messageControls = document.querySelector('#message-controls')
messageControls.addEventListener('submit', sendMessage, true)*/
