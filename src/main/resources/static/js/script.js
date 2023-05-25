'use strict'

let stompClient
let username
let gameId
const url = 'http://localhost:8080';
let gameStatus;
let gameMode;

const connectSocket = () =>{
    let socket = new SockJS(url + "/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({},
        function () {
            stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            displayResponse(data);
        })
    })
}

const displayResponse = (data) => {
    const login = document.querySelector('#login')
    const waitingPage = document.querySelector('#waiting-players')
    const chatPage = document.querySelector('#chat-page')

    if(gameMode == "pc"){
        data.player2 = "pc"
    }

    if(data.player2 == null || data.player2 == undefined ){
        login.classList.add('hide')
        waitingPage.classList.remove('hide')
        waitingPage.style.color = "white"
        return
    }

    if (data.status == "MOVE"){
        showMove(data)
    }

    if(data.status == "FINISHED"){
        const chat = document.getElementById("chat")
        chat.innerHTML = `${data.winner} venceu!`
        chat.style.color = "white"
    }
    login.classList.add('hide')
    waitingPage.classList.add('hide')
    chatPage.classList.remove('hide')
    gameStatus = "on";
}

const setInput = (inputKey) => {
    let playerInput = {"login": username, "input": inputKey, "gameId": gameId}
    if(gameMode == "mp"){
        stompClient.send("/app/move", {}, JSON.stringify(playerInput))
    }
    if(gameMode == "pc"){
        const moves = ["pape", "stone", "scissors"]
        const playerPC = {"login": "PC", "input": moves[Math.floor(Math.random() * moves.length)], "gameId": gameId}
        const montarJSON = [playerInput,playerPC ]
        stompClient.send("/app/pc", {}, JSON.stringify(montarJSON))
    }
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
        const avatarText = document.createTextNode(message.playerTurn.login.slice(0,2))
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

    messageElement.innerHTML = message.playerTurn.login + ' fez sua escolha'

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

const playPC = () => {
    gameMode = "pc"
    username = document.querySelector('#username').value.trim()

    if (username) {
        $.ajax({
            url: url + "/game/pc",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": username
            }),
            success: function (data) {

                console.log(data);
                gameId = data.id;
                connectSocket(gameId);
                alert("Voce ingressou no jogo de id: " + data.id);
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


const joinGame = () => {
    gameMode = "mp"
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
                alert("Voce ingressou no jogo de id: " + data.id);
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
    gameMode = "mp"
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
    } else {
        alert("Por favor insira o login")
    }
}
