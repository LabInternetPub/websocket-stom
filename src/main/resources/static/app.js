var stompClient = null;
var myChannel = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
        $("#conversation2").show();
    }
    else {
        $("#conversation").hide();
        $("#conversation2").hide();
        //$("#greetings").deleteContents();
        //$("#channelGreetings").deleteContents();
    }
    $("#greetings").html("");
    $("#channelGreetings").html("");
}

function connect(channel) {
    myChannel = channel;
    var socket = new SockJS('/gs-guide-websocket');
    console.log('Channel: ' + channel)
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        //document.getElementById('titleChannel').innerHTML = 'Channel ' + channel;
        $("#titleChannel").html('Channel ' + channel);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messaging/' + channel, function (greeting) {
            showGreetingChannel(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/topic/messaging/', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage(mapping) {
    stompClient.send(mapping, {}, JSON.stringify({'message': $("#message").val(), 'channel': myChannel}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showGreetingChannel(message) {
    $("#channelGreetings").append("<tr><td>" + message + "</td></tr>");
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect($("#channel").val()); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage('/app/messageChannel'); });
    $( "#sendDefault" ).click(function() { sendMessage('/app/message'); });
    $( "#sendBoth" ).click(function() { sendMessage('/app/messageBoth/' + myChannel); });
});

